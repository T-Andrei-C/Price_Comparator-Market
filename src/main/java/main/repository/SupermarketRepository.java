package main.repository;

import main.model.Supermarket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SupermarketRepository extends JpaRepository<Supermarket, Long> {

    @Query(
            "SELECT s " +
            "FROM Supermarket s " +
            "WHERE s.supermarket_name = ?1 " +
                    "AND s.product_name = ?2 " +
                    "AND s.unit = ?3 " +
                    "AND s.category = ?4 " +
                    "AND s.quantity = ?5 " +
                    "AND s.brand = ?6"
    )
    Supermarket findSupermarketByFields(
            String supermarketName,
            String productName,
            String unit,
            String category,
            Double quantity,
            String brand
    );

//    @Query("SELECT s FROM Supermarket s WHERE s.supermarket_name = ?1")
//    Supermarket findSupermarketByName (String supermarketName);
}
