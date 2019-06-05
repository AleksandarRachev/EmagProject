package finalproject.emag.model.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(name = "full_name" ,nullable = false)
    private String name;
    private String username;
    private String phoneNumber;
    private LocalDate birthDate;
    private boolean subscribed;
    @Column(nullable = false)
    private boolean admin;
    private String imageUrl;
//    private HashSet<Order> orders;

}