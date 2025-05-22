from rest_framework import serializers
from core.models import (
    User,
    Order,
    DeliveryPerson,
    OrderTracking,
    ComplementaryOrder,
    OrderStatus
)


class UserSerializer(serializers.ModelSerializer[User]):
    class Meta:
        model = User
        fields = ["full_name", "cpf", "email", "user_type", "url"]
        read_only_fields = ["user_type"] # user_type pode ser definido na criação, mas não deve ser alterado via API por um usuário comum

        extra_kwargs = {
            # Assumindo que você terá um lookup_field por 'pk' para o User
            # ou você pode adicionar um campo único no modelo User, tipo 'username'
            "url": {"view_name": "api:user-detail", "lookup_field": "pk"},
        }


class DeliveryPersonSerializer(serializers.ModelSerializer[DeliveryPerson]):
    # Inclui o serializador de User aninhado para ver os detalhes do usuário associado
    user = UserSerializer(read_only=True)
    # ou se preferir apenas o ID do usuário: user_id = serializers.PrimaryKeyRelatedField(source='user', read_only=True)

    class Meta:
        model = DeliveryPerson
        fields = [
            "id",
            "user",
            "availability",
            "vehicle",
            "license_plate",
            "created_at",
            "updated_at",
            "url",
        ]
        read_only_fields = ["created_at", "updated_at"]

        extra_kwargs = {
            "url": {"view_name": "api:deliveryperson-detail", "lookup_field": "pk"},
        }


class OrderSerializer(serializers.ModelSerializer[Order]):
    # Para mostrar o status por descrição em vez de apenas o ID
    status_description = serializers.CharField(
        source="status.description", read_only=True
    )
    # Para mostrar o nome completo do usuário associado ao pedido
    user_full_name = serializers.CharField(source="user.full_name", read_only=True)
    # Para mostrar o nome do entregador associado ao pedido
    delivery_person_full_name = serializers.CharField(
        source="delivery_person.user.full_name", read_only=True
    )

    class Meta:
        model = Order
        fields = [
            "id",
            "restaurant_id",
            "user",  # ID do usuário
            "user_full_name",  # Nome do usuário (read-only)
            "delivery_person",  # ID do entregador
            "delivery_person_full_name",  # Nome do entregador (read-only)
            "delivery_fee",
            "order_value",
            "status",  # ID do status
            "status_description",  # Descrição do status (read-only)
            "closing_date",
            "app_origin",
            "created_at",
            "updated_at",
            "url",
        ]
        read_only_fields = ["created_at", "updated_at", "status_description", "user_full_name", "delivery_person_full_name"]
        extra_kwargs = {
            "url": {"view_name": "api:order-detail", "lookup_field": "pk"},
            "user": {"view_name": "api:user-detail", "lookup_field": "pk"},
            "delivery_person": {"view_name": "api:deliveryperson-detail", "lookup_field": "pk"},
            "status": {"view_name": "api:orderstatus-detail", "lookup_field": "pk"},
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
