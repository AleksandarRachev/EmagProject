package finalproject.emag.repositories;

import finalproject.emag.model.pojo.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Promotion findByProductId(long productId);

}
