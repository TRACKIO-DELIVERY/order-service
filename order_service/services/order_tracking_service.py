import logging

from django.utils import timezone

from order_service.core.models import ComplementaryOrder
from order_service.core.models import OrderTracking
from order_service.services.geocode_service import GeoocodeService


def create_tracking_for_order(order):
    try:
        comp_order = ComplementaryOrder.objects.get(order=order)

        address = order.establishment.address

        pickup_address = (
            f"{address.street}, {address.number}, "
            f"{address.neighborhood}, {address.city}, "
            f"{address.state}, {address.country}"
        )

        delivery_address = (
            f"{comp_order.delivery_street}, {comp_order.delivery_number}, "
            f"{comp_order.delivery_neighborhood}, {comp_order.delivery_city}, "
            f"{comp_order.delivery_state}, {comp_order.delivery_country}"
        )

        pickup_coords = GeoocodeService.address_to_coordinates(pickup_address)
        delivery_coords = GeoocodeService.address_to_coordinates(delivery_address)
        logging.info(
            f"Coordenadas obtidas para Order#{order.id}: "
            f"Pickup({pickup_coords['lat']}, {pickup_coords['lng']}), "
            f"Delivery({delivery_coords['lat']}, {delivery_coords['lng']})"
        )
        return OrderTracking.objects.create(
            order=order,
            start_latitude=str(pickup_coords["lat"]),
            start_longitude=str(pickup_coords["lng"]),
            end_latitude=str(delivery_coords["lat"]),
            end_longitude=str(delivery_coords["lng"]),
            timestamp=timezone.now(),
        )

    except ValueError as e:
        logging.exception(
            f"Erro ao criar as coordenadas para o Order#{order.id}: {e!s}. Certifique que o endereço está correto."
        )

    except ComplementaryOrder.DoesNotExist:
        logging.exception(
            f"Complementary order não encontrado para Order#{order.id}. "
            "Certifique que Order tem um Complementary Order."
        )
