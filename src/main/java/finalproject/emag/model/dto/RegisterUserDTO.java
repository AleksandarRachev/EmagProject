package finalproject.emag.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class RegisterUserDTO {

    @Email
    @NotNull(message = "Missing valuable fields")
    private String email;
    @NotNull(message = "Missing valuable fields")
    private String password;
    @NotNull(message = "Missing valuable fields")
    @Size(min = 3, max = 20)
    private String fullName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate birthDate;
    private boolean subscribed;
    private String phoneNumber;
    private String username;

}
