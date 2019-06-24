package finalproject.emag.controller;

import finalproject.emag.model.dto.CartViewProductDTO;
import finalproject.emag.model.dto.FilterParamsDTO;
import finalproject.emag.model.dto.ProductAddDTO;
import finalproject.emag.model.pojo.Product;
import finalproject.emag.model.service.ProductService;
import finalproject.emag.util.exception.BaseException;
import finalproject.emag.util.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping(value = "/products", produces = "application/json")
public class ProductController extends BaseController {

    @Autowired
    protected ProductService productService;

    @PostMapping
    public ResponseEntity addProduct(@RequestBody ProductAddDTO product, HttpSession session) throws BaseException {
        validateLoginAdmin(session);
        return productService.addProduct(product);
    }

    @GetMapping()
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping(value = ("/category/{id}"))
    public List<Product> getAllProductsByCategory(@PathVariable("id") long categoryId) {
        return productService.getProductsByCategory(categoryId);
    }

    @GetMapping(value = ("/{id}"))
    public Product getProduct(@PathVariable("id") long productId) throws ProductNotFoundException {
        return productService.getProduct(productId);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteProduct(@PathVariable("id") long productId, HttpSession session) throws BaseException {
        validateLoginAdmin(session);
        return productService.deleteProduct(productId);
    }

    @GetMapping(value = ("/filter"))
    public List<Product> getAllProductsFiltered(@RequestBody FilterParamsDTO filter) {
        return productService.getProductsFiltered(filter);
    }

    @GetMapping(value = ("/category/{id}/filter"))
    public List<Product> getAllProductsBySubcategoryFiltered(@PathVariable(value = "id") long categoryId,
                                                             @RequestBody FilterParamsDTO filter) {
        return productService.getProductsByCategoryFiltered(filter, categoryId);
    }

    @GetMapping(value = ("/search/{name}"))
    public List<Product> searchProducts(@PathVariable("name") String name) {
        return productService.getProductsByName(name);
    }

    @PutMapping(value = ("/{id}/quantity/{quantity}"))
    public ResponseEntity changeProductQuantity(
            @PathVariable("id") long productId, @PathVariable("quantity") int quantity,
            HttpSession session) throws BaseException {
        validateLoginAdmin(session);
        return productService.changeProductQuantity(productId, quantity);
    }

    @PostMapping(value = ("/{id}/add"))
    public ResponseEntity addToCart(@PathVariable("id") long productId, HttpSession session) throws Exception {
        validateLogin(session);
        return productService.addProductToCart(productId, session);
    }

    @GetMapping(value = ("/view/cart"))
    public List<CartViewProductDTO> viewCart(HttpSession session) throws BaseException {
        validateLogin(session);
        return productService.viewCart(session);
    }

    @PostMapping(value = ("/order"))
    public ResponseEntity makeOrder(HttpSession session) throws BaseException {
        validateLogin(session);
        return productService.makeOrder(session);
    }
}
