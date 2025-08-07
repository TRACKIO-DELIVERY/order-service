from rest_framework_simplejwt.serializers import TokenObtainPairSerializer


class CustomTokenObtainPairSerializer(TokenObtainPairSerializer):
    @classmethod
    def get_token(cls, user):
        token = super().get_token(user)

        token["name"] = user.name
        token["email"] = user.email
        token["cpf"] = user.cpf
        token["birth_date"] = user.birth_date.isoformat() if user.birth_date else None

        return token
