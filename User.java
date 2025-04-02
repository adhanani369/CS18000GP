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
    }
    
    /**
     * Gets the user's unique identifier.
     */
    public String getUserId() {
        // Return userId
    }
    
    /**
     * Gets the user's username.
     */
    public String getUsername() {
        // Return username
    }
    
    /**
     * Validates if the provided password matches the user's password.
     */
    public boolean validatePassword(String password) {
        // Compare passwords and return result
    }
    
    /**
     * Adds funds to the user's balance.
     */
    public void depositFunds(double amount) {
        // Verify amount is positive
        // Add amount to balance
    }
    
    /**
     * Removes funds from the user's balance if sufficient funds are available.
     */
    public boolean withdrawFunds(double amount) {
        // Verify amount is positive
        // Check if balance is sufficient
        // Subtract amount if possible and return success/failure
    }
    
    /**
     * Gets the current balance of the user's account.
     */
    public double getBalance() {
        // Return balance
    }
    
    /**
     * Adds a new item to the user's active listings.
     */
    public void addListing(Item item) {
        // Verify item belongs to this user
        // Add item to activeListings
    }
    
    /**
     * Removes an item from the user's active listings.
     */
    public boolean removeListing(String itemId) {
        // Find and remove item with matching ID
        // Return success/failure
    }
    
    /**
     * Records a purchased item in the user's purchase history.
     */
    public void addToPurchaseHistory(Item item) {
        // Add item to purchaseHistory
    }
    
    /**
     * Records a sold item in the user's sold items history.
     */
    public void recordItemSold(Item item) {
        // Remove item from activeListings
        // Add item to soldItems
    }
    
    /**
     * Gets all items currently listed by the user.
     */
    public List<Item> getActiveListings() {
        // Return activeListings
    }
    
    /**
     * Gets all items purchased by the user.
     */
    public List<Item> getPurchaseHistory() {
        // Return purchaseHistory
    }
    
    /**
     * Gets all items sold by the user.
     */
    public List<Item> getSoldItems() {
        // Return soldItems
    }
}
