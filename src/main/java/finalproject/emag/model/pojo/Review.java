package finalproject.emag.model.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "reviews")
public class Review {

    @EmbeddedId
    private ReviewId id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String comment;
    @Column(nullable = false)
    private int grade;

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Review review = (Review) o;
//        return userId == review.userId &&
//                productId == review.productId;
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(userId, productId);
//    }
}
