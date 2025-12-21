package br.com.amisahdev.trackio_order.order_service.user.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "company")
@PrimaryKeyJoinColumn(name = "companyId")
@Getter
@Setter
public class Company extends User {

    @Column(length = 14, nullable = false, unique = true)
    private String cnpj;
    @Column(length = 100, nullable = false)
    private String bussiness_name;
    private String image_url;

}
