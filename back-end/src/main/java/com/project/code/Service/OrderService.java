import com.example.retailinventory.dto.PlaceOrderRequestDTO;
import com.example.retailinventory.models.*;
import com.example.retailinventory.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Transactional
    public void saveOrder(PlaceOrderRequestDTO placeOrderRequest) {

        // 1. Retrieve or Create the Customer
        Customer customer = customerRepository.findByEmail(placeOrderRequest.getEmail());
        if (customer == null) {
            customer = new Customer();
            customer.setName(placeOrderRequest.getName());
            customer.setEmail(placeOrderRequest.getEmail());
            customer.setPhone(placeOrderRequest.getPhone());
            customer = customerRepository.save(customer);
        }

        // 2. Retrieve the Store
        Store store = storeRepository.findById(placeOrderRequest.getStoreId());
        if (store == null) {
            throw new RuntimeException("Store not found with ID: " + placeOrderRequest.getStoreId());
        }

        // 3. Create and Save OrderDetails
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCustomer(customer);
        orderDetails.setStore(store);
        orderDetails.setDate(LocalDateTime.now());
        orderDetails.setTotalPrice(placeOrderRequest.getTotalPrice());
        orderDetailsRepository.save(orderDetails);

        // 4. Create and Save OrderItems and Update Inventory
        for (PlaceOrderRequestDTO.PurchaseItem item : placeOrderRequest.getPurchaseItems()) {
            Inventory inventory = inventoryRepository.findByProductIdandStoreId(item.getProductId(), store.getId());
            if (inventory == null || inventory.getStockLevel() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product ID: " + item.getProductId());
            }

            // Decrease stock level
            inventory.setStockLevel(inventory.getStockLevel() - item.getQuantity());
            inventoryRepository.save(inventory);

            // Create OrderItem
            Product product = productRepository.findById(item.getProductId());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(orderDetails);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(product.getPrice()); // Assuming price is constant at time of order
            orderItemRepository.save(orderItem);
        }
    }
}
