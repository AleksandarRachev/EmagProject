package finalproject.emag.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class FilterParamsDTO {

    @NotNull
    @Min(0)
    private Double from;
    @NotNull
    @Max(999999)
    private Double to;
    @NotNull
    private String order;

}
