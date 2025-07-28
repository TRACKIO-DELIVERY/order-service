import subprocess
import datetime

from celery import shared_task

@shared_task
def generate_backup_postgres():
    data = datetime.datetime.now().strftime("%Y%m%d_%H%M%S")
    backup_filename = f"backup_{data}.sql"
    backup_path = f"/backups/{backup_filename}"

    comando = [
        "docker", "exec", "order_service_local_postgres",
        "pg_dump", "-U", "PjuHeKcBPyqGCsZHUouCBBTuySoOnWfT", "order_service"
    ]

    try:
        with open(backup_path, "w") as f:
            subprocess.run(comando, check=True, stdout=f)
        print(f"[BACKUP OK] Backup salvo em {backup_path}")
    except subprocess.CalledProcessError as e:
        print(f"[BACKUP ERRO] {e.stderr}")
