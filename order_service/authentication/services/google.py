import logging

from google.auth.exceptions import GoogleAuthError
from google.auth.transport import requests as google_request
from google.oauth2 import id_token

from config.settings.base import env
from order_service.authentication.exceptions import SocialAuthException
from order_service.authentication.exceptions import TokenInvalidOrExpired

CLIENT_ID_PER_DEVICE = {
    "mobile": env("GOOGLE_CLIENT_ID_MOBILE"),
    "web": env("GOOGLE_CLIENT_ID_WEB"),
}


class Google:
    """
    Google class provides a method to validate the Google OAuth2 token
    and fetch user information.
    """

    @staticmethod
    def _validate_device_type(device_type):
        """
        Validates the device type and raises a ValueError if invalid.
        """
        if not device_type or device_type not in CLIENT_ID_PER_DEVICE:
            raise ValueError("Valid device type must be specified")

    @staticmethod
    def validate(auth_token, device_type=None):
        """
        Validates the Google OAuth2 token and returns user information.
        :param auth_token: The OAuth2 token received from Google.
        :param device_type: The type of device (android, ios, or site).
        """
        try:
            # Validate the device type
            Google._validate_device_type(device_type)

            # Fetch the client ID for the device type
            client_id = CLIENT_ID_PER_DEVICE[device_type]

            # Verify the OAuth2 token
            return id_token.verify_oauth2_token(auth_token, google_request.Request(), client_id, 200)

        except ValueError as exc:
            logging.exception("An error occurred while validating the Google token: %s", exc)
            raise TokenInvalidOrExpired("The token is either invalid or has expired") from exc
        except GoogleAuthError as exc:
            logging.warning("Issuer is invalid for Google token")
            raise SocialAuthException("Issuer is invalid") from exc
