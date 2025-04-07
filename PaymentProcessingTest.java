import static org.junit.Assert.*;
import java.util.List;
import org.junit.Test;

public class PaymentProcessingTest {

    /*
     * check for funds to a user's account before and after adding
     *
     */
    @Test
    public void testAddFunds() {
        Database db = new Database();
        PaymentProcessing pp = new PaymentProcessing(db);
        double initialBalance = 100.0;
        double amount = 0.0;

        User user = new User("username", "pass", "bio", db);
        user.depositFunds(initialBalance);  // Set the initial balance to 100.0
        db.addUser("username", "pass", "bio");

        boolean result = pp.addFunds(user.getUserId(), amount);
        assertTrue(result);
    }

}
