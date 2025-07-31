from django.db.models import QuerySet


class UserQuerySet(QuerySet):
    def active(self):
        return self.filter(is_active=True)

    def inactive(self):
        return self.filter(is_active=False)

    def customers(self):
        return self.filter(user_type__description="Customer")

    def administrator(self):
        return self.filter(user_type__description="Administrator")

    def delivery_man(self):
        return self.filter(user_type__description="Delivery Man")

    def order_by_name(self):
        return self.order_by("name")


class DeliveryPersonQuerySet(QuerySet):
    pass
