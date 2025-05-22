import pika

class AMQPClient:
    """
    A simple AMQP client for connecting to an AMQP server.
    This client uses the pika library to establish a connection and perform basic operations.
    :param host: Hostname of the AMQP server (default is 'localhost').
    :param port: Port number of the AMQP server (default is 5672).
    :param username: Username for authentication (default is 'user').
    :param password: Password for authentication (default is 'password').
    """
    
    def __init__(self, host='localhost', port=5672, username='user', password='password'):
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
            print("Connection already established.")
            return self.connection
        credentials = pika.PlainCredentials(self.username, self.password)
        parameters = pika.ConnectionParameters(
            host=self.host,
            port=self.port,
            credentials=credentials
        )
        self.connection = pika.SelectConnection(parameters)
        
        self.channel = self.connection.channel()


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
        
    def on_close(self, connection, reason):
        """
        Callback function to handle connection closure.
        :param connection: The connection object.
        :param reason: The reason for the closure.
        """
        print(f"Connection closed: {reason}")
        self.connection = None
        self.channel = None
        
    
    def close(self):
        if self.connection:
            self.connection.close()
