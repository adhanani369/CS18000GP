import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * This is the item class where the user can create a items 
 * which can put on the market and sold to other users.
 * 
 * @frahman284
 * 
 * April 5th, 2025
 * 
 */
public class Item implements ItemInterface{

    private static int itemCount;
    private String itemId;
    private String sellerId;
    private String title;
    private String description;
    private String category;
    private List<String> tags;
    private double price;
    private double rating;
    private int ratingCount;
    private boolean sold;
    private String buyerId;
    private List<String> specialCharacters;
    
    /**
     * Creates a new item listing with the specified details.
     */
    public Item(String sellerId, String title, String description, String category, double price) {
        // Initialize item with provided details
        // Generate a simple ID
        // Set sold to false and buyerId to null
        // Extract tags from description
        this.sellerId = sellerId;
        this.title = title;
        this.category = category;
        this.price = price;
        this.sold = false;
        this.buyerId = null;
        this.ratingCount = 0;
        this.itemId = Integer.toString(++Item.itemCount);   
        this.description = (description == null) ? ("") : (description);
        this.tags = this.extractTags(this.getStopwords());

    }
    
    /**
     * Gets the item's unique identifier.
     */ 
    @Override
    public String getItemId() {
        // Return itemId
        return this.itemId;
    }
    
    /**
     * Gets the ID of the user selling this item.
     */
    @Override
    public String getSellerId() {
        // Return sellerId
        return this.sellerId;
    }
    
    /**
     * Gets the title of this item.
     */
    @Override
    public String getTitle() {
        // Return title
        return this.title;
    }
    
    /**
     * Gets the description of this item.
     */
    @Override
    public String getDescription() {
        // Return description
        return this.description;
    }
    
    /**
     * Gets the category of this item.
     */
    @Override
    public String getCategory() {
        // Return category
        return this.category;
    }
    
    /**
     * Gets the tags associated with this item.
     */
    @Override
    public List<String> getTags() {
        // Return tags
        return this.tags;
    }
    
    /**
     * Gets the price of this item.
     */
    @Override
    public double getPrice() {
        // Return price
        return this.price;
    }
    
    /**
     * Gets the current rating of this item.
     */
    @Override
    public double getRating() {
        // Return rating
        return this.rating;
    }

    
    /**
     * Updates the item's rating with a new user rating.
     */
    @Override
    public void updateRating(double newRating) {
        // Calculate new average rating
        if (newRating > 5) {
            System.out.println("Invalid Rating");
            return;
        }
        double currentRating = this.getRating(); // Tracks the current rating
        double newAverageRating = (ratingCount * currentRating + newRating) / (++this.ratingCount); // new rating
        this.rating = newAverageRating;
    }
    
    /**
     * Checks whether this item has been sold.
     */
     @Override
     public boolean isSold() {
        // Return sold status
        return this.sold;
    }

    
    /**
     * Marks this item as sold to the specified buyer.
     */
    @Override
    public boolean markAsSold(String buyerId) {
        // If not already sold:
        // Set sold to true
        // Set buyerId
        // Return success/failure
        if (this.sold == false) {
            this.sold = true;
            this.buyerId = buyerId;
            return true; // Marks successfull
        }
        return false; // Marks unsuccessfull
    }
    
    /**
     * Gets the ID of the user who purchased this item.
     */
    @Override
    public String getBuyerId() {
        // Return buyerId
        return this.buyerId;
    }
    
    /*
     * Gets the stop words from reading the stopwords.txt file
     */
    public List<String> getStopwords() {
        try (BufferedReader br = new BufferedReader(new FileReader("stopword.txt"))) {
            String stopWordsRaw = br.readLine(); // Gets the uncleaned version of all the stop words
            return Arrays.asList(stopWordsRaw.split(",")); // Splits and returns an array containing the stop words
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Extracts the tags from the description and cleans each word
     */
    public List<String> extractTags(List<String> stopwords) {
        List<String> descriptionWords = Arrays.asList(this.description.split("[- ]")); // Splits description to words
        List<String> finalTagList = new ArrayList<>(); // Tracks the final list of tags
        for (String word : descriptionWords) {
            if (stopwords.contains(word.toLowerCase()) == false) {
                finalTagList.add(cleanWord(word));
            }
        }
        return finalTagList;
    }

    /*
     * Gets the special characters from the specialCharacters.txt file
     */
    public void getSpecialCharacters() {
        try (BufferedReader br = new BufferedReader(new FileReader("special_characters.txt"))) {
            String characters = br.readLine(); // Gets the uncleaned version of all the stop words
            this.specialCharacters = Arrays.asList(characters.split(" ")); // returns array with special characters
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Cleans the word from any commas or other special characters
     */
    public String cleanWord(String word) {

        for (String character : this.specialCharacters) {
            int count = word.indexOf(character); // Tracks the index to see if the word contain special character
            if (count != -1) {
                word = word.replaceAll("\\".concat(character), "");
            }
        }
        System.out.println(word);
        return word;
    }

}
