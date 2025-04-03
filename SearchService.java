import java.util.List;

public class SearchService {
    private Database database;
    
    /**
     * Creates a new SearchService instance.
     */
    public SearchService(Database database) {
        // Store reference to database
    }
    
    /**
     * Searches for items matching the given query.
     */
    public List<Item> search(String query) {
        // Extract keywords from query
        // Find items with matching keywords in title, description, or tags
        // Return matching items
    }
    
    /**
     * Searches for items in a specific category.
     */
    public List<Item> search(String query, String category) {
        // Find items matching query and category
        // Return matching items
    }
    
    /**
     * Searches with a limit on results.
     */
    public List<Item> search(String query, int maxResults) {
        // Find items matching query
        // Limit results to maxResults
        // Return matching items
    }
    
    /**
     * Full search with all parameters.
     */
    public List<Item> search(String query, String category, int maxResults) {
        // Find items matching query and category
        // Limit results to maxResults
        // Return matching items
    }
}
