package br.com.amisahdev.trackio_order.order_service.user.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "company")
@PrimaryKeyJoinColumn(name = "company_id", referencedColumnName = "user_id")
@Getter
@Setter
public class Company extends User {

    @Column(length = 14, nullable = false, unique = true)
    private String cnpj;
    @Column(length = 100, nullable = false)
    private String bussinessName;
    private String imageUrl;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id_address")
    private Address address;
    private BigDecimal deliveryFee;
    private String fileKey;

}
