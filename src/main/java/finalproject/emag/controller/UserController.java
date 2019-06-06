package finalproject.emag.controller;

import finalproject.emag.model.dto.ShowUserDto;
import finalproject.emag.model.service.UserService;
import finalproject.emag.util.SuccessMessage;
import finalproject.emag.util.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/users",produces = "application/json")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/register")
    public SuccessMessage register(HttpServletRequest request,HttpSession session) throws BaseException {
        return userService.register(request,session);
    }

    @PostMapping(value = "/login")
    public ShowUserDto login(HttpServletRequest request,HttpSession session) throws BaseException {
        return userService.login(request,session);
    }

    @PostMapping(value = "/logout")
    public SuccessMessage logout(HttpSession session) throws NotLoggedException {
        validateLogin(session);
        session.invalidate();
        return new SuccessMessage("You logged out",HttpStatus.OK.value(),LocalDateTime.now());
    }

    @PutMapping(value = "/subscribe")
    public SuccessMessage subscribe(HttpSession session) throws BaseException{
        validateLogin(session);
        return userService.subscribe(session);
    }

    @PutMapping(value = "/unsubscribe")
    public SuccessMessage unsubscribe(HttpSession session) throws BaseException{
        validateLogin(session);
        return userService.unsubscribe(session);
    }

    @PutMapping(value = "/edit-pass")
    public SuccessMessage editPassword(HttpServletRequest request,HttpSession session) throws BaseException{
        validateLogin(session);
        return userService.editPassword(request,session);
    }

    @PutMapping(value = "/edit-email")
    public SuccessMessage editEmail(HttpServletRequest request,HttpSession session) throws BaseException{
        validateLogin(session);
        return userService.editEmail(request,session);
    }

    @PutMapping(value = "/edit-personal-info")
    public SuccessMessage editPersonalInfo(HttpServletRequest request,HttpSession session) throws BaseException{
        validateLogin(session);
        return userService.editPersonalInfo(request,session);
    }
}