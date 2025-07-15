import asyncio
from typing import TYPE_CHECKING

import aio_pika
from callbacks import on_message
from connection import get_connection

if TYPE_CHECKING:
    import aio_pika.abc


async def consumer(queue_name: str) -> None:
    """
    Consumer that listens to the queue and processes messages.

    :param: queue_name: Name of the queue to consume messages from.
    :return: None
    """

    connection: aio_pika.abc.AbstractRobustConnection = await get_connection()
    async with connection:
        channel = await connection.channel()

        queue = await channel.get_queue(queue_name)

        await queue.consume(on_message, no_ack=False)

        await asyncio.Future()
