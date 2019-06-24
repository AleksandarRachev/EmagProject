package finalproject.emag.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class ProductAddDTO {

    @NonNull
    @Size(max = 30)
    private String name;
    @NotNull
    @Min(0)
    @Max(999999)
    private Double price;
    @NotNull
    @Min(0)
    private Integer quantity;
    @NotNull
    private Long categoryId;

}
