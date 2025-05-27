from core.models import ComplementaryOrder
from core.models import DeliveryPerson
from core.models import Order
from core.models import OrderTracking
from core.models import User
from rest_framework import serializers


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
        fields = ["id", "full_name", "is_active", "user_type"]
        read_only_fields = fields


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
        fields = ["full_name", "email", "birth_date", "is_active", "user_type", "cpf"]

    def create(self, validated_data):
        return super().create(validated_data)


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
        fields = ["full_name", "email", "is_active", "user_type"]
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
    class Meta:
        model = DeliveryPerson
        fields = ["user", "availability", "vehicle", "license_plate"]

    def create(self, validated_data):
        return super().create(validated_data)


class DeliveryPersonUpdateSerializer(serializers.ModelSerializer[DeliveryPerson]):
    class Meta:
        model = DeliveryPerson
        fields = ["user", "availability", "vehicle", "license_plate"]

    def update(self, instance, validated_data):
        return super().update(instance, validated_data)


class ReadOnlyComplementaryOrderSerializer(serializers.ModelSerializer):
    """
    Read-only serializer for ComplementaryOrder.

    Provides a non-editable representation of delivery and pickup address details,
    including both individual fields and formatted full address strings.

    Computed fields:
        - full_delivery_address (str): Concatenated string of the delivery address.
        - full_pickup_address (str): Concatenated string of the pickup address.

    Fields:
        - id (int): Unique identifier of the complementary order.
        - delivery_street (str): Street address for delivery.
        - delivery_neighborhood (str): Neighborhood for delivery.
        - delivery_number (str): Number of the delivery location.
        - delivery_city (str): City of the delivery address.
        - delivery_state (str): State of the delivery address.
        - delivery_country (str): Country of the delivery address.
        - full_delivery_address (str): Full formatted delivery address.
        - pickup_street (str): Street address for pickup.
        - pickup_neighborhood (str): Neighborhood for pickup.
        - pickup_number (str): Number of the pickup location.
        - pickup_city (str): City of the pickup address.
        - pickup_state (str): State of the pickup address.
        - pickup_country (str): Country of the pickup address.
        - full_pickup_address (str): Full formatted pickup address.

    All fields are read-only.
    """

    full_delivery_address = serializers.SerializerMethodField()
    full_pickup_address = serializers.SerializerMethodField()

    class Meta:
        model = ComplementaryOrder
        fields = [
            "id",
            "delivery_street",
            "delivery_neighborhood",
            "delivery_number",
            "delivery_city",
            "delivery_state",
            "delivery_country",
            "full_delivery_address",
            "pickup_street",
            "pickup_neighborhood",
            "pickup_number",
            "pickup_city",
            "pickup_state",
            "pickup_country",
            "full_pickup_address",
        ]
        read_only_fields = fields

    def get_full_delivery_address(self, obj):
        parts = [
            obj.delivery_street,
            obj.delivery_number,
        ]
        if obj.delivery_neighborhood:
            parts.append(obj.delivery_neighborhood)
        parts.extend([obj.delivery_city, obj.delivery_state, obj.delivery_country])
        return ", ".join(filter(None, parts))

    def get_full_pickup_address(self, obj):
        parts = [
            obj.pickup_street,
            obj.pickup_number,
        ]
        if obj.pickup_neighborhood:
            parts.append(obj.pickup_neighborhood)
        parts.extend([obj.pickup_city, obj.pickup_state, obj.pickup_country])
        return ", ".join(filter(None, parts))


class CreateComplementaryOrderSerializer(serializers.ModelSerializer):
    """
    Serializer for creating a new ComplementaryOrder.

    Handles the creation of delivery and pickup address details associated with an order.

    Fields:
        - delivery_street (str): Street address for delivery.
        - delivery_neighborhood (str): Neighborhood for delivery.
        - delivery_number (str): Number of the delivery location.
        - delivery_city (str): City of the delivery address.
        - delivery_state (str): State of the delivery address.
        - delivery_country (str): Country of the delivery address.
        - pickup_street (str): Street address for pickup.
        - pickup_neighborhood (str): Neighborhood for pickup.
        - pickup_number (str): Number of the pickup location.
        - pickup_city (str): City of the pickup address.
        - pickup_state (str): State of the pickup address.
        - pickup_country (str): Country of the pickup address.

    Notes:
        - The related "order" field is read-only and must be set through other means (e.g., view logic).
    """

    class Meta:
        model = ComplementaryOrder
        fields = [
            "delivery_street",
            "delivery_neighborhood",
            "delivery_number",
            "delivery_city",
            "delivery_state",
            "delivery_country",
            "pickup_street",
            "pickup_neighborhood",
            "pickup_number",
            "pickup_city",
            "pickup_state",
            "pickup_country",
        ]
        extra_kwargs = {"order": {"read_only": True}}

    def create(self, validated_data):
        return super().create(validated_data)


