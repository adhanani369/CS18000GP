/**
 * Interface for Client class
 * @author Rayaan Grewal
 * @version April 20, 2024
 */


public interface ClientInterface {
	boolean connect();
	void disconnect();
	String sendMessage(String message);
	String register(String username, String password, String bio);
	String login(String username, String password);
	String deleteAccount(String userId);
	String addItem(String sellerId, String title, String description, String category, double price);
	String getItem(String itemId);
	String searchItems(String query, String category, int maxResults);
	String getUserListings(String userId, boolean activeOnly);
	String markSold(String itemId, String buyerId);
	String removeItem(String itemId, String sellerId);
	String sendMessageToUser(String senderId, String receiverId, String content, String itemId);
	String getMessages(String buyerId, String sellerId);
	String getConversations(String userId);
	String addFunds(String userId, double amount);
	String withdrawFunds(String userId, double amount);
	String processPurchase(String buyerId, String itemId);
	String rateSeller(String sellerId, double rating);
	String getRating(String sellerId);
	String getAllUsers();
	String getActiveSellers();
	String getCurrentUserId();
	void setCurrentUserId(String userId);
	String getMyRating(String userId);
}
