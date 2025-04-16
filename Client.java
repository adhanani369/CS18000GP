import java.io.*;
import java.net.*;


public class Client {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String currentUserId;
    private final String SERVER_ADDRESS = "localhost";
    private final int SERVER_PORT = 1234;


    public Client() {
        this.socket = null;
        this.reader = null;
        this.writer = null;
        this.currentUserId = null;
        System.out.println("Client created");
    }

    public boolean connect() {
        try {
            System.out.println("Connecting to server at " + SERVER_ADDRESS + ":" + SERVER_PORT);
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            System.out.println("Connected to server successfully");
            return true;
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        try {
            System.out.println("Disconnecting from server");

            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            System.out.println("Disconnected from server");
        } catch (IOException e) {
            System.out.println("Disconnect error: " + e.getMessage());
        }
    }

    public String sendMessage(String message) {
        try {
            System.out.println("Sending: " + message);

            writer.write(message);
            writer.newLine();
            writer.flush();

            String response = reader.readLine();
            System.out.println("Received: " + response);

            return response;
        } catch (IOException e) {
            System.out.println("Communication error: " + e.getMessage());
            return "ERROR,Communication failure";
        }
    }

    public String register(String username, String password, String bio) {
        System.out.println("Registering user: " + username);
        String message = "REGISTER," + username + "," + password + "," + bio;
        return sendMessage(message);
    }

    public String login(String username, String password) {
        System.out.println("Logging in user: " + username);
        String message = "LOGIN," + username + "," + password;
        String response = sendMessage(message);

        // Extract user ID if login successful
        String[] parts = response.split(",");
        if (parts.length >= 3 && parts[1].equals("SUCCESS")) {
            currentUserId = parts[2];
            System.out.println("Login successful. User ID: " + currentUserId);
        }

        return response;
    }

    public String deleteAccount(String username) {
        System.out.println("Deleting account: " + username);
        String message = "DELETE_ACCOUNT," + username;
        return sendMessage(message);
    }

    public String addItem(String sellerId, String title, String description, String category, double price) {
        System.out.println("Adding item: " + title + " (Price: $" + price + ")");
        String message = "ADD_ITEM," + sellerId + "," + title + "," + description + "," + category + "," + price;
        return sendMessage(message);
    }

    public String getItem(String itemId) {
        System.out.println("Getting item: " + itemId);
        String message = "GET_ITEM," + itemId;
        return sendMessage(message);
    }

    public String searchItems(String query, String category, int maxResults) {
        System.out.println("Searching for items: " + query);
        String message = "SEARCH_ITEMS," + query + "," + (category == null ? "" : category) + "," + maxResults;
        return sendMessage(message);
    }

    public String markSold(String itemId, String buyerId) {
        System.out.println("Marking item sold: " + itemId + " to buyer " + buyerId);
        String message = "MARK_SOLD," + itemId + "," + buyerId;
        return sendMessage(message);
    }

    public String removeItem(String itemId, String sellerId) {
        System.out.println("Removing item: " + itemId);
        String message = "REMOVE_ITEM," + itemId + "," + sellerId;
        return sendMessage(message);
    }

    public String sendMessageToUser(String senderId, String receiverId, String content, String itemId) {
        System.out.println("Sending message to user " + receiverId);
        String message = "SEND_MESSAGE," + senderId + "," + receiverId + "," + content + "," + itemId;
        return sendMessage(message);
    }

    public String getMessages(String buyerId, String sellerId) {
        System.out.println("Getting messages between " + buyerId + " and " + sellerId);
        String message = "GET_MESSAGES," + buyerId + "," + sellerId;
        return sendMessage(message);
    }

    public String getConversations(String userId) {
        System.out.println("Getting conversations for user " + userId);
        String message = "GET_CONVERSATIONS," + userId;
        return sendMessage(message);
    }

    public String addFunds(String userId, double amount) {
        System.out.println("Adding funds: $" + amount + " to user " + userId);
        String message = "ADD_FUNDS," + userId + "," + amount;
        return sendMessage(message);
    }

    public String withdrawFunds(String userId, double amount) {
        System.out.println("Withdrawing funds: $" + amount + " from user " + userId);
        String message = "WITHDRAW_FUNDS," + userId + "," + amount;
        return sendMessage(message);
    }

    public String processPurchase(String buyerId, String itemId) {
        System.out.println("Processing purchase of item " + itemId + " by buyer " + buyerId);
        String message = "PROCESS_PURCHASE," + buyerId + "," + itemId;
        return sendMessage(message);
    }

    public String rateSeller(String sellerId, double rating) {
        System.out.println("Rating seller " + sellerId + " with " + rating + " stars");
        String message = "RATE_SELLER," + sellerId + "," + rating;
        return sendMessage(message);
    }

    public String getRating(String sellerId) {
        System.out.println("Getting rating for seller " + sellerId);
        String message = "GET_RATING," + sellerId;
        return sendMessage(message);
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String userId) {
        this.currentUserId = userId;
    }


    public static void main(String[] args) {
        Client client = new Client();

        try {
            // Test connection
            if (!client.connect()) {
                System.out.println("Failed to connect to server. Exiting.");
                return;
            }

            // Test registration
            String registerResponse = client.register("testuser", "password123", "This is a test user");
            System.out.println("Register response: " + registerResponse);

            // Test login
            String loginResponse = client.login("testuser", "password123");
            System.out.println("Login response: " + loginResponse);

            // Test adding an item
            String userId = client.getCurrentUserId();
            if (userId != null) {
                String addItemResponse = client.addItem(userId, "Test Item", "This is a test item", "Electronics", 99.99);
                System.out.println("Add item response: " + addItemResponse);

                // Extract item ID if successful
                String itemId = null;
                String[] addItemParts = addItemResponse.split(",");
                if (addItemParts.length >= 3 && addItemParts[1].equals("SUCCESS")) {
                    itemId = addItemParts[2];

                    // Test getting item
                    String getItemResponse = client.getItem(itemId);
                    System.out.println("Get item response: " + getItemResponse);

                    // Test processing purchase
                    String purchaseResponse = client.processPurchase(userId, itemId);
                    System.out.println("Purchase response: " + purchaseResponse);
                }
            }

            // Test searching for items
            String searchResponse = client.searchItems("Test", "Electronics", 10);
            System.out.println("Search response: " + searchResponse);

            // Disconnect from server
            client.disconnect();

        } catch (Exception e) {
            System.out.println("Error during test: " + e.getMessage());
            e.printStackTrace();
            client.disconnect();
        }
    }
}