class UpdateComplementaryOrderSerializer(serializers.ModelSerializer):
    """
    Serializer for updating ComplementaryOrder details.

    Allows partial or full updates of delivery and pickup address fields
    associated with a ComplementaryOrder instance.

    Fields:
        - delivery_street (str): Street address for delivery.
        - delivery_neighborhood (str): Neighborhood for delivery.
        - delivery_number (str): Number of the delivery location.
        - delivery_city (str): City of the delivery address.
        - delivery_state (str): State of the delivery address.
        - delivery_country (str): Country of the delivery address.
        - pickup_street (str): Street address for pickup.
        - pickup_neighborhood (str): Neighborhood for pickup.
        - pickup_number (str): Number of the pickup location.
        - pickup_city (str): City of the pickup address.
        - pickup_state (str): State of the pickup address.
        - pickup_country (str): Country of the pickup address.

    Notes:
        - The related "order" field is read-only and cannot be modified through this serializer.
    """

    class Meta:
        model = ComplementaryOrder
        fields = [
            "delivery_street",
            "delivery_neighborhood",
            "delivery_number",
            "delivery_city",
            "delivery_state",
            "delivery_country",
            "pickup_street",
            "pickup_neighborhood",
            "pickup_number",
            "pickup_city",
            "pickup_state",
            "pickup_country",
        ]
        extra_kwargs = {"order": {"read_only": True}}

    def update(self, instance, validated_data):
        return super().update(instance, validated_data)


class OrderReadSerializer(serializers.ModelSerializer[Order]):
    """
    Read-only serializer for the Order model.

    Used to present detailed, non-editable information about an order, including
    customer and delivery person data, pricing, and timestamps.

    Computed fields:
        - user_full_name (str): Full name of the customer.
        - user_cpf (str): CPF (identifier) of the customer.
        - delivery_person_full_name (str): Full name of the delivery person.
        - full_delivery_address (str): Full formatted delivery address.
        - full_pickup_address (str): Full formatted pickup address.

    Fields:
        - id (int): Unique identifier of the order.
        - url (str): Link to the order detail endpoint.
        - establishment (str): Name of the establishment where the order was placed.
        - delivery_fee (decimal): Delivery fee amount.
        - order_value (decimal): Total order value.
        - order_status (str): Current status of the order.
        - closing_date (datetime): When the order was closed, if applicable.
        - app_origin (str): Source application of the order.
        - created_at (datetime): When the order was created.
        - updated_at (datetime): When the order was last updated.

    All fields are read-only.

    Additional configuration:
        - The "url" field uses the view name "api:order-detail" and looks up by primary key.
    """

    user_full_name = serializers.CharField(source="user.full_name", read_only=True)
    user_cpf = serializers.CharField(source="user.cpf", read_only=True)
    delivery_person_full_name = serializers.CharField(
        source="delivery_person.user.full_name", read_only=True
    )

    full_delivery_address = serializers.SerializerMethodField()
    full_pickup_address = serializers.SerializerMethodField()

    class Meta:
        model = Order
        fields = [
            "id",
            "url",
            "establishment",
            "user_full_name",
            "user_cpf",
            "delivery_person_full_name",
            "full_delivery_address",
            "full_pickup_address",
            "delivery_fee",
            "order_value",
            "order_status",
            "closing_date",
            "app_origin",
            "created_at",
            "updated_at",
        ]
        read_only_fields = fields
        extra_kwargs = {"url": {"view_name": "api:order-detail", "lookup_field": "pk"}}

    def get_full_delivery_address(self, obj: Order):
        if hasattr(obj, "complementary_order") and obj.complementary_order:
            comp_order = obj.complementary_order
            parts = [
                comp_order.delivery_street,
            ]
            if comp_order.delivery_number:
                parts.append(comp_order.delivery_number)
            if comp_order.delivery_neighborhood:
                parts.append(comp_order.delivery_neighborhood)
            parts.extend(
                [
                    comp_order.delivery_city,
                    comp_order.delivery_state,
                    comp_order.delivery_country,
                ]
            )
            return ", ".join(filter(None, parts))
        return None

    def get_full_pickup_address(self, obj: Order):
        if hasattr(obj, "complementary_order") and obj.complementary_order:
            comp_order = obj.complementary_order
            parts = [
                comp_order.pickup_street,
            ]
            if comp_order.pickup_number:
                parts.append(comp_order.pickup_number)
            if comp_order.pickup_neighborhood:
                parts.append(comp_order.pickup_neighborhood)
            parts.extend(
                [
                    comp_order.pickup_city,
                    comp_order.pickup_state,
                    comp_order.pickup_country,
                ]
            )
            return ", ".join(filter(None, parts))
        return None


