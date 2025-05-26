# Create your models here.
from django.db import models
from django.utils import timezone
from . import querysets

# Models Abstract
class CreatedAtModel(models.Model):
    created_at = models.DateTimeField(auto_now_add=True,editable=False)

    class Meta:
        abstract = True

class TimeStampedModel(CreatedAtModel):
    updated_at = models.DateTimeField(auto_now=True)
    
    class Meta:
        abstract = True


# Models Main

class User(TimeStampedModel):
    full_name = models.CharField(max_length=250)
    cpf = models.CharField(max_length=11, unique=True)
    birth_date = models.DateField()
    email = models.EmailField(max_length=250)
    password = models.CharField(max_length=50)
    is_active = models.BooleanField(default=True)

    ADMINISTRATOR = 'Administrator'
    CUSTOMER = 'Customer'
    DELIVERY_MAN = 'Delivery Man'

    USER_TYPE_CHOICES = [
        (ADMINISTRATOR, 'Administrador'),
        (CUSTOMER, 'Cliente'),
        (DELIVERY_MAN, 'Entregador'),
    ]

    user_type = models.CharField(
        max_length=50,
        choices=USER_TYPE_CHOICES
    )

    objects = querysets.UserQuerySet.as_manager()

    def __str__(self):
        return self.full_name

class DeliveryPerson(TimeStampedModel):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    availability = models.CharField(max_length=50)
    vehicle = models.CharField(max_length=50)
    license_plate = models.CharField(max_length=50)

    def __str__(self):
        return f"Delivery Person {self.user.full_name}"

class Order(TimeStampedModel):
    establishment = models.CharField(max_length=100)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    delivery_person = models.ForeignKey(DeliveryPerson, on_delete=models.SET_NULL, null=True)
    delivery_fee = models.DecimalField(max_digits=10, decimal_places=2)
    order_value = models.DecimalField(max_digits=10, decimal_places=2)
    closing_date = models.DateTimeField(null=True, blank=True)
    app_origin = models.CharField(max_length=100)

    Waiting_Collection = 'Waiting for Collection'
    En_Route = 'En Route'
    Delivered = 'Delivered'
    Cancelled = 'Cancelled'
    In_Production = 'In Production'

    Order_Status_Choices = [
        (Waiting_Collection, 'Aguardando Coleta'),
        (En_Route, 'Em Rota'),
        (Delivered, 'Entregue'),
        (Cancelled,'Cancelado'),
        (In_Production,'Em Produção')
    ]

    order_status = models.CharField(
        max_length=50,
        choices=Order_Status_Choices
    )


    objects = querysets.OrderQuerySet.as_manager()

    def __str__(self):
        return f"Order #{self.id}"
    
class ComplementaryOrder(models.Model):
    order = models.ForeignKey(Order, on_delete=models.CASCADE)
    delivery_street = models.CharField(max_length=100)
    delivery_neighborhood = models.CharField(max_length=100,blank=True)
    delivery_number = models.CharField(max_length=10)
    delivery_city = models.CharField(max_length=100)
    delivery_state = models.CharField(max_length=100)
    delivery_country = models.CharField(max_length=100)
    full_delivery_address = models.CharField(max_length=400,editable=False)
    pickup_street = models.CharField(max_length=100)
    pickup_neighborhood = models.CharField(max_length=100,blank=True)
    pickup_number = models.CharField(max_length=10)
    pickup_city = models.CharField(max_length=100)
    pickup_state = models.CharField(max_length=100)
    pickup_country = models.CharField(max_length=100)
    full_pickup_address = models.CharField(max_length=400,editable=False)

    def save_delivery(self, *args, **kwargs):
        # Monta o endereço completo
        parts = [
            self.delivery_street,
            self.delivery_number,
        ]
        if self.delivery_neighborhood:
            parts.append(self.delivery_neighborhood)
        parts += [
            self.delivery_city,
            self.delivery_state,
            self.delivery_country
        ]
        self.full_delivery_address = ', '.join(parts)
        super().save(*args, **kwargs)

    def save_pickup(self, *args, **kwargs):
        # Monta o endereço completo
        parts = [
            self.pickup_street,
            self.pickup_number,
        ]
        if self.pickup_neighborhood:
            parts.append(self.pickup_neighborhood)
        parts += [
            self.pickup_city,
            self.pickup_state,
            self.pickup_country
        ]
        self.full_pickup_address = ', '.join(parts)
        super().save(*args, **kwargs)
    


class OrderTracking(models.Model):
    order = models.ForeignKey(Order, on_delete=models.CASCADE)
    start_latitude = models.CharField(max_length=50)
    start_longitude = models.CharField(max_length=50)
    end_latitude = models.CharField(max_length=50)
    end_longitude = models.CharField(max_length=50)
    event_status = models.CharField(max_length=50)
    timestamp = models.DateTimeField(default=timezone.now)

    def __str__(self):
        return f"Tracking {self.order.id} - {self.event_status}"

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
    action = models.CharField(max_length=50)
    timestamp = models.DateTimeField(default=timezone.now)

    def __str__(self):
        return f"Action: {self.action}"
