package finalproject.emag.repository;

import finalproject.emag.model.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByEmail(String email);

    List<User> findAllByUsername(String username);

    User findByEmail(String email);

    List<User> findAllBySubscribedIs(boolean subscribed);
}
