package finalproject.emag.model.services;

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

    private static final String EDIT_SUCCESSFUL = "Edit successful";
    private static final String USER = "user";

    @Autowired
    private UserRepository userRepository;

    public SuccessMessage registerUser(HttpServletRequest request, HttpSession session) throws BaseException {
        User user = new User();
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String password2 = request.getParameter("password2");
        String fullName = request.getParameter("full_name");
        Boolean subscribed = Boolean.valueOf(request.getParameter("subscribed"));
        if(email == null || password == null || password2 == null || fullName == null){
            throw new MissingValuableFieldsException();
        }
        if (!password.matches(password2)){
            throw new PasswordsNotMatchingException();
        }
        if(!checkIfEmailFree(email)){
            throw new EmailTakenException();
        }
        String username = request.getParameter("username");
        if(!checkIfUsernameFree(username)){
            throw new UsernameTakenException();
        }
        String phoneNumber = request.getParameter("phone_number");
        String date = request.getParameter("birth_date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
        LocalDate birthDate = LocalDate.parse(date,formatter);
        user.setEmail(email);
        user.setPassword(PasswordEncoder.hashPassword(password));
        user.setName(fullName);
        user.setSubscribed(subscribed);
        user.setUsername(username);
        user.setPhoneNumber(phoneNumber);
        user.setBirthDate(birthDate);
        userRepository.save(user);
        session.setAttribute(USER,user);
        session.setMaxInactiveInterval((60*60));
        return new SuccessMessage("Register successful", HttpStatus.OK.value(), LocalDateTime.now());
    }

    private boolean checkIfEmailFree(String email){
        int count = userRepository.findAllByEmail(email).size();
        return count < 1;
    }

    private boolean checkIfUsernameFree(String username){
        int count = userRepository.findAllByUsername(username).size();
        return count < 1;
    }

    public ShowUserDto loginUser(HttpServletRequest request,HttpSession session) throws BaseException {
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



}
