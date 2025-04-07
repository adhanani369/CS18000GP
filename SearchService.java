import java.util.ArrayList;
import java.util.List;

/**
 * The SearchService class provides methods for searching items in the database.
 * Items are ranked by a score computed from keyword matches in the title,
 * description, and tags.
 * @author Rayaan Grewal
 * @version April 6th, 2024
 */
public class SearchService {
    private Database db;

    /**
     * Constructor for SearchService.
     */
    public SearchService(Database db) {
        this.db = db;
    }

    /**
     * Searches for items matching the given query.
     */
    public List<Item> search(String query) {
        return search(query, null, Integer.MAX_VALUE);
    }

    /**
     * Searches for items matching the given query and category.
     */
    public List<Item> search(String query, String category) {
        return search(query, category, Integer.MAX_VALUE);
    }

    /**
     * Searches for items matching the given query with a limit on the number of results.
     */
    public List<Item> search(String query, int maxResults) {
        return search(query, null, maxResults);
    }

    /**
     * Searches for items matching the given query and category, returning up to maxResults items.
     */
    public List<Item> search(String query, String category, int maxResults) {
        List<Item> allItems = db.getAllItems();
        List<Item> matchedItems = new ArrayList<Item>();
        List<Integer> scores = new ArrayList<Integer>();

        String[] keywords = query.toLowerCase().split("\\s+");

        for (int i = 0; i < allItems.size(); i++) {
            Item item = allItems.get(i);
            if (category != null && category.length() > 0) {
                if (!item.getCategory().equalsIgnoreCase(category)) {
                    continue;
                }
            }
            int score = 0;
            String title = item.getTitle().toLowerCase();
            String description = item.getDescription().toLowerCase();
            for (int j = 0; j < keywords.length; j++) {
                String kw = keywords[j];
                if (title.indexOf(kw) != -1) {
                    score += 3;
                }
                if (description.indexOf(kw) != -1) {
                    score += 1;
                }
                List<String> tags = item.getTags();
                if (tags != null) {
                    for (int k = 0; k < tags.size(); k++) {
                        String tag = tags.get(k).toLowerCase();
                        if (tag.indexOf(kw) != -1) {
                            score += 2;
                        }
                    }
                }
            }
            if (score > 0) {
                matchedItems.add(item);
                scores.add(score);
            }
        }

        // Bubble sort matchedItems in descending order by using the score.
        for (int i = 0; i < matchedItems.size() - 1; i++) {
            for (int j = i + 1; j < matchedItems.size(); j++) {
                if (scores.get(j) > scores.get(i)) {
                    Item tempItem = matchedItems.get(i);
                    matchedItems.set(i, matchedItems.get(j));
                    matchedItems.set(j, tempItem);
                    int tempScore = scores.get(i);
                    scores.set(i, scores.get(j));
                    scores.set(j, tempScore);
                }
            }
        }

        List<Item> result = new ArrayList<Item>();
        int limit = (maxResults < matchedItems.size()) ? maxResults : matchedItems.size();
        for (int i = 0; i < limit; i++) {
            result.add(matchedItems.get(i));
        }
        return result;
    }

    
}
