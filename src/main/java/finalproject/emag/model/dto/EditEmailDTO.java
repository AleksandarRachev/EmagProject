package finalproject.emag.model.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class EditEmailDTO {

    @Email
    @NotNull(message = "Missing valuable fields")
    private String email;
    @NotNull(message = "Missing valuable fields")
    private String password;

}
