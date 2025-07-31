from django.conf import settings
from rest_framework.routers import DefaultRouter
from rest_framework.routers import SimpleRouter

from order_service.core.api.views import ComplementaryOrderViewSet
from order_service.core.api.views import EstablishmentViewSet
from order_service.core.api.views import OrderAlignedViewSet
from order_service.core.api.views import OrderTrackingViewSet
from order_service.core.api.views import OrderViewSet
from order_service.users.api.views import DeliveryPersonViewSet
from order_service.users.api.views import UserViewSet

router = DefaultRouter() if settings.DEBUG else SimpleRouter()

router.register(r"users", UserViewSet)
router.register(r"delivery-people", DeliveryPersonViewSet)
router.register(r"orders", OrderViewSet)
router.register(r"tracking", OrderTrackingViewSet)
router.register(r"complementary-order", ComplementaryOrderViewSet)
router.register(r"aligned-order", OrderAlignedViewSet, basename="aligned-order")
router.register(r"establishment", EstablishmentViewSet)


app_name = "api"
urlpatterns = router.urls
