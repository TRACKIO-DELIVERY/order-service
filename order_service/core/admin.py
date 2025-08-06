from django.contrib import admin

from .models import ComplementaryOrder
from .models import DeliveryPerson
from .models import Order
from .models import OrderLog
from .models import OrderTracking
from .models import UserLog

# Register your models here.
admin.site.register(Order)
admin.site.register(OrderTracking)
admin.site.register(DeliveryPerson)
admin.site.register(ComplementaryOrder)
admin.site.register(OrderLog)
admin.site.register(UserLog)
