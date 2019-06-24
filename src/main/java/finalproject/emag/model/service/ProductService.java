package finalproject.emag.model.service;

import finalproject.emag.model.dto.*;
import finalproject.emag.model.pojo.*;
import finalproject.emag.repository.CategoryRepository;
import finalproject.emag.repository.OrderRepository;
import finalproject.emag.repository.ProductRepository;
import finalproject.emag.repository.UserRepository;
import finalproject.emag.util.Message;
import finalproject.emag.util.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
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

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity addProduct(ProductAddDTO productAdd) throws BaseException {
        Category category = getCategory(productAdd.getCategoryId());
        checkIfProductExists(productAdd.getName());
        Product product = new Product();
        product.setName(productAdd.getName());
        product.setPrice(productAdd.getPrice());
        product.setQuantity(productAdd.getQuantity());
        product.setCategory(category);
        productRepository.save(product);
        return new ResponseEntity(new Message("Product added"), HttpStatus.OK);
    }

    private Category getCategory(long categoryId) throws InvalidCategoryException {
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (!category.isPresent()) {
            throw new InvalidCategoryException();
        }
        return category.get();
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

    public ResponseEntity deleteProduct(long productId) throws ProductNotFoundException {
        Product product = getProduct(productId);
        productRepository.delete(product);
        return new ResponseEntity(new Message("Product deleted"), HttpStatus.OK);
    }

    public List<Product> getProductsFiltered(FilterParamsDTO filter) {
        if (filter.getOrder().equals("ASC")) {
            return productRepository.findAllByPriceBetweenOrderByPriceAsc(filter.getFrom(), filter.getTo());
        } else {
            return productRepository.findAllByPriceBetweenOrderByPriceDesc(filter.getFrom(), filter.getTo());
        }
    }

    public List<Product> getProductsByCategoryFiltered(FilterParamsDTO filter, Long categoryId) {
        if (filter.getOrder().equals("ASC")) {
            return productRepository.findAllByCategoryIdAndPriceBetweenOrderByPriceAsc(categoryId, filter.getFrom(), filter.getTo());
        } else {
            return productRepository.findAllByCategoryIdAndPriceBetweenOrderByPriceDesc(categoryId, filter.getFrom(), filter.getTo());
        }
    }

    public List<Product> getProductsByName(String name) {
        return productRepository.findByNameContaining(name);
    }

    public ResponseEntity changeProductQuantity(long productId, int quantity) throws BaseException {
        if (quantity >= MIN_NUMBER_OF_PRODUCTS && quantity <= MAX_NUMBER_OF_PRODUCTS) {
            Product product = getProduct(productId);
            product.setQuantity(quantity);
            productRepository.save(product);
        } else {
            throw new InvalidQuantityException();
        }
        return new ResponseEntity(new Message("Quantity changed"), HttpStatus.OK);
    }

    private void checkValidQuantity(HashMap<CartProductDTO, Integer> cart, CartProductDTO productShow)
            throws ProductNotFoundException, OverProductQuantityException {
        Product product = getProduct(productShow.getId());
        int quantityInCart = cart.get(productShow);
        if (product.getQuantity() <= quantityInCart) {
            throw new OverProductQuantityException();
        }
    }

    public ResponseEntity addProductToCart(long productId, HttpSession session) throws BaseException {
        HashMap<CartProductDTO, Integer> cart;
        Product productOG = getProduct(productId);
        CartProductDTO product = new CartProductDTO(productOG.getId(), productOG.getCategory()
                , productOG.getName(), productOG.getPrice());
        if (session.getAttribute(ProductService.CART) != null) {
            cart = (HashMap<CartProductDTO, Integer>) session.getAttribute(CART);
            if (cart.containsKey(product)) {
                checkValidQuantity(cart, product);
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
        return new ResponseEntity(new Message("Product added to cart"), HttpStatus.OK);
    }

    private HashMap<CartProductDTO, Integer> getCart(HttpSession session) throws EmptyCartException {
        HashMap<CartProductDTO, Integer> cart = (HashMap<CartProductDTO, Integer>) session.getAttribute(CART);
        if (cart == null) {
            throw new EmptyCartException();
        }
        return cart;
    }

    public List<CartViewProductDTO> viewCart(HttpSession session) throws EmptyCartException {
        List<CartViewProductDTO> cart = new ArrayList<>();
        HashMap<CartProductDTO, Integer> cartSession = getCart(session);
        for (Map.Entry<CartProductDTO, Integer> entry : cartSession.entrySet()) {
            CartViewProductDTO product = new CartViewProductDTO(entry.getKey().getId(), entry.getKey().getCategory()
                    , entry.getKey().getName(), entry.getKey().getPrice(), entry.getValue());
            cart.add(product);
        }
        return cart;
    }

    public ResponseEntity makeOrder(HttpSession session) throws EmptyCartException, ProductNotFoundException {
        HashMap<CartProductDTO, Integer> cart = getCart(session);
        for (Map.Entry<CartProductDTO, Integer> product : cart.entrySet()) {
            Product productToUpdate = getProduct(product.getKey().getId());
            productToUpdate.setQuantity(productToUpdate.getQuantity() - product.getValue());
            ShowUserDTO userSession = (ShowUserDTO) session.getAttribute(USER);
            User user = userRepository.findById(userSession.getId()).get();
            insertOrder(productToUpdate, user, product.getValue());
            productRepository.save(productToUpdate);
        }
        return new ResponseEntity(new Message("Order made"), HttpStatus.OK);
    }

    private void insertOrder(Product product, User user, int quantity) {
        OrderedId id = new OrderedId();
        id.setUser(user);
        id.setProduct(product);
        Order order = new Order();
        order.setId(id);
        order.setQuantity(quantity);
        orderRepository.save(order);
    }
}
