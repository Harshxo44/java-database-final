import com.example.retailinventory.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Find a customer by their email address
    Customer findByEmail(String email);

    // Find a customer by their ID
    Customer findById(long id);
}
