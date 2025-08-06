import logging

from rest_framework import serializers
from rest_framework_simplejwt.tokens import RefreshToken

from order_service.authentication.exceptions import SocialAuthException
from order_service.authentication.exceptions import TokenInvalidOrExpired
from order_service.authentication.services.google import CLIENT_ID_PER_DEVICE
from order_service.authentication.services.google import Google


class GoogleSocialAuthSerializer(serializers.Serializer):
    auth_token = serializers.CharField(write_only=True)
    device_type = serializers.CharField(write_only=True, default="mobile")

    def validate(self, attrs) -> dict | None:
        from order_service.users.models import User

        attrs = super().validate(attrs)
        try:
            auth_token = attrs.get("auth_token")
            device_type = attrs.get("device_type")

            user_data = Google.validate(auth_token, device_type=device_type)

            if device_type == "android":
                if user_data["aud"] != CLIENT_ID_PER_DEVICE.get("mobile"):
                    raise SocialAuthException("Invalid Google client ID for Android device")

            user = {
                "name": user_data["name"],
                "email": user_data["email"],
                "uid": user_data["sub"],
                "provider": "google",
            }

            user = User.objects.authenticate_social(**user)

            refresh = RefreshToken.for_user(user)

            return {
                "refresh": str(refresh),
                "access": str(refresh.access_token),
            }

        except (SocialAuthException, TokenInvalidOrExpired) as exc:
            logging.exception("Google social login failed")
            raise serializers.ValidationError("Invalid authentication token") from exc
