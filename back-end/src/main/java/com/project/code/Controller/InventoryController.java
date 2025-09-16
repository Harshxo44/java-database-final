import com.example.retailinventory.dto.CombinedRequest;
import com.example.retailinventory.models.Inventory;
import com.example.retailinventory.models.Product;
import com.example.retailinventory.repository.InventoryRepository;
import com.example.retailinventory.repository.ProductRepository;
import com.example.retailinventory.service.ServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private ServiceClass serviceClass;

    @PutMapping
    public Map<String, String> updateInventory(@RequestBody CombinedRequest combinedRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!serviceClass.validateProductId(combinedRequest.getProduct().getId())) {
                response.put("message", "Product ID not valid.");
                return response;
            }

            Inventory existingInventory = inventoryRepository.findByProductIdandStoreId(
                    combinedRequest.getProduct().getId(),
                    combinedRequest.getInventory().getStore().getId()
            );

            if (existingInventory != null) {
                existingInventory.setStockLevel(combinedRequest.getInventory().getStockLevel());
                inventoryRepository.save(existingInventory);
                response.put("message", "Successfully updated product");
            } else {
                response.put("message", "No data available");
            }
        } catch (DataIntegrityViolationException e) {
            response.put("Error", "Data integrity violation: " + e.getMessage());
        }
        return response;
    }

    @PostMapping
    public Map<String, String> saveInventory(@RequestBody Inventory inventory) {
        Map<String, String> response = new HashMap<>();
        try {
            if (!serviceClass.validateInventory(inventory)) {
                response.put("message", "Data is already present");
            } else {
                inventoryRepository.save(inventory);
                response.put("message", "Data saved successfully");
            }
        } catch (DataIntegrityViolationException e) {
            response.put("Error", "Data integrity violation: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/{storeid}")
    public Map<String, Object> getAllProducts(@PathVariable Long storeid) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findProductsByStoreId(storeid);
        response.put("products", products);
        return response;
    }

    @GetMapping("filter/{category}/{name}/{storeid}")
    public Map<String, Object> getProductName(@PathVariable String category, @PathVariable String name, @PathVariable Long storeid) {
        Map<String, Object> response = new HashMap<>();
        List<Product> filteredProducts;

        if ("null".equalsIgnoreCase(category) && !"null".equalsIgnoreCase(name)) {
            filteredProducts = productRepository.findProductBySubName(name);
        } else if (!"null".equalsIgnoreCase(category) && "null".equalsIgnoreCase(name)) {
            filteredProducts = productRepository.findProductByCategory(category, storeid);
        } else if (!"null".equalsIgnoreCase(category) && !"null".equalsIgnoreCase(name)) {
            filteredProducts = productRepository.findProductBySubNameAndCategory(name, category);
        } else {
            filteredProducts = productRepository.findProductsByStoreId(storeid);
        }
        response.put("product", filteredProducts);
        return response;
    }

    @GetMapping("search/{name}/{storeId}")
    public Map<String, Object> searchProduct(@PathVariable String name, @PathVariable Long storeId) {
        Map<String, Object> response = new HashMap<>();
        List<Product> products = productRepository.findProductsByStoreId(storeId);
        response.put("product", products);
        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> removeProduct(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        if (!serviceClass.validateProductId(id)) {
            response.put("message", "Product not present in database");
            return response;
        }

        try {
            inventoryRepository.deleteByProductId(id);
            productRepository.deleteById(id);
            response.put("message", "Product deleted successfully");
        } catch (Exception e) {
            response.put("Error", "Failed to delete product: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("validate/{quantity}/{storeId}/{productId}")
    public boolean validateQuantity(@PathVariable Integer quantity, @PathVariable Long storeId, @PathVariable Long productId) {
        Inventory inventory = inventoryRepository.findByProductIdandStoreId(productId, storeId);
        return inventory != null && inventory.getStockLevel() >= quantity;
    }
}
