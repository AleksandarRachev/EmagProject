package finalproject.emag.model.service;

import finalproject.emag.model.dto.ReviewAddDTO;
import finalproject.emag.model.dto.ShowUserDTO;
import finalproject.emag.model.pojo.Product;
import finalproject.emag.model.pojo.Review;
import finalproject.emag.model.pojo.ReviewId;
import finalproject.emag.model.pojo.User;
import finalproject.emag.repositories.ReviewRepository;
import finalproject.emag.repositories.UserRepository;
import finalproject.emag.util.SuccessMessage;
import finalproject.emag.util.exception.BaseException;
import finalproject.emag.util.exception.InvalidGradeEception;
import finalproject.emag.util.exception.MissingValuableFieldsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

@Component
public class ReviewService extends ProductService{

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    private void addReviewFieldsCheck(ReviewAddDTO review) throws BaseException {
        if(review.getTitle() == null || review.getComment() == null || review.getGrade() == null){
            throw new MissingValuableFieldsException();
        }
        if(review.getGrade() < 0 || review.getGrade() > 6){
            throw new InvalidGradeEception();
        }
    }
    public SuccessMessage addReview(ReviewAddDTO reviewView, long productId, HttpSession session) throws BaseException {
        addReviewFieldsCheck(reviewView);
        Product product = getProduct(productId);
        ShowUserDTO userSession = (ShowUserDTO) session.getAttribute("user");
        User user = userRepository.findById(userSession.getId()).get();
        Review review = new Review();
        ReviewId id = new ReviewId();
        id.setProduct(product);
        id.setUser(user);
        review.setId(id);
        review.setTitle(reviewView.getTitle());
        review.setComment(reviewView.getComment());
        review.setGrade(reviewView.getGrade());
        reviewRepository.save(review);
        return new SuccessMessage("Review added", HttpStatus.OK.value(), LocalDateTime.now());
    }

}
