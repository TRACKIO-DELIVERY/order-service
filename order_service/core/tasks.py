import datetime
import logging
import subprocess

from celery import shared_task
from order_service.messaging.consumer import consumer_delivered,consumer_in_route,consumer_accepted

import asyncio

@shared_task
def generate_backup_postgres():
    """
    Generates a PostgreSQL database backup.

    This task connects to the 'order_service_local_postgres' Docker container
    and uses `pg_dump` to create a backup of the 'order_service' database.
    The backup file is saved in the `/backups/` directory with a timestamp in its name.
    The process is executed as a shared task.

    Raises:
    subprocess.CalledProcessError: If the `pg_dump` command fails.
    """
    data = datetime.datetime.now().strftime("%Y%m%d_%H%M%S") 
    backup_filename = f"backup_{data}.sql"
    backup_path = f"/backups/{backup_filename}"

    comando = [
        "docker",
        "exec",
        "order_service_postgres",
        "pg_dump",
        "-U",
        "PjuHeKcBPyqGCsZHUouCBBTuySoOnWfT",
        "order_service",
    ]

    try:
        with open(backup_path, "w") as f: 
            subprocess.run(comando, check=True, stdout=f) 
        logging.info(f"[BACKUP OK] Backup salvo em {backup_path}")
    except subprocess.CalledProcessError as e:
        logging.info(f"[BACKUP ERRO] {e.stderr}")


@shared_task
def consumer_order_delivered():
    """
    Consumes messages from the 'order.delivered.queue' and processes them.

    This task uses an asynchronous function `consumer_delivered` to handle messages
    from the specified RabbitMQ queue. It logs the outcome of the processing,
    indicating whether the order was successfully handled or if an error occurred.
    """
    success = asyncio.run(consumer_delivered("order.delivered.queue"))
    if success:
        logging.info("Pedido processado com sucesso.")
    else:
        logging.warning("Erro ao processar o pedido.")

@shared_task
def consumer_order_in_route():
    """
    Consumes messages from the 'order.in_route.queue"' and processes them.

    This task uses an asynchronous function `consumer_in_route` to handle messages
    from the specified RabbitMQ queue. It logs the outcome of the processing,
    indicating whether the order was successfully handled or if an error occurred.
    """
    sucess = asyncio.run(consumer_in_route("order.in_route.queue"))
    if sucess:
        logging.info("Pedido processado com sucesso.")
    else:
        logging.warning("Erro ao processar o pedido.")


@shared_task
def consumer_order_accepted():
    """
    Consumes messages from the 'order.accepted.queue"' and processes them.

    This task uses an asynchronous function `consumer_accepted` to handle messages
    from the specified RabbitMQ queue. It logs the outcome of the processing,
    indicating whether the order was successfully handled or if an error occurred.
    """
    sucess = asyncio.run(consumer_accepted("order.accepted.queue"))
    if sucess:
        logging.info("Pedido processado com sucesso.")
    else:
        logging.warning("Erro ao processar o pedido.")
        logging.exception(f"[BACKUP ERRO] {e.stderr}")
