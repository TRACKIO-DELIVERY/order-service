package br.com.amisahdev.trackio_order.order_service.user.models;

import br.com.amisahdev.trackio_order.order_service.geral.models.TimeStamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "address")
@Getter
@Setter
public class Address extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_address")
    private Long idAddress;

    @Column(length = 200, nullable = false)
    private String street;
    @Column(length = 100, nullable = false)
    private String city;
    @Column(length = 50, nullable = false)
    private String state;
    @Column(length = 8, nullable = false)
    private String zipCode;
    @Column(length = 50, nullable = false)
    private String neighborhood;
    private Integer number;

}
