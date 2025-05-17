package main.repository;

import main.model.Product;
import main.model.Supermarket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SupermarketRepository extends JpaRepository<Supermarket, Long> {

    @Query(
            "SELECT s " +
            "FROM Supermarket s " +
            "WHERE s.name = ?1 " +
                    "AND s.product = ?2"
    )
    Supermarket findSupermarketByFields (String name, Product product);

}
