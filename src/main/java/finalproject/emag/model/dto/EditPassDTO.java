package finalproject.emag.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EditPassDTO {

    @NotNull(message = "Missing valuable fields")
    private String currentPass;
    @NotNull(message = "Missing valuable fields")
    private String password;

}
