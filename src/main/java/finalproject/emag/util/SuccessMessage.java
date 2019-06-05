package finalproject.emag.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SuccessMessage {

    private String msg;
    private int status;
    private LocalDateTime time;

}
