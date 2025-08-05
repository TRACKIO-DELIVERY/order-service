from rest_framework import permissions
from rest_framework import status
from rest_framework.response import Response
from rest_framework.views import APIView

from .serializers import GoogleSocialAuthSerializer


class GoogleAuthView(APIView):
    permission_classes = [permissions.AllowAny]
    serializer_class = GoogleSocialAuthSerializer

    """
    View to handle Google social authentication.
    This view uses the GoogleSocialAuthSerializer to validate and process
    the authentication data.
    """

    def post(self, request, *args, **kwargs):
        """
        Initiates Google authentication process.
        """
        serializer = self.serializer_class(data=request.data)
        if serializer.is_valid():
            return Response(serializer.data, status=status.HTTP_200_OK)
        return Response({"error": "Authentication Failed"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
