package finalproject.emag.controller;

import finalproject.emag.model.dto.ProductAddDTO;
import finalproject.emag.model.pojo.Product;
import finalproject.emag.model.service.ProductService;
import finalproject.emag.util.SuccessMessage;
import finalproject.emag.util.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping(value = "/products",produces = "application/json")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public SuccessMessage addProduct(@RequestBody ProductAddDTO product, HttpSession session) throws BaseException {
        validateLoginAdmin(session);
        return productService.addProduct(product);
    }

    @GetMapping()
    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }

    @GetMapping(value = ("/category/{id}"))
    public List<Product> getAllProductsByCategory(@PathVariable("id") long categoryId){
        return productService.getProductsByCategory(categoryId);
    }

    @GetMapping(value = ("/{id}"))
    public Product getProduct(@PathVariable("id") long productId) throws ProductNotFoundException {
        return productService.getProduct(productId);
    }

    @DeleteMapping(value = "/{id}")
    public SuccessMessage deleteProduct(@PathVariable("id") long productId,HttpSession session) throws BaseException{
        validateLoginAdmin(session);
        return productService.deleteProduct(productId);
    }
//
//    @GetMapping(value = ("/products/filter"))
//    public ArrayList<GlobalViewProductDto> getAllProductsFiltered(
//            @RequestParam(value = "order", required = false, defaultValue = "ASC") String order,
//            @RequestParam(value = "from", required = false, defaultValue = MIN_PRICE) Double min,
//            @RequestParam(value = "to", required = false, defaultValue = MAX_PRICE) Double max
//    ) throws Exception {
//        return dao.getAllProductsFiltered(order, min, max);
//    }
//
//    @GetMapping(value = ("/products/subcategory/{id}/filter"))
//    public ArrayList<GlobalViewProductDto> getAllProductsBySubcategoryFiltered(
//            @PathVariable(value = "id") long subcatId,
//            @RequestParam(value = "order", required = false, defaultValue = "ASC") String order,
//            @RequestParam(value = "from", required = false, defaultValue = MIN_PRICE) Double min,
//            @RequestParam(value = "to", required = false, defaultValue = MAX_PRICE) Double max) throws Exception {
//        return dao.getAllProductsBySubcategoryFiltered(subcatId, order, min, max);
//    }
//
//    @GetMapping(value = ("/products/search/{name}"))
//    public ArrayList<GlobalViewProductDto> searchProducts(@PathVariable("name") String name) throws Exception {
//        return dao.searchProducts(name);
//    }
//
//    @PutMapping(value = ("/products/{id}/quantity/{quantity}"))
//    public String changeProductQuantity(
//            @PathVariable("id") long productId,
//            @PathVariable("quantity") int quantity, HttpServletRequest request) throws Exception {
//        validateLoginAdmin(request.getSession());
//        if (quantity >= MIN_NUMBER_OF_PRODUCTS && quantity <= MAX_NUMBER_OF_PRODUCTS) {
//            dao.changeQuantity(productId, quantity);
//        }
//        else {
//            throw new InvalidQuantityException();
//        }
//        return "Product with id - " + productId + " now has quantity - " + quantity + ".";
//    }
//
//    @PostMapping(value = ("/products/{id}/add"))
//    public Product addToCart(@PathVariable("id") long productId, HttpServletRequest request) throws Exception {
//        validateLogin(request.getSession());
//        HashMap<CartProductDto, Integer> cart = null;
//        CartProductDto p = dao.getProductForCart(productId);
//        if (request.getSession().getAttribute("cart") != null ) {
//            cart = (HashMap<CartProductDto, Integer>) request.getSession().getAttribute(CART);
//            if(cart.containsKey(p)) {
//                int quantity = cart.get(p);
//                cart.put(p, quantity+1);
//            }
//            else {
//                cart.put(p, 1);
//            }
//        }
//        else {
//            request.getSession().setAttribute(CART, new HashMap<Product, Integer>());
//            cart = (HashMap<CartProductDto, Integer>) request.getSession().getAttribute(CART);
//            cart.put(p, 1);
//        }
//        Product product = dao.getProductById(productId);
//        return product;
//    }
//
//    @GetMapping(value = ("/view/cart"))
//    public ArrayList<CartViewProductDto> viewCart(HttpServletRequest request) throws Exception{
//        validateLogin(request.getSession());
//        if (request.getSession().getAttribute(CART) != null) {
//            HashMap<CartProductDto, Integer> cart =
//                    (HashMap<CartProductDto, Integer>) request.getSession().getAttribute(CART);
//            return dao.viewCart(cart);
//        }
//        else {
//            throw new EmptyCartException();
//        }
//    }
//
//    @PostMapping(value = ("/view/cart/order"))
//    public String makeOrder(HttpServletRequest request) throws Exception {
//        validateLogin(request.getSession());
//        if (request.getSession().getAttribute(CART) != null) {
//            User user = (User) request.getSession().getAttribute(USER);
//            HashMap<CartProductDto, Integer> cart =
//                    (HashMap<CartProductDto, Integer>) request.getSession().getAttribute(CART);
//            dao.makeOrder(user, cart);
//            request.getSession().setAttribute(CART, null);
//            return "Your order was successful.";
//        }
//        else {
//            throw new EmptyCartException();
//        }
//    }
//
//    @PostMapping(value = "/products/promotions/{id}")
//    public String addPromotion(@PathVariable("id") long productId,
//                               @RequestBody String input,HttpServletRequest request) throws Exception {
//        validateLoginAdmin(request.getSession());
//        JsonNode jsonNode = this.objectMapper.readTree(input);
//        if(!jsonNode.has("start_date")|| !jsonNode.has("end_date")||
//                !jsonNode.has("old_price")|| !jsonNode.has("new_price")){
//            throw new MissingValuableFieldsException();
//        }
//        else{
//            LocalDate startDate = GetDate.getDate(jsonNode.get("start_date").textValue());
//            LocalDate endDate = GetDate.getDate(jsonNode.get("end_date").textValue());
//            double oldPrice = jsonNode.get("old_price").asDouble();
//            double newPrice = jsonNode.get("new_price").asDouble();
//            PromotionProductDto product = new PromotionProductDto(productId,startDate,endDate,oldPrice,newPrice);
//            dao.addPromotion(product);
//            return "Promotion added";
//        }
//
//    }
//
//    @DeleteMapping(value = "/products/promotions/{id}")
//    public String removePromotion(@PathVariable("id") long productId, HttpServletRequest request) throws Exception {
//        validateLoginAdmin(request.getSession());
//        RemovePromotionDto product = new RemovePromotionDto(productId);
//        dao.removePromotion(product);
//        return "Promotion removed";
//    }
}
