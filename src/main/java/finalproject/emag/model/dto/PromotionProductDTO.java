package finalproject.emag.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class PromotionProductDTO {

    @NotNull(message = "Missing valuable fields")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate startDate;
    @NotNull(message = "Missing valuable fields")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate endDate;
    @NotNull(message = "Missing valuable fields")
    private Double newPrice;

}
