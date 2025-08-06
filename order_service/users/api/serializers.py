from rest_framework import serializers

from order_service.users.models import DeliveryPerson
from order_service.users.models import User


class UserSerializer(serializers.ModelSerializer[User]):
    class Meta:
        model = User
        fields = ["username", "name", "url"]

        extra_kwargs = {
            "url": {"view_name": "api:user-detail", "lookup_field": "username"},
        }


class UserReadSerializer(serializers.ModelSerializer[User]):
    """
    Read-only serializer for the User model.

    This serializer is used to represent user data in read operations (e.g., GET requests),
    ensuring that all returned fields are read-only.

    Returned fields:
        - name (str): The user's full name.
        - is_active (bool): Indicates whether the user is active.
        - user_type (str): The type of user (Administrator, Customer, or Delivery Man).

    All fields are marked as read-only.
    """

    class Meta:
        model = User
        fields = ["id", "name", "is_active", "user_type"]
        read_only_fields = fields


class UserCreatedSerializer(serializers.ModelSerializer[User]):
    """

    Serializer for creating User instances.

    This serializer is used during user creation processes, such as registration.
    It allows setting key user attributes including full name, email, birth date,
    active status, and user type.

    Fields:
        - name (str): The full name of the user.
        - username (str): The username for the user, defaults to an empty string if not provided.
        - email (str): The user's email address.
        - birth_date (date): The user's date of birth.
        - password (str): The user's password, which is write-only and required for creation.
        - cpf (str): The user's CPF (Cadastro de Pessoas Físicas), a unique identifier in Brazil.
        - user_type (str): The role of the user (Administrator, Customer, or Delivery Man).

    """

    password = serializers.CharField(write_only=True, required=True)
    cpf = serializers.CharField(required=False, allow_blank=True, default=None)
    username = serializers.CharField(required=False, allow_blank=True, default="")

    class Meta:
        model = User
        fields = ["name", "username", "password", "email", "birth_date", "user_type", "cpf"]

    def create(self, validated_data):
        return User.objects.create_user(
            email=validated_data.get("email"),
            password=validated_data.get("password"),
            name=validated_data.get("name"),
            username=validated_data.get("username", ""),
            cpf=validated_data.get("cpf"),
            birth_date=validated_data.get("birth_date"),
            user_type=validated_data.get("user_type"),
        )


class UserUpdateSerializer(serializers.ModelSerializer[User]):
    """

    Serializer for updating User instances.

    This serializer is used to update user data, including their name, active status,
    and user role. Some fields are marked as read-only and cannot be modified.

    Writable fields:
        - name (str): The full name of the user.
        - is_active (bool): Indicates whether the user account is active.
        - user_type (str): The user's role (Administrator, Customer, or Delivery Man).

    Read-only fields:
        - cpf (str): The user's CPF (unique identifier).
        - birth_date (date): The user's date of birth.

    """

    class Meta:
        model = User
        fields = ["name", "email", "is_active", "user_type", "cpf", "birth_date"]
        read_only_fields = ["cpf", "birth_date"]

    def update(self, instance, validated_data):
        return super().update(instance, validated_data)


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
        read_only_fields = fields
        extra_kwargs = {
            "url": {"view_name": "api:deliveryperson-detail", "lookup_field": "pk"},
        }


class DeliveryPersonCreatedSerializer(serializers.ModelSerializer[DeliveryPerson]):
    user = UserCreatedSerializer()

    class Meta:
        model = DeliveryPerson
        fields = ["user", "availability", "vehicle", "license_plate"]

    def create(self, validated_data):
        user_data = validated_data.pop("user")
        user = UserCreatedSerializer.create(UserCreatedSerializer(), validated_data=user_data)
        validated_data["user"] = user
        return super().create(validated_data)


class DeliveryPersonUpdateSerializer(serializers.ModelSerializer[DeliveryPerson]):
    class Meta:
        model = DeliveryPerson
        fields = ["user", "availability", "vehicle", "license_plate"]

    def update(self, instance, validated_data):
        return super().update(instance, validated_data)
