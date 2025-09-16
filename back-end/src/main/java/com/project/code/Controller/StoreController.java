import com.example.retailinventory.dto.PlaceOrderRequestDTO;
import com.example.retailinventory.models.Store;
import com.example.retailinventory.repository.StoreRepository;
import com.example.retailinventory.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private OrderService orderService;

    @PostMapping
    public Map<String, String> addStore(@RequestBody Store store) {
        Map<String, String> response = new HashMap<>();
        Store savedStore = storeRepository.save(store);
        response.put("message", "Store with ID " + savedStore.getId() + " created successfully");
        return response;
    }

    @GetMapping("validate/{storeId}")
    public boolean validateStore(@PathVariable Long storeId) {
        return storeRepository.findById(storeId).isPresent();
    }

    @PostMapping("/placeOrder")
    public Map<String, String> placeOrder(@RequestBody PlaceOrderRequestDTO placeOrderRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            orderService.saveOrder(placeOrderRequest);
            response.put("message", "Order placed successfully");
        } catch (Exception e) {
            response.put("Error", e.getMessage());
        }
        return response;
    }
}
