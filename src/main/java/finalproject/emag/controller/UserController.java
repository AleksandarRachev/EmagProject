package finalproject.emag.controller;

import finalproject.emag.model.dto.*;
import finalproject.emag.model.service.UserService;
import finalproject.emag.util.SuccessMessage;
import finalproject.emag.util.exception.BaseException;
import finalproject.emag.util.exception.NotLoggedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/users", produces = "application/json")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/register")
    public SuccessMessage register(@RequestBody RegisterUserDTO user, HttpSession session) throws BaseException {
        return userService.register(user, session);
    }

    @PostMapping(value = "/login")
    public ShowUserDTO login(@RequestBody LoginUserDTO user, HttpSession session) throws BaseException {
        return userService.login(user, session);
    }

    @PostMapping(value = "/logout")
    public SuccessMessage logout(HttpSession session) throws NotLoggedException {
        validateLogin(session);
        session.invalidate();
        return new SuccessMessage("You logged out", HttpStatus.OK.value(), LocalDateTime.now());
    }

    @PutMapping(value = "/subscribe")
    public SuccessMessage subscribe(HttpSession session) throws BaseException {
        validateLogin(session);
        return userService.subscribe(session);
    }

    @PutMapping(value = "/unsubscribe")
    public SuccessMessage unsubscribe(HttpSession session) throws BaseException {
        validateLogin(session);
        return userService.unsubscribe(session);
    }

    @PutMapping(value = "/edit-pass")
    public SuccessMessage editPassword(@RequestBody EditPassDTO user, HttpSession session) throws BaseException {
        validateLogin(session);
        return userService.editPassword(user, session);
    }

    @PutMapping(value = "/edit-email")
    public SuccessMessage editEmail(@RequestBody EditEmailDTO user, HttpSession session) throws BaseException {
        validateLogin(session);
        return userService.editEmail(user, session);
    }

    @PutMapping(value = "/edit-personal-info")
    public SuccessMessage editPersonalInfo(@RequestBody EditPersonalInfoDTO user, HttpSession session) throws BaseException {
        validateLogin(session);
        return userService.editPersonalInfo(user, session);
    }
}