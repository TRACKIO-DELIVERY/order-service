from django.db.models.signals import post_migrate
from django.dispatch import receiver
from .models import TipoUsuario,StatusPedido

@receiver(post_migrate)
def criar_tipos_usuario(sender, **kwargs):
    tipos_padrao = ['Cliente', 'Admin', 'Entregador']
    for descricao in tipos_padrao:
        TipoUsuario.objects.get_or_create(descricao=descricao)

@receiver(post_migrate)
def criar_status_pedido(sender, **kwargs):
    tipos_padrao = ['Aguardando Coleta','Em Rota','Entregue','Cancelado','Em Produção']
    for descricao in tipos_padrao:
        StatusPedido.objects.get_or_create(descricao=descricao)
