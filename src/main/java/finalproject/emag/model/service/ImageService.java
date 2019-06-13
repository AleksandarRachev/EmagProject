package finalproject.emag.model.service;

import finalproject.emag.model.dto.ShowUserDTO;
import finalproject.emag.model.pojo.Product;
import finalproject.emag.model.pojo.User;
import finalproject.emag.repositories.ProductRepository;
import finalproject.emag.repositories.UserRepository;
import finalproject.emag.util.SuccessMessage;
import finalproject.emag.util.exception.BaseException;
import finalproject.emag.util.exception.ImageMissingException;
import finalproject.emag.util.exception.InvalidUserException;
import finalproject.emag.util.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class ImageService {

    private static final String IMAGE_PATH = "C:\\Users\\Aleksandar_Rachev\\Desktop\\images\\";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private User getUser(long userId) throws InvalidUserException {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get();
        }
        throw new InvalidUserException();
    }

    private Product getProduct(long productId) throws ProductNotFoundException {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            return product.get();
        }
        throw new ProductNotFoundException();
    }

    public SuccessMessage userImageUpload(MultipartFile file, HttpSession session) throws IOException {
        ShowUserDTO userSession = (ShowUserDTO) session.getAttribute("user");
        User user = userRepository.findById(userSession.getId()).get();
        String name = user.getId() + System.currentTimeMillis() + ".png";
        File newImage = new File(IMAGE_PATH + name);
        file.transferTo(newImage);
        user.setImageUrl(name);
        userRepository.save(user);
        return new SuccessMessage("Image uploaded", HttpStatus.OK.value(), LocalDateTime.now());
    }

    public byte[] getUserImage(long userId) throws BaseException, IOException {
        User user = getUser(userId);
        if (user.getImageUrl() == null) {
            throw new ImageMissingException();
        }
        File image = new File(IMAGE_PATH + user.getImageUrl());
        FileInputStream fis = new FileInputStream(image);
        return fis.readAllBytes();
    }

    public SuccessMessage productImageUpload(MultipartFile file, long productId) throws ProductNotFoundException, IOException {
        Product product = getProduct(productId);
        String name = product.getId() + System.currentTimeMillis() + ".png";
        File newImage = new File(IMAGE_PATH + name);
        file.transferTo(newImage);
        product.setImageUrl(name);
        productRepository.save(product);
        return new SuccessMessage("Image uploaded", HttpStatus.OK.value(), LocalDateTime.now());
    }

    public byte[] getProductImage(long productId) throws Exception {
        Product product = getProduct(productId);
        if (product.getImageUrl() == null) {
            throw new ImageMissingException();
        }
        File image = new File(IMAGE_PATH + product.getImageUrl());
        FileInputStream fis = new FileInputStream(image);
        return fis.readAllBytes();
    }

}
