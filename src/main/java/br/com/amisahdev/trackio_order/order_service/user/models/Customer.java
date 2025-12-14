package br.com.amisahdev.trackio_order.order_service.user.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "customer")
@PrimaryKeyJoinColumn(name = "customerId")
@Getter
@Setter
public class Customer extends User {
    @Column(length = 11, nullable = false)
    private String cpf;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date dateOfBirth;
    @Column(length = 11, nullable = false)
    private String imageUrl;

}
