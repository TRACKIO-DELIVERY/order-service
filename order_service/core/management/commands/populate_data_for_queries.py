from django.core.management.base import BaseCommand

from order_service.core.models import Address
from order_service.core.models import Establishment
from order_service.core.models import Order
from order_service.core.models import OrderStatus
from order_service.core.models import User

status_default = OrderStatus.WAITING_COLLECTION


class Command(BaseCommand):
    help = "Populate Address, User, Establishment, and Order models with sample data using bulk_create"

    def handle(self, *args, **kwargs):
        self.populate_data()
        self.stdout.write(self.style.SUCCESS("Successfully populated data for queries using bulk_create"))

    def populate_data(self):
        address, _ = Address.objects.get_or_create(
            street="123 Main St", city="Anytown", state="CA", number="12345", country="Country", neighborhood="Downtown"
        )

        user, _ = User.objects.get_or_create(email="dummy@dummy.com", username="dummy_user")
        user.set_password("dummy_password")
        user.save()

        establishments = [
            Establishment(
                name="Establishment dummy", cnpj="900", email="estab@gmail.com", address=address, administrator=user
            )
            for _ in range(500_000)
        ]
        Establishment.objects.bulk_create(establishments)

        orders = [
            Order(
                establishment_id=3,
                email="client@gmail.com",
                url="https://google.com",
                delivery_fee=22.2,
                order_value=90.0,
                app_origin="mobile",
                order_status=status_default,
            )
            for _ in range(500_000)
        ]

        Order.objects.bulk_create(orders)
