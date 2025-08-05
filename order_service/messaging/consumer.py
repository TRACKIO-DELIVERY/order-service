from typing import TYPE_CHECKING
import traceback
import logging
import aio_pika
from aio_pika.exceptions import QueueEmpty
from asgiref.sync import sync_to_async
import json
from order_service.core.api.serializers import OrderUpdateSerializer,Order
from order_service.users.models import DeliveryPerson
#from callbacks import on_message
from order_service.messaging.connection import get_connection

if TYPE_CHECKING:
    import aio_pika.abc


VALID_TRANSITIONS = {
    2: [1],    # in_route 
    3: [2],    # delivered
}

def is_valid_transition(current_status, new_status):
    """
    Checks if a status transition is valid.

    This function determines whether an order can transition from its `current_status`
    to a `new_status` based on a predefined set of valid transitions.

    Args:
        current_status (int): The current status of the order.
        new_status (int): The new status the order is attempting to transition to.

    Returns:
        bool: `True` if the transition is valid, `False` otherwise.
    """
    return current_status in VALID_TRANSITIONS.get(new_status, [])

@sync_to_async
def atualizar_pedido(order, mensagem):
    """Updates an order with new data.

    This asynchronous function takes an `order` instance and a `mensagem`
    (message) dictionary containing updated data. It uses the `OrderUpdateSerializer`
    to validate and apply the changes, allowing for partial updates. The function
    is wrapped with `@sync_to_async` to enable it to be called from an async context.

    Args:
        order: The `Order` model instance to be updated.
        mensagem: A dictionary with the data to update the order.

    Returns:
        A tuple `(success, errors)`.
        `success` is a boolean indicating if the update was successful.
        `errors` is a dictionary of validation errors if the update failed;
        otherwise, it is `None`.
    """
    serializer = OrderUpdateSerializer(instance=order, data=mensagem, partial=True)
    if serializer.is_valid():
        serializer.save()
        return True, None
    return False, serializer.errors



async def consumer_accepted(queue_name: str) -> None:
    """
    Consumes and processes 'order accepted' messages from a RabbitMQ queue.

    This asynchronous function connects to a RabbitMQ queue, consumes messages
    one by one, and attempts to update a corresponding order in the database.
    It performs several validation steps:
    - Checks if the message body contains a valid `order_id` and `delivery_person` ID.
    - Verifies that the order and delivery person exist in the database.
    - Uses `OrderUpdateSerializer` to validate and save the order update.

    If the update is successful, the message is acknowledged (`ack`).
    If any part of the process fails (e.g., validation errors, database
    lookup failures), the function logs the error and the message is not
    acknowledged, allowing it to be reprocessed later.

    Args:
        queue_name (str): The name of the RabbitMQ queue to consume from.
    """
    connection = await get_connection()
    async with connection:
        channel = await connection.channel()
        queue = await channel.get_queue(queue_name)

        while True:
            try:
                try:
                    message = await queue.get(no_ack=False)
                except QueueEmpty:
                    logging.info("Fila vazia, nenhuma mensagem para consumir.")
                    break

                if not message:
                    logging.info("Nenhuma mensagem disponível.")
                    break

                raw_mensagem = message.body.decode()
                logging.info(f"Mensagem bruta: {raw_mensagem}")
                mensagem = json.loads(raw_mensagem)
                logging.info(f"Requisição: {mensagem}")

                order_id = mensagem.get("order_id")
                if not order_id:
                    logging.warning("Mensagem recebida sem order_id.")
                    continue

                try:
                    order = await sync_to_async(Order.objects.get)(id=order_id)
                    logging.info(f"Order encontrado: {order_id}")
                except Order.DoesNotExist:
                    logging.warning(f"Pedido com ID {order_id} não encontrado.")
                    continue
                
                delivery_person_id = mensagem.get("delivery_person")
                if delivery_person_id in [None, "", 0]:
                    logging.warning("Mensagem recebida com delivery_person inválido.")
                    continue
                
                try:
                    delivery_person = await sync_to_async(DeliveryPerson.objects.get)(id=delivery_person_id)
                    logging.info(f"Delivery person encontrada: ID {delivery_person.id}")
                except DeliveryPerson.DoesNotExist:
                    logging.warning(f"Delivery Person com id {delivery_person_id} não encontrada")
                    continue

                url = mensagem.get("url")
                if not url:
                    logging.warning("Mensagem recebida sem url")
                    continue


                try:
                    success, errors = await atualizar_pedido(order, mensagem)
                    if success:
                        logging.info(f"Pedido {order_id} atualizado com sucesso.")
                        await message.ack()
                    else:
                        logging.error(f"Erros de validação: {errors}")
                except Exception as e:
                    logging.error(f"Erro ao validar/salvar o pedido: {e}")
                    logging.error(traceback.format_exc())

            except Exception as e:
                logging.error(f"Erro inesperado ao processar mensagem da fila: {e}")
                logging.error(traceback.format_exc())


