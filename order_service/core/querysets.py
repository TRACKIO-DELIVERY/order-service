from django.db.models import QuerySet


class OrderQuerySet(QuerySet):
    def status_waiting_collection(self):
        return self.filter(status__description="Waiting for Collection")

    def status_en_route(self):
        return self.filter(status__description="En Route")

    def status_delivered(self):
        return self.filter(status__description="Delivered")

    def status_cancelled(self):
        return self.filter(status__description="Cancelled")

    def status_in_production(self):
        return self.filter(status__description="In Production")

    def by_administrator(self, user):
        """
        Returns orders managed by the given user.
        """
        establishments = list(user.establishments.all().values_list("id", flat=True))
        return self.filter(establishment__in=establishments)


class EstablishmentQueryset(QuerySet):
    def by_administrator(self, user):
        """
        Returns establishments managed by the given user.
        """
        return self.filter(administrator=user)

    def active(self):
        """
        Returns only active establishments.
        """
        return self.filter(active=True)
