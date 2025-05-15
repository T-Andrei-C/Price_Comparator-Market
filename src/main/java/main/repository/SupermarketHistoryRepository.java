package main.repository;

import main.model.SupermarketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupermarketHistoryRepository extends JpaRepository<SupermarketHistory, Long> {
}
