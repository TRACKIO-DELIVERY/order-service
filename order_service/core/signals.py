

from django.db.models.signals import post_save
from django.dispatch import receiver
from django.db import connection
from messaging.producer import producer

from .models import Order


@receiver(post_save, sender=Order)
async def send_message_to_broker(sender, instance, created, **kwargs):
    try:
        if created:
            payload = {
                "order_id": instance.id,
                "status": instance.order_status,
            }

            await producer(routing_key_name="order.created", payload=payload)
            logging.info(f"Message sent to broker for order {instance.id}")
    except Exception as exc:
        logging.exception(
            f"Error sending message to broker. Exception: {type(exc).__name__} Message: {exc}"
        )
        
@receiver(post_migrate)
def create_audit_trigger(sender, **kwargs):
    with connection.cursor() as cursor:
        cursor.execute(
            """
            CREATE OR REPLACE FUNCTION audit_order()
            RETURNS TRIGGER AS $$
            DECLARE
            msg JSONB;
            BEGIN
                IF TG_OP = 'INSERT' THEN
                -- Monta o JSONB com o usuário que inseriu e os dados novos inseridos
                    msg := jsonb_build_object(
                        'user', NEW.created_user,
                        'new_data', to_jsonb(NEW)
                    );

            ELSIF TG_OP = 'UPDATE' THEN
            -- Monta o JSONB com o usuário, dados antigos e dados atualizados
            msg := jsonb_build_object(
                'user', NEW.created_user,
                'old_data', to_jsonb(OLD),
                'new_data', to_jsonb(NEW)
            );

            ELSIF TG_OP = 'DELETE' THEN
            -- Monta o JSONB com o usuário e os dados deletados
            msg := jsonb_build_object(
                'user', OLD.created_user,
                'old_data', to_jsonb(OLD)
            );
            END IF;

            -- Insere um registro na tabela de log com as informações de auditoria
            INSERT INTO core_orderlog(order_id, action, msg, timestamp)
            VALUES (
                COALESCE(NEW.id, OLD.id),
                TG_OP,
                msg,
                now()
            );

            RETURN COALESCE(NEW, OLD);
            END;
            $$ LANGUAGE plpgsql;
                       """
        )

        cursor.execute(
            """
            DO $$
            BEGIN
                IF NOT EXISTS (
                    SELECT 1 FROM pg_trigger WHERE tgname = 'trg_audit_order'
                ) THEN
                    CREATE TRIGGER trg_audit_order
                    AFTER INSERT OR UPDATE OR DELETE ON core_order
                    FOR EACH ROW EXECUTE FUNCTION audit_order();
                END IF;
            END
            $$;
        """
        )

        cursor.execute(
            """
        CREATE OR REPLACE FUNCTION audit_user()
        RETURNS TRIGGER AS $$
        DECLARE
            msg JSONB;
        BEGIN
            IF TG_OP = 'INSERT' THEN
                -- Monta o JSONB com o usuário que inseriu e os dados novos inseridos
                msg := jsonb_build_object(
                    'user', NEW.created_user,
                    'new_data', to_jsonb(NEW)
                );

            ELSIF TG_OP = 'UPDATE' THEN
                -- Monta o JSONB com o usuário, dados antigos e dados atualizados
                msg := jsonb_build_object(
                    'user', NEW.created_user,
                    'old_data', to_jsonb(OLD),
                    'new_data', to_jsonb(NEW)
                );

            ELSIF TG_OP = 'DELETE' THEN
                -- Monta o JSONB com o usuário e os dados deletados
                msg := jsonb_build_object(
                    'user', OLD.created_user,
                    'old_data', to_jsonb(OLD)
                );
            END IF;

            -- Insere um registro na tabela de log com as informações de auditoria
            INSERT INTO core_userlog(user_id, action, msg, timestamp)
            VALUES (
                COALESCE(NEW.id, OLD.id),
                TG_OP,
                msg,
                now()
            );

            RETURN COALESCE(NEW, OLD);
        END;
        $$ LANGUAGE plpgsql;
            """
        )

        cursor.execute("""
        DO $$
        BEGIN
            IF NOT EXISTS (
                SELECT 1 FROM pg_trigger WHERE tgname = 'trg_audit_user'
            ) THEN
                CREATE TRIGGER trg_audit_user
                AFTER INSERT OR UPDATE OR DELETE ON core_user
                FOR EACH ROW EXECUTE FUNCTION audit_user();
            END IF;
        END
        $$;
        """)

