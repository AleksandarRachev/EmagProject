package finalproject.emag.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilterParamsDTO {

    private Double from;
    private Double to;
    private String order;

}
