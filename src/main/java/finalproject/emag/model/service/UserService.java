package finalproject.emag.model.service;

import finalproject.emag.model.dto.*;
import finalproject.emag.model.pojo.User;
import finalproject.emag.repository.UserRepository;
import finalproject.emag.util.Message;
import finalproject.emag.util.PasswordEncoder;
import finalproject.emag.util.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.List;

@Component
public class UserService {

    private static final String USER = "user";

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity register(RegisterUserDTO registerUser, HttpSession session) throws BaseException {
        User user = new User();
        registerValidation(registerUser.getEmail(), registerUser.getPassword(), registerUser.getPassword2(),
                registerUser.getFullName(), registerUser.getUsername());
        user.setEmail(registerUser.getEmail());
        user.setPassword(PasswordEncoder.hashPassword(registerUser.getPassword()));
        user.setName(registerUser.getFullName());
        user.setSubscribed(registerUser.isSubscribed());
        user.setUsername(registerUser.getUsername());
        user.setPhoneNumber(registerUser.getPhoneNumber());
        user.setBirthDate(registerUser.getBirthDate());
        userRepository.save(user);
        ShowUserDTO userSession = new ShowUserDTO(user.getId(), user.getEmail(), user.getName(), user.getUsername(),
                user.getPhoneNumber(), user.getBirthDate(), user.isSubscribed(), user.isAdmin(), user.getImageUrl());
        session.setAttribute(USER, userSession);
        session.setMaxInactiveInterval((60 * 60));
        return new ResponseEntity<>(new Message("Register successful"),HttpStatus.OK);
    }

    private void registerValidation(String email, String password, String password2, String fullName, String username)
            throws BaseException {
        if (email == null || password == null || password2 == null || fullName == null) {
            throw new MissingValuableFieldsException();
        }
        if (!password.matches(password2)) {
            throw new PasswordsNotMatchingException();
        }
        checkIfEmailFree(email);
        checkIfUsernameFree(username);
    }

    private void checkIfEmailFree(String email) throws EmailTakenException {
        int count = userRepository.findAllByEmail(email).size();
        if (count > 0 && email != null) {
            throw new EmailTakenException();
        }
    }

    private void checkIfUsernameFree(String username) throws UsernameTakenException {
        int count = userRepository.findAllByUsername(username).size();
        if (count > 0 && username != null) {
            throw new UsernameTakenException();
        }
    }

    public ShowUserDTO login(LoginUserDTO loginUser, HttpSession session) throws BaseException {
        if (loginUser.getEmail() == null || loginUser.getPassword() == null) {
            throw new MissingValuableFieldsException();
        }
        List<User> users = userRepository.findAllByEmail(loginUser.getEmail());
        User getUser = userRepository.findByEmail(loginUser.getEmail());
        if (users.size() < 1 || !BCrypt.checkpw(loginUser.getPassword(), getUser.getPassword())) {
            throw new WrongCredentialsException();
        }
        ShowUserDTO user = new ShowUserDTO(getUser.getId(), getUser.getEmail(), getUser.getName(),
                getUser.getUsername(), getUser.getPhoneNumber(), getUser.getBirthDate(),
                getUser.isSubscribed(), getUser.isAdmin(), getUser.getImageUrl());
        session.setAttribute(USER, user);
        session.setMaxInactiveInterval((60 * 60));
        return user;
    }

    public ResponseEntity subscribe(HttpSession session) throws AlreadySubscribedException {
        ShowUserDTO userSession = (ShowUserDTO) session.getAttribute(USER);
        User user = userRepository.findByEmail(userSession.getEmail());
        if (user.isSubscribed()) {
            throw new AlreadySubscribedException();
        }
        userSession.setSubscribed(true);
        user.setSubscribed(true);
        userRepository.save(user);
        return new ResponseEntity(new Message("You subscribed"), HttpStatus.OK);
    }

    public ResponseEntity unsubscribe(HttpSession session) throws NotSubscribedException {
        ShowUserDTO userSession = (ShowUserDTO) session.getAttribute(USER);
        User user = userRepository.findByEmail(userSession.getEmail());
        if (!user.isSubscribed()) {
            throw new NotSubscribedException();
        }
        userSession.setSubscribed(false);
        user.setSubscribed(false);
        userRepository.save(user);
        return new ResponseEntity(new Message("You unsubscribed"), HttpStatus.OK);
    }

    private void passEditFieldsCheck(EditPassDTO user) throws MissingValuableFieldsException {
        if (user.getCurrentPass() == null || user.getPassword() == null || user.getPassword2() == null) {
            throw new MissingValuableFieldsException();
        }
    }

    public ResponseEntity editPassword(EditPassDTO userEdit, HttpSession session) throws BaseException {
        ShowUserDTO userSession = (ShowUserDTO) session.getAttribute(USER);
        User user = userRepository.findById(userSession.getId()).get();
        passEditFieldsCheck(userEdit);
        if (!BCrypt.checkpw(userEdit.getCurrentPass(), user.getPassword())) {
            throw new WrongCredentialsException();
        }
        if (!userEdit.getPassword().matches(userEdit.getPassword2())) {
            throw new PasswordsNotMatchingException();
        }
        user.setPassword(PasswordEncoder.hashPassword(userEdit.getPassword()));
        userRepository.save(user);
        return new ResponseEntity(new Message("Password edited"), HttpStatus.OK);
    }

    private void emailEditFieldsCheck(EditEmailDTO user) throws MissingValuableFieldsException {
        if (user.getEmail() == null || user.getPassword() == null) {
            throw new MissingValuableFieldsException();
        }
    }

    public ResponseEntity editEmail(EditEmailDTO userEdit, HttpSession session) throws BaseException {
        ShowUserDTO userSession = (ShowUserDTO) session.getAttribute(USER);
        User user = userRepository.findById(userSession.getId()).get();
        emailEditFieldsCheck(userEdit);
        if (!BCrypt.checkpw(userEdit.getPassword(), user.getPassword())) {
            throw new WrongCredentialsException();
        }
        checkIfEmailFree(userEdit.getEmail());
        user.setEmail(userEdit.getEmail());
        userRepository.save(user);
        return new ResponseEntity(new Message("Email edited"), HttpStatus.OK);
    }

    public ResponseEntity editPersonalInfo(EditPersonalInfoDTO userEdit, HttpSession session) throws BaseException {
        ShowUserDTO userSession = (ShowUserDTO) session.getAttribute(USER);
        User user = userRepository.findById(userSession.getId()).get();
        checkIfUsernameFree(userEdit.getUsername());
        user.setUsername(userEdit.getUsername());
        user.setName(userEdit.getFullName());
        user.setBirthDate(userEdit.getBirthDate());
        user.setPhoneNumber(userEdit.getPhoneNumber());
        if (user.getName() == null) {
            throw new MissingValuableFieldsException();
        }
        userRepository.save(user);
        return new ResponseEntity(new Message("Personal info edited"), HttpStatus.OK);
    }
}
