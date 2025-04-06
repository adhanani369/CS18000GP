import java.io.IOException;

/*
 * This is the payment processing class through which the user can add,
 * withdraw and process the purchase.
 *
 * @Sarah Epelbaum
 *
 * April 6th, 2025
 *
 */

public class PaymentProcessing implements PaymentProcessingInterface {
    private Database database;
    
    /**
     * Creates a new PaymentProcessing instance.
     */
    public PaymentProcessing(Database database) {
        // Store reference to database
        this.database = database;
    }
    
    /**
     * Adds funds to a user's account.
     */
    @Override
    public boolean addFunds(String userId, double amount) {
        // Find user by ID
        // Call user.depositFunds()
        // Return success/failure
        try {
            User user = database.getUserById(userId);
            user.depositFunds(amount);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Withdraws funds from a user's account.
     */
    @Override
    public boolean withdrawFunds(String userId, double amount) {
        // Find user by ID
        // Call user.withdrawFunds()
        // Return success/failure
        try {
            User user = database.getUserById(userId);
            return user.withdrawFunds(amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Processes a purchase transaction between buyer and seller.
     */
    @Override
    public boolean processPurchase(String buyerId, String itemId) {
        // Find buyer, item, and seller
        // Check if buyer has sufficient funds
        // Withdraw funds from buyer
        // Add funds to seller
        // Mark item as sold
        // Update purchase/sale records
        // Return success/failure
        try {
            User buyer = database.getUserById(buyerId);
            Item item = database.getItemById(itemId);
            User seller = database.getUserById(item.getSellerId());
            if (buyer == null || item == null || seller == null) {
                return false;
            }
            double price = item.getPrice();
            double balance = buyer.getBalance();
            if (balance >= price) {
                buyer.withdrawFunds(price);
                seller.depositFunds(price);
                item.markAsSold(buyerId);
                seller.recordItemSold(item);
                buyer.addToPurchaseHistory(item);

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
