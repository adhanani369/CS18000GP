import java.util.List;

/**
 * Interface for the User class.
 */
public interface UserInterface {
    String getUserId();
    String getUsername();
    void addListing(Item item);
    boolean removeListing(String itemId);
    List<Item> getActiveListings();
    List<Item> getPurchaseHistory();
    List<Item> getSoldItems();
    String getBio();
    String getPassword();
    boolean validatePassword(String password);
    void depositFunds(double amount);
    boolean withdrawFunds(double amount);
    double getBalance();
}
