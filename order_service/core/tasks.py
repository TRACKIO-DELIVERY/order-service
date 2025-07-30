import datetime
import logging
import subprocess

from celery import shared_task


@shared_task
def generate_backup_postgres():
    data = datetime.datetime.now().strftime("%Y%m%d_%H%M%S")  # noqa: DTZ005
    backup_filename = f"backup_{data}.sql"
    backup_path = f"/backups/{backup_filename}"

    comando = [
        "docker",
        "exec",
        "order_service_local_postgres",
        "pg_dump",
        "-U",
        "PjuHeKcBPyqGCsZHUouCBBTuySoOnWfT",
        "order_service",
    ]

    try:
        with open(backup_path, "w") as f:  # noqa: PTH123
            subprocess.run(comando, check=True, stdout=f)  # noqa: S603
        logging.info(f"[BACKUP OK] Backup salvo em {backup_path}")
    except subprocess.CalledProcessError as e:
        logging.info(f"[BACKUP ERRO] {e.stderr}")
