import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private String username;
    private String password;
    private String bio;
    private double balance;
    private ArrayList<Item> activeListings;
    private ArrayList<Item> purchaseHistory;
    private ArrayList<Item> soldItems;

    /**
     * Creates a new user with the specified credentials.
     */
    public User(String username, String password, String bio) {
        // Initialize user with username, password, bio
        // Set initial balance to zero
        // Create empty lists for listings, purchases, and sales
        this.username = username;
        this.password = password;
        this.bio = bio;
        this.balance = 0;
        this.soldItems = new ArrayList<Item>();
        this.purchaseHistory = new ArrayList<Item>();
        this.activeListings = new ArrayList<Item>();
        this.userId = java.util.UUID.randomUUID().toString();

    }
    /**
     * Gets the user's bio.
     */
    public String getBio() {
        return this.bio;
    }


    /**
     * Gets the user's unique identifier.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the user's username.
     */
    public String getUsername() {
        return username;

    }

    /**
     * Validates if the provided password matches the user's password.
     */
    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }

    /**
     * Adds funds to the user's balance.
     */
    public void depositFunds(double amount) {
        // Verify amount is positive
        // Add amount to balance
        if (amount > 0) this.balance += amount;

    }

    /**
     * Removes funds from the user's balance if sufficient funds are available.
     */
    public boolean withdrawFunds(double amount) {
        if (amount > 0 && amount <= balance) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    /**
     * Gets the current balance of the user's account.
     */
    public double getBalance() {
        return this.balance;
    }

    /**
     * Adds a new item to the user's active listings.
     */
    public void addListing(Item item) {
        if (item.getSellerId().equals(this.userId)) {
            this.activeListings.add(item);
        }
    }

    /**
     * Removes an item from the user's active listings.
     */
    public boolean removeListing(String itemId) {
        // Find and remove item with matching ID
        // Return success/failure
        for (int i = 0; i < this.activeListings.size(); ++i) {
            if (this.activeListings.get(i).getItemId().equals(itemId)){
                this.activeListings.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Records a purchased item in the user's purchase history.
     */
    public void addToPurchaseHistory(Item item) {
        this.purchaseHistory.add(item);
    }

    /**
     * Records a sold item in the user's sold items history.
     */
    public void recordItemSold(Item item) {
        // Remove item from activeListings
        // Add item to soldItems
        if (this.activeListings.contains(item)) {
            int index = this.activeListings.indexOf(item);
            Item temp = this.activeListings.get(index);
            this.activeListings.remove(index);
            this.soldItems.add(temp);
        }
    }

    /**
     * Gets all items currently listed by the user.
     */
    public ArrayList<Item> getActiveListings() {
        return activeListings;
    }

    /**
     * Gets all items purchased by the user.
     */
    public ArrayList<Item> getPurchaseHistory() {
        return purchaseHistory;
    }

    /**
     * Gets all items sold by the user.
     */
    public ArrayList<Item> getSoldItems() {
        return soldItems;
    }
}
