package finalproject.emag.controller;

import finalproject.emag.model.dto.ShowUserDTO;
import finalproject.emag.util.Message;
import finalproject.emag.util.exception.BaseException;
import finalproject.emag.util.exception.NotAdminException;
import finalproject.emag.util.exception.NotLoggedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public abstract class BaseController {

//    private static Logger log = Logger.getLogger(BaseController.class.getName());

    @ExceptionHandler({NotLoggedException.class, NotAdminException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ResponseEntity handleNotLogged(Exception e) {
//        log.error("exception: "+e);
        return new ResponseEntity(new Message(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({BaseException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity handleMyErrors(Exception e) {
//        log.error("exception: "+e);
        return new ResponseEntity(new Message(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    void validateLogin(HttpSession session) throws NotLoggedException {
        if (session.getAttribute("user") == null) {
            throw new NotLoggedException();
        }
    }

    void validateLoginAdmin(HttpSession session) throws NotAdminException, NotLoggedException {
        if (session.getAttribute("user") == null) {
            throw new NotLoggedException();
        } else {
            ShowUserDTO logged = (ShowUserDTO) (session.getAttribute("user"));
            if (!logged.isAdmin()) {
                throw new NotAdminException();
            }
        }
    }
}