package finalproject.emag.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ReviewDTO {

    @NotNull(message = "Missing valuable fields")
    private String title;
    @NotNull(message = "Missing valuable fields")
    private String comment;
    @NotNull
    @Size(max = 6)
    private Integer grade;

}
