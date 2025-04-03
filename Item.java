import java.util.List;

public class Item {
    private String itemId;
    private String sellerId;
    private String title;
    private String description;
    private String category;
    private List<String> tags;
    private double price;
    private double rating;
    private boolean sold;
    private String buyerId;
    
    /**
     * Creates a new item listing with the specified details.
     */
    public Item(String sellerId, String title, String description, String category, double price) {
        // Initialize item with provided details
        // Generate a simple ID
        // Set sold to false and buyerId to null
        // Extract tags from description
    }
    
    /**
     * Gets the item's unique identifier.
     */
    public String getItemId() {
        // Return itemId
    }
    
    /**
     * Gets the ID of the user selling this item.
     */
    public String getSellerId() {
        // Return sellerId
    }
    
    /**
     * Gets the title of this item.
     */
    public String getTitle() {
        // Return title
    }
    
    /**
     * Gets the description of this item.
     */
    public String getDescription() {
        // Return description
    }
    
    /**
     * Gets the category of this item.
     */
    public String getCategory() {
        // Return category
    }
    
    /**
     * Gets the tags associated with this item.
     */
    public List<String> getTags() {
        // Return tags
    }
    
    /**
     * Gets the price of this item.
     */
    public double getPrice() {
        // Return price
    }
    
    /**
     * Gets the current rating of this item.
     */
    public double getRating() {
        // Return rating
    }
    
    /**
     * Updates the item's rating with a new user rating.
     */
    public void updateRating(double newRating) {
        // Calculate new average rating
    }
    
    /**
     * Checks whether this item has been sold.
     */
    public boolean isSold() {
        // Return sold status
    }
    
    /**
     * Marks this item as sold to the specified buyer.
     */
    public boolean markAsSold(String buyerId) {
        // If not already sold:
        // Set sold to true
        // Set buyerId
        // Return success/failure
    }
    
    /**
     * Gets the ID of the user who purchased this item.
     */
    public String getBuyerId() {
        // Return buyerId
    }
}
