package finalproject.emag.repository;

import finalproject.emag.model.pojo.Order;
import finalproject.emag.model.pojo.OrderedId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, OrderedId> {
}
