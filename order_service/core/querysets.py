from django.db.models import QuerySet


class UserQuerySet(QuerySet):
    def active(self):
        return self.filter(is_active=True)

    def customers(self):
        return self.filter(user_type__description='Customer')

    def order_by_name(self):
        return self.order_by('full_name')

class DeliveryPersonQuerySet(QuerySet):
    pass


class OrderQuerySet(QuerySet):
    
    def status_waiting_collection(self):
        return self.filter(status__description='Waiting for Collection')
    
    def status_en_route(self):
        return self.filter(status__description='En Route')
    
    def status_delivered(self):
        return self.filter(status__description='Delivered')
    
    def status_cancelled(self):
        return self.filter(status__description='Cancelled')
    
    def status_in_production(self):
        return self.filter(status__description='In Production')