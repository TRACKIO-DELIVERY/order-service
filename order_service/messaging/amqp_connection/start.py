from amqp_client import AMQPClient
import environ

env = environ.Env()

if __name__ == "__main__":
    amqp_client = AMQPClient(
        host=env("AMQP_HOST"), port=env("AMQP_PORT"), username=env("AMPQ_USER"), password=env("AMQP_PASSWORD"))

    try:
        connection = amqp_client.connect()
    except KeyboardInterrupt:
        connection.close()
        print("Connection closed.")
        