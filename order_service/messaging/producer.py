import json
import logging

import aio_pika
import aio_pika.abc

from order_service.messaging.connection import get_connection


async def producer(routing_key_name: str, payload: str | dict) -> None:
    connection: aio_pika.abc.AbstractRobustConnection = await get_connection()

    async with connection:
        routing_key = routing_key_name
        channel: aio_pika.abc.AbstractChannel = await connection.channel()

        exchange = await channel.declare_exchange("order.direct", aio_pika.ExchangeType.DIRECT, durable=True)

        channel = await connection.channel()

        if isinstance(payload, dict):
            payload = json.dumps(payload)

        message = aio_pika.Message(
            payload.encode(),
            delivery_mode=aio_pika.DeliveryMode.PERSISTENT,
        )

        try:
            await exchange.publish(message, routing_key=routing_key)
        except aio_pika.exceptions.NackError:
            logging.exception(" [!] Failed to publish message")
