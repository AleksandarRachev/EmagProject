package finalproject.emag.model.dto;

import finalproject.emag.model.pojo.Category;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartViewProductDTO {

    private Long id;
    private Category category;
    private String name;
    private double price;
    private int quantity;

}
