import com.example.retailinventory.models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.store.id = :storeId")
    Inventory findByProductIdandStoreId(Long productId, Long storeId);

    List<Inventory> findByStoreId(Long storeId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Inventory i WHERE i.product.id = :productId")
    void deleteByProductId(Long productId);
}
