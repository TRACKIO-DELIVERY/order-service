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
class UserType(models.Model):
    Administrator = 'Administrator'
    Customer = 'Customer'
    Delivery_Man = 'Delivery Man'

    User_Type_Choices = [
        (Administrator, 'Administrador'),
        (Customer, 'Cliente'),
        (Delivery_Man, 'Entregador'),
    ]

    description = models.CharField(
        max_length=50,
        choices=User_Type_Choices
    )

    def __str__(self):
        return self.get_description_display()


class User(TimeStampedModel):
    full_name = models.CharField(max_length=250)
    cpf = models.CharField(max_length=11)
    tax_id = models.CharField(max_length=14, unique=True) 
    birth_date = models.DateField()
    email = models.EmailField(max_length=250)
    password = models.CharField(max_length=50)
    user_type = models.ForeignKey(UserType, on_delete=models.PROTECT)
    is_active = models.BooleanField(default=True)

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

class OrderStatus(models.Model):
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

    description = models.CharField(
        max_length=50,
        choices=Order_Status_Choices
    )

    def __str__(self):
        return self.description

class Order(TimeStampedModel):
    restaurant_id = models.IntegerField()
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    delivery_person = models.ForeignKey(DeliveryPerson, on_delete=models.SET_NULL, null=True)
    delivery_fee = models.DecimalField(max_digits=10, decimal_places=2)
    order_value = models.DecimalField(max_digits=10, decimal_places=2)
    status = models.ForeignKey(OrderStatus, on_delete=models.PROTECT)
    closing_date = models.DateTimeField(null=True, blank=True)
    app_origin = models.CharField(max_length=100)

    objects = querysets.OrderQuerySet.as_manager()

    def __str__(self):
        return f"Order #{self.id}"
    
class ComplementaryOrder(models.Model):
    order = models.ForeignKey(Order, on_delete=models.CASCADE)
    delivery_street = models.CharField(max_length=100)
    delivery_neighborhood = models.CharField(max_length=100)
    delivery_number = models.CharField(max_length=10)
    delivery_zip_code = models.CharField(max_length=20)
    pickup_street = models.CharField(max_length=100)
    pickup_neighborhood = models.CharField(max_length=100)
    pickup_number = models.CharField(max_length=10)
    pickup_zip_code = models.CharField(max_length=10)


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
