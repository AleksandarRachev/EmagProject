package finalproject.emag.controller;

import finalproject.emag.model.dto.ProductAddDTO;
import finalproject.emag.model.pojo.Product;
import finalproject.emag.model.service.ProductService;
import finalproject.emag.util.SuccessMessage;
import finalproject.emag.util.exception.BaseException;
import finalproject.emag.util.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping(value = "/products",produces = "application/json")
public class ProductController extends BaseController {

    @Autowired
    protected ProductService productService;

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
    @GetMapping(value = ("/filter"))
    public List<Product> getAllProductsFiltered(
            @RequestParam(value = "order",required = false,defaultValue = "ASC") String order,
            @RequestParam(value = "from",required = false) Double min,
            @RequestParam(value = "to",required = false) Double max){
        return productService.getProductsFiltered(min,max,order);
    }

    @GetMapping(value = ("/subcategory/{id}/filter"))
    public List<Product> getAllProductsBySubcategoryFiltered(
            @PathVariable(value = "id") long categoryId,
            @RequestParam(value = "order", required = false, defaultValue = "ASC") String order,
            @RequestParam(value = "from", required = false) Double min,
            @RequestParam(value = "to", required = false) Double max) {
        return productService.getProductsByCategoryFiltered(min,max,categoryId,order);
    }

    @GetMapping(value = ("/search/{name}"))
    public List<Product> searchProducts(@PathVariable("name") String name) {
        return productService.getProductsByName(name);
    }

    @PutMapping(value = ("/{id}/quantity/{quantity}"))
    public SuccessMessage changeProductQuantity(
            @PathVariable("id") long productId,
            @PathVariable("quantity") int quantity, HttpSession session) throws BaseException {
        validateLoginAdmin(session);
        return productService.changeProductQuantity(productId,quantity);
    }



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
}
