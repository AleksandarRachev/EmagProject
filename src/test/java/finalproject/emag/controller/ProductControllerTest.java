package finalproject.emag.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import finalproject.emag.model.dto.FilterParamsDTO;
import finalproject.emag.model.dto.ProductAddDTO;
import finalproject.emag.model.dto.ShowUserDTO;
import finalproject.emag.model.pojo.User;
import finalproject.emag.repositories.UserRepository;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    private ShowUserDTO getUserForSessionById(long id) {
        User admin = userRepository.findById(id).get();
        return new ShowUserDTO(admin.getId(), admin.getEmail(), admin.getName(),
                admin.getUsername(), admin.getPhoneNumber(), admin.getBirthDate(),
                admin.isSubscribed(), admin.isAdmin(), admin.getImageUrl());
    }

    private String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    public void getProductById() throws Exception {
        mvc.perform(get("/products/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Asus PC"));
    }

    @Test
    public void getAllProducts() throws Exception {
        mvc.perform(get("/products").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(4)));
    }

    @Test
    public void addProductSuccess() throws Exception {
        ProductAddDTO product = new ProductAddDTO();
        product.setName("HP");
        product.setCategoryId(1L);
        product.setPrice(5000.0);
        product.setQuantity(6);
        String productJson = mapToJson(product);
        mvc.perform(post("/products")
                .sessionAttr("user", getUserForSessionById(2))
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Product added"));
    }

    @Test
    public void addProductNotAdmin() throws Exception {
        ProductAddDTO product = new ProductAddDTO();
        product.setName("HP");
        product.setCategoryId(1L);
        product.setPrice(5000.0);
        product.setQuantity(6);
        String productJson = mapToJson(product);
        mvc.perform(post("/products")
                .sessionAttr("user", getUserForSessionById(1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not admin."));
    }

    @Test
    public void addProductNotLogged() throws Exception {
        ProductAddDTO product = new ProductAddDTO();
        product.setName("HP");
        product.setCategoryId(1L);
        product.setPrice(5000.0);
        product.setQuantity(6);
        String productJson = mapToJson(product);
        mvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not logged."));
    }

    @Test
    public void addProductMissingFields() throws Exception {
        ProductAddDTO product = new ProductAddDTO();
        product.setName("HP");
        product.setCategoryId(1L);
        String productJson = mapToJson(product);
        mvc.perform(post("/products")
                .sessionAttr("user", getUserForSessionById(2))
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("Missing valuable fields"));
    }

    @Test
    public void getAllProductsFilteredDesc() throws Exception {
        FilterParamsDTO filter = new FilterParamsDTO(1000.0,2000.0,"DESC");
        String filterJson = mapToJson(filter);
        mvc.perform(get("/products/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(filterJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",Matchers.hasSize(3)))
                .andExpect(jsonPath("$.[0].name").value("Asus PC"));
    }

    @Test
    public void getAllProductsFilteredAsc() throws Exception {
        FilterParamsDTO filter = new FilterParamsDTO(1000.0,2000.0,"ASC");
        String filterJson = mapToJson(filter);
        mvc.perform(get("/products/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(filterJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",Matchers.hasSize(2)))
                .andExpect(jsonPath("$.[0].name").value("Samsung S10"));
    }

    @Test
    public void getAllProductsByCategoryFiltered() throws Exception {
        FilterParamsDTO filter = new FilterParamsDTO(1000.0,2000.0,"ASC");
        String filterJson = mapToJson(filter);
        mvc.perform(get("/products/category/{id}/filter",4)
                .contentType(MediaType.APPLICATION_JSON)
                .content(filterJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",Matchers.hasSize(1)))
                .andExpect(jsonPath("$.[0].name").value("Whirlpool"));
    }

    @Test
    public void getAllProductsByName() throws Exception {
        mvc.perform(get("/products/search/{name}","Asus")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",Matchers.hasSize(1)))
                .andExpect(jsonPath("$.[0].price").value(2000.0));
    }

    @Test
    public void changeQuantityTestSuccess() throws Exception{
        mvc.perform(put("/products/{id}/quantity/{quantity}",1,50)
                .sessionAttr("user",getUserForSessionById(2))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Quantity changed"));
    }

    @Test
    public void changeQuantityTestInvalidQuality() throws Exception{
        mvc.perform(put("/products/{id}/quantity/{quantity}",1,-1)
                .sessionAttr("user",getUserForSessionById(2))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("You can't change the quantity to this value!"));
    }

    @Test
    public void changeQuantityTestNotLogged() throws Exception{
        mvc.perform(put("/products/{id}/quantity/{quantity}",1,50)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not logged."));
    }

    @Test
    public void changeQuantityTestNotAdmin() throws Exception{
        mvc.perform(put("/products/{id}/quantity/{quantity}",1,50)
                .sessionAttr("user",getUserForSessionById(1))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not admin."));
    }

    @Test
    public void changeQuantityTestMissingProduct() throws Exception{
        mvc.perform(put("/products/{id}/quantity/{quantity}",10,50)
                .sessionAttr("user",getUserForSessionById(2))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("Product not found"));
    }

    @Test
    public void deleteProductSuccess() throws Exception {
        mvc.perform(delete("/products/{id}", 1)
                .sessionAttr("user", getUserForSessionById(2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Product deleted"));
    }

    @Test
    public void deleteProductNotLogged() throws Exception {
        mvc.perform(delete("/products/{id}", 1))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not logged."));
    }

    @Test
    public void deleteProductNotAdmin() throws Exception {
        mvc.perform(delete("/products/{id}", 1)
                .sessionAttr("user", getUserForSessionById(1)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not admin."));
    }
}
