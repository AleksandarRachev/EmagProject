package finalproject.emag.model.service;

import finalproject.emag.model.dto.ProductAddDTO;
import finalproject.emag.model.pojo.Category;
import finalproject.emag.model.pojo.Product;
import finalproject.emag.repositories.CategoryRepository;
import finalproject.emag.repositories.ProductRepository;
import finalproject.emag.util.SuccessMessage;
import finalproject.emag.util.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class ProductService {

    private static final int MIN_NUMBER_OF_PRODUCTS = 0;
    private static final int MAX_NUMBER_OF_PRODUCTS = 9999;
    private static final String MIN_PRICE = "0";
    private static final String MAX_PRICE = "99999";
    private static final String CART = "cart";
    private static final String USER = "user";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public SuccessMessage addProduct(ProductAddDTO productAdd) throws BaseException {
        fieldsCheck(productAdd);
        Category category = getCategory(productAdd.getCategoryId());
        checkIfProductExists(productAdd.getName());
        Product product = new Product();
        product.setName(productAdd.getName());
        product.setPrice(productAdd.getPrice());
        product.setQuantity(productAdd.getQuantity());
        product.setCategory(category);
        productRepository.save(product);
        return new SuccessMessage("Product added", HttpStatus.OK.value(), LocalDateTime.now());
    }

    private Category getCategory(long categoryId) throws InvalidCategoryException {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if(!category.isPresent()){
            throw new InvalidCategoryException();
        }
        return category.get();
    }

    private void fieldsCheck(ProductAddDTO product) throws MissingValuableFieldsException {
        if(product.getPrice() == null || product.getCategoryId() == null ||
                product.getQuantity() == null || product.getName() == null){
            throw new MissingValuableFieldsException();
        }
    }

    private void checkIfProductExists(String name) throws ProductExistsException {
        int count = productRepository.findAllByName(name).size();
        if(count > 0){
            throw new ProductExistsException();
        }
    }

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public List<Product> getProductsByCategory(long categoryId){
        return productRepository.findAllByCategoryId(categoryId);
    }

    public Product getProduct(long productId) throws ProductNotFoundException {
        Optional<Product> product = productRepository.findById(productId);
        if(!product.isPresent()){
            throw new ProductNotFoundException();
        }
        return product.get();
    }

    public SuccessMessage deleteProduct(long productId) throws ProductNotFoundException {
        Product product = getProduct(productId);
        productRepository.delete(product);
        return new SuccessMessage("Product deleted",HttpStatus.OK.value(),LocalDateTime.now());
    }


}
