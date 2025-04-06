import java.util.List;

public class SearchBar implements SearchBarInterface {
    private Database database;
    
    /**
     * Creates a new SearchBar instance for finding sellers.
     */
    public SearchBar(Database database) {
        // Store reference to database
        this.database = database;
    }

    /**
     * Searches for a seller by their exact user ID.
     *
     * @param userId The user ID to search for
     * @return The User object if found, null otherwise
     */
    @Override
    public User findSellerById(String userId) {
        // Get user from database by ID
        // Check if user has any active listings (is a seller)
        // Return user if they are a seller, otherwise null
        User user = database.getUserById(userId); 
        if (user = null || user.getListings().isEmpty()) {
            System.out.println("User not find!");
            return;
        }
        return user;
    }

    /**
     * Searches for sellers whose user IDs contain the search term.
     *
     * @param searchTerm The term to search for in user IDs
     * @return List of users whose IDs contain the search term and who have listings
     */
    @Override
    public List<User> searchSellersByPartialId(String searchTerm) {
        // Get all users from database
        // Filter for users that have active listings
        // Filter by userID containing searchTerm
        // Return matching sellers
        // WE DONT NEED TO IMPLEMENT IT FOR THE START ONLY IF WE THINK THIS WILL WORK AND IS CUTE!!! EXTRA FEATURE TO MAKE THE PROJECT MORE REAL AND COMPLELLING!
        List<User> allUsers = database.getAllUsers();  
        List<User> matchedSellers = new ArrayList<>();
        List<Integer> relevanceRateList = new ArrayList<>();
        String keyword = searchTerm.toLowerCase();
        
        // get matching seller list based on search word, description, tag
        for (User user : allUsers) {
            List<Item> itemListing = user.getActiveListings();
            boolean ifMatch = false;
            int relevanceRate  = 0;

            for (Item item : itemListing) {
                String title = item.getTitle().toLowerCase();
                String description = item.getDescription().toLowerCase();
                List<String> tags = item.getTags();

                if (title.contains(keyword)) relevanceRate  += 3;
                if (description.contains(keyword)) relevanceRate  += 1;
                
                if (tags != null) {
                    for (String tag : tags) {
                        if (tag.toLowerCase().contains(keyword)) {
                            relevanceRate  += 2;
                        }
                    }
                }
            }
            if (relevanceRate  > 0) {
                    matchedSellers.add(user);
                    relevanceRateList.add(relevanceRate );
                }
        }
        
        // Sort the matched list based on their relevanceRate
        for (int i = 0; i < matchedSellers.size() - 1; i++) {
            for (int j = i + 1; j < matchedSellers.size(); j++) {
                if (relevanceRateList.get(j) > relevanceRateList.get(i)) {
                    // swap sellers
                    User someone = matchedSellers.get(i);
                    matchedSellers.set(i, matchedSellers.get(j));
                    matchedSellers.set(j, someone);
    
                    // swap relevanceRate
                    int someRate = scores.get(i);
                    scores.set(i, scores.get(j));
                    scores.set(j, someRate);
                }
            }
        }

        return matchedSellers;
    }
    
    /**
     * Gets all active sellers in the marketplace.
     * 
     * @return List of all users who have at least one active listing
     */
    @Override
    public List<User> getAllActiveSellers() {
        // Get all users from database
        // Filter for users that have at least one active listing
        // Return list of active sellers
        // we could use this to reduce burden on the database and faster the code if we want to make the code a little faster!! Again only a cute thing to have not necessary!
        List<User> allUsers = database.getAllUsers();
        List<User> activeSellers = new ArrayList<>();

        for (User user : allUsers) {
            if (!user.getActiveListings().isEmpty()) {
                activeSellers.add(user);
            }
        }
    
        return activeSellers;
        }
}
