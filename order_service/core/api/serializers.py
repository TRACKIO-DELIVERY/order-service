from rest_framework import serializers
from core.models import (
    User,
    Order,
    DeliveryPerson,
    OrderTracking,
    ComplementaryOrder
)

class UserReadSerializer(serializers.ModelSerializer[User]):

    """
        Read-only serializer for the User model.

        This serializer is used to represent user data in read operations (e.g., GET requests),
        ensuring that all returned fields are read-only.

        Returned fields:
            - full_name (str): The user's full name.
            - is_active (bool): Indicates whether the user is active.
            - user_type (str): The type of user (Administrator, Customer, or Delivery Man).

        All fields are marked as read-only.
    """


    class Meta:
        model = User
        fields = ["full_name","is_active","user_type"]
        read_only_fields = ["full_name","is_active","user_type"]


class UserCreatedSerializer(serializers.ModelSerializer[User]):

    """

        Serializer for creating User instances.

        This serializer is used during user creation processes, such as registration.
        It allows setting key user attributes including full name, email, birth date,
        active status, and user type.

        Fields:
            - full_name (str): The full name of the user.
            - email (str): The user's email address.
            - birth_date (date): The user's date of birth.
            - is_active (bool): Whether the user account is active.
            - user_type (str): The role of the user (Administrator, Customer, or Delivery Man).

    """

    class Meta:
        model = User
        fields = ["full_name","email","birth_date","is_active","user_type","cpf"]

class UserUpdateSerializer(serializers.ModelSerializer[User]):

    """

        Serializer for updating User instances.

        This serializer is used to update user data, including their name, active status,
        and user role. Some fields are marked as read-only and cannot be modified.

        Writable fields:
            - full_name (str): The full name of the user.
            - is_active (bool): Indicates whether the user account is active.
            - user_type (str): The user's role (Administrator, Customer, or Delivery Man).

        Read-only fields:
            - cpf (str): The user's CPF (unique identifier).
            - birth_date (date): The user's date of birth.

    """

    class Meta:
        model = User
        fields = ["full_name","email","is_active","user_type"]
        read_only_fields = ["cpf","birth_date"]

class DeliveryPersonReadSerializer(serializers.ModelSerializer[DeliveryPerson]):
    """
    
    Read-only serializer for the DeliveryPerson model.

    This serializer is used to present delivery person information in read operations
    (e.g., GET requests), including user details and delivery-specific attributes.

    Nested Serializers:
        - user (UserReadSerializer): Read-only user information linked to the delivery person.

    Fields:
        - user (dict): User details (e.g., full name, email, user type).
        - availability (str): Current availability status of the delivery person.
        - vehicle (str): Type or model of the delivery vehicle.
        - license_plate (str): License plate of the delivery vehicle.
        - url (str): URL to the detailed delivery person endpoint.

    Read-only fields:
        - availability
        - vehicle
        - license_plate

    Additional configuration:
        - The "url" field uses "api:deliveryperson-detail" with the primary key as lookup.
    """

    user = UserReadSerializer() 

    class Meta:
        model = DeliveryPerson
        fields = [
            "user",
            "availability",
            "vehicle",
            "license_plate",
            "url",
        ]
        read_only_fields = ["availability", "vehicle", "license_plate"]
        extra_kwargs = {
            "url": {"view_name": "api:deliveryperson-detail", "lookup_field": "pk"},
        }

class OrderReadSerializer(serializers.ModelSerializer[Order]):

    """
    Read-only serializer for the Order model.

    This serializer is used to present detailed, non-editable information about orders,
    including customer and delivery person data, financials, and timestamps.

    Computed fields:
        - user_full_name (str): Full name of the user who placed the order.
        - user_cpf (str): CPF (identifier) of the user.
        - delivery_person_full_name (str): Full name of the delivery person assigned to the order.

    Fields:
        - id (int): Unique identifier of the order.
        - url (str): Hyperlink to the detailed order endpoint.
        - establishment (str): Name of the establishment where the order was placed.
        - user_full_name (str): Full name of the customer.
        - user_cpf (str): CPF of the customer.
        - delivery_person_full_name (str): Full name of the delivery person.
        - delivery_fee (decimal): Fee charged for delivery.
        - order_value (decimal): Total value of the order.
        - order_status (str): Current status of the order (e.g., En Route, Delivered).
        - closing_date (datetime): Date and time the order was closed, if applicable.
        - app_origin (str): Name of the application where the order originated.
        - created_at (datetime): When the order was created.
        - updated_at (datetime): When the order was last updated.

    All fields in this serializer are read-only.

    Additional configuration:
        - The "url" field uses the view name "api:order-detail" and looks up by primary key.
    """

    user_full_name = serializers.CharField(source="user.full_name", read_only=True)
    user_cpf = serializers.CharField(source="user.cpf", read_only=True)
    delivery_person_full_name = serializers.CharField(
        source="delivery_person.user.full_name", read_only=True
    )

    class Meta:
        model = Order
        fields = [
            "id",
            "url", 
            "establishment",
            "user_full_name", 
            "user_cpf",       
            "delivery_person_full_name", 
            "delivery_fee",
            "order_value",
            "order_status",
            "closing_date",
            "app_origin",
            "created_at",
            "updated_at",
        ]
        read_only_fields = [
            "id", "url", "establishment", "user_full_name", "user_cpf",
            "delivery_person_full_name", "delivery_fee", "order_value",
            "order_status", "closing_date", "app_origin", "created_at", "updated_at",
        ]
        extra_kwargs = {
            "url": {"view_name": "api:order-detail", "lookup_field": "pk"}
        }


class OrderTrackingSerializer(serializers.ModelSerializer[OrderTracking]):
    class Meta:
        model = OrderTracking
        fields = [
            "id",
            "order",
            "start_latitude",
            "start_longitude",
            "end_latitude",
            "end_longitude",
            "event_status",
            "timestamp",
            "url",
        ]
        read_only_fields = ["timestamp"]
        extra_kwargs = {
            "url": {"view_name": "api:ordertracking-detail", "lookup_field": "pk"},
            "order": {"view_name": "api:order-detail", "lookup_field": "pk"},
        }
