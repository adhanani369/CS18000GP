import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Item {
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
        this.description = description;
        this.category = category;
        this.price = price;
        this.sold = false;
        this.buyerId = null;
        this.ratingCount = 0;
        this.itemId = Integer.toString(++Item.itemCount);   
        this.tags = this.getTags(this.getStopwords());

    }
    
    /**
     * Gets the item's unique identifier.
     */
    public String getItemId() {
        // Return itemId
        return this.itemId;
    }
    
    /**
     * Gets the ID of the user selling this item.
     */
    public String getSellerId() {
        // Return sellerId
        return this.sellerId;
    }
    
    /**
     * Gets the title of this item.
     */
    public String getTitle() {
        // Return title
        return this.title;
    }
    
    /**
     * Gets the description of this item.
     */
    public String getDescription() {
        // Return description
        return this.description;
    }
    
    /**
     * Gets the category of this item.
     */
    public String getCategory() {
        // Return category
        return this.category;
    }
    
    /**
     * Gets the tags associated with this item.
     */
    public List<String> getTags() {
        // Return tags
        return this.tags;
    }
    
    /**
     * Gets the price of this item.
     */
    public double getPrice() {
        // Return price
        return this.price;
    }
    
    /**
     * Gets the current rating of this item.
     */
    public double getRating() {
        // Return rating
        return this.rating;
    }

    
    /**
     * Updates the item's rating with a new user rating.
     */
    public void updateRating(double newRating) {
        // Calculate new average rating
        double currentRating = this.getRating(); // Tracks the current rating
        double newAverageRating = (ratingCount * currentRating + newRating) / (++ratingCount); // Calculates the new rating
        this.rating = newAverageRating;
    }
    
    /**
     * Checks whether this item has been sold.
     */
    public boolean isSold() {
        // Return sold status
        return this.sold;
    }

    
    /**
     * Marks this item as sold to the specified buyer.
     */
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
    public String getBuyerId() {
        // Return buyerId
        return this.buyerId;
    }

    public List<String> extractTags() {
        // Return tags
        return tags;
    } 

    public List<String> getStopwords() {
        System.out.println(new File(".").getAbsolutePath());
        try (BufferedReader br = new BufferedReader(new FileReader("stopword.txt"))) {
            String stopWordsRaw = br.readLine(); // Gets the uncleaned version of all the stop words
            return Arrays.asList(stopWordsRaw.split(",")); // Splits and returns an array containing the stop words
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getTags(List<String> stopwords) {
        List<String> descriptionWords = Arrays.asList(this.description.split(" ")); // Splits the description to indivitual words
        List<String> finalTagList = new ArrayList<>();
        for (String word : descriptionWords) {
            if (stopwords.contains(word.toLowerCase()) == false) {
                finalTagList.add(cleanWord(word));
            }
        }
        return finalTagList;
    }

    /*
     * Cleans the word from any commas or other special characters
     */
    public String cleanWord(String word) {
        List<String> finalSpecialCharacters = null;
        try (BufferedReader br = new BufferedReader(new FileReader("special_characters.txt"))) {
            String characters = br.readLine(); // Gets the uncleaned version of all the stop words
            finalSpecialCharacters = Arrays.asList(characters.split(" ")); // Splits and returns an array containing the special characters
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        for (String character : finalSpecialCharacters) {
            int count = word.indexOf(character);
            if (count > 0) {
                word = word.replaceAll("\\Q" + character + "\\E", "");
            }
        }
        return word;
    }

}
