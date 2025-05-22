from rest_framework import viewsets
from core.models import User,Order,OrderTracking,DeliveryPerson
from .serializers import UserSerializer,OrderSerializer,OrderTrackingSerializer,DeliveryPersonSerializer
from rest_framework.permissions import AllowAny



class UserViewSet(viewsets.ModelViewSet):
    permission_classes = [AllowAny]  # Permite acesso mesmo para usuário anônimo
    serializer_class = UserSerializer
    queryset = User.objects.none()

    def get_queryset(self):
        user = self.request.user
        if user.is_authenticated:
            # Usuário autenticado vê todos os usuários
            return User.objects.all()
        else:
            # Usuário anônimo não vê ninguém (queryset vazio)
            return User.objects.all()

class DeliveryPersonViewSet(viewsets.ModelViewSet):
    queryset = DeliveryPerson.objects.all()
    serializer_class = DeliveryPersonSerializer

class OrderViewSet(viewsets.ModelViewSet):
    queryset = Order.objects.all()
    serializer_class = OrderSerializer

class OrderTrackingViewSet(viewsets.ModelViewSet):
    queryset = OrderTracking.objects.all()
    serializer_class = OrderTrackingSerializer
    