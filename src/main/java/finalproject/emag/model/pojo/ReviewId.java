package finalproject.emag.model.pojo;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ReviewId implements Serializable {

    @OneToOne
    private User user;
    @OneToOne
    private Product product;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewId reviewId = (ReviewId) o;
        return Objects.equals(user, reviewId.user) &&
                Objects.equals(product, reviewId.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, product);
    }
}
