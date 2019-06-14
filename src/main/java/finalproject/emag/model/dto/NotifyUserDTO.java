package finalproject.emag.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotifyUserDTO {

    private String email;
    private String fullName;

}
