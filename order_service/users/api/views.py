import logging

from django.core.exceptions import ValidationError
from rest_framework import exceptions
from rest_framework import permissions
from rest_framework import status
from rest_framework import viewsets
from rest_framework.response import Response

from order_service.users.models import DeliveryPerson
from order_service.users.models import User

from .serializers import DeliveryPersonCreatedSerializer
from .serializers import DeliveryPersonReadSerializer
from .serializers import DeliveryPersonUpdateSerializer
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

    ##ver isso aqui
    permission_classes = [permissions.AllowAny]
    serializer_class = UserReadSerializer
    queryset = User.objects.all()

    def create(self, request, *args, **kwargs):
        try:
            return super().create(request, *args, **kwargs)
        except ValidationError as exc:
            logging.exception(f"Validation error during user creation: {exc}")
            raise exceptions.ValidationError({"error": exc.detail}) from exc
        except exceptions.ValidationError as exc:
            logging.exception(f"Validation error during user creation: {exc}")
            return Response({"error": exc.detail}, status=status.HTTP_400_BAD_REQUEST)
        except Exception as exc:
            return Response(f"An unexpected error occurred: {exc}", status=status.HTTP_500_INTERNAL_SERVER_ERROR)

    def get_serializer_class(self):
        if self.action == "create":
            return UserCreatedSerializer
        if self.action in ["update", "partial_update"]:
            return UserUpdateSerializer
        return UserReadSerializer

    def get_queryset(self):
        queryset = super().get_queryset()

        is_active = self.request.query_params.get("is_active", "true").lower() == "true"
        if is_active:
            queryset = queryset.filtered_objects.active()
        else:
            queryset = queryset.filtered_objects.inactive()

        user_type = self.request.query_params.get("user_type", None)
        if user_type is not None:
            if user_type == "Customer":
                queryset = queryset.filtered_objects.customers()
            elif user_type == "Administrator":
                queryset = queryset.filtered_objects.administrator()
            elif user_type == "Delivery Man":
                queryset = queryset.filtered_objects.delivery_man()

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

    permission_classes = [permissions.AllowAny]

    queryset = DeliveryPerson.objects.all().select_related("user")
    serializer_class = DeliveryPersonReadSerializer

    def create(self, request, *args, **kwargs):
        try:
            return super().create(request, *args, **kwargs)
        except ValidationError as exc:
            logging.exception(f"Validation error during delivery person creation: {exc}")
            raise exceptions.ValidationError({"error": exc.detail}) from exc
        except exceptions.ValidationError as exc:
            logging.exception(f"Validation error during user creation: {exc}")
            return Response({"error": exc.detail}, status=status.HTTP_400_BAD_REQUEST)
        except Exception as exc:
            return Response(f"An unexpected error occurred: {exc}", status=status.HTTP_500_INTERNAL_SERVER_ERROR)

    def get_serializer_class(self):
        if self.action == "create":
            return DeliveryPersonCreatedSerializer
        if self.action in ["update", "partial_update"]:
            return DeliveryPersonUpdateSerializer
        return DeliveryPersonReadSerializer
