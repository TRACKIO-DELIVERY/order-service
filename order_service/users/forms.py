from allauth.account.forms import SignupForm
from allauth.socialaccount.forms import SignupForm as SocialSignupForm
from django import forms
from django.contrib.auth import forms as admin_forms
from django.utils.translation import gettext_lazy as _

from .models import User


class UserAdminChangeForm(admin_forms.UserChangeForm):
    class Meta(admin_forms.UserChangeForm.Meta):  # type: ignore[name-defined]
        model = User


class UserAdminCreationForm(admin_forms.AdminUserCreationForm):
    """
    Form for User Creation in the Admin Area.
    To change user signup, see UserSignupForm and UserSocialSignupForm.
    """

    class Meta(admin_forms.UserCreationForm.Meta):  # type: ignore[name-defined]
        model = User
        error_messages = {
            "username": {"unique": _("This username has already been taken.")},
        }


class UserSignupForm(SignupForm):
    """
    Form that will be rendered on a user sign up section/screen.
    Default fields will be added automatically.
    Check UserSocialSignupForm for accounts created from social.
    """

    field_order = [
        "name",
        "username",
        "email",
        "cpf",
        "birth_date",
    ]
    name = forms.CharField(
        required=True,
        label=_("Name"),
        max_length=255,
        widget=forms.TextInput(attrs={"placeholder": "Nome Completo"}),
    )
    birth_date = forms.DateField(
        required=False,
        label=_("Birth Date"),
        widget=forms.DateInput(attrs={"type": "date"}),
    )
    cpf = forms.CharField(
        required=True,
        label=_("CPF"),
        max_length=11,
        min_length=11,
        widget=forms.TextInput(attrs={"placeholder": "12345678901"}),
        error_messages={
            "max_length": _("CPF must be 11 digits long."),
            "min_length": _("CPF must be 11 digits long."),
        },
    )

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.fields["name"].label = "Nome Completo"
        self.fields["birth_date"].label = "Data de Nascimento"
        self.fields["cpf"].label = "CPF"
        self.fields["email"].label = "E-mail"
        self.fields["username"].label = "Nome de Usuário"


class UserSocialSignupForm(SocialSignupForm):
    """
    Renders the form when user has signed up using social accounts.
    Default fields will be added automatically.
    See UserSignupForm otherwise.
    """
