package finalproject.emag.service;

import finalproject.emag.model.dto.*;
import finalproject.emag.model.pojo.User;
import finalproject.emag.model.service.UserService;
import finalproject.emag.repository.UserRepository;
import finalproject.emag.util.Message;
import finalproject.emag.util.PasswordEncoder;
import finalproject.emag.util.exception.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpSession session;

    @InjectMocks
    private UserService userService;

    private User userShow;

    private ShowUserDTO userSession;

    private List<User> users;

    @Before
    public void setUp() {
        userShow = new User();
        userShow.setId(1);
        userShow.setEmail("test@abv.bg");
        userShow.setPassword(PasswordEncoder.hashPassword("123"));
        userShow.setAdmin(false);
        userShow.setName("Test");
        userShow.setUsername("test");
        userShow.setPhoneNumber("012345");
        userShow.setBirthDate(LocalDate.now());

        users = new ArrayList<>();
        users.add(userShow);

        userSession = new ShowUserDTO();
        userSession.setId(1);
        userSession.setEmail("test@abv.bg");
        userSession.setUsername("test");
        userSession.setPhoneNumber("123456");
        userSession.setSubscribed(true);
    }

    @Test
    public void register() throws BaseException {
        RegisterUserDTO registerUser = new RegisterUserDTO();
        registerUser.setEmail("test@abv.bg");
        registerUser.setPassword("123");
        registerUser.setFullName("Test");
        registerUser.setSubscribed(false);

        when(userRepository.findAllByEmail(registerUser.getEmail())).thenReturn(new ArrayList<>());
        when(userRepository.findAllByUsername(registerUser.getUsername())).thenReturn(new ArrayList<>());

        ResponseEntity response = new ResponseEntity(new Message("Register successful"), HttpStatus.OK);

        assertEquals(userService.register(registerUser, session).getStatusCode(), response.getStatusCode());
    }

    @Test(expected = EmailTakenException.class)
    public void registerEmailTaken() throws BaseException {
        RegisterUserDTO registerUser = new RegisterUserDTO();
        registerUser.setEmail("test@abv.bg");

        when(userRepository.findAllByEmail(registerUser.getEmail())).thenReturn(users);

        userService.register(registerUser, session);
    }

    @Test(expected = UsernameTakenException.class)
    public void registerUsernameTaken() throws BaseException {
        RegisterUserDTO registerUser = new RegisterUserDTO();
        registerUser.setUsername("test");

        when(userRepository.findAllByUsername(registerUser.getUsername())).thenReturn(users);

        userService.register(registerUser, session);
    }

    @Test
    public void login() throws BaseException {
        when(userRepository.findByEmail("test@abv.bg")).thenReturn(userShow);
        when(userRepository.findAllByEmail("test@abv.bg")).thenReturn(users);
        LoginUserDTO loginUser = new LoginUserDTO();
        loginUser.setEmail("test@abv.bg");
        loginUser.setPassword("123");
        ShowUserDTO userDTO = userService.login(loginUser, session);

        assertNotNull(userDTO);
        assertEquals(userDTO.getEmail(), userShow.getEmail());
    }

    @Test(expected = WrongCredentialsException.class)
    public void loginWrongCredentials() throws BaseException {
        when(userRepository.findByEmail("test@abv.bg")).thenReturn(userShow);
        when(userRepository.findAllByEmail("test@abv.bg")).thenReturn(users);
        LoginUserDTO loginUser = new LoginUserDTO();
        loginUser.setEmail("test@abv.bg");
        loginUser.setPassword("1234");
        userService.login(loginUser, session);
    }

    @Test(expected = MissingValuableFieldsException.class)
    public void loginMissingFields() throws BaseException {
        LoginUserDTO loginUser = new LoginUserDTO();
        loginUser.setEmail("test@abv.bg");
        userService.login(loginUser, session);
    }

    @Test
    public void subscribe() throws AlreadySubscribedException {
        when(session.getAttribute("user")).thenReturn(userSession);
        when(userRepository.findByEmail("test@abv.bg")).thenReturn(userShow);
        userService.subscribe(session);
        assertTrue(userSession.isSubscribed());
    }


    @Test(expected = AlreadySubscribedException.class)
    public void subscribeSubscribed() throws AlreadySubscribedException {
        userShow.setSubscribed(true);
        when(session.getAttribute("user")).thenReturn(userSession);
        when(userRepository.findByEmail("test@abv.bg")).thenReturn(userShow);
        userService.subscribe(session);
    }

    @Test
    public void unsubscribe() throws NotSubscribedException {
        userShow.setSubscribed(true);
        when(session.getAttribute("user")).thenReturn(userSession);
        when(userRepository.findByEmail("test@abv.bg")).thenReturn(userShow);
        userService.unsubscribe(session);
        assertFalse(userShow.isSubscribed());
    }

    @Test(expected = NotSubscribedException.class)
    public void unsubscribeUnsubscribed() throws NotSubscribedException {
        when(session.getAttribute("user")).thenReturn(userSession);
        when(userRepository.findByEmail("test@abv.bg")).thenReturn(userShow);
        userService.unsubscribe(session);
    }

    @Test
    public void editPass() throws BaseException {
        EditPassDTO editPass = new EditPassDTO();
        editPass.setCurrentPass("123");
        editPass.setPassword("1234");
        when(session.getAttribute("user")).thenReturn(userSession);
        when(userRepository.findById(userSession.getId())).thenReturn(java.util.Optional.ofNullable(userShow));
        userService.editPassword(editPass, session);
        assertTrue(BCrypt.checkpw(editPass.getPassword(), userShow.getPassword()));
    }

    @Test(expected = WrongCredentialsException.class)
    public void editPassWrongCredentials() throws BaseException {
        EditPassDTO editPass = new EditPassDTO();
        editPass.setCurrentPass("1234");
        editPass.setPassword("123");
        when(session.getAttribute("user")).thenReturn(userSession);
        when(userRepository.findById(userSession.getId())).thenReturn(java.util.Optional.ofNullable(userShow));
        userService.editPassword(editPass, session);
    }

    @Test
    public void editEmail() throws BaseException {
        EditEmailDTO editEmail = new EditEmailDTO();
        editEmail.setEmail("testEmailEdit@abv.bg");
        editEmail.setPassword("123");
        when(session.getAttribute("user")).thenReturn(userSession);
        when(userRepository.findById(userSession.getId())).thenReturn(java.util.Optional.ofNullable(userShow));
        userService.editEmail(editEmail, session);
        assertEquals(userShow.getEmail(), editEmail.getEmail());
    }

    @Test(expected = WrongCredentialsException.class)
    public void editEmailWrongCredentials() throws BaseException {
        EditEmailDTO editEmail = new EditEmailDTO();
        editEmail.setEmail("testEmailEdit@abv.bg");
        editEmail.setPassword("1234");
        when(session.getAttribute("user")).thenReturn(userSession);
        when(userRepository.findById(userSession.getId())).thenReturn(java.util.Optional.ofNullable(userShow));
        userService.editEmail(editEmail, session);
    }

    @Test(expected = EmailTakenException.class)
    public void editEmailEmailExists() throws BaseException {
        EditEmailDTO editEmail = new EditEmailDTO();
        editEmail.setEmail("test@abv.bg");
        editEmail.setPassword("123");
        when(session.getAttribute("user")).thenReturn(userSession);
        when(userRepository.findById(userSession.getId())).thenReturn(java.util.Optional.ofNullable(userShow));
        when(userRepository.findAllByEmail(editEmail.getEmail())).thenReturn(users);
        userService.editEmail(editEmail, session);
    }

    @Test
    public void editPersonalInfo() throws BaseException {
        EditPersonalInfoDTO editInfo = new EditPersonalInfoDTO();
        editInfo.setUsername("test");
        when(session.getAttribute("user")).thenReturn(userSession);
        when(userRepository.findById(userSession.getId())).thenReturn(java.util.Optional.ofNullable(userShow));
        userService.editPersonalInfo(editInfo, session);
        assertEquals(editInfo.getUsername(), userShow.getUsername());
    }

    @Test(expected = UsernameTakenException.class)
    public void editPersonalInfoUsernameTaken() throws BaseException {
        EditPersonalInfoDTO editInfo = new EditPersonalInfoDTO();
        editInfo.setUsername("test");
        when(userRepository.findAllByUsername(editInfo.getUsername())).thenReturn(users);
        when(session.getAttribute("user")).thenReturn(userSession);
        when(userRepository.findById(userSession.getId())).thenReturn(java.util.Optional.ofNullable(userShow));
        userService.editPersonalInfo(editInfo, session);
    }

}
