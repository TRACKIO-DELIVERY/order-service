package br.com.amisahdev.trackio_order.order_service.order.model;

import br.com.amisahdev.trackio_order.order_service.geral.models.TimeStamp;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.models.Customer;
import br.com.amisahdev.trackio_order.order_service.user.models.DeliveryPerson;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"order\"")
@Getter
@Setter
public class Order extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "delivery_id")
    private DeliveryPerson deliveryPerson;
    private LocalDateTime orderDate;
    private BigDecimal orderAmount;
    private BigDecimal orderFlee;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;


    public void setPayment(Payment payment) {
        this.payment = payment;
        if (payment != null) {
            payment.setOrder(this);
        }
    }

}
