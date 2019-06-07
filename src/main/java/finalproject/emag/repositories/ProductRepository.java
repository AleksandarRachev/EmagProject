package finalproject.emag.repositories;

import finalproject.emag.model.pojo.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findAllByName(String name);
    List<Product> findAllByCategoryId(Long categoryId);
//    @Query(value = "SELECT * FROM products", nativeQuery = true)
//    List<Product> getAllProductsBetweenOrderBy(Sort sort);
    @Query(value = "SELECT * FROM products WHERE price > :min AND price < :max ORDER BY price DESC", nativeQuery = true)
    List<Product> findAllByPriceBetweenOrderByPriceDesc(@Param("min")Double min, @Param("max")Double max);
    @Query(value = "SELECT * FROM products WHERE price > :min AND price < :max ORDER BY price ASC", nativeQuery = true)
    List<Product> findAllByPriceBetweenOrderByPriceAsc(@Param("min")Double min, @Param("max")Double max);
    @Query(value = "SELECT * FROM products WHERE (price > :min AND price < :max) AND category_id = :categoryId ORDER BY price DESC", nativeQuery = true)
    List<Product> findAllByCategoryByPriceBetweenOrderByPriceDesc(@Param("min")Double min, @Param("max")Double max,@Param("categoryId")Long categoryId);
    @Query(value = "SELECT * FROM products WHERE (price > :min AND price < :max) AND category_id = :categoryId ORDER BY price ASC", nativeQuery = true)
    List<Product> findAllByCategoryByPriceBetweenOrderByPriceAsc(@Param("min")Double min, @Param("max")Double max,@Param("categoryId")Long categoryId);
    List<Product> findByNameContaining(String name);
}
