import com.example.retailinventory.models.Customer;
import com.example.retailinventory.models.Review;
import com.example.retailinventory.repository.CustomerRepository;
import com.example.retailinventory.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/{storeId}/{productId}")
    public Map<String, Object> getReviews(@PathVariable Long storeId, @PathVariable Long productId) {
        Map<String, Object> response = new HashMap<>();
        List<Review> reviews = reviewRepository.findByStoreIdAndProductId(storeId, productId);

        List<Map<String, Object>> reviewDetails = reviews.stream().map(review -> {
            Map<String, Object> details = new HashMap<>();
            details.put("comment", review.getComment());
            details.put("rating", review.getRating());

            Customer customer = customerRepository.findById(review.getCustomerId());
            details.put("customerName", customer != null ? customer.getName() : "Unknown");

            return details;
        }).collect(Collectors.toList());

        response.put("reviews", reviewDetails);
        return response;
    }
}
