package finalproject.emag.model.service;

import finalproject.emag.model.dto.ShowUserDto;
import finalproject.emag.model.pojo.User;
import finalproject.emag.repositories.UserRepository;
import finalproject.emag.util.PasswordEncoder;
import finalproject.emag.util.SuccessMessage;
import finalproject.emag.util.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class UserService {

    private static final String USER = "user";

    @Autowired
    private UserRepository userRepository;

    public SuccessMessage register(HttpServletRequest request, HttpSession session) throws BaseException {
        User user = new User();
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String password2 = request.getParameter("password2");
        String fullName = request.getParameter("full_name");
        boolean subscribed = Boolean.parseBoolean(request.getParameter("subscribed"));
        String phoneNumber = request.getParameter("phone_number");
        String date = request.getParameter("birth_date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        LocalDate birthDate = date == null?null:LocalDate.parse(date,formatter);
        String username = request.getParameter("username");
        registerValidation(email,password,password2,fullName,username);
        user.setEmail(email);
        user.setPassword(PasswordEncoder.hashPassword(password));
        user.setName(fullName);
        user.setSubscribed(subscribed);
        user.setUsername(username);
        user.setPhoneNumber(phoneNumber);
        user.setBirthDate(birthDate);
        userRepository.save(user);
        ShowUserDto userSession = new ShowUserDto(user.getId(),user.getEmail(),user.getName(),user.getUsername(),
                user.getPhoneNumber(),user.getBirthDate(),user.isSubscribed(),user.isAdmin(),user.getImageUrl());
        session.setAttribute(USER,userSession);
        session.setMaxInactiveInterval((60*60));
        return new SuccessMessage("Register successful", HttpStatus.OK.value(), LocalDateTime.now());
    }

    private void registerValidation(String email,String password,String password2,String fullName,String username) throws BaseException{
        if(email == null || password == null || password2 == null || fullName == null){
            throw new MissingValuableFieldsException();
        }
        if (!password.matches(password2)){
            throw new PasswordsNotMatchingException();
        }
        checkIfEmailFree(email);
        checkIfUsernameFree(username);
    }

    private void checkIfEmailFree(String email) throws EmailTakenException {
        int count = userRepository.findAllByEmail(email).size();
        if(count > 0){
            throw new EmailTakenException();
        }
    }

    private void checkIfUsernameFree(String username) throws UsernameTakenException {
        int count = userRepository.findAllByUsername(username).size();
        if(count > 0 ){
            throw new UsernameTakenException();
        }
    }

    public ShowUserDto login(HttpServletRequest request,HttpSession session) throws BaseException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        if(email == null || password == null){
            throw new MissingValuableFieldsException();
        }
        List<User> users = userRepository.findAllByEmail(email);
        User getUser = userRepository.findByEmail(email);
        if(users.size() < 1 || !BCrypt.checkpw(password,getUser.getPassword())){
            throw new WrongCredentialsException();
        }
        ShowUserDto user = new ShowUserDto(getUser.getId(),getUser.getEmail(),getUser.getName(),getUser.getUsername(),
                getUser.getPhoneNumber(),getUser.getBirthDate(),getUser.isSubscribed(),getUser.isAdmin(),getUser.getImageUrl());
        session.setAttribute(USER,user);
        session.setMaxInactiveInterval((60*60));
        return user;
    }

    public SuccessMessage subscribe(HttpSession session) throws AlreadySubscribedException {
        ShowUserDto userSession = (ShowUserDto) session.getAttribute(USER);
        User user = userRepository.findByEmail(userSession.getEmail());
        if(user.isSubscribed()){
            throw new AlreadySubscribedException();
        }
        userSession.setSubscribed(true);
        user.setSubscribed(true);
        userRepository.save(user);
        return new SuccessMessage("You subscribed",HttpStatus.OK.value(),LocalDateTime.now());
    }

    public SuccessMessage unsubscribe(HttpSession session) throws NotSubscribedException {
        ShowUserDto userSession = (ShowUserDto) session.getAttribute(USER);
        User user = userRepository.findByEmail(userSession.getEmail());
        if(!user.isSubscribed()){
            throw new NotSubscribedException();
        }
        userSession.setSubscribed(false);
        user.setSubscribed(false);
        userRepository.save(user);
        return new SuccessMessage("You unsubscribed",HttpStatus.OK.value(),LocalDateTime.now());
    }

    public SuccessMessage editPassword(HttpServletRequest request,HttpSession session) throws WrongCredentialsException, PasswordsNotMatchingException {
        ShowUserDto userSession = (ShowUserDto) session.getAttribute(USER);
        User user = userRepository.findById(userSession.getId()).get();
        String currentPass = request.getParameter("current_pass");
        String password = request.getParameter("password");
        String password2 = request.getParameter("password2");
        if(!BCrypt.checkpw(currentPass,user.getPassword())){
            throw new WrongCredentialsException();
        }
        if(!password.matches(password2)){
            throw new PasswordsNotMatchingException();
        }
        user.setPassword(PasswordEncoder.hashPassword(password));
        userRepository.save(user);
        return new SuccessMessage("Password edited",HttpStatus.OK.value(),LocalDateTime.now());
    }

    public SuccessMessage editEmail(HttpServletRequest request,HttpSession session) throws WrongCredentialsException, EmailTakenException {
        ShowUserDto userSession = (ShowUserDto) session.getAttribute(USER);
        User user = userRepository.findById(userSession.getId()).get();
        String newEmail = request.getParameter("new_email");
        String password = request.getParameter("password");
        if(!BCrypt.checkpw(password,user.getPassword())){
            throw new WrongCredentialsException();
        }
        checkIfEmailFree(newEmail);
        user.setEmail(newEmail);
        userRepository.save(user);
        return new SuccessMessage("Email edited",HttpStatus.OK.value(),LocalDateTime.now());
    }

    public SuccessMessage editPersonalInfo(HttpServletRequest request,HttpSession session) throws UsernameTakenException, MissingValuableFieldsException {
        ShowUserDto userSession = (ShowUserDto) session.getAttribute(USER);
        User user = userRepository.findById(userSession.getId()).get();
        String username = request.getParameter("username");
        String fullName = request.getParameter("full_name");
        String date = request.getParameter("birth_date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        LocalDate birthDate = date == null?null:LocalDate.parse(date,formatter);
        String phoneNumber = request.getParameter("phone_number");
        checkIfUsernameFree(username);
        user.setUsername(username);
        user.setName(fullName);
        user.setBirthDate(birthDate);
        user.setPhoneNumber(phoneNumber);
        if(user.getName() == null){
            throw new MissingValuableFieldsException();
        }
        userRepository.save(user);
        return new SuccessMessage("Personal info edited",HttpStatus.OK.value(),LocalDateTime.now());
    }
}
