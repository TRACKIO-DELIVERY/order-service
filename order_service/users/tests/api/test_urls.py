from django.urls import resolve
from django.urls import reverse

from order_service.users.models import User


def test_user_detail(user: User):
    assert reverse("api:user-detail", kwargs={"pk": user.pk}) == f"/api/users/{user.pk}/"
    assert resolve(f"/api/users/{user.username}/").view_name == "api:user-detail"


def test_user_list():
    assert reverse("api:user-list") == "/api/users/"
    assert resolve("/api/users/").view_name == "api:user-list"
