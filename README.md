# Order Service

Essa branch é destinada para teste e implementação de recursos para otimização de consultas com ORM do Django.


### Execute o projeto
```bash
docker-compose -f docker-compose.local.yml up --build -d
```

### Popule o banco com um volume de dados
Aguarde pelo menos 1 minuto, até o fim da criação do bulk...
```bash
docker-compose -f docker-compose.local.yml run --rm django python manage.py populate_data_for_queries
```

Logue no sistema
<a href="http://localhost:8000/accounts/login/">[Login](http://localhost:8000/accounts/login/)</a>
com email dummy@dummy.com e senha dummy_password

**/api/orders/**

Vá até o `order_service.core.api.views.OrderViewSet`
Essa será a view utilizada para os testes de otimização, com a listagem dos pedidos.
Nessa viewset, é implementada o filtro para queryset e uso de paginação com 20 itens por página.

Realize o consumo do endpoint em questão e visualize a quantidade de queries e tempo gasto na ferramenta local `Django Silk` (<a href="http://localhost:8000/silk/">Ferramenta Silk</a> )


#### Agora realize o teste adicionando os campos no select_related na view
A query final será...

queryset = Order.objects.all().select_related("complementary_order", "delivery_person__user", "establishment__administrator", "establishment__address")
