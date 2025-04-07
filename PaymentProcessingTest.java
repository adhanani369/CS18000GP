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
        assertEquals(150, user.getBalance());

    }

    /*
     * sufficient Funds: withdrow funds from a user's account
     *
     */
    @Test
    public void testWithdrawFunds_success() {
        Database db = new Database();
        PaymentProcessing pp = new PaymentProcessing(db);
        double initialBalance = 100.0;
        double amount = 50.0;

        User user = new User("username", "pass", "bio", db);
        user.depositFunds(initialBalance);  // Set the initial balance to 100.0
        db.addUser("username", "pass", "bio");

        boolean result = user.withdrawFunds(amount);
        assertTrue(result);
        assertEquals(50, user.getBalance());
    }

    /*
     * testing a purchase transaction between buyer and seller.
     *
     */
    @Test
    public void processPurchase() {
        Database db = new Database();
        PaymentProcessing pp = new PaymentProcessing(db);
        double initialBuyerBalance = 100.0;
        double initialSellerBalance = 100.0;

        double price = 20.0;

        User buyer = new User("buyer", "pass", "bio", db);
        User seller = new User("seller", "pass", "bio", db);
        buyer.depositFunds(initialBuyerBalance);
        buyer.depositFunds(initialSellerBalance);

        Item item = new Item(seller.getUserId(), "Item Title", "Item Description", "Category", price);
        db.addItem(item);
        db.addUser("buyer", "pass", "bio");
        db.addUser("seller", "pass", "bio");

        boolean result = pp.processPurchase(buyer.getUserId(), item.getItemId());
        assertTrue(result);
        assertEquals(initialBuyerBalance - price, buyer.getBalance());
        assertEquals(initialSellerBalance + price, seller.getBalance());
        assertTrue(item.isSold());
    }

}
