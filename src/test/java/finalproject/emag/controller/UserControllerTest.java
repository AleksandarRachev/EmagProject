package finalproject.emag.controller;

import finalproject.emag.model.dto.*;
import finalproject.emag.model.pojo.User;
import finalproject.emag.util.PasswordEncoder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest extends AbstractTest {

    @Before
    public void addTestUsers() {
        User registerUser = new User();
        registerUser.setEmail("test@abv.bg");
        registerUser.setPassword(PasswordEncoder.hashPassword("123"));
        registerUser.setName("Test");
        registerUser.setSubscribed(false);
        userRepository.save(registerUser);

        User registerUser1 = new User();
        registerUser1.setEmail("test1@abv.bg");
        registerUser1.setPassword(PasswordEncoder.hashPassword("123"));
        registerUser1.setName("Test1");
        registerUser1.setSubscribed(true);
        userRepository.save(registerUser1);
    }

    @Test
    public void registerSuccess() throws Exception {
        RegisterUserDTO registerUser = new RegisterUserDTO();
        registerUser.setEmail("misho@abv.bg");
        registerUser.setPassword("123");
        registerUser.setFullName("Mihail Mihailov");
        registerUser.setSubscribed(false);
        String jsonUser = mapToJson(registerUser);
        mvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Register successful"));
    }

    @Test
    public void registerMissingFields() throws Exception {
        RegisterUserDTO registerUser = new RegisterUserDTO();
        registerUser.setEmail("misho@abv.bg");
        registerUser.setPassword("123");
        String jsonUser = mapToJson(registerUser);
        mvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void registerEmailTaken() throws Exception {
        User user = userRepository.findById(1L).get();
        RegisterUserDTO registerUser = new RegisterUserDTO();
        registerUser.setEmail(user.getEmail());
        registerUser.setPassword("123");
        registerUser.setFullName("Mihail Mihailov");
        registerUser.setSubscribed(false);
        String jsonUser = mapToJson(registerUser);
        mvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("Email is taken"));
    }

    @Test
    public void registerUsernameTaken() throws Exception {
        User user = userRepository.findById(1L).get();
        RegisterUserDTO registerUser = new RegisterUserDTO();
        registerUser.setEmail("pesho@abv.bg");
        registerUser.setPassword("123");
        registerUser.setFullName("Mihail Mihailov");
        registerUser.setSubscribed(false);
        registerUser.setUsername(user.getUsername());
        String jsonUser = mapToJson(registerUser);
        mvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("Username is taken"));
    }

    @Test
    public void loginSuccess() throws Exception {
        User user = userRepository.findById(2L).get();
        LoginUserDTO loginUser = new LoginUserDTO();
        loginUser.setEmail(user.getEmail());
        loginUser.setPassword("admin");
        String jsonUser = mapToJson(loginUser);
        mvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    public void loginWrongCredentials() throws Exception {
        LoginUserDTO loginUser = new LoginUserDTO();
        loginUser.setEmail("gencho@abv.bg");
        loginUser.setPassword("123");
        String jsonUser = mapToJson(loginUser);
        mvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("Wrong credentials"));
    }

    @Test
    public void loginMissingFields() throws Exception {
        LoginUserDTO loginUser = new LoginUserDTO();
        loginUser.setEmail("gencho@abv.bg");
        String jsonUser = mapToJson(loginUser);
        mvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void logoutNotLogged() throws Exception {
        mvc.perform(post("/users/logout")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not logged."));
    }

    @Test
    public void logoutSuccess() throws Exception {
        mvc.perform(post("/users/logout")
                .accept(MediaType.APPLICATION_JSON)
                .sessionAttr("user", getUserForSessionByIdAndSubscribed(1, false)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("You logged out"));
    }

    @Test
    public void subscribeSuccess() throws Exception {
        mvc.perform(put("/users/subscribe")
                .accept(MediaType.APPLICATION_JSON)
                .sessionAttr("user", getUserForSessionByIdAndSubscribed(1, false)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("You subscribed"));
    }

    @Test
    public void subscribeAlreadySubscribed() throws Exception {
        mvc.perform(put("/users/subscribe")
                .accept(MediaType.APPLICATION_JSON)
                .sessionAttr("user", getUserForSessionByIdAndSubscribed(1, true)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("You are already subscribed"));
    }

    @Test
    public void subscribeNotLogged() throws Exception {
        mvc.perform(put("/users/subscribe")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not logged."));
    }

    @Test
    public void unsubscribeSuccess() throws Exception {
        mvc.perform(put("/users/unsubscribe")
                .accept(MediaType.APPLICATION_JSON)
                .sessionAttr("user", getUserForSessionByIdAndSubscribed(1, true)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("You unsubscribed"));
    }

    @Test
    public void unsubscribeAlreadySubscribed() throws Exception {
        mvc.perform(put("/users/unsubscribe")
                .accept(MediaType.APPLICATION_JSON)
                .sessionAttr("user", getUserForSessionByIdAndSubscribed(1, false)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("You are not subscribed"));
    }

    @Test
    public void unsubscribeNotLogged() throws Exception {
        mvc.perform(put("/users/unsubscribe")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not logged."));
    }

    @Test
    public void editPassSuccess() throws Exception {
        EditPassDTO editPass = new EditPassDTO();
        editPass.setCurrentPass("123");
        editPass.setPassword("1234");
        String editJson = mapToJson(editPass);
        mvc.perform(put("/users/edit-pass")
                .sessionAttr("user", getUserForSessionByIdAndSubscribed(1, false))
                .contentType(MediaType.APPLICATION_JSON)
                .content(editJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Password edited"));
    }

    @Test
    public void editPassWrongCredentials() throws Exception {
        EditPassDTO editPass = new EditPassDTO();
        editPass.setCurrentPass("1234");
        editPass.setPassword("1234");
        String editJson = mapToJson(editPass);
        mvc.perform(put("/users/edit-pass")
                .sessionAttr("user", getUserForSessionByIdAndSubscribed(1, false))
                .contentType(MediaType.APPLICATION_JSON)
                .content(editJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void editPassNotLogged() throws Exception {
        EditPassDTO editPass = new EditPassDTO();
        editPass.setCurrentPass("123");
        editPass.setPassword("1234");
        String editJson = mapToJson(editPass);
        mvc.perform(put("/users/edit-pass")
                .contentType(MediaType.APPLICATION_JSON)
                .content(editJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not logged."));
    }

    @Test
    public void editEmailSuccess() throws Exception {
        EditEmailDTO editEmail = new EditEmailDTO();
        editEmail.setEmail("testing@abv.bg");
        editEmail.setPassword("123");
        String editJson = mapToJson(editEmail);
        mvc.perform(put("/users/edit-email")
                .sessionAttr("user", getUserForSessionByIdAndSubscribed(3, false))
                .contentType(MediaType.APPLICATION_JSON)
                .content(editJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Email edited"));
    }

    @Test
    public void editEmailTaken() throws Exception {
        User user = userRepository.findById(1L).get();
        EditEmailDTO editEmail = new EditEmailDTO();
        editEmail.setEmail(user.getEmail());
        editEmail.setPassword("123");
        String editJson = mapToJson(editEmail);
        mvc.perform(put("/users/edit-email")
                .sessionAttr("user", getUserForSessionByIdAndSubscribed(3, false))
                .contentType(MediaType.APPLICATION_JSON)
                .content(editJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("Email is taken"));
    }

    @Test
    public void editEmailNotLogged() throws Exception {
        EditEmailDTO editEmail = new EditEmailDTO();
        editEmail.setEmail("testing@abv.bg");
        editEmail.setPassword("123");
        String editJson = mapToJson(editEmail);
        mvc.perform(put("/users/edit-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(editJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not logged."));
    }

    @Test
    public void editPersonalInfoSuccess() throws Exception {
        EditPersonalInfoDTO editInfo = new EditPersonalInfoDTO();
        editInfo.setUsername("testingUsername");
        editInfo.setPhoneNumber("045654");
        editInfo.setFullName("Test");
        String editJson = mapToJson(editInfo);
        mvc.perform(put("/users/edit-personal-info")
                .sessionAttr("user", getUserForSessionByIdAndSubscribed(4, false))
                .contentType(MediaType.APPLICATION_JSON)
                .content(editJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Personal info edited"));
    }

    @Test
    public void editPersonalInfoNotLogged() throws Exception {
        EditPersonalInfoDTO editInfo = new EditPersonalInfoDTO();
        editInfo.setUsername("testingUsername1");
        editInfo.setPhoneNumber("045654");
        editInfo.setFullName("Test");
        String editJson = mapToJson(editInfo);
        mvc.perform(put("/users/edit-personal-info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(editJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not logged."));
    }

    @Test
    public void editPersonalInfoMissingFields() throws Exception {
        EditPersonalInfoDTO editInfo = new EditPersonalInfoDTO();
        editInfo.setUsername("testingUsername2");
        editInfo.setPhoneNumber("045654");
        String editJson = mapToJson(editInfo);
        mvc.perform(put("/users/edit-personal-info")
                .sessionAttr("user", getUserForSessionByIdAndSubscribed(4, false))
                .contentType(MediaType.APPLICATION_JSON)
                .content(editJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void editPersonalInfoUsernameTaken() throws Exception {
        EditPersonalInfoDTO editInfo = new EditPersonalInfoDTO();
        editInfo.setUsername("admin");
        editInfo.setPhoneNumber("045654");
        editInfo.setFullName("Test");
        String editJson = mapToJson(editInfo);
        mvc.perform(put("/users/edit-personal-info")
                .sessionAttr("user", getUserForSessionByIdAndSubscribed(4, false))
                .contentType(MediaType.APPLICATION_JSON)
                .content(editJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("Username is taken"));
    }

}
