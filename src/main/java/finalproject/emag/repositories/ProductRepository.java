package finalproject.emag.repositories;

import finalproject.emag.model.pojo.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findAllByName(String name);
    List<Product> findAllByCategoryId(Long categoryId);

}
