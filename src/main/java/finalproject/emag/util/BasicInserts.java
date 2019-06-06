package finalproject.emag.util;

import finalproject.emag.model.pojo.Category;
import finalproject.emag.model.pojo.Product;
import finalproject.emag.model.pojo.User;
import finalproject.emag.repositories.CategoryRepository;
import finalproject.emag.repositories.ProductRepository;
import finalproject.emag.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@Component
public class BasicInserts {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    @PostConstruct
    public void insert(){
        User user = new User();
        user.setEmail("gosho@abv.bg");
        user.setPassword(PasswordEncoder.hashPassword("123"));
        user.setUsername("gosho");
        user.setSubscribed(true);
        user.setName("Georgi Petkov");
        user.setPhoneNumber("0345345");
        user.setBirthDate(LocalDate.now());
        userRepository.save(user);

        User user1 = new User();
        user1.setEmail("admin@abv.bg");
        user1.setPassword(PasswordEncoder.hashPassword("admin"));
        user1.setUsername("admin");
        user1.setSubscribed(false);
        user1.setName("Admin");
        user1.setPhoneNumber("666");
        user1.setAdmin(true);
        user1.setBirthDate(LocalDate.now());
        userRepository.save(user1);

        Category category = new Category();
        category.setName("Computers");
        categoryRepository.save(category);

        Category category1 = new Category();
        category1.setName("Mobile phones");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setName("Accessories");
        categoryRepository.save(category2);

        Category category3 = new Category();
        category3.setName("White tech");
        categoryRepository.save(category3);

        Product product = new Product();
        product.setName("Asus PC");
        product.setPrice(2000.0);
        product.setQuantity(5);
        product.setCategory(categoryRepository.findById(1L).get());
        productRepository.save(product);

        Product product1 = new Product();
        product1.setName("Samsung S10");
        product1.setPrice(1200.0);
        product1.setQuantity(1);
        product1.setCategory(categoryRepository.findById(2L).get());
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Charger");
        product2.setPrice(10.0);
        product2.setQuantity(50);
        product2.setCategory(categoryRepository.findById(3L).get());
        productRepository.save(product2);

        Product product3 = new Product();
        product3.setName("Whirlpool");
        product3.setPrice(1500.0);
        product3.setQuantity(7);
        product3.setCategory(categoryRepository.findById(4L).get());
        productRepository.save(product3);

    }

}