class OrderCreatedSerializer(serializers.ModelSerializer[Order]):
    """
    Serializer for creating a new Order instance.

    Handles the creation of an order with its core attributes such as customer,
    delivery person, financial details, status, and origin.

    Fields:
        - establishment (str): Establishment where the order is placed.
        - user (User): Customer placing the order.
        - delivery_person (DeliveryPerson): Assigned delivery person for the order.
        - delivery_fee (decimal): Fee charged for delivery.
        - order_value (decimal): Total value of the order.
        - order_status (str): Current status of the order (e.g., Created, Pending, Delivered).
        - closing_date (datetime): Date and time when the order was closed, if applicable.
        - app_origin (str): Application or platform where the order originated.
    """

    class Meta:
        model = Order
        fields = [
            "establishment",
            "user",
            "delivery_person",
            "delivery_fee",
            "order_value",
            "order_status",
            "closing_date",
            "app_origin",
        ]

    def create(self, validated_data):
        return super().create(validated_data)


class OrderUpdateSerializer(serializers.ModelSerializer[Order]):
    """
    Serializer for updating Order instances.

    Allows modification of core order attributes such as the associated user, delivery person,
    financial values, and status.

    Fields:
        - establishment (str): Establishment where the order was placed.
        - user (User): Customer who placed the order.
        - delivery_person (DeliveryPerson): Assigned delivery person for the order.
        - delivery_fee (decimal): Fee charged for delivery.
        - order_value (decimal): Total monetary value of the order.
        - order_status (str): Current status of the order (e.g., Created, En Route, Delivered).
        - closing_date (datetime): Timestamp when the order was closed, if applicable.
        - app_origin (str): Source application from which the order originated.
    """

    class Meta:
        model = Order
        fields = [
            "establishment",
            "user",
            "delivery_person",
            "delivery_fee",
            "order_value",
            "order_status",
            "closing_date",
            "app_origin",
        ]

    def update(self, instance, validated_data):
        return super().update(instance, validated_data)


class OrderTrackingReadSerializer(serializers.ModelSerializer[OrderTracking]):
    """
    Read-only serializer for the OrderTracking model.

    Used to retrieve tracking data for an order, including geographic coordinates,
    timestamp, and a reference URL.

    Fields:
        - id (int): Unique identifier of the tracking record.
        - order (Order): Reference to the associated order.
        - start_latitude (float): Latitude where tracking started.
        - start_longitude (float): Longitude where tracking started.
        - end_latitude (float): Latitude where tracking ended.
        - end_longitude (float): Longitude where tracking ended.
        - timestamp (datetime): Date and time the tracking record was created.
        - url (str): Hyperlink to the detail view of this tracking record.

    Notes:
        - All fields are read-only.
        - The "url" field uses the view name "api:ordertracking-detail" and looks up by primary key.
    """

    class Meta:
        model = OrderTracking
        fields = [
            "id",
            "order",
            "start_latitude",
            "start_longitude",
            "end_latitude",
            "end_longitude",
            "timestamp",
            "url",
        ]
        read_only_fields = fields
        extra_kwargs = {
            "url": {"view_name": "api:ordertracking-detail", "lookup_field": "pk"}
        }


class OrderTrackingCreatedSerializer(serializers.ModelSerializer[OrderTracking]):
    """
    Serializer for creating a new OrderTracking instance.

    Captures the initial and final geographic coordinates associated with an order's tracking.

    Fields:
        - order (Order): Reference to the related order.
        - start_latitude (float): Latitude where tracking starts.
        - start_longitude (float): Longitude where tracking starts.
        - end_latitude (float): Latitude where tracking ends.
        - end_longitude (float): Longitude where tracking ends.
    """

    class Meta:
        model = OrderTracking
        fields = [
            "order",
            "start_latitude",
            "start_longitude",
            "end_latitude",
            "end_longitude",
        ]

        def create(self, validated_data):
            return super().create(validated_data)


class OrderTrackingUpdateSerializer(serializers.ModelSerializer[OrderTracking]):
    """
    Serializer for updating OrderTracking coordinates.

    Allows updating of start and end geographic coordinates related to a specific order.

    Fields:
        - order (Order): Reference to the related order.
        - start_latitude (float): Latitude where tracking started.
        - start_longitude (float): Longitude where tracking started.
        - end_latitude (float): Latitude where tracking ended.
        - end_longitude (float): Longitude where tracking ended.
    """

    class Meta:
        model = OrderTracking
        fields = [
            "order",
            "start_latitude",
            "start_longitude",
            "end_latitude",
            "end_longitude",
        ]

    def update(self, instance, validated_data):
        return super().update(instance, validated_data)
