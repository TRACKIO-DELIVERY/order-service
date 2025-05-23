from django.contrib import admin
from .models import User,Order,OrderTracking,DeliveryPerson,ComplementaryOrder
# Register your models here.
admin.site.register(Order)
admin.site.register(User)
admin.site.register(OrderTracking)
admin.site.register(DeliveryPerson)
admin.site.register(ComplementaryOrder)