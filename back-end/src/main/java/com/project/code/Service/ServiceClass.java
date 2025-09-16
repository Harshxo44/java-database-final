import com.example.retailinventory.models.Inventory;
import com.example.retailinventory.models.Product;
import com.example.retailinventory.repository.InventoryRepository;
import com.example.retailinventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceClass {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ServiceClass(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    public boolean validateInventory(Inventory inventory) {
        Inventory existingInventory = inventoryRepository.findByProductIdandStoreId(inventory.getProduct().getId(), inventory.getStore().getId());
        return existingInventory == null;
    }

    public boolean validateProduct(Product product) {
        Product existingProduct = productRepository.findByName(product.getName());
        return existingProduct == null;
    }

    public boolean validateProductId(long id) {
        return productRepository.findById(id) != null;
    }

    public Inventory getInventoryId(Inventory inventory) {
        return inventoryRepository.findByProductIdandStoreId(inventory.getProduct().getId(), inventory.getStore().getId());
    }
}
