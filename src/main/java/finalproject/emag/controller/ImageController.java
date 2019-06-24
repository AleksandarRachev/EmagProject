package finalproject.emag.controller;

import finalproject.emag.model.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/images")
public class ImageController extends BaseController {

    @Autowired
    private ImageService imageService;

    @PostMapping(value = "/users")
    public ResponseEntity uploadUserImage(@RequestPart(value = "image") MultipartFile image,
                                          HttpSession session) throws Exception {
        validateLogin(session);
        return imageService.userImageUpload(image, session);
    }

    @GetMapping(value = "/users/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] downloadImageById(@PathVariable("id") long userId) throws Exception {
        return imageService.getUserImage(userId);
    }

    @PostMapping(value = "/products/{id}")
    public ResponseEntity uploadProductImage(@RequestPart(value = "image") MultipartFile image,
                                             @PathVariable("id") long productId, HttpSession session) throws Exception {
        validateLoginAdmin(session);
        return imageService.productImageUpload(image, productId);
    }

    @GetMapping(value = "/products/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getProductImage(@PathVariable("id") Long productId) throws Exception {
        return imageService.getProductImage(productId);
    }
}