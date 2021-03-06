package finalproject.emag.controller;

import finalproject.emag.model.dto.PromotionProductDTO;
import finalproject.emag.model.service.PromotionService;
import finalproject.emag.util.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/products/promotions")
public class PromotionsController extends BaseController {

    @Autowired
    private PromotionService promotionService;

    @PostMapping(value = "/{id}")
    public ResponseEntity addPromotion(@PathVariable("id") Long productId, @RequestBody @Valid PromotionProductDTO promotion,
                                       HttpSession session) throws BaseException {
        validateLoginAdmin(session);
        return promotionService.addPromotion(productId, promotion);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deletePromotion(@PathVariable("id") Long productId, HttpSession session) throws BaseException {
        validateLoginAdmin(session);
        return promotionService.deletePromotion(productId);
    }

}
