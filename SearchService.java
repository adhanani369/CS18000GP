import java.util.ArrayList;
import java.util.List;

public class SearchService {
    private Database db;

    public SearchService(Database db) {
        this.db = db;
    }

    public List<Item> search(String query) {
        return search(query, null, Integer.MAX_VALUE);
    }

    public List<Item> search(String query, String category) {
        return search(query, category, Integer.MAX_VALUE);
    }

    public List<Item> search(String query, int maxResults) {
        return search(query, null, maxResults);
    }

    public List<Item> search(String query, String category, int maxResults) {
        List<Item> result = new ArrayList<>();
        String[] keywords = query.toLowerCase().split("\\s+");

        for (Item item : db.getAllItems()) {
            if (category != null && !category.isEmpty() &&
                !item.getCategory().equalsIgnoreCase(category)) {
                continue;
            }

            String title = item.getTitle().toLowerCase();
            String description = item.getDescription().toLowerCase();
            
            boolean found = false;

            for (String kw : keywords) {
                if (title.contains(kw) || description.contains(kw)) {
                    found = true;
                    break;
                }
                List<String> tags = item.getTags();
                if (tags != null) {
                    for (String tag : tags) {
                        if (tag.toLowerCase().contains(kw)) {
                            found = true;
                            break;
                        }
                    }
                }
                if (found)
                    break;
            }

            if (found) {
                result.add(item);
            }

            if (result.size() >= maxResults) {
                break;
            }
        }

        return result;
    }
}
