from order_service.core.models import OrderTracking, ComplementaryOrder
from .geocode_service import GeoocodeService
from django.utils import timezone

def create_tracking_for_order(order):
    try:
        comp_order = ComplementaryOrder.objects.get(order=order)
        
        pickup_address = f"{comp_order.pickup_street}, {comp_order.pickup_number}, {comp_order.pickup_neighborhood}, {comp_order.pickup_zip_code}"
        delivery_address = f"{comp_order.delivery_street}, {comp_order.delivery_number}, {comp_order.delivery_neighborhood}, {comp_order.delivery_zip_code}"
        
        start_lat, start_lng = GeoocodeService.address_to_coordinates(pickup_address)
        end_lat, end_lng = GeoocodeService.address_to_coordinates(delivery_address)
        
        tracking = OrderTracking.objects.create(
            order=order,
            start_latitude=str(start_lat),
            start_longitude=str(start_lng),
            end_latitude=str(end_lat),
            end_longitude=str(end_lng),
            timestamp=timezone.now()
        )

        return tracking

    except ComplementaryOrder.DoesNotExist:
        raise Exception("Complementary order not found.")
