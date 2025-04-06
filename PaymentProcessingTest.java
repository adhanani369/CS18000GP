import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class PaymentProcessingTest {
    @Test
    public void testProcessPayment() {
        Database database = new Database();
        User buyer = new User("sarah", "123", "hello", database);
        User seller = new User("papa", "123", "hello", database);
        Item item = new Item(seller.getUserId(), "dog", "genya", "animal", 30);
        database.addItem(item);
        //assertEquals(0, result);
    }
}


