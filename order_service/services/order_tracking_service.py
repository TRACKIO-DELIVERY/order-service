from django.utils import timezone

from order_service.core.models import ComplementaryOrder,Establishment,Address
from order_service.core.models import OrderTracking
from order_service.services.geocode_service import GeoocodeService


def create_tracking_for_order(order):
    try:
        print(order)
        comp_order = ComplementaryOrder.objects.get(order=order)

        address = order.establishment.address

        print(address)
        print(comp_order)

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

        return OrderTracking.objects.create(
            order=order,
            start_latitude=str(pickup_coords["lat"]),
            start_longitude=str(pickup_coords["lng"]),
            end_latitude=str(delivery_coords["lat"]),
            end_longitude=str(delivery_coords["lng"]),
            timestamp=timezone.now(),
        )

    except ComplementaryOrder.DoesNotExist:
        raise Exception("Complementary order not found.")  # noqa: B904
