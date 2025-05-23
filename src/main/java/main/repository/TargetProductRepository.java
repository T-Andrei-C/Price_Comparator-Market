package main.repository;

import main.model.user.TargetProduct;
import main.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TargetProductRepository extends JpaRepository<TargetProduct, Long> {

    @Query("SELECT tp " +
            "FROM TargetProduct tp " +
            "WHERE tp.user = ?1")
    Optional<TargetProduct> getTargetProductByUser(User user);

}
