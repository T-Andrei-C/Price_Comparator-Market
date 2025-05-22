package main.repository;

import jakarta.transaction.Transactional;
import main.model.user.TargetProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TargetProductRepository extends JpaRepository<TargetProduct, Long> {

    @Modifying
    @Transactional
    @Query(
            "DELETE FROM TargetProduct tp WHERE tp.id = ?1"
    )
    void removeById (Long id);

}
