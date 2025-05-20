from django.db import models
from django.utils import timezone

# Models Abstratos
class CreatedAtModel(models.Model):
    created_at = models.DateTimeField(auto_now_add=True,editable=False)

    class Meta:
        abstract = True

class TimeStampedModel(CreatedAtModel):
    updated_at = models.DateTimeField(auto_now=True, auto_now_add=True)
    
    class Meta:
        abstract = True


# Models principais
class TipoUsuario(models.Model):
    descricao = models.CharField(max_length=50)

    def __str__(self):
        return self.descricao

class Usuario(TimeStampedModel):
    nome_completo = models.CharField(max_length=250)
    cpf_cnpj = models.CharField(max_length=14, unique=True)
    data_nascimento = models.DateField()
    email = models.EmailField(max_length=250)
    senha = models.CharField(max_length=50)
    tipo_usuario = models.ForeignKey(TipoUsuario, on_delete=models.PROTECT)
    status_usuario = models.BooleanField(default=True)

    def __str__(self):
        return self.nome_completo

class Entregador(TimeStampedModel):
    usuario = models.OneToOneField(Usuario, on_delete=models.CASCADE)
    disponibilidade = models.CharField(max_length=50)
    veiculo = models.CharField(max_length=50)
    placa = models.CharField(max_length=50)

    def __str__(self):
        return f"Entregador {self.usuario.nome_completo}"

class StatusPedido(models.Model):
    descricao = models.CharField(max_length=100)

    def __str__(self):
        return self.descricao

class Pedido(TimeStampedModel):
    restaurante_id = models.IntegerField()
    usuario = models.ForeignKey(Usuario, on_delete=models.CASCADE)
    entregador = models.ForeignKey(Entregador, on_delete=models.SET_NULL, null=True)
    taxa_entrega = models.DecimalField(max_digits=10, decimal_places=2)
    valor_pedido = models.DecimalField(max_digits=10, decimal_places=2)
    status = models.ForeignKey(StatusPedido, on_delete=models.PROTECT)
    data_encerramento = models.DateTimeField(null=True, blank=True)
    origem_app = models.CharField(max_length=100)

    def __str__(self):
        return f"Pedido #{self.id}"

class RastreamentoPedido(models.Model):
    pedido = models.ForeignKey(Pedido, on_delete=models.CASCADE)
    latitude_inicial = models.CharField(max_length=50)
    longitude_inicial = models.CharField(max_length=50)
    latitude_final = models.CharField(max_length=50)
    longitude_final = models.CharField(max_length=50)
    status_evento = models.CharField(max_length=50)
    data_hora = models.DateTimeField(default=timezone.now)

    def __str__(self):
        return f"Rastreamento {self.pedido.id} - {self.status_evento}"

class NotificacaoUsuario(TimeStampedModel):
    usuario = models.ForeignKey(Usuario, on_delete=models.CASCADE)
    mensagem = models.TextField()
    canal_envio = models.CharField(max_length=50)
    data_envio = models.DateTimeField(default=timezone.now)
    status_leitura = models.BooleanField(default=False)

    def __str__(self):
        return f"Notificação para {self.usuario.nome_completo}"

class LogUsuario(models.Model):
    usuario = models.ForeignKey(Usuario, on_delete=models.CASCADE)
    acao = models.CharField(max_length=50)
    data_hora = models.DateTimeField(default=timezone.now)

class LogAcesso(models.Model):
    usuario = models.ForeignKey(Usuario, on_delete=models.CASCADE)
    acao = models.CharField(max_length=50)
    ip_origem = models.CharField(max_length=45)
    data_hora = models.DateTimeField(default=timezone.now)

class LogPedido(models.Model):
    pedido = models.ForeignKey(Pedido, on_delete=models.CASCADE)
    acao = models.CharField(max_length=50)
    data_hora = models.DateTimeField(default=timezone.now)

