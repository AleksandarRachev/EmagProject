package finalproject.emag.repository;

import finalproject.emag.model.pojo.Review;
import finalproject.emag.model.pojo.ReviewId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, ReviewId> {

}
