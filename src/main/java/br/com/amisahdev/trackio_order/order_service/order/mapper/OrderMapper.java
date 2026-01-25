package br.com.amisahdev.trackio_order.order_service.order.mapper;

import br.com.amisahdev.trackio_order.order_service.order.dto.request.OrderRequest;
import br.com.amisahdev.trackio_order.order_service.order.dto.response.OrderItemResponse;
import br.com.amisahdev.trackio_order.order_service.order.dto.response.OrderResponse;
import br.com.amisahdev.trackio_order.order_service.order.dto.response.PaymentResponse;
import br.com.amisahdev.trackio_order.order_service.order.model.Order;
import br.com.amisahdev.trackio_order.order_service.order.model.OrderItem;
import br.com.amisahdev.trackio_order.order_service.order.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company.userId", source = "companyId")
    @Mapping(target = "customer.userId", source = "customerId")
    Order toEntity(OrderRequest request);

    @Mapping(target = "companyId", source = "company.userId")
    @Mapping(target = "companyName", source = "company.bussinessName")
    @Mapping(target = "customerId", source = "customer.userId")
    @Mapping(target = "customerName", source = "customer.username")
    @Mapping(target = "deliveryId",source = "deliveryPerson.userId")
    @Mapping(target = "deliveryName",source = "deliveryPerson.username")
    @Mapping(target = "orderFlee", source = "orderFlee")
    @Mapping(target = "orderStatus", source = "orderStatus")
    OrderResponse toResponse(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemResponse toItemResponse(OrderItem item);

    @Mapping(target = "orderId", source = "order.id")
    PaymentResponse toPaymentResponse(Payment payment);
}
