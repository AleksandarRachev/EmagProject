package finalproject.emag.model.dao;

import finalproject.emag.model.dto.DeleteReviewDto;
import finalproject.emag.model.dto.ReviewDto;
import finalproject.emag.util.exception.ReviewExistsException;
import finalproject.emag.util.exception.ReviewMissingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Component
public class ReviewDao {

    @Autowired
    private JdbcTemplate template;

    private boolean hasReview(ReviewDto review) throws SQLException {
        ArrayList<Long> ids = new ArrayList<>();
        try(Connection connection = this.template.getDataSource().getConnection()){
            PreparedStatement ps = connection.prepareStatement("SELECT product_id FROM reviews WHERE user_id = ?");
            ps.setLong(1,review.getUserId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                ids.add(rs.getLong(1));
            }
        }
        for (Long l : ids){
            if(l == review.getProductId()){
                return true;
            }
        }
        return false;
    }
    private boolean hasReview(DeleteReviewDto review) throws SQLException {
        ArrayList<Long> ids = new ArrayList<>();
        try(Connection connection = this.template.getDataSource().getConnection()){
            PreparedStatement ps = connection.prepareStatement("SELECT product_id FROM reviews WHERE user_id = ?");
            ps.setLong(1,review.getUserId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                ids.add(rs.getLong(1));
            }
        }
        for (Long l : ids){
            if(l == review.getProductId()){
                return true;
            }
        }
        return false;
    }

    public void addReview(ReviewDto review) throws SQLException, ReviewExistsException {
        boolean hasReview = hasReview(review);
        if(hasReview){
            throw new ReviewExistsException();
        }
        try(Connection connection = this.template.getDataSource().getConnection()){
            PreparedStatement ps = connection.prepareStatement("INSERT INTO reviews(user_id,product_id,title,comment,grade) VALUES (?,?,?,?,?)");
            ps.setLong(1,review.getUserId());
            ps.setLong(2,review.getProductId());
            ps.setString(3,review.getTitle());
            ps.setString(4,review.getComment());
            ps.setInt(5,review.getGrade());
            ps.executeUpdate();
        }
    }

    public void deleteReview(DeleteReviewDto review) throws SQLException, ReviewMissingException {
        boolean hasReview = hasReview(review);
        if(!hasReview){
            throw new ReviewMissingException();
        }
        try(Connection connection = this.template.getDataSource().getConnection()){
            PreparedStatement ps = connection.prepareStatement("DELETE FROM reviews WHERE user_id = ? AND product_id = ?");
            ps.setLong(1,review.getUserId());
            ps.setLong(2,review.getProductId());
            ps.executeUpdate();
        }
    }

    public void editReview(ReviewDto review) throws SQLException, ReviewMissingException {
        boolean hasReview = hasReview(review);
        if(!hasReview){
            throw new ReviewMissingException();
        }
        try(Connection connection = this.template.getDataSource().getConnection()){
            PreparedStatement ps = connection.prepareStatement("UPDATE reviews SET title = ?, comment = ?, grade = ? WHERE user_id = ? AND product_id = ?");
            ps.setString(1,review.getTitle());
            ps.setString(2,review.getComment());
            ps.setInt(3,review.getGrade());
            ps.setLong(4,review.getUserId());
            ps.setLong(5,review.getProductId());
            ps.executeUpdate();
        }
    }
}