from rest_framework.exceptions import AuthenticationFailed


class SocialAuthException(AuthenticationFailed):
    """
    Custom exception for handling social authentication errors.
    Inherits from AuthenticationFailed to provide a consistent error response.
    """

    status_code = 401
    default_detail = "Social authentication failed."
    default_code = "social_auth_failed"

    def __init__(self, detail=None, code=None):
        if detail is not None:
            self.detail = detail
        if code is not None:
            self.code = code
        super().__init__(detail, code)


class TokenInvalidOrExpired(SocialAuthException):
    """
    Exception raised when a token is invalid or expired.
    Inherits from AuthenticationFailed to provide a consistent error response.
    """

    status_code = 401
    default_detail = "Token is invalid or has expired."
    default_code = "token_invalid_or_expired"

    def __init__(self, detail=None, code=None):
        if detail is not None:
            self.detail = detail
        if code is not None:
            self.code = code
        super().__init__(detail, code)
