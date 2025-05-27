import logging


def on_open_connection():
    """Callback function for when the AMQP connection is established."""
    logging.info("AMQP connection established successfully.")


def on_close_connection(self, connection, reason):
    """
    Callback function to handle connection closure.
    :param connection: The connection object.
    :param reason: The reason for the closure.
    """
    self.connection = None
    self.channel = None


def on_publish():
    """Callback function for when a message is published."""
    logging.info("Message published successfully.")


def on_consume():
    """Callback function for when a message is consumed."""
    logging.info("Message consumed successfully.")
