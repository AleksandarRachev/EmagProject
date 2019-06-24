package finalproject.emag.model.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
public class ReviewDTO {

    @NotNull(message = "Missing valuable fields")
    private String title;
    @NotNull(message = "Missing valuable fields")
    private String comment;
    @NotNull
    @Max(6)
    private Integer grade;

}
