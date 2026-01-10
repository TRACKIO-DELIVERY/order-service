package br.com.amisahdev.trackio_order.order_service.order.service.interf;

import br.com.amisahdev.trackio_order.order_service.order.dto.request.OrderCompletedCanceledRequest;
import br.com.amisahdev.trackio_order.order_service.order.dto.request.OrderDeliveryRequest;
import br.com.amisahdev.trackio_order.order_service.order.dto.request.OrderRequest;
import br.com.amisahdev.trackio_order.order_service.order.dto.response.OrderResponse;
import org.aspectj.weaver.ast.Or;

import java.util.List;

public interface OrderService {
    OrderResponse create (OrderRequest request);
    OrderResponse findById(Long id);
    List<OrderResponse> findAll();
    void deleteById(Long id);
    OrderResponse update (OrderRequest request);
    OrderResponse orderDelivery(OrderDeliveryRequest request);
    OrderResponse orderCancel(OrderCompletedCanceledRequest request);
    OrderResponse orderCompleted(OrderCompletedCanceledRequest request);
}
