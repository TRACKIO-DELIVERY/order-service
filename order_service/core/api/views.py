from core.models import ComplementaryOrder
from core.models import DeliveryPerson
from core.models import Order
from core.models import OrderTracking
from core.models import User
from rest_framework import viewsets

from .serializers import CreateComplementaryOrderSerializer
from .serializers import DeliveryPersonCreatedSerializer
from .serializers import DeliveryPersonReadSerializer
from .serializers import DeliveryPersonUpdateSerializer
from .serializers import OrderCreatedSerializer
from .serializers import OrderReadSerializer
from .serializers import OrderTrackingCreatedSerializer
from .serializers import OrderTrackingReadSerializer
from .serializers import OrderTrackingUpdateSerializer
from .serializers import OrderUpdateSerializer
from .serializers import ReadOnlyComplementaryOrderSerializer
from .serializers import UpdateComplementaryOrderSerializer
from .serializers import UserCreatedSerializer
from .serializers import UserReadSerializer
from .serializers import UserUpdateSerializer


class UserViewSet(viewsets.ModelViewSet):
    """
    ViewSet for managing User instances.

    Handles standard CRUD operations (Create, Retrieve, Update, Delete) for the User model.
    Automatically selects the appropriate serializer based on the current action.

    Serializer classes:
        - UserCreatedSerializer: Used when creating a new user (POST).
        - UserUpdateSerializer: Used for updating user data (PUT/PATCH).
        - UserReadSerializer: Default serializer for read operations (GET).

    Query parameter filters:
        - is_active (bool): Filters users by active status.
            Example: ?is_active=true
        - user_type (str): Filters users by role/type.
            Options: "Customer", "Administrator", "Delivery Man"
            Example: ?user_type=Customer

    Example endpoints:
        - GET    /api/users/           → List users
        - POST   /api/users/           → Create a new user
        - GET    /api/users/{id}/      → Retrieve a specific user
        - PUT    /api/users/{id}/      → Fully update a user
        - PATCH  /api/users/{id}/      → Partially update a user
        - DELETE /api/users/{id}/      → Delete a user
    """

    serializer_class = UserReadSerializer
    queryset = User.objects.all()

    def get_serializer_class(self):
        if self.action == "create":
            return UserCreatedSerializer
        if self.action in ["update", "partial_update"]:
            return UserUpdateSerializer
        return UserReadSerializer

    def get_queryset(self):
        queryset = super().get_queryset()

        is_active = self.request.query_params.get("is_active", None)
        if is_active is not None:
            if is_active.lower() == "true":
                queryset = queryset.active()
            elif is_active.lower() == "false":
                queryset = queryset.inactive()

        user_type = self.request.query_params.get("user_type", None)
        if user_type is not None:
            if user_type == "Customer":
                queryset = queryset.customers()
            elif user_type == "Administrator":
                queryset = queryset.administrator
            elif user_type == "Delivery Man":
                queryset = queryset.delivery_man

        return queryset


class DeliveryPersonViewSet(viewsets.ModelViewSet):
    """
    ViewSet for managing DeliveryPerson instances.

    Provides full CRUD operations (Create, Retrieve, Update, Delete) for delivery personnel.
    Automatically selects the appropriate serializer class based on the type of action.

    Serializer classes:
        - DeliveryPersonCreatedSerializer: Used when creating a new delivery person (POST).
        - DeliveryPersonUpdateSerializer: Used for updating delivery person data (PUT/PATCH).
        - DeliveryPersonReadSerializer: Default serializer for read operations (GET).

    Query optimizations:
        - Uses `select_related("user")` to optimize database access by prefetching related user data.

    Example endpoints:
        - GET    /api/delivery-people/           → List delivery personnel
        - POST   /api/delivery-people/           → Create a new delivery person
        - GET    /api/delivery-people/{id}/      → Retrieve a specific delivery person
        - PUT    /api/delivery-people/{id}/      → Fully update a delivery person
        - PATCH  /api/delivery-people/{id}/      → Partially update a delivery person
        - DELETE /api/delivery-people/{id}/      → Delete a delivery person
    """

    queryset = DeliveryPerson.objects.all().select_related("user")
    serializer_class = DeliveryPersonReadSerializer

    def get_serializer_class(self):
        if self.action == "create":
            return DeliveryPersonCreatedSerializer
        if self.action in ["update", "partial_update"]:
            return DeliveryPersonUpdateSerializer
        return DeliveryPersonReadSerializer


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

    queryset = Order.objects.all().select_related(
        "complementary_order", "user", "delivery_person__user"
    )
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
        - ReadOnlyComplementaryOrderSerializer: Default serializer for retrieving complementary order information (GET).

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
