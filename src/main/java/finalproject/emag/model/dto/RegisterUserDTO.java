package finalproject.emag.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterUserDTO {

    private String email;
    private String password;
    private String password2;
    private String fullName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate birthDate;
    private boolean subscribed;
    private String phoneNumber;
    private String username;

}
