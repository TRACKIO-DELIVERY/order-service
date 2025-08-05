import logging
from django.db import connection
from django.db.models.signals import post_migrate
from django.dispatch import receiver


@receiver(post_migrate)
def create_audit_trigger(sender, **kwargs):
    with connection.cursor() as cursor:
        cursor.execute(
            """
        CREATE OR REPLACE FUNCTION audit_user()
        RETURNS TRIGGER AS $$
        DECLARE
            msg JSONB;
        BEGIN
            IF TG_OP = 'INSERT' THEN
                msg := jsonb_build_object(
                    'new_data', to_jsonb(NEW)
                );

            ELSIF TG_OP = 'UPDATE' THEN
                msg := jsonb_build_object(
                    'old_data', to_jsonb(OLD),
                    'new_data', to_jsonb(NEW)
                );

            ELSIF TG_OP = 'DELETE' THEN
                msg := jsonb_build_object(
                    'old_data', to_jsonb(OLD)
                );
            END IF;

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
                AFTER INSERT OR UPDATE OR DELETE ON users_user
                FOR EACH ROW EXECUTE FUNCTION audit_user();
            END IF;
        END
        $$;
        """)
