import pika
from callbacks import on_close_connection
from callbacks import on_open_connection


class AMQPClient:
    """
    A simple AMQP client for connecting to an AMQP server.
    This client uses the pika library to establish a connection and perform basic operations.

    :param host: Hostname of the AMQP server (default is 'localhost').
    :param port: Port number of the AMQP server (default is 5672).
    :param username: Username for authentication (default is 'user').
    :param password: Password for authentication (default is 'password').
    """

    def __init__(self, host="localhost", port=5672, username="user", password="password"):  # noqa: S107
        self.host = host
        self.port = port
        self.username = username
        self.password = password
        self.connection = None
        self.channel = None

    def connect(self):
        """
        Establishes a connection to the AMQP server.
        :return: The connection object.
        """
        if self.connection:
            return self.connection
        credentials = pika.PlainCredentials(self.username, self.password)
        parameters = pika.ConnectionParameters(
            host=self.host, port=self.port, credentials=credentials
        )
        self.connection = pika.SelectConnection(
            parameters,
            on_open_callback=on_open_connection,
            on_close_callback=on_close_connection,
        )

        self.channel = self.connection.channel()

        return self.connection

    def get_connection(self):
        """
        Returns the connection object.
        :return: The connection object.
        """
        return self.connection

    def get_channel(self):
        """
        Returns the channel object.
        :return: The channel object.
        """
        return self.channel

    def close(self):
        if self.connection:
            self.connection.close()
