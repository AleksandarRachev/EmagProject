package finalproject.emag.model.pojo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

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
    private Integer grade;

}
