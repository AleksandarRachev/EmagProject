package finalproject.emag.controller;

import finalproject.emag.model.dto.CartProductDTO;
import finalproject.emag.model.dto.FilterParamsDTO;
import finalproject.emag.model.dto.ProductAddDTO;
import finalproject.emag.model.pojo.Product;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest extends AbstractTest {

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
        FilterParamsDTO filter = new FilterParamsDTO(1000.0, 2000.0, "DESC");
        String filterJson = mapToJson(filter);
        mvc.perform(get("/products/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(filterJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$.[0].name").value("Asus PC"));
    }

    @Test
    public void getAllProductsFilteredAsc() throws Exception {
        FilterParamsDTO filter = new FilterParamsDTO(1000.0, 2000.0, "ASC");
        String filterJson = mapToJson(filter);
        mvc.perform(get("/products/filter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(filterJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.[0].name").value("Samsung S10"));
    }

    @Test
    public void getAllProductsByCategoryFiltered() throws Exception {
        FilterParamsDTO filter = new FilterParamsDTO(1000.0, 2000.0, "ASC");
        String filterJson = mapToJson(filter);
        mvc.perform(get("/products/category/{id}/filter", 4)
                .contentType(MediaType.APPLICATION_JSON)
                .content(filterJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.[0].name").value("Whirlpool"));
    }

    @Test
    public void getAllProductsByName() throws Exception {
        mvc.perform(get("/products/search/{name}", "Asus")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.[0].price").value(2000.0));
    }

    @Test
    public void changeQuantityTestSuccess() throws Exception {
        mvc.perform(put("/products/{id}/quantity/{quantity}", 1, 50)
                .sessionAttr("user", getUserForSessionById(2))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Quantity changed"));
    }

    @Test
    public void changeQuantityTestInvalidQuality() throws Exception {
        mvc.perform(put("/products/{id}/quantity/{quantity}", 1, -1)
                .sessionAttr("user", getUserForSessionById(2))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("You can't change the quantity to this value!"));
    }

    @Test
    public void changeQuantityTestNotLogged() throws Exception {
        mvc.perform(put("/products/{id}/quantity/{quantity}", 1, 50)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not logged."));
    }

    @Test
    public void changeQuantityTestNotAdmin() throws Exception {
        mvc.perform(put("/products/{id}/quantity/{quantity}", 1, 50)
                .sessionAttr("user", getUserForSessionById(1))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not admin."));
    }

    @Test
    public void changeQuantityTestMissingProduct() throws Exception {
        mvc.perform(put("/products/{id}/quantity/{quantity}", 10, 50)
                .sessionAttr("user", getUserForSessionById(2))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("Product not found"));
    }

    @Test
    public void deleteProductSuccess() throws Exception {
        mvc.perform(delete("/products/{id}", 1)
                .sessionAttr("user", getUserForSessionById(2))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Product deleted"));
    }

    @Test
    public void deleteProductNotLogged() throws Exception {
        mvc.perform(delete("/products/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not logged."));
    }

    @Test
    public void deleteProductNotAdmin() throws Exception {
        mvc.perform(delete("/products/{id}", 1)
                .sessionAttr("user", getUserForSessionById(1))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not admin."));
    }

    @Test
    public void putItemInCartSuccess() throws Exception {
        mvc.perform(post("/products/{id}/add", 1)
                .sessionAttr("user", getUserForSessionById(1))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Product added to cart"));
    }

    @Test
    public void putItemInCartNotLogged() throws Exception {
        mvc.perform(post("/products/{id}/add", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not logged."));
    }

    @Test
    public void putItemInCartProductMissing() throws Exception {
        mvc.perform(post("/products/{id}/add", 10)
                .sessionAttr("user", getUserForSessionById(1))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("Product not found"));
    }

    @Test
    public void viewCartSuccess() throws Exception {
        Product productOG = productRepository.findById(4L).get();
        CartProductDTO product = new CartProductDTO(productOG.getId(), productOG.getCategory()
                , productOG.getName(), productOG.getPrice());
        HashMap<CartProductDTO, Integer> cart = new HashMap<>();
        cart.put(product, 1);
        mvc.perform(get("/products/view/cart").accept(MediaType.APPLICATION_JSON)
                .sessionAttr("user", getUserForSessionById(1))
                .sessionAttr("cart", cart))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void viewCartEmpty() throws Exception {
        mvc.perform(get("/products/view/cart").accept(MediaType.APPLICATION_JSON)
                .sessionAttr("user", getUserForSessionById(1)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("Your cart is empty."));
    }

    @Test
    public void viewCartNotLogged() throws Exception {
        mvc.perform(get("/products/view/cart").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not logged."));
    }

    @Test
    public void makeOrderSuccess() throws Exception {
        Product productOG = productRepository.findById(4L).get();
        CartProductDTO product = new CartProductDTO(productOG.getId(), productOG.getCategory()
                , productOG.getName(), productOG.getPrice());
        HashMap<CartProductDTO, Integer> cart = new HashMap<>();
        cart.put(product, 1);
        mvc.perform(post("/products/order").accept(MediaType.APPLICATION_JSON)
                .sessionAttr("user", getUserForSessionById(1))
                .sessionAttr("cart", cart))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Order made"));
    }

    @Test
    public void makeOrderNotLogged() throws Exception {
        mvc.perform(post("/products/order").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not logged."));
    }

    @Test
    public void makeOrderCartEmpty() throws Exception {
        mvc.perform(post("/products/order").accept(MediaType.APPLICATION_JSON)
                .sessionAttr("user", getUserForSessionById(1)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("Your cart is empty."));
    }
}
