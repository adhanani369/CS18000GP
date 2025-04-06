<<<<<<< HEAD
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
=======
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
>>>>>>> 16e0052fec1ef3841a863abcb5a0b6a159cc32cc
}
