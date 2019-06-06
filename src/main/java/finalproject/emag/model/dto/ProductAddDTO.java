package finalproject.emag.model.dto;

import lombok.Data;

@Data
public class ProductAddDTO {

    private String name;
    private Double price;
    private Integer quantity;
    private Long categoryId;

}
