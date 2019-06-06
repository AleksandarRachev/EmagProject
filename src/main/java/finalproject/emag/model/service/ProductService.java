package finalproject.emag.model.service;

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

    public SuccessMessage addProduct(HttpServletRequest request) throws BaseException {
        fieldsCheck(request);
        String name = request.getParameter("name");
        double price = Double.parseDouble(request.getParameter("price"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        long categoryId = Long.parseLong(request.getParameter("category_id"));
        Category category = getCategory(categoryId);
        checkIfProductExists(name);
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setQuantity(quantity);
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

    private void fieldsCheck(HttpServletRequest request) throws MissingValuableFieldsException {
        if(request.getParameter("price") == null || request.getParameter("category_id") == null ||
                request.getParameter("quantity") == null || request.getParameter("name") == null){
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


}
