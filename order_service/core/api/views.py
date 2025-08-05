import logging

from asgiref.sync import async_to_sync
from django.db import transaction
from rest_framework import status
from rest_framework import viewsets
from rest_framework.response import Response

from order_service.core.models import ComplementaryOrder
from order_service.core.models import Establishment
from order_service.core.models import Order
from order_service.core.models import OrderTracking
from order_service.messaging.producer import producer
from order_service.services import order_tracking_service

from .serializers import CreateComplementaryOrderSerializer
from .serializers import CreatedEstablishmentSerializer
from .serializers import CreateOrderAlignedSerializer
from .serializers import OrderCreatedSerializer
from .serializers import OrderReadSerializer
from .serializers import OrderTrackingCreatedSerializer
from .serializers import OrderTrackingReadSerializer
from .serializers import OrderTrackingUpdateSerializer
from .serializers import OrderUpdateSerializer
from .serializers import ReadEstablishmentSerializer
from .serializers import ReadOnlyComplementaryOrderSerializer
from .serializers import UpdateComplementaryOrderSerializer
from .serializers import UpdateEstablishmentSerializer


class OrderViewSet(viewsets.ModelViewSet):
    """
    ViewSet for managing Order instances.

    Handles standard CRUD operations (Create, Retrieve, Update, Delete) for orders.
    Automatically chooses the appropriate serializer class based on the current action.

    Serializer classes:
        - OrderCreatedSerializer: Used when creating a new order (POST).
        - OrderUpdateSerializer: Used for updating an order (PUT/PATCH).
        - OrderReadSerializer: Default serializer used for retrieving order data (GET).

    Query optimizations:
        - Uses `select_related()` to efficiently fetch related objects:
            * complementary_order
            * user
            * delivery_person and its related user

    Example endpoints:
        - GET    /api/orders/           → List all orders
        - POST   /api/orders/           → Create a new order
        - GET    /api/orders/{id}/      → Retrieve a specific order
        - PUT    /api/orders/{id}/      → Fully update an order
        - PATCH  /api/orders/{id}/      → Partially update an order
        - DELETE /api/orders/{id}/      → Delete an order
    """

    queryset = Order.objects.all().select_related("complementary_order", "delivery_person__user")
    serializer_class = OrderReadSerializer

    def get_serializer_class(self):
        if self.action == "create":
            return OrderCreatedSerializer
        if self.action in ["update", "partial_update"]:
            return OrderUpdateSerializer
        return OrderReadSerializer
    


class OrderTrackingViewSet(viewsets.ModelViewSet):
    """
    ViewSet for managing OrderTracking records.

    Supports full CRUD operations for tracking the delivery progress of orders.
    Dynamically selects the appropriate serializer depending on the action performed.

    Serializer classes:
        - OrderTrackingCreatedSerializer: Used when creating a new tracking record (POST).
        - OrderTrackingUpdateSerializer: Used when updating tracking data (PUT/PATCH).
        - OrderTrackingReadSerializer: Default serializer for read operations (GET).

    Example endpoints:
        - GET    /api/order-tracking/           → List all tracking records
        - POST   /api/order-tracking/           → Create a new tracking record
        - GET    /api/order-tracking/{id}/      → Retrieve a specific tracking record
        - PUT    /api/order-tracking/{id}/      → Fully update a tracking record
        - PATCH  /api/order-tracking/{id}/      → Partially update a tracking record
        - DELETE /api/order-tracking/{id}/      → Delete a tracking record
    """

    queryset = OrderTracking.objects.all()

    serializer_class = OrderTrackingReadSerializer

    def get_serializer_class(self):
        if self.action == "create":
            return OrderTrackingCreatedSerializer
        if self.action in ["update", "partial_update"]:
            return OrderTrackingUpdateSerializer
        return OrderTrackingReadSerializer

class ComplementaryOrderViewSet(viewsets.ModelViewSet):
    """
    ViewSet for managing ComplementaryOrder instances.

    Handles all standard CRUD operations related to additional order details,
    such as delivery and pickup addresses.

    Automatically selects the appropriate serializer based on the current action.

    Serializer classes:
        - CreateComplementaryOrderSerializer: Used for creating a new complementary order (POST).
        - UpdateComplementaryOrderSerializer: Used for updating complementary order data (PUT/PATCH).
        - ReadOnlyComplementaryOrderSerializer: Default serializer for retrieving
        complementary order information (GET).

    Example endpoints:
        - GET    /api/complementary-orders/           → List all complementary orders
        - POST   /api/complementary-orders/           → Create a new complementary order
        - GET    /api/complementary-orders/{id}/      → Retrieve a specific complementary order
        - PUT    /api/complementary-orders/{id}/      → Fully update a complementary order
        - PATCH  /api/complementary-orders/{id}/      → Partially update a complementary order
        - DELETE /api/complementary-orders/{id}/      → Delete a complementary order
    """

    queryset = ComplementaryOrder.objects.all()

    def get_serializer_class(self):
        if self.action == "create":
            return CreateComplementaryOrderSerializer
        if self.action in ["update", "partial_update"]:
            return UpdateComplementaryOrderSerializer
        return ReadOnlyComplementaryOrderSerializer

    def perform_create(self, serializer):
        with transaction.atomic():
            complemantary_order = serializer.save()
            order_tracking_service.create_tracking_for_order(complemantary_order.order)


class OrderAlignedViewSet(viewsets.ModelViewSet):
    """
    ViewSet for handling aligned order creation and management.
    This uses a custom serializer that nests complementary order data.
    """

    queryset = Order.objects.all()
    serializer_class = CreateOrderAlignedSerializer

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        order = serializer.save()

        order_tracking_service.create_tracking_for_order(order)
        tracking = order.tracking_order

        read_data = OrderReadSerializer(order).data
        read_data2 = OrderTrackingReadSerializer(tracking).data
        
        payload = {
            "order_id": read_data.get("id"),
            "email": read_data.get("email") or None,
            "status": read_data.get("order_status") or None,
            "delivery_person_full_name": read_data.get("delivery_person_full_name") or None,
            "delivery_fee": read_data.get("delivery_fee") or None,
            "full_delivery_address": read_data.get("full_delivery_address"),
            "full_pickup_address": read_data.get("full_pickup_address"),
            "start_latitude": read_data2.get("start_latitude"),
            "start_longitude": read_data2.get("start_longitude"),
            "end_latitude": read_data2.get("end_latitude"),
            "end_longitude": read_data2.get("end_longitude")
        }

        try:
            async_to_sync(producer)(routing_key_name="order.created", payload=payload)
            logging.info(f"Message sent to broker for order {order.id}")
        except Exception as exc:
            logging.exception(f"Error sending message to broker. Exception: {type(exc).__name__} Message: {exc}")

        return Response(read_data, status=status.HTTP_201_CREATED)


class EstablishmentViewSet(viewsets.ModelViewSet):
    serializer_class = CreatedEstablishmentSerializer
    queryset = Establishment.objects.all()

    def get_queryset(self):
        return Establishment.objects.by_administrator(self.request.user).select_related("administrator")

    def get_serializer_class(self):
        if self.action == "create":
            return CreatedEstablishmentSerializer
        if self.action in ["update", "partial_update"]:
            return UpdateEstablishmentSerializer
        return ReadEstablishmentSerializer

