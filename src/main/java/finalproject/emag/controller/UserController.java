package finalproject.emag.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import finalproject.emag.model.dto.ShowUserDto;
import finalproject.emag.model.pojo.User;
import finalproject.emag.model.services.UserService;
import finalproject.emag.repositories.UserRepository;
import finalproject.emag.util.GetDate;
import finalproject.emag.util.SuccessMessage;
import finalproject.emag.util.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping(value = "/users",produces = "application/json")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/register")
    public SuccessMessage registerUser(HttpServletRequest request,HttpSession session) throws BaseException {
        return userService.registerUser(request,session);
    }

    @PostMapping(value = "/login")
    public ShowUserDto loginUser(HttpServletRequest request,HttpSession session) throws BaseException {
        return userService.loginUser(request,session);
    }

    @PostMapping(value = "/logout")
    public SuccessMessage logoutUser(HttpSession session) throws NotLoggedException {
        validateLogin(session);
        session.invalidate();
        return new SuccessMessage("You logged out",HttpStatus.OK.value(),LocalDateTime.now());
    }
//
//    private User getUser(HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException {
//        String email = request.getParameter("email");
//        String password = checkIfNull(request, "password");
//        String fullName = request.getParameter("full_name");
//        String username = checkIfNull(request, "username");
//        String phone = checkIfNull(request, "phone");
//        String birthDate = checkIfNull(request, "birth_date");
//        String imageUrl = checkIfNull(request, "image_url");
//        boolean subscribed = Boolean.parseBoolean(request.getParameter("subscribed"));
//        boolean admin;
//        if (request.getParameter("admin") != null) {
//            admin = Boolean.parseBoolean(request.getParameter("admin"));
//        } else {
//            admin = false;
//        }
//        LocalDate date = GetDate.getDate(birthDate);
//        String phoneRegex = "08[789]\\d{7}";
//        String emailRegex = "([A-Za-z0-9-_.]+@[A-Za-z0-9-_]+(?:\\.[A-Za-z]+)+)";
//        if (!email.matches(emailRegex)) {
//            response.setStatus(400);
//            response.getWriter().append("Invalid email.");
//        }
//        if (phone != null) {
//            if (!phone.matches(phoneRegex)) {
//                response.setStatus(400);
//                response.getWriter().append("Invalid phone number");
//            }
//        }
//        User user = new User();
//        user.setEmail(email);
//        user.setPassword(password);
//        user.setName(fullName);
//        user.setUsername(username);
//        user.setPhoneNumber(phone);
//        user.setBirthDate(date);
//        user.setSubscribed(subscribed);
//        user.setAdmin(admin);
//        user.setImageUrl(imageUrl);
//        return user;
//    }
//

//
//    @PostMapping(value = "/logout")
//    public String logoutUser(HttpSession session) throws NotLoggedException {
//        validateLogin(session);
//        session.invalidate();
//        return "You logged out";
//    }
//
//    @PutMapping(value = "/subscribe")
//    public String subscribe(HttpSession session) throws NotLoggedException, SQLException {
//        validateLogin(session);
//        return this.dao.subscribe((User)session.getAttribute(USER));
//    }
//
//    @PutMapping(value = "/unsubscribe")
//    public String unsubscribe(HttpSession session) throws NotLoggedException, SQLException {
//        validateLogin(session);
//        return this.dao.unsubscribe((User)session.getAttribute(USER));
//    }
//
//    @PutMapping(value = "/edit-personal-info")
//    public String editPersonalInfoUser(@RequestBody String input,HttpServletResponse response,HttpSession session)
//            throws Exception{
//        validateLogin(session);
//        JsonNode jsonNode = objectMapper.readTree(input);
//        if(!jsonNode.has("full_name")){
//            throw new MissingValuableFieldsException();
//        }
//        String birthDate = jsonNode.get("birth_date").textValue();
//        LocalDate date = GetDate.getDate(birthDate);
//        EditPersonalInfoDto user = new EditPersonalInfoDto(jsonNode.get("full_name").textValue(),
//                checkIfNull(jsonNode,"username"),
//                checkIfNull(jsonNode,"phone"),
//                date);
//        if(this.dao.checkIfUsernameExists(user.getUsername())) {
//            User loggedUser = (User)session.getAttribute(USER);
//            this.dao.editPersonalInfoUser(user,loggedUser.getId());
//            return EDIT_SUCCESSFUL;
//        }
//        else{
//            throw new UsernameTakenException();
//        }
//    }
//
//    @PutMapping(value = "/edit-email")
//    public String editUserSecurity(@RequestBody String input,HttpServletResponse response,HttpSession session)
//            throws Exception{
//        validateLogin(session);
//        JsonNode jsonNode = objectMapper.readTree(input);
//        if(!jsonNode.has("email") || !jsonNode.has("password")){
//            response.setStatus(404);
//            throw new MissingValuableFieldsException();
//        }
//        if(!dao.checkIfEmailIsFree(jsonNode.get("email").textValue())){
//            throw new EmailTakenException();
//        }
//        User loggedUser = (User)session.getAttribute(USER);
//        String email = jsonNode.get("email").textValue();
//        String pass = jsonNode.get("password").textValue();
//        if(!BCrypt.checkpw(pass,loggedUser.getPassword())){
//            throw new WrongCredentialsException();
//        }
//        EditEmailDto user = new EditEmailDto(email);
//        this.dao.editEmail(user,loggedUser.getId());
//        return EDIT_SUCCESSFUL;
//    }
//
//    @PutMapping(value = "/edit-password")
//    public String editPassword(@RequestBody String input,HttpSession session,HttpServletResponse response)
//            throws Exception {
//        JsonNode jsonNode = objectMapper.readTree(input);
//        validateLogin(session);
//        if (!jsonNode.has("current_password") || !jsonNode.has("password") ||
//                !jsonNode.has("password2")) {
//            response.setStatus(404);
//            throw new MissingValuableFieldsException();
//        }
//        User loggedUser = (User)session.getAttribute(USER);
//        String currentPass = jsonNode.get("current_password").textValue();
//        String pass = jsonNode.get("password").textValue();
//        String pass2 = jsonNode.get("password2").textValue();
//        if(!BCrypt.checkpw(currentPass,loggedUser.getPassword())){
//            throw new WrongCredentialsException();
//        }
//        if (!pass.equals(pass2)) {
//            throw new PasswordsNotMatchingException();
//        }
//        EditPasswordDto user = new EditPasswordDto(pass);
//        this.dao.editPassword(user,loggedUser.getId());
//        return EDIT_SUCCESSFUL;
//    }
}