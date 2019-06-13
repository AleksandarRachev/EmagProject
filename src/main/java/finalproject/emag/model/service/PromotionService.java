package finalproject.emag.model.service;

import finalproject.emag.model.dto.PromotionProductDTO;
import finalproject.emag.model.pojo.Product;
import finalproject.emag.model.pojo.Promotion;
import finalproject.emag.repositories.PromotionRepository;
import finalproject.emag.util.SuccessMessage;
import finalproject.emag.util.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Component
public class PromotionService extends ProductService{

    private void promotionValidationFieldsCheck(Product product,PromotionProductDTO promotion) throws BaseException{
        if(promotion.getNewPrice() == null || promotion.getEndDate() == null || promotion.getStartDate() == null){
            throw new MissingValuableFieldsException();
        }
        if(product.getPrice() < promotion.getNewPrice()){
            throw new InvalidPromotionException();
        }
        if(promotion.getStartDate().isAfter(promotion.getEndDate())){
            throw new InvalidDatesException();
        }
    }

    @Transactional
    public SuccessMessage addPromotion(long productId, PromotionProductDTO promotionValues) throws BaseException {
        Product product = getProduct(productId);
        promotionValidationFieldsCheck(product,promotionValues);
        Promotion promotion = new Promotion();
        promotion.setOldPrice(product.getPrice());
        promotion.setProduct(product);
        promotion.setStartDate(promotionValues.getStartDate());
        promotion.setEndDate(promotionValues.getEndDate());
        promotion.setNewPrice(promotionValues.getNewPrice());
        promotionRepository.save(promotion);
        product.setPrice(promotionValues.getNewPrice());
        productRepository.save(product);
        //TODO add promotion notify
        return new SuccessMessage("Promotion added",HttpStatus.OK.value(),LocalDateTime.now());
    }

    @Autowired
    private PromotionRepository promotionRepository;

    private Promotion getPromotion(long productId) throws MissingPromotionException {
        Promotion promotion = promotionRepository.findByProductId(productId);
        if(promotion == null){
            throw new MissingPromotionException();
        }
        return promotion;
    }
    @Transactional
    public SuccessMessage deletePromotion(long productId) throws BaseException {
        Promotion promotion = getPromotion(productId);
        Product product = getProduct(productId);
        product.setPrice(promotion.getOldPrice());
        productRepository.save(product);
        promotionRepository.delete(promotion);
        return new SuccessMessage("Promotion deleted", HttpStatus.OK.value(), LocalDateTime.now());
    }

}
