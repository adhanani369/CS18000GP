import java.util.ArrayList;

public interface UserInterface {
    String getBio();

    String getUserId();

    String getUsername();

    boolean validatePassword(String password);

    void depositFunds(double amount);

    boolean withdrawFunds(double amount);

    double getBalance();

    void addListing(Item item);

    boolean removeListing(String itemId);

    void addToPurchaseHistory(Item item);

    void recordItemSold(Item item);

    ArrayList<Item> getActiveListings();

    ArrayList<Item> getPurchaseHistory();

    ArrayList<Item> getSoldItems();
}
