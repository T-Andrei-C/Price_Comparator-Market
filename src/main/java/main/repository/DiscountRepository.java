package main.repository;

import main.model.Discount;
import main.model.Supermarket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    @Query(
            "SELECT d " +
                    "FROM Discount d " +
                    "WHERE d.supermarket = ?1 " +
                    "AND (d.from_date = ?2 " +
                    "OR d.to_date = ?2 " +
                    "OR ?2 BETWEEN d.from_date AND to_date)"
    )
    Discount getDiscountByFields (Supermarket supermarket, LocalDate date);
}
