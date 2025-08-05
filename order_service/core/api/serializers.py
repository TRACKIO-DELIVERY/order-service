from rest_framework import serializers

from order_service.core.models import Address
from order_service.core.models import ComplementaryOrder
from order_service.core.models import Establishment
from order_service.core.models import Order
from order_service.core.models import OrderTracking


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

    Notes:
        - The related "order" field is read-only and must be set through other means (e.g., view logic).
    """

    class Meta:
        model = ComplementaryOrder
        fields = [
            "order",
            "delivery_street",
            "delivery_neighborhood",
            "delivery_number",
            "delivery_city",
            "delivery_state",
            "delivery_country",
        ]

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

    delivery_person_full_name = serializers.CharField(source="delivery_person.user.name", read_only=True)
    establishment = serializers.CharField(source="establishment.name", read_only=True)

    full_delivery_address = serializers.SerializerMethodField()
    full_pickup_address = serializers.SerializerMethodField()

    class Meta:
        model = Order
        fields = [
            "id",
            "establishment",
            "email",
            "url",
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

    def get_full_delivery_address(self, obj: Order):
        if hasattr(obj, "complementary_order") and obj.complementary_order:
            comp_order = obj.complementary_order
            parts = [
                comp_order.delivery_street,
                comp_order.delivery_number,
            ]
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
        address = obj.establishment.address
        if not address:
            return None
        parts = [
            address.street,
            address.number,
        ]
        if address.neighborhood:
            parts.append(address.neighborhood)
        parts.extend([address.city, address.state, address.country])
        return ", ".join(filter(None, parts))


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
            "email",
            "url",
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
            "email",
            "url",
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
    """

    start_latitude = serializers.FloatField(read_only=True)
    start_longitude = serializers.FloatField(read_only=True)
    end_latitude = serializers.FloatField(read_only=True)
    end_longitude = serializers.FloatField(read_only=True)

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
        ]
        read_only_fields = fields


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


class CreateComplementaryOrderAlignedSerializer(serializers.ModelSerializer):
    """
    Serializer for the ComplementaryOrder model.
    Captures delivery and pickup address details.
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
        ]


class CreateOrderAlignedSerializer(serializers.ModelSerializer):
    """
    Serializer for creating an Order along with its ComplementaryOrder data.
    """

    complementary_order = CreateComplementaryOrderAlignedSerializer()

    class Meta:
        model = Order
        fields = [
            "establishment",
            "email",
            "delivery_person",
            "delivery_fee",
            "order_value",
            "order_status",
            "closing_date",
            "app_origin",
            "complementary_order",
        ]

    def create(self, validated_data):
        """
        Creates an Order instance along with its nested ComplementaryOrder data.
        """

        complementary_data = validated_data.pop("complementary_order")

        order = Order.objects.create(**validated_data)

        ComplementaryOrder.objects.create(order=order, **complementary_data)

        return order


class CreateDeliveryPersonAlignedSerializer(serializers.ModelSerializer):
    """
    Serializer for the ComplementaryOrder model.
    Captures delivery and pickup address details.
    """

    class Meta:
        model = DeliveryPerson
        fields = [ 
            "availability", 
            "vehicle", 
            "license_plate"
        ]
class CreateUserAlignedSerializer(serializers.ModelSerializer):
    delivery_person = CreateDeliveryPersonAlignedSerializer(source="deliveryperson")

    class Meta:
        model = User
        fields = [
            "full_name", 
            "email", 
            "birth_date", 
            "is_active", 
            "user_type", 
            "cpf",
            "delivery_person", 
        ]

    def create(self, validated_data):
        delivery_data = validated_data.pop("deliveryperson")
        user = User.objects.create(**validated_data)
        DeliveryPerson.objects.create(user=user, **delivery_data)
        return user


class ReadAddressSerializer(serializers.ModelSerializer[Address]):
    class Meta:
        model = Address
        fields = ["street", "neighborhood", "number", "city", "state", "country"]
        read_only_fields = fields


class CreatedAddressSerializer(serializers.ModelSerializer[Address]):
    neighborhood = serializers.CharField(required=False)
    number = serializers.CharField(required=False)

    class Meta:
        model = Address
        fields = ["street", "neighborhood", "number", "city", "state", "country"]


class UpdateAddressSerializer(serializers.ModelSerializer[Address]):
    class Meta:
        model = Address
        fields = ["street", "neighborhood", "number", "city", "state", "country"]


class ReadEstablishmentSerializer(serializers.ModelSerializer[Establishment]):
    address = ReadAddressSerializer()

    class Meta:
        model = Establishment
        fields = ["id", "name", "cnpj", "email", "active", "address"]
        read_only_fields = fields
        extra_kwargs = {
            "url": {"view_name": "api:establishment-detail", "lookup_field": "pk"},
        }


class CreatedEstablishmentSerializer(serializers.ModelSerializer):
    address = CreatedAddressSerializer()
    active = serializers.HiddenField(default=True)
    administrator = serializers.HiddenField(default=serializers.CurrentUserDefault())

    class Meta:
        model = Establishment
        fields = ["name", "cnpj", "email", "active", "address", "administrator", "phone"]

    def create(self, validated_data):
        address_data = validated_data.pop("address")

        address_serializer = CreatedAddressSerializer(data=address_data)
        address_serializer.is_valid(raise_exception=True)
        address = address_serializer.save()

        return Establishment.objects.create(address=address, **validated_data)


class UpdateEstablishmentSerializer(serializers.ModelSerializer[Establishment]):
    class Meta:
        model = Establishment
        fields = ["name", "email", "active", "address"]
        read_only_fields = ["cnpj"]

    def update(self, instance, validated_data):
        return super().update(instance, validated_data)
