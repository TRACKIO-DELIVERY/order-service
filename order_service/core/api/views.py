from rest_framework import viewsets
from core.models import User,Order,OrderTracking,DeliveryPerson
from .serializers import UserReadSerializer,UserCreatedSerializer,UserUpdateSerializer,OrderTrackingSerializer,DeliveryPersonReadSerializer,OrderReadSerializer
from rest_framework.permissions import AllowAny

class UserViewSet(viewsets.ModelViewSet):

    """
    ViewSet for managing User instances.

    This viewset handles all standard CRUD operations (create, retrieve, update, delete)
    for the User model. It dynamically selects the appropriate serializer class
    based on the type of action being performed.

    Serializer classes:
        - UserCreatedSerializer: Used for creating new users.
        - UserUpdateSerializer: Used for updating user data (PUT/PATCH).
        - UserReadSerializer: Default serializer used for read operations (GET).

    Filtering options (via query parameters):
        - is_active (bool): Filters users based on their active status.
            Example: ?is_active=true
        - user_type (str): Filters users by their user type.
            Example: ?user_type=Customer

    Example usage:
        - GET /api/users/ → List users.
        - POST /api/users/ → Create a new user.
        - GET /api/users/{id}/ → Retrieve a specific user.
        - PUT /api/users/{id}/ → Update an existing user.
        - DELETE /api/users/{id}/ → Delete a user.
    """


    serializer_class = UserReadSerializer 
    queryset = User.objects.all()

    def get_serializer_class(self):
        if self.action == 'create':
            return UserCreatedSerializer
        if self.action in ['update', 'partial_update']:
            return UserUpdateSerializer
        return UserReadSerializer

 
    def get_queryset(self):
        queryset = super().get_queryset()

        is_active = self.request.query_params.get('is_active', None)
        if is_active is not None: 
            is_active_bool = is_active.lower() == 'true'
            queryset = queryset.filter(is_active=is_active_bool)

        user_type = self.request.query_params.get("user_type", None)
        if user_type:
            queryset = queryset.filter(user_type=user_type)
            
        return queryset


class DeliveryPersonViewSet(viewsets.ModelViewSet):
    queryset = DeliveryPerson.objects.all()
    serializer_class = DeliveryPersonReadSerializer

class OrderViewSet(viewsets.ModelViewSet):
    queryset = Order.objects.all()
    serializer_class = OrderReadSerializer

class OrderTrackingViewSet(viewsets.ModelViewSet):
    queryset = OrderTracking.objects.all()
    serializer_class = OrderTrackingSerializer
    