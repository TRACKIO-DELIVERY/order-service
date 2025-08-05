# Create your models here.
from django.contrib.auth import get_user_model
from django.db import models
from django.utils import timezone

from order_service.core.querysets import EstablishmentQueryset
from order_service.core.querysets import OrderQuerySet
from order_service.users.models import DeliveryPerson

User = get_user_model()


# Models Abstract
class CreatedAtModel(models.Model):
    created_at = models.DateTimeField(auto_now_add=True, editable=False)
    created_user = models.CharField(max_length=100, default="Admin")

    class Meta:
        abstract = True


class TimeStampedModel(CreatedAtModel):
    updated_at = models.DateTimeField(auto_now=True)
    updated_user = models.CharField(max_length=100, default="Admin")

    class Meta:
        abstract = True


class Address(models.Model):
    street = models.CharField(max_length=100)
    neighborhood = models.CharField(max_length=100)
    number = models.CharField(max_length=10, blank=True)
    city = models.CharField(max_length=100)
    state = models.CharField(max_length=50)
    country = models.CharField(max_length=50)

    def __str__(self):
        return f"{self.street}, {self.number}, {self.neighborhood}, {self.city}, {self.state}, {self.country}"


class Establishment(TimeStampedModel):
    name = models.CharField(max_length=100)
    cnpj = models.CharField(max_length=100)
    email = models.EmailField()
    active = models.BooleanField(default=True)
    phone = models.CharField(max_length=15, blank=True)
    address = models.ForeignKey(Address, on_delete=models.CASCADE)
    administrator = models.ForeignKey(User, on_delete=models.CASCADE, related_name="establishments")

    objects: EstablishmentQueryset = EstablishmentQueryset.as_manager()

    def __str__(self):
        return f"{self.name}, {self.cnpj}"


class OrderStatus(models.IntegerChoices):
    WAITING_COLLECTION = 1, "Aguardando Coleta"
    EN_ROUTE = 2, "Em Rota"
    DELIVERED = 3, "Entregue"
    CANCELLED = 4, "Cancelado"
    IN_PRODUCTION = 5, "Em Produção"


class Order(TimeStampedModel):
    establishment = models.ForeignKey(Establishment, on_delete=models.CASCADE)
    email = models.CharField(max_length=100)
    delivery_person = models.ForeignKey(DeliveryPerson, on_delete=models.SET_NULL, null=True)
    url = models.CharField(max_length=100,blank=True)
    delivery_fee = models.DecimalField(max_digits=10, decimal_places=2)
    order_value = models.DecimalField(max_digits=10, decimal_places=2)
    closing_date = models.DateTimeField(null=True, blank=True)
    app_origin = models.CharField(max_length=100)

    order_status = models.IntegerField(choices=OrderStatus)

    objects = OrderQuerySet.as_manager()

    def __str__(self):
        return f"Order #{self.id}"


class ComplementaryOrder(models.Model):
    order = models.OneToOneField(Order, on_delete=models.CASCADE, related_name="complementary_order")
    delivery_street = models.CharField(max_length=100)
    delivery_neighborhood = models.CharField(max_length=100, blank=True)
    delivery_number = models.CharField(max_length=10)
    delivery_city = models.CharField(max_length=100)
    delivery_state = models.CharField(max_length=100)
    delivery_country = models.CharField(max_length=100)

    def __str__(self):
        return f"Complementary Order for {self.order.id}"


class OrderTracking(models.Model):
    order = models.OneToOneField(Order, on_delete=models.CASCADE, related_name="tracking_order")
    start_latitude = models.CharField(max_length=50)
    start_longitude = models.CharField(max_length=50)
    end_latitude = models.CharField(max_length=50)
    end_longitude = models.CharField(max_length=50)
    timestamp = models.DateTimeField(default=timezone.now)

    def __str__(self):
        return f"Tracking {self.order.id}"


class UserNotification(TimeStampedModel):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    message = models.TextField()
    delivery_channel = models.CharField(max_length=50)
    sent_at = models.DateTimeField(default=timezone.now)
    is_read = models.BooleanField(default=False)

    def __str__(self):
        return f"Notification for {self.user.full_name}"


class UserLog(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    action = models.CharField(max_length=50)
    msg = models.JSONField(null=True, blank=True)
    timestamp = models.DateTimeField(default=timezone.now)

    def __str__(self):
        return f"Action: {self.action}"


class AccessLog(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    action = models.CharField(max_length=50)
    origin_ip = models.CharField(max_length=45)
    timestamp = models.DateTimeField(default=timezone.now)

    def __str__(self):
        return f"Action: {self.action}"


class OrderLog(models.Model):
    order = models.ForeignKey(Order, on_delete=models.CASCADE)
    action = models.CharField(10)
    msg = models.JSONField(null=True, blank=True)
    timestamp = models.DateTimeField(default=timezone.now)

    def __str__(self):
        return f"Action: {self.action}"
