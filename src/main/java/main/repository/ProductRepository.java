package main.repository;

import main.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(
            "SELECT p " +
                    "FROM Product p " +
                    "WHERE p.name = ?1 " +
                    "AND p.unit = ?2 " +
                    "AND p.category = ?3 " +
                    "AND p.brand = ?4 " +
                    "AND p.quantity = ?5 "
    )
    Optional<Product> findProductByFields(String name, String unit, String category, String brand, Double quantity);

}
