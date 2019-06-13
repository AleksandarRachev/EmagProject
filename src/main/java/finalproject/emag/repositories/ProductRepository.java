package finalproject.emag.repositories;

import finalproject.emag.model.pojo.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByName(String name);

    List<Product> findAllByCategoryId(Long categoryId);

    List<Product> findAllByPriceBetweenOrderByPriceDesc(Double min, Double max);

    List<Product> findAllByPriceBetweenOrderByPriceAsc(Double min, Double max);

    List<Product> findAllByCategoryIdAndPriceBetweenOrderByPriceDesc(Long categoryId, Double min, Double max);

    List<Product> findAllByCategoryIdAndPriceBetweenOrderByPriceAsc(Long categoryId, Double min, Double max);

    List<Product> findByNameContaining(String name);

    Product findFirstByOrderByPriceDesc();

    Product findFirstByCategoryIdOrderByPriceDesc(Long categoryId);

    Product findFirstByOrderByPriceAsc();

    Product findFirstByCategoryIdOrderByPriceAsc(Long categoryId);
}
