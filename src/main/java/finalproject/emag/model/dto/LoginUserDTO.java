package finalproject.emag.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginUserDTO {

    @NotNull
    private String email;
    @NotNull
    private String password;

}
