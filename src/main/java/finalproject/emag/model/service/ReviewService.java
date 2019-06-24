package finalproject.emag.model.service;

import finalproject.emag.model.dto.ReviewDTO;
import finalproject.emag.model.dto.ShowUserDTO;
import finalproject.emag.model.pojo.Product;
import finalproject.emag.model.pojo.Review;
import finalproject.emag.model.pojo.ReviewId;
import finalproject.emag.model.pojo.User;
import finalproject.emag.repository.ReviewRepository;
import finalproject.emag.repository.UserRepository;
import finalproject.emag.util.Message;
import finalproject.emag.util.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Component
public class ReviewService extends ProductService {

    private static final String USER = "user";

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    private void addReviewFieldsCheck(ReviewDTO review) throws BaseException {
        if (review.getTitle() == null || review.getComment() == null || review.getGrade() == null) {
            throw new MissingValuableFieldsException();
        }
        if (review.getGrade() < 0 || review.getGrade() > 6) {
            throw new InvalidGradeEception();
        }
    }

    private void checkIfExists(ReviewId id) throws ReviewExistsException {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isPresent()) {
            throw new ReviewExistsException();
        }
    }

    public ResponseEntity addReview(ReviewDTO reviewView, long productId, HttpSession session) throws BaseException {
        addReviewFieldsCheck(reviewView);
        Product product = getProduct(productId);
        ShowUserDTO userSession = (ShowUserDTO) session.getAttribute(USER);
        User user = userRepository.findById(userSession.getId()).get();
        Review review = new Review();
        ReviewId id = new ReviewId();
        id.setProduct(product);
        id.setUser(user);
        checkIfExists(id);
        review.setId(id);
        review.setTitle(reviewView.getTitle());
        review.setComment(reviewView.getComment());
        review.setGrade(reviewView.getGrade());
        reviewRepository.save(review);
        return new ResponseEntity(new Message("Review added"), HttpStatus.OK);
    }

    private Review getReview(ReviewId id) throws ReviewMissingException {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isPresent()) {
            return review.get();
        }
        throw new ReviewMissingException();
    }

    public ResponseEntity deleteReview(long productId, HttpSession session) throws BaseException {
        ShowUserDTO userSession = (ShowUserDTO) session.getAttribute(USER);
        User user = userRepository.findById(userSession.getId()).get();
        Product product = getProduct(productId);
        ReviewId id = new ReviewId();
        id.setUser(user);
        id.setProduct(product);
        Review review = getReview(id);
        reviewRepository.delete(review);
        return new ResponseEntity(new Message("Review deleted"), HttpStatus.OK);
    }

    private void fieldsCheck(Review review) throws MissingValuableFieldsException {
        if (review.getTitle() == null || review.getComment() == null || review.getGrade() == null) {
            throw new MissingValuableFieldsException();
        }
    }

    public ResponseEntity editReview(ReviewDTO reviewEdit, long productId, HttpSession session) throws BaseException {
        ShowUserDTO userSession = (ShowUserDTO) session.getAttribute(USER);
        User user = userRepository.findById(userSession.getId()).get();
        Product product = getProduct(productId);
        ReviewId id = new ReviewId();
        id.setUser(user);
        id.setProduct(product);
        Review review = getReview(id);
        review.setTitle(reviewEdit.getTitle() == null ? review.getTitle() : reviewEdit.getTitle());
        review.setComment(reviewEdit.getComment() == null ? review.getComment() : reviewEdit.getComment());
        review.setGrade(reviewEdit.getGrade() == null ? review.getGrade() : reviewEdit.getGrade());
        fieldsCheck(review);
        reviewRepository.save(review);
        return new ResponseEntity(new Message("Review edited"), HttpStatus.OK);
    }

}