async def consumer_in_route(queue_name: str) -> None:
    """
    Consumes and processes 'in route' messages from a RabbitMQ queue.

    This asynchronous function connects to a specified RabbitMQ queue, retrieves
    messages, and updates the corresponding order's status in the database.
    It performs several critical checks before processing:
    - **Validates Order ID**: Ensures the message contains a valid `order_id`.
    - **Verifies Order Existence**: Confirms that the order exists in the database.
    - **Validates Status Transition**: Uses `is_valid_transition` to check if the
    requested status change from `current_status` to `new_status` is permitted.
    - **Checks for Delivery Person and URL**: Confirms that the order is
    assigned to a delivery person and has a URL.

    If all checks pass, the function uses `atualizar_pedido` to update the order.
    If the update is successful, the message is acknowledged (`ack`).
    If any validation fails or an error occurs during processing, the error is logged,
    and the message is not acknowledged, allowing it to be reprocessed later.

    Args:
        queue_name (str): The name of the RabbitMQ queue to consume from.
    """
    connection = await get_connection()
    async with connection:
        channel = await connection.channel()
        queue = await channel.get_queue(queue_name)

        while True:
            try:
                try:
                    message = await queue.get(no_ack=False)
                except QueueEmpty:
                    logging.info("Fila vazia, nenhuma mensagem para consumir.")
                    break
                if not message:
                    logging.info("Nenhuma mensagem disponível.")
                    break

                raw_mensagem = message.body.decode()
                logging.info(f"Mensagem bruta: {raw_mensagem}")
                mensagem = json.loads(raw_mensagem)
                logging.info(f"Requisição: {mensagem}")

                order_id = mensagem.get("order_id")
                if not order_id:
                    logging.warning("Mensagem recebida sem order_id.")
                    continue

                try:
                    order = await sync_to_async(Order.objects.get)(id=order_id)
                    logging.info(f"Order encontrado: {order_id}")
                except Order.DoesNotExist:
                    logging.warning(f"Pedido com ID {order_id} não encontrado.")
                    continue

                new_status = mensagem.get("order_status")
                current_status = order.order_status

                if not is_valid_transition(current_status, new_status):
                    logging.warning(
                        f"Transição de status inválida para o pedido {order_id}: "
                        f"{current_status} → {new_status}. Ignorando mensagem."
                    )
                    continue

                url = order.url
                if url is None:
                    logging.warning("Pedido sem url ainda")
                    continue

                person = await sync_to_async(lambda: order.delivery_person)()
                if person is None:
                    logging.warning("Pedido sem Entregador")
                    continue

                try:
                    success, errors = await atualizar_pedido(order, mensagem)
                    if success:
                        logging.info(f"Pedido {order_id} atualizado com sucesso.")
                        await message.ack()
                    else:
                        logging.error(f"Erros de validação: {errors}")
                except Exception as e:
                    logging.error(f"Erro ao validar/salvar o pedido: {e}")
                    logging.error(traceback.format_exc())


            except Exception as e:
                logging.error(f"Erro inesperado ao processar mensagem da fila: {e}")
                logging.error(traceback.format_exc())



async def consumer_delivered(queue_name: str) -> None:
    """
    Consumes and processes 'order delivered' messages from a RabbitMQ queue.

    This asynchronous function connects to the specified RabbitMQ queue, retrieves
    messages, and updates the corresponding order's status in the database. It
    performs several key checks before processing:
    - **Validates Order ID**: Ensures the message contains a valid `order_id`.
    - **Verifies Order Existence**: Confirms that the order exists in the database.
    - **Validates Status Transition**: Uses `is_valid_transition` to check if the
    requested status change from `current_status` to `new_status` is permitted.

    If all checks pass, the function uses `atualizar_pedido` to update the order.
    If the update is successful, the message is acknowledged (`ack`).
    If any validation fails or an unexpected error occurs, the error is logged,
    and the message is not acknowledged. This ensures that a failed message
    can be reprocessed later.

    Args:
        queue_name (str): The name of the RabbitMQ queue to consume from.
    """
    connection = await get_connection()
    async with connection:
        channel = await connection.channel()
        queue = await channel.get_queue(queue_name)

        while True:
            try:
                try:
                    message = await queue.get(no_ack=False)
                except QueueEmpty:
                    logging.info("Fila vazia, nenhuma mensagem para consumir.")
                    break
                if not message:
                    logging.info("Nenhuma mensagem disponível.")
                    break

                raw_mensagem = message.body.decode()
                logging.info(f"Mensagem bruta: {raw_mensagem}")
                mensagem = json.loads(raw_mensagem)
                logging.info(f"Requisição: {mensagem}")

                order_id = mensagem.get("order_id")
                if not order_id:
                    logging.warning("Mensagem recebida sem order_id.")
                    continue

                try:
                    order = await sync_to_async(Order.objects.get)(id=order_id)
                    logging.info(f"Order encontrado: {order_id}")
                except Order.DoesNotExist:
                    logging.warning(f"Pedido com ID {order_id} não encontrado.")
                    continue

                new_status = mensagem.get("order_status")
                current_status = order.order_status

                if not is_valid_transition(current_status, new_status):
                    logging.warning(
                        f"Transição de status inválida para o pedido {order_id}: "
                        f"{current_status} → {new_status}. Ignorando mensagem."
                    )
                    continue

                try:
                    success, errors = await atualizar_pedido(order, mensagem)
                    if success:
                        logging.info(f"Pedido {order_id} atualizado com sucesso.")
                        await message.ack()
                    else:
                        logging.error(f"Erros de validação: {errors}")
                except Exception as e:
                    logging.error(f"Erro ao validar/salvar o pedido: {e}")
                    logging.error(traceback.format_exc())


            except Exception as e:
                logging.error(f"Erro inesperado ao processar mensagem da fila: {e}")
                logging.error(traceback.format_exc())