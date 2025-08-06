from allauth.socialaccount.models import SocialAccount
from django.contrib.auth.models import UserManager
from django.db.models import QuerySet

from config.settings.base import env


class UserManagerCustom(UserManager):
    def create_user(self, email, password=None, **extra_fields):
        """
        Create and return a `User` with an email, password and other fields.
        """

        if not email:
            raise ValueError("The Email field must be set")
        email = self.normalize_email(email)
        user = self.model(email=email, **extra_fields)
        user.set_password(password)
        username = extra_fields.get("username") if extra_fields.get("username") != "" else email.split("@")[0]
        user.username = self.model.get_username_not_used(username)

        user.validate_unique()

        user.save(using=self._db)
        return user

    def create_superuser(self, email, password=None, **extra_fields):
        """
        Create and return a `User` with superuser (admin) permissions.
        """
        extra_fields.setdefault("is_staff", True)
        extra_fields.setdefault("is_superuser", True)

        if extra_fields.get("is_staff") is not True:
            raise ValueError("Superuser must have is_staff=True.")
        if extra_fields.get("is_superuser") is not True:
            raise ValueError("Superuser must have is_superuser=True.")

        return self.create_user(email, password, cpf=None, **extra_fields)

    def get_or_create_social_user(self, name, email, **extra_fields):
        """
        Get or create and return a SocialAccount User
        """
        user = self.filter(email__iexact=email).first()
        if not user:
            user = self.model(name=name, email=email, cpf=None)
            username = extra_fields.get("username") if extra_fields.get("username") != "" else email.split("@")[0]
            user.username = self.model.get_username_not_used(username)
            user.set_password(env("SOCIAL_PASSWORD_SECRET"))
            user.validate_unique()
            user.save(using=self._db)

        social_user, _ = SocialAccount.objects.update_or_create(
            user=user,
            email=email,
            name=name,
            provider=extra_fields.get("provider", "google"),
            uid=extra_fields.get("uid"),
        )
        return social_user

    def authenticate_social(self, name, email, **extra_fields):
        return self.get_or_create_social_user(name, email, **extra_fields).user


class UserQuerySet(QuerySet):
    def active(self):
        return self.filter(is_active=True)

    def inactive(self):
        return self.filter(is_active=False)

    def customers(self):
        return self.filter(user_type__description="Customer")

    def administrator(self):
        return self.filter(user_type__description="Administrator")

    def delivery_man(self):
        return self.filter(user_type__description="Delivery Man")

    def order_by_name(self):
        return self.order_by("name")


class DeliveryPersonQuerySet(QuerySet):
    pass
