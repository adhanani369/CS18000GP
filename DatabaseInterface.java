import java.util.List;
import java.util.Map;

/**
 * Interface for Database class
 * @author Rayaan Grewal
 * @version April 6, 2024
 */
public interface DatabaseInterface {
    boolean addUser(String username, String password, String bio);
    boolean login(String data);
  
    
    User userExists(String username);
    void readUserFile();
  
    void writeUserFile();
    void writeItemFile();
    void readMessageFiles();
  
    List<User> getAllUsers();
    boolean addItem(Item item);
  
    User getUserByUsername(String username);
    User getUserById(String userId);
    Item getItemById(String itemId);
  
    List<Item> getAllItems();
    List<Item> getActiveItems();
  
    boolean addMessage(Message message, String itemId);
    boolean addMessage(Message message);
  
    List<Message> getMessagesBetweenBuyerAndSeller(String buyerId, String sellerId);
    Map<String, List<String>> getUserRoleBasedConversations(String userId);
  
    List<String> getAllConversationPartners(String userId);
    
  
    void writeMessageFile();
  
    void readItemFile();
  
    boolean removeItem(String itemId, String requesterId);
    List<Message> getMessagesBetweenUsers(String user1Id, String user2Id);
}
