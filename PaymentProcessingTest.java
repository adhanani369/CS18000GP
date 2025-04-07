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
        double amountToAdd = 50.0;


        db.addUser("username", "pass", "bio");

        User user = db.getUserByUsername("username");

        user.depositFunds(initialBalance);


        boolean result = pp.addFunds(user.getUserId(), amountToAdd);
        assertTrue(result);
        assertEquals(150.0, user.getBalance(), 0.0001);
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

        db.addUser("username", "pass", "bio");
        User user = db.getUserByUsername("username");
        user.depositFunds(initialBalance); 
        

        boolean result = user.withdrawFunds(amount);
        assertTrue(result);
        assertEquals(50.0, user.getBalance(), 0.0001);
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


        db.addUser("buyer", "pass", "bio");
        db.addUser("seller", "pass", "bio");
        User buyer = db.getUserByUsername("buyer");
        User seller = db.getUserByUsername("seller");


        buyer.depositFunds(initialBuyerBalance);
        seller.depositFunds(initialSellerBalance);


        Item item = new Item(seller.getUserId(), "Item Title", "Item Description", "Category", price);
        boolean added = db.addItem(item);
        
        assertTrue("Item was not added successfully", added);


        boolean result = pp.processPurchase(buyer.getUserId(), item.getItemId());
        assertTrue(result);
        
        assertEquals(initialBuyerBalance - price, buyer.getBalance(), 0.0001);
        assertEquals(initialSellerBalance + price, seller.getBalance(), 0.0001);
        
        assertTrue(item.isSold());
    }

}
