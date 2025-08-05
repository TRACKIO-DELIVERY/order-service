import logging
import random
import string

from allauth.socialaccount.models import SocialAccount
from django.contrib.auth import get_user_model
from django.contrib.auth.models import UserManager
from django.db.models import QuerySet

from config.settings.base import env


def get_username_not_used(username):
    """
    Generate a unique username by appending a random suffix if the provided username is already taken.
    :param username: The base username to check.
    :return: A unique username.
    """

    auth_user_model = get_user_model()
    while auth_user_model.objects.filter(username=username).exists():
        logging.warning(f"Username '{username}' is already taken. Generating a new one.")
        base = username
        suffix = "".join(random.choices(string.digits, k=4))  # noqa: S311
        username = f"{base}{suffix}"

    return username


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
        user.username = get_username_not_used(username)

        user.validate_unique()

        user.save(using=self._db)
        return user

    def get_or_create_social_user(self, name, email, **extra_fields):
        """
        Get or create and return a SocialAccount User
        """
        user = self.filter(email__iexact=email).first()
        if not user:
            user = self.model(name=name, email=email)
            username = extra_fields.get("username") if extra_fields.get("username") != "" else email.split("@")[0]
            user.username = get_username_not_used(username)
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
