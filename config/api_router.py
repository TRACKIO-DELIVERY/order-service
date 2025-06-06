from django.conf import settings
from rest_framework.routers import DefaultRouter
from rest_framework.routers import SimpleRouter

from order_service.core.api.views import ComplementaryOrderViewSet

# from order_service.users.api.views import UserViewSet
from order_service.core.api.views import DeliveryPersonViewSet
from order_service.core.api.views import OrderTrackingViewSet
from order_service.core.api.views import OrderViewSet
from order_service.core.api.views import UserViewSet
from order_service.core.api.views import OrderAlignedViewSet

router = DefaultRouter() if settings.DEBUG else SimpleRouter()

# router.register("users", UserViewSet)
router.register(r"users", UserViewSet)
router.register(r"delivery-people", DeliveryPersonViewSet)
router.register(r"orders", OrderViewSet)
router.register(r"tracking", OrderTrackingViewSet)
router.register(r"complementary-order", ComplementaryOrderViewSet)
router.register(r"aligned-order", OrderAlignedViewSet, basename="aligned-order")


app_name = "api"
urlpatterns = router.urls
