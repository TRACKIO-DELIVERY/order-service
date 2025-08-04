from rest_framework import status
from rest_framework.response import Response
from rest_framework.views import APIView

from .serializers import GoogleSocialAuthSerializer


class GoogleAuthView(APIView):
    serializer_class = GoogleSocialAuthSerializer

    def get(self, request, *args, **kwargs):
        """
        Initiates Google authentication process.
        """
        serializer = self.serializer_class(data=request.data)
        if serializer.is_valid():
            return Response(serializer.data, status=status.HTTP_200_OK)
        return Response({"error": "Authentication Failed"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
