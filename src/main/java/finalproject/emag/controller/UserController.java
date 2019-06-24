package finalproject.emag.controller;

import finalproject.emag.model.dto.*;
import finalproject.emag.model.service.UserService;
import finalproject.emag.util.Message;
import finalproject.emag.util.exception.BaseException;
import finalproject.emag.util.exception.NotLoggedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/users", produces = "application/json")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/register")
    public ResponseEntity register(@RequestBody @Valid RegisterUserDTO user, HttpSession session, Errors errors) throws BaseException {
        if (errors.hasErrors()) {
            throw new BaseException("Missing valuable fields");
        }
        return userService.register(user, session);
    }

    @PostMapping(value = "/login")
    public ShowUserDTO login(@RequestBody @Valid LoginUserDTO user, HttpSession session) throws BaseException {
        return userService.login(user, session);
    }

    @PostMapping(value = "/logout")
    public ResponseEntity logout(HttpSession session) throws NotLoggedException {
        validateLogin(session);
        session.invalidate();
        return new ResponseEntity(new Message("You logged out"), HttpStatus.OK);
    }

    @PutMapping(value = "/subscribe")
    public ResponseEntity subscribe(HttpSession session) throws BaseException {
        validateLogin(session);
        return userService.subscribe(session);
    }

    @PutMapping(value = "/unsubscribe")
    public ResponseEntity unsubscribe(HttpSession session) throws BaseException {
        validateLogin(session);
        return userService.unsubscribe(session);
    }

    @PutMapping(value = "/edit-pass")
    public ResponseEntity editPassword(@RequestBody @Valid EditPassDTO user, HttpSession session) throws BaseException {
        validateLogin(session);
        return userService.editPassword(user, session);
    }

    @PutMapping(value = "/edit-email")
    public ResponseEntity editEmail(@RequestBody @Valid EditEmailDTO user, HttpSession session) throws BaseException {
        validateLogin(session);
        return userService.editEmail(user, session);
    }

    @PutMapping(value = "/edit-personal-info")
    public ResponseEntity editPersonalInfo(@RequestBody @Valid EditPersonalInfoDTO user, HttpSession session)
            throws BaseException {
        validateLogin(session);
        return userService.editPersonalInfo(user, session);
    }
}