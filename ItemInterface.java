import java.util.List;

public interface ItemInterface {
    String getItemId();

    String getSellerId();

    String getTitle();

    String getDescription();

    String getCategory();

    List<String> getTags();

    double getPrice();

    double getRating();

    void updateRating(double newRating);

    boolean isSold();

    boolean markAsSold(String buyerId);

    String getBuyerId();

    List<String> getStopwords();

    List<String> extractTags(List<String> stopwords);

    void getSpecialCharacters();

    /*
     * Cleans the word from any commas or other special characters
     */
    String cleanWord(String word);
}
