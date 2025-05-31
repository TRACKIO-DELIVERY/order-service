from pathlib import Path

import aio_pika
import aio_pika.abc
import environ

BASE_DIR = Path(__file__).resolve().parent.parent.parent

env = environ.Env()
environ.Env.read_env(BASE_DIR / ".env")


AMQP_USER = env("AMQP_USER")
AMQP_PASSWORD = env("AMQP_PASSWORD")
AMQP_HOST = env("AMQP_HOST")
AMQP_PORT = env("AMQP_PORT")

AMQP_URL = f"amqp://{AMQP_USER}:{AMQP_PASSWORD}@{AMQP_HOST}:{AMQP_PORT}/"


async def get_connection() -> aio_pika.abc.AbstractRobustConnection:
    """
    Establishes a connection to the AMQP server.
    :return: An instance of AbstractRobustConnection.
    """
    return await aio_pika.connect_robust(AMQP_URL)
