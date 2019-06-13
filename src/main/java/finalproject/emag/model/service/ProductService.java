package finalproject.emag.model.service;

import finalproject.emag.model.dto.CartProductDTO;
import finalproject.emag.model.dto.CartViewProductDTO;
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

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class ProductService {

    private static final int MIN_NUMBER_OF_PRODUCTS = 0;
    private static final int MAX_NUMBER_OF_PRODUCTS = 9999;
    private static final double MIN_PRICE = 0.0;
    private static final double MAX_PRICE = 9999999.9;
    private static final String CART = "cart";
    private static final String USER = "user";

    @Autowired
    protected ProductRepository productRepository;

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
        if (!category.isPresent()) {
            throw new InvalidCategoryException();
        }
        return category.get();
    }

    private void fieldsCheck(ProductAddDTO product) throws MissingValuableFieldsException {
        if (product.getPrice() == null || product.getCategoryId() == null ||
                product.getQuantity() == null || product.getName() == null) {
            throw new MissingValuableFieldsException();
        }
    }

    private void checkIfProductExists(String name) throws ProductExistsException {
        int count = productRepository.findAllByName(name).size();
        if (count > 0) {
            throw new ProductExistsException();
        }
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByCategory(long categoryId) {
        return productRepository.findAllByCategoryId(categoryId);
    }

    public Product getProduct(long productId) throws ProductNotFoundException {
        Optional<Product> product = productRepository.findById(productId);
        if (!product.isPresent()) {
            throw new ProductNotFoundException();
        }
        return product.get();
    }

    public SuccessMessage deleteProduct(long productId) throws ProductNotFoundException {
        Product product = getProduct(productId);
        productRepository.delete(product);
        return new SuccessMessage("Product deleted", HttpStatus.OK.value(), LocalDateTime.now());
    }

    public List<Product> getProductsFiltered(Double min, Double max, String order) {
        if (min == null) {
            min = MIN_PRICE;
        }
        if (max == null) {
            max = MAX_PRICE;
        }
        if (order.equals("ASC")) {
            return productRepository.findAllByPriceBetweenOrderByPriceAsc(min, max);
        } else {
            return productRepository.findAllByPriceBetweenOrderByPriceDesc(min, max);
        }
    }

    public List<Product> getProductsByCategoryFiltered(Double min, Double max, Long categoryId, String order) {
        if (min == null) {
            min = 0.0;
        }
        if (max == null) {
            max = 999999.9;
        }
        if (order.equals("ASC")) {
            return productRepository.findAllByCategoryByPriceBetweenOrderByPriceAsc(min, max, categoryId);
        } else {
            return productRepository.findAllByCategoryByPriceBetweenOrderByPriceDesc(min, max, categoryId);
        }
    }

    public List<Product> getProductsByName(String name) {
        return productRepository.findByNameContaining(name);
    }

    public SuccessMessage changeProductQuantity(long productId, int quantity) throws InvalidQuantityException, ProductNotFoundException {
        if (quantity >= MIN_NUMBER_OF_PRODUCTS && quantity <= MAX_NUMBER_OF_PRODUCTS) {
            Product product = getProduct(productId);
            product.setQuantity(quantity);
            productRepository.save(product);
        } else {
            throw new InvalidQuantityException();
        }
        return new SuccessMessage("Quantity changed", HttpStatus.OK.value(), LocalDateTime.now());
    }

    private double getMaxPrice() {
        return productRepository.getMaxPriceForProduct();
    }

    private Product getProductForCart(long productId) throws BaseException {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            if (product.get().getQuantity() > 0) {
                product.get().setQuantity(product.get().getQuantity() - 1);
                productRepository.save(product.get());
                return product.get();
            } else {
                throw new ProductOutOfStockException();
            }
        }
        throw new ProductNotFoundException();
    }

    public SuccessMessage addProductToCart(long productId, HttpSession session) throws BaseException {
        HashMap<CartProductDTO, Integer> cart;
        Product productOG = getProductForCart(productId);
        CartProductDTO product = new CartProductDTO(productOG.getId(), productOG.getCategory()
                , productOG.getName(), productOG.getPrice());
        if (session.getAttribute(ProductService.CART) != null) {
            cart = (HashMap<CartProductDTO, Integer>) session.getAttribute(CART);
            if (cart.containsKey(product)) {
                int quantity = cart.get(product);
                cart.put(product, quantity + 1);
            } else {
                cart.put(product, 1);
            }
        } else {
            session.setAttribute(CART, new HashMap<Product, Integer>());
            cart = (HashMap<CartProductDTO, Integer>) session.getAttribute(CART);
            cart.put(product, 1);
        }
        return new SuccessMessage("Product added to cart", HttpStatus.OK.value(), LocalDateTime.now());
    }

    public List<CartViewProductDTO> viewCart(HttpSession session) throws EmptyCartException {
        List<CartViewProductDTO> cart = new ArrayList<>();
        HashMap<CartProductDTO, Integer> cartSession;
        if (session.getAttribute(CART) != null) {
            cartSession = (HashMap<CartProductDTO, Integer>) session.getAttribute(CART);
            for (Map.Entry<CartProductDTO, Integer> entry : cartSession.entrySet()) {
                CartViewProductDTO product = new CartViewProductDTO(entry.getKey().getId(), entry.getKey().getCategory()
                        , entry.getKey().getName(), entry.getKey().getPrice(), entry.getValue());
                cart.add(product);
            }
            return cart;
        }
        throw new EmptyCartException();
    }

}
