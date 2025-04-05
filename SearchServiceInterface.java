import java.util.List;

/**
 * Interface for the SearchService class.
 */
public interface SearchServiceInterface {
    List<Item> search(String query);
    List<Item> search(String query, String category, int maxResults);
    List<Item> search(String query, String category);
    List<Item> search(String query, int maxResults);
}
