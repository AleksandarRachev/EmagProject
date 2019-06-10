package finalproject.emag.repositories;

import finalproject.emag.model.pojo.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review,Long> {
}
