import logging

from drf_spectacular.utils import OpenApiResponse
from drf_spectacular.utils import extend_schema
from rest_framework import permissions
from rest_framework import status
from rest_framework.response import Response
from rest_framework.serializers import ValidationError
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

    @extend_schema(
        operation_id="Google Social Authentication",
        request=GoogleSocialAuthSerializer,
        responses={
            200: OpenApiResponse(response={"refresh": "str", "access": "str"}),
            500: OpenApiResponse(description="Internal Server Error"),
        },
        tags=["Social Authentication"],
    )
    def post(self, request, *args, **kwargs):
        """
        Initiates Google authentication process.
        Expects the request data to contain the device type that generated the Google ID token.
        Returns a response with the validated data or an error message.
        """
        serializer = self.serializer_class(data=request.data)
        try:
            serializer.is_valid(raise_exception=True)
            return Response(serializer.data, status=status.HTTP_200_OK)
        except ValidationError as exc:
            logging.exception(f"Google Authentication failed. Message: {exc}")
            return Response({"error": "Authentication Failed"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
        except Exception as exc:
            logging.exception(f"An unexpected error occurred during Google authentication. Message: {exc}")
            return Response({"error": "An unexpected error occurred"}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
