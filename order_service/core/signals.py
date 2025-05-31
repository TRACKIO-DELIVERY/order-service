import logging

from django.db.models.signals import post_save
from django.dispatch import receiver
from messaging.producer import producer

from .models import Order


@receiver(post_save, sender=Order)
async def send_message_to_broker(sender, instance, created, **kwargs):
    try:
        additional = "AV independencia"
        if created:
            payload = {
                "order_id": instance.id,
                "status": instance.order_status,
                "street": additional,
            }

            await producer(routing_key_name="order.created", payload=payload)
            logging.info(f"Message sent to broker for order {instance.id}")
    except Exception as exc:
        logging.exception(
            f"Error sending message to broker. Exception: {type(exc).__name__} Message: {exc}"
        )
