package br.com.amisahdev.trackio_order.order_service.user.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "deliveryPerson")
@PrimaryKeyJoinColumn(name = "DeliveryPersonId" , referencedColumnName = "user_id")
@Getter
@Setter
public class DeliveryPerson extends User {
    private String cpf;
    private String vehicleType;
    private String image_url;
    private Boolean active;
    private String fileKey;

}
