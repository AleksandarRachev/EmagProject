package finalproject.emag.controller;

import finalproject.emag.model.dto.ReviewDTO;
import finalproject.emag.model.service.ReviewService;
import finalproject.emag.util.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/products/reviews", produces = "application/json")
public class ReviewController extends BaseController {


    @Autowired
    private ReviewService reviewService;

    @PostMapping(value = "/{id}")
    public ResponseEntity addReview(@RequestBody @Valid ReviewDTO review, @PathVariable("id") long productId,
                                    HttpSession session) throws BaseException {
        validateLogin(session);
        return reviewService.addReview(review, productId, session);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteReview(@PathVariable("id") long productId, HttpSession session) throws BaseException {
        validateLogin(session);
        return reviewService.deleteReview(productId, session);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity editReview(@RequestBody @Valid ReviewDTO editReview, @PathVariable("id") long productId,
                                     HttpSession session) throws Exception {
        validateLogin(session);
        return reviewService.editReview(editReview, productId, session);
    }
}