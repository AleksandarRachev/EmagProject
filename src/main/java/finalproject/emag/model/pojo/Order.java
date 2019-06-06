package finalproject.emag.model.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "ordered_products")
public class Order {

    @EmbeddedId
    private OrderedId id;
    @Column(nullable = false)
    private Integer quantity;
}
