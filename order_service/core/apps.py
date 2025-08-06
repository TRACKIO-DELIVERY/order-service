import contextlib

from django.apps import AppConfig


class CoreConfig(AppConfig):
    default_auto_field = "django.db.models.BigAutoField"
    name = "order_service.core"

    def ready(self):
        with contextlib.suppress(ImportError):
            import order_service.core.signals  # noqa: F401
