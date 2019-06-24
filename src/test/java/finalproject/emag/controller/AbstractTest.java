package finalproject.emag.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import finalproject.emag.model.dto.ShowUserDTO;
import finalproject.emag.model.pojo.User;
import finalproject.emag.repository.ProductRepository;
import finalproject.emag.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

public abstract class AbstractTest {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ProductRepository productRepository;

    String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    ShowUserDTO getUserForSessionById(long id) {
        User admin = userRepository.findById(id).get();
        userRepository.save(admin);
        return new ShowUserDTO(admin.getId(), admin.getEmail(), admin.getName(),
                admin.getUsername(), admin.getPhoneNumber(), admin.getBirthDate(),
                admin.isSubscribed(), admin.isAdmin(), admin.getImageUrl());
    }

    ShowUserDTO getUserForSessionByIdAndSubscribed(long id, boolean subscribed) {
        User admin = userRepository.findById(id).get();
        admin.setSubscribed(subscribed);
        userRepository.save(admin);
        return new ShowUserDTO(admin.getId(), admin.getEmail(), admin.getName(),
                admin.getUsername(), admin.getPhoneNumber(), admin.getBirthDate(),
                admin.isSubscribed(), admin.isAdmin(), admin.getImageUrl());
    }

}
