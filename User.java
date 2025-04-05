import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class User implements UserInterface {
    private String userId;
    private String username;
    private String password;
    private String bio;
    private double balance;
    private ArrayList<Item> activeListings;
    private ArrayList<Item> purchaseHistory;
    private ArrayList<Item> soldItems;
    private Database database;


    /**
     * Creates a new user with the specified credentials.
     */
    public User(String username, String password, String bio, Database database) {
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
        this.database = database;

    }


    //Another Constructor to fill the user information.
    public User(String username, String password, String bio, double balance, ArrayList<Item> activeListings, ArrayList<Item> purchaseHistory, ArrayList<Item> soldItems, String existingUserId, Database database) {
        this.username = username;
        this.password = password;
        this.bio = bio;
        this.balance = balance;
        this.activeListings = activeListings !=null ? activeListings : new ArrayList<>();
        this.purchaseHistory = purchaseHistory != null ? purchaseHistory : new ArrayList<>();
        this.soldItems = soldItems != null ? soldItems : new ArrayList<>();
        this.userId = existingUserId !=null ? existingUserId : java.util.UUID.randomUUID().toString();
        this.database = database;

    }

    /**
     * Gets the user's bio.
     */
    @Override
    public String getBio() {
        return this.bio;
    }


    /**
     * Gets the user's unique identifier.
     */
    @Override
    public String getUserId() {
        return userId;
    }

    /**
     * Gets the user's username.
     */
    @Override
    public String getUsername() {
        return username;

    }

    /**
     * Validates if the provided password matches the user's password.
     */
    @Override
    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }

    /**
     * Adds funds to the user's balance.
     */
    @Override
    public void depositFunds(double amount) {
        // Verify amount is positive
        // Add amount to balance
        if (amount > 0) this.balance += amount;

    }

    /**
     * Removes funds from the user's balance if sufficient funds are available.
     */
    @Override
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
    @Override
    public double getBalance() {
        return this.balance;
    }

    /**
     * Adds a new item to the user's active listings.
     */
    @Override
    public void addListing(Item item) {
        if (item.getSellerId().equals(this.userId)) {
            this.activeListings.add(item);
        }
    }

    /**
     * Removes an item from the user's active listings.
     */
    @Override
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
    @Override
    public void addToPurchaseHistory(Item item) {
        this.purchaseHistory.add(item);
    }

    /**
     * Records a sold item in the user's sold items history.
     */
    @Override
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
     * Dynamically calculates active listings.
     */
    @Override
    public ArrayList<Item> getActiveListings() {
        // Filter items that belong to this user and are not sold
        return database.getAllItems().stream()
                .filter(item -> item.getSellerId().equals(this.userId) && !item.isSold())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Dynamically calculates purchase history.
     */
    @Override
    public ArrayList<Item> getPurchaseHistory() {
        // Filter items purchased by this user
        return database.getAllItems().stream()
                .filter(item -> item.isSold() && item.getBuyerId().equals(this.userId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Dynamically calculates sold items.
     */
    @Override
    public ArrayList<Item> getSoldItems() {
        // Filter items sold by this user
        return database.getAllItems().stream()
                .filter(item -> item.getSellerId().equals(this.userId) && item.isSold())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Retrieves the user password
     * */
    public String getPassword() {
        return this.password;
    }
}
