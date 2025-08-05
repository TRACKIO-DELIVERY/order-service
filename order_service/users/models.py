from django.contrib.auth.models import AbstractUser
from django.core.exceptions import ValidationError
from django.db import models
from django.urls import reverse
from django.utils.translation import gettext_lazy as _

from . import querysets


class UserType(models.IntegerChoices):
    ADMINISTRATOR = 1, "Administrator"
    CUSTOMER = 2, "Customer"
    DELIVERY_MAN = 3, "Delivery Man"
    System = 4, "System"


class User(AbstractUser):
    """
    Default custom user model for Order Service.
    If adding fields that need to be filled at user signup,
    check forms.SignupForm and forms.SocialSignupForms accordingly.
    """

    # First and last name do not cover name patterns around the globe
    name = models.CharField(_("Name of User"), blank=True, max_length=255)
    first_name = None  # type: ignore[assignment]
    last_name = None  # type: ignore[assignment]
    cpf = models.CharField(_("CPF"), max_length=11, null=True, blank=True, default=None, unique=True)
    birth_date = models.DateField(_("Birth Date"), blank=True, null=True)
    email = models.EmailField(max_length=250, unique=True)
    is_active = models.BooleanField(_("Is Active"), default=True)

    user_type = models.CharField(_("User type"), max_length=10, choices=UserType, default=UserType.CUSTOMER)

    objects = querysets.UserManagerCustom()
    filtered_objects = querysets.UserQuerySet.as_manager()

    USERNAME_FIELD = "email"
    REQUIRED_FIELDS = ["name"]

    def __str__(self):
        return f"{self.name} ({self.user_type})"

    def get_absolute_url(self) -> str:
        """Get URL for user's detail view.

        Returns:
            str: URL for user detail.

        """
        return reverse("users:detail", kwargs={"username": self.username})

    def validate_unique(self, exclude=None):
        """Validate uniqueness of email and CPF fields."""
        super().validate_unique(exclude=exclude)
        if User.objects.filter(cpf=self.cpf).exclude(pk=self.pk).exists():
            raise ValidationError({"cpf": _("CPF must be unique.")})
        if User.objects.filter(email=self.email).exclude(pk=self.pk).exists():
            raise ValidationError({"email": _("Email must be unique.")})


class DeliveryPerson(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    availability = models.CharField(max_length=50)
    vehicle = models.CharField(max_length=50)
    license_plate = models.CharField(max_length=50)

    def __str__(self):
        return f"Delivery Person {self.user}"
