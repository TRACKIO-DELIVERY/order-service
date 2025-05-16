import contextlib

from django.apps import AppConfig
from django.utils.translation import gettext_lazy as _


class UsersConfig(AppConfig):
    name = "order_service.users"
    verbose_name = _("Users")

    def ready(self):
        with contextlib.suppress(ImportError):
            import order_service.users.signals  # noqa: F401
