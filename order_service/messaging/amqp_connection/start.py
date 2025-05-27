import logging

import environ
from amqp_client import AMQPClient

env = environ.Env()

if __name__ == "__main__":
    amqp_client = AMQPClient(
        host=env("RABBITMQ_HOST"),
        port=env("RABBITMQ_PORT"),
        username=env("RABBITMQ_USER"),
        password=env("RABBITMQ_PASSWORD"),
    )

    try:
        connection = amqp_client.connect()
    except KeyboardInterrupt:
        logging.info("Connection interrupted by user.")
