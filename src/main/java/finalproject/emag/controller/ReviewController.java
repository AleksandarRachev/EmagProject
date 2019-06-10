package finalproject.emag.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import finalproject.emag.model.dto.ReviewAddDTO;
import finalproject.emag.model.pojo.User;
import finalproject.emag.model.service.ReviewService;
import finalproject.emag.util.SuccessMessage;
import finalproject.emag.util.exception.MissingValuableFieldsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/products/reviews",produces = "application/json")
public class ReviewController extends BaseController{

    private static final String USER = "user";

    @Autowired
    private ReviewService reviewService;

//    @Autowired
//    private ReviewDao dao;
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    private ReviewDto getReview(String input, long productId, HttpServletRequest request) throws Exception {
//        JsonNode jsonNode = this.objectMapper.readTree(input);
//        if(!jsonNode.has("title") || !jsonNode.has("comment") || !jsonNode.has("grade")){
//            throw new MissingValuableFieldsException();
//        }
//        validateLogin(request.getSession());
//        User user = (User)request.getSession().getAttribute(USER);
//        return new ReviewDto(user.getId(),productId,jsonNode.get("title").textValue(),
//                jsonNode.get("comment").textValue(),jsonNode.get("grade").intValue());
//    }
//
    @PostMapping(value = "/{id}")
    public SuccessMessage addReview(@RequestBody ReviewAddDTO review, @PathVariable("id")long productId,
                                    HttpSession session) throws Exception {
        validateLogin(session);
        return reviewService.addReview(review,productId,session);
    }
//
//    @DeleteMapping(value = "/{id}")
//    public String deleteReview(@PathVariable("id")long productId,HttpServletRequest request) throws Exception {
//        validateLogin(request.getSession());
//        User user = (User)request.getSession().getAttribute("user");
//        DeleteReviewDto review = new DeleteReviewDto(user.getId(),productId);
//        this.dao.deleteReview(review);
//        return "Review deleted";
//    }
//
//    @PutMapping(value = "/{id}")
//    public String editReview(@RequestBody String input, @PathVariable("id")long productId, HttpServletRequest request) throws Exception {
//        ReviewDto review = getReview(input,productId,request);
//        this.dao.editReview(review);
//        return "Review updated";
//    }
}