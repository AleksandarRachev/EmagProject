package finalproject.emag.controller;

import finalproject.emag.model.dto.ReviewDTO;
import finalproject.emag.model.pojo.Review;
import finalproject.emag.model.pojo.ReviewId;
import finalproject.emag.repository.ReviewRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ReviewControllerTest extends AbstractTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Before
    public void setUp() {
        Review review = new Review();
        ReviewId id = new ReviewId();
        id.setUser(userRepository.findById(1L).get());
        id.setProduct(productRepository.findById(3L).get());
        review.setTitle("title");
        review.setGrade(5);
        review.setComment("Good item");
        review.setId(id);
        reviewRepository.save(review);
    }

    @Test
    public void addReviewSuccess() throws Exception {
        ReviewDTO review = new ReviewDTO();
        review.setTitle("title");
        review.setGrade(5);
        review.setComment("Good item");
        String reviewJson = mapToJson(review);
        mvc.perform(post("/products/reviews/{id}", 4)
                .sessionAttr("user", getUserForSessionById(1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Review added"));
    }

    @Test
    public void addReviewExists() throws Exception {
        ReviewDTO review = new ReviewDTO();
        review.setTitle("title");
        review.setGrade(5);
        review.setComment("Good item");
        String reviewJson = mapToJson(review);
        mvc.perform(post("/products/reviews/{id}", 3)
                .sessionAttr("user", getUserForSessionById(1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("You already have review on this product"));
    }

    @Test
    public void addReviewNotLogged() throws Exception {
        ReviewDTO review = new ReviewDTO();
        review.setTitle("title");
        review.setGrade(5);
        review.setComment("Good item");
        String reviewJson = mapToJson(review);
        mvc.perform(post("/products/reviews/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not logged."));
    }

    @Test
    public void addReviewMissingFields() throws Exception {
        ReviewDTO review = new ReviewDTO();
        review.setTitle("title");
        String reviewJson = mapToJson(review);
        mvc.perform(post("/products/reviews/{id}", 1)
                .sessionAttr("user", getUserForSessionById(1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("Missing valuable fields"));
    }

    @Test
    public void deleteReviewSuccess() throws Exception {
        mvc.perform(delete("/products/reviews/{id}",3)
                .sessionAttr("user",getUserForSessionById(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Review deleted"));
    }

    @Test
    public void deleteReviewMissing() throws Exception {
        mvc.perform(delete("/products/reviews/{id}",2)
                .sessionAttr("user",getUserForSessionById(1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("Review missing"));
    }

    @Test
    public void deleteReviewNotLogged() throws Exception {
        mvc.perform(delete("/products/reviews/{id}",3))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not logged."));
    }

    @Test
    public void editReviewSuccess() throws Exception {
        ReviewDTO review = new ReviewDTO();
        review.setTitle("title");
        review.setGrade(4);
        review.setComment("Decent item");
        String reviewJson = mapToJson(review);
        mvc.perform(put("/products/reviews/{id}", 3)
                .sessionAttr("user", getUserForSessionById(1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("Review edited"));
    }

    @Test
    public void editReviewMissing() throws Exception {
        ReviewDTO review = new ReviewDTO();
        review.setTitle("title");
        review.setGrade(4);
        review.setComment("Decent item");
        String reviewJson = mapToJson(review);
        mvc.perform(put("/products/reviews/{id}", 2)
                .sessionAttr("user", getUserForSessionById(1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg").value("Review missing"));
    }

    @Test
    public void editReviewNotLogged() throws Exception {
        ReviewDTO review = new ReviewDTO();
        review.setTitle("title");
        review.setGrade(4);
        review.setComment("Decent item");
        String reviewJson = mapToJson(review);
        mvc.perform(put("/products/reviews/{id}", 3)
                .contentType(MediaType.APPLICATION_JSON)
                .content(reviewJson)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.msg").value("You are not logged."));
    }
}
