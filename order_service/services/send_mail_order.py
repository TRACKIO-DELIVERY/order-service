import logging

from django.core.mail import EmailMessage
from django.template.loader import render_to_string


def send_email_to_order_client(user_email, establishment, status, tracking_link=None):
    logging.info(f"Sending email to {user_email} about order status {status}")

    order_email_templates = {
        "accepted": "email/email_order_accepted.html",
        "in_route": "email/email_order_in_route.html",
    }
    subject_options = {
        "accepted": f"📦 Seu pedido foi aceito - [{establishment.name}]",
        "in_route": f"🚚 Seu pedido está a caminho [{establishment.name}]",
    }
    subject_email = subject_options.get(status, "Atualização do seu pedido")

    html_body = render_to_string(
        order_email_templates[status],
        {
            "establishment": establishment.name,
            "establishment_email": establishment.email,
            "tracking_link": tracking_link,
        },
    )

    email = EmailMessage(subject_email, html_body, from_email=establishment.email, to=[user_email])
    email.content_subtype = "html"
    email.send()
