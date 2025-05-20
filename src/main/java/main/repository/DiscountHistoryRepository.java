package main.repository;

import main.model.Discount;
import main.model.DiscountHistory;
import main.model.Supermarket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiscountHistoryRepository extends JpaRepository<DiscountHistory, Long> {

    @Query(
            "SELECT dh " +
                    "FROM DiscountHistory dh " +
                    "WHERE dh.supermarket = ?3 " +
                    "AND dh.from_date = ?1 " +
                    "OR (dh.from_date BETWEEN ?1 AND ?2)"
    )
    List<DiscountHistory> findDiscountsHistoryToRemoveByFields (LocalDate from_date, LocalDate to_date, Supermarket supermarket);

    @Query (
            "SELECT dh " +
                    "FROM DiscountHistory dh " +
                    "WHERE dh.supermarket = ?3 " +
                    "AND dh.from_date < ?1 " +
                    "AND (dh.to_date BETWEEN ?1 AND ?2)"
    )
    List<DiscountHistory> findDiscountsHistoryToUpdateByFields (LocalDate from_date, LocalDate to_date, Supermarket supermarket);
}
