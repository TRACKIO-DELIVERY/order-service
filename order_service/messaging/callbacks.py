import logging

import aio_pika.abc

# Aqui fica o processamento
async def on_message(message: aio_pika.abc.AbstractIncomingMessage) -> None:
    logging.info(f"[x] Processing message {message}")
