import java.util.List;
import java.util.ArrayList;

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
        if (user == null || user.getActiveListings().isEmpty()) {
            System.out.println("User not find!");
            return null;
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
        String keyword = searchTerm.toLowerCase();

        for (User user : allUsers) {
            // Check if the user's ID contains the search term
            if (user.getUserId().toLowerCase().contains(keyword)) {
                matchedSellers.add(user);
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
