import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Database {
    private Map<String, User> usersByUsername;
    private Map<String, User> usersById;
    private Map<String, Item> items;
    private List<Message> messages;


    private static final String USER_FILE = "users.txt";
    private static final String ITEM_FILE = "items.txt";
    private static final String MESSAGE_FILE = "messages.txt";

    /**
     * Creates a new Database instance.
     */
    public Database() {
        usersByUsername = new HashMap<>();
        usersById = new HashMap<>();
        items = new HashMap<>();
        messages = new ArrayList<>();

    }

    /**
     * Adds a new user to the database.
     */
    public synchronized boolean addUser(String username, String password, String bio) {
        if (usersByUsername.containsKey(username)) {
            return false;
        }

        User newUser = new User(username, password, bio, this);
        usersByUsername.put(username, newUser);
        usersById.put(newUser.getUserId(), newUser);

        writeUserFile();
        return true;
    }

    /**
     * Validates user login credentials.
     */
    public synchronized boolean login(String data) {
        String[] parts = data.split(",");
        if (parts.length < 2) {
            return false;
        }

        String username = parts[0];
        String password = parts[1];

        User user = usersByUsername.get(username);
        return user != null && user.validatePassword(password);
    }

    /**
     * Finds a user by username.
     */
    public synchronized User getUserByUsername(String username) {
        return usersByUsername.get(username);
    }

    /**
     * Finds a user by ID.
     */
    public synchronized User getUserById(String userId) {
        return usersById.get(userId);
    }

    /**
     * Checks if a user exists by their username.
     */
    public synchronized User userExists(String username) {
        return getUserByUsername(username);
    }

    /**
     * Gets all available users.
     */
    public synchronized List<User> getAllUsers() {
        return new ArrayList<>(usersByUsername.values());
    }

    /**
     * Adds a new item to the database.
     */
    public synchronized boolean addItem(Item item) {
        if (items.containsKey(item.getItemId())) {
            return false;
        }


        User seller = usersById.get(item.getSellerId());
        if (seller == null) {
            return false;
        }

        items.put(item.getItemId(), item);
        seller.addListing(item);
        writeItemFile();
        return true;
    }

    /**
     * Finds an item by ID.
     */
    public synchronized Item getItemById(String itemId) {
        return items.get(itemId);
    }

    /**
     * Gets all available items.
     */
    public synchronized List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    /**
     * Gets all active (unsold) items.
     */
    public synchronized List<Item> getActiveItems() {
        List<Item> activeItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (!item.isSold()) {
                activeItems.add(item);
            }
        }
        return activeItems;
    }



    /**
     * Adds a message to the database.
     */
    public synchronized boolean addMessage(Message message) {
        User sender = usersById.get(message.getSenderId());
        User receiver = usersById.get(message.getReceiverId());

        if (sender == null || receiver == null) {
            return false;
        }

        messages.add(message);
        return true;
    }



    /**
     * Reads user data from file.
     */
    public synchronized void readUserFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String username = parts[0];
                    String password = parts[1];
                    String bio = parts[2];

                    // Parse balance
                    double balance = 0;
                    try {
                        balance = Double.parseDouble(parts[3]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid balance for user: " + username);
                    }

                    // Existing user ID
                    String existingUserId = parts[4];

                    // Deserialize item lists
                    ArrayList<Item> activeListings = deserializeItemIds(parts.length > 5 ? parts[5] : "");
                    ArrayList<Item> purchaseHistory = deserializeItemIds(parts.length > 6 ? parts[6] : "");
                    ArrayList<Item> soldItems = deserializeItemIds(parts.length > 7 ? parts[7] : "");

                    User user = new User(
                            username,
                            password,
                            bio,
                            balance,
                            activeListings,
                            purchaseHistory,
                            soldItems,
                            existingUserId,this
                    );

                    usersByUsername.put(username, user);
                    usersById.put(user.getUserId(), user);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading user file: " + e.getMessage());
            //nothing
        }
    }


    // Helper method to deserialize item IDs
    private ArrayList<Item> deserializeItemIds(String itemIdsString) {
        ArrayList<Item> items = new ArrayList<>();
        if (itemIdsString == null || itemIdsString.isEmpty()) {
            return items;
        }

        String[] itemIds = itemIdsString.split(";");
        for (String itemId : itemIds) {
            Item item = getItemById(itemId);
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }

    /**
     * Writes user data to file.
     */
    public synchronized void writeUserFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE))) {
            for (User user : usersByUsername.values()) {
                writer.println(user.getUsername() + "," +
                        user.getPassword() + "," +
                        user.getBio() + "," +
                        user.getBalance() + ","+
                        user.getUserId() + "," +
                        serializeItemIds(user.getActiveListings()) + ","+
                        serializeItemIds(user.getPurchaseHistory()) + ","+
                        serializeItemIds(user.getSoldItems()));

            }
        } catch (IOException e) {
            System.err.println("Error writing user file: " + e.getMessage());
        }
    }

    // Helper method to serialize item IDs
    private String serializeItemIds(List<Item> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        return items.stream()
                .map(Item::getItemId)
                .collect(Collectors.joining(";"));
    }


    /**
     * Writes item data to file.
     */
    public synchronized void writeItemFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ITEM_FILE, true))) {
            for (Item item : items.values()) {
                writer.println(item.getItemId() + "," +
                        item.getSellerId() + "," +
                        item.getTitle() + "," +
                        item.getDescription() + "," +
                        item.getCategory() + "," +
                        item.getPrice() + "," +
                        item.isSold() + "," +
                        (item.isSold() ? item.getBuyerId() : ""));
            }
        } catch (IOException e) {
            System.err.println("Error writing item file: " + e.getMessage());
        }
    }

    /**
     * Reads message data from file.
     */
    public synchronized void readMessageFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(MESSAGE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String messageId = parts[0];
                    String senderId = parts[1];
                    String receiverId = parts[2];
                    long timestamp = Long.parseLong(parts[3]);

                    // Content might contain commas, so rejoin remaining parts
                    StringBuilder content = new StringBuilder();
                    for (int i = 4; i < parts.length; i++) {
                        if (i > 4) content.append(",");
                        content.append(parts[i]);
                    }

                    // Create message
                    Message message = new Message(senderId, receiverId, content.toString());
                    // We need access to set the ID and timestamp or a constructor that takes them
                    // This is simplified - actual implementation would depend on Message class

                    messages.add(message);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading message file: " + e.getMessage());
            // Continue with empty messages list
        }
    }

    /**
     * Writes message data to file.
     */
    public synchronized void writeMessageFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(MESSAGE_FILE, true))) {
            for (Message message : messages) {
                writer.println(message.getMessageId() + "," +
                        message.getSenderId() + "," +
                        message.getReceiverId() + "," +
                        message.getTimestamp() + "," +
                        message.getContent());
            }
        } catch (IOException e) {
            System.err.println("Error writing message file: " + e.getMessage());
        }
    }

    /**
     * Reads item data from file.
     */
    public synchronized void readItemFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ITEM_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String itemId = parts[0];
                    String sellerId = parts[1];
                    String title = parts[2];
                    String description = parts[3];
                    String category = parts[4];

                    try {
                        double price = Double.parseDouble(parts[5]);

                        // Create item but set ID manually since we're loading from file
                        Item item = new Item(sellerId, title, description, category, price);
                        // We need access to set the ID or a constructor that takes ID
                        // This is simplified - actual implementation would depend on Item class

                        // If file contains sold status and buyer info (optional)
                        if (parts.length > 6) {
                            boolean sold = Boolean.parseBoolean(parts[6]);
                            if (sold && parts.length > 7) {
                                String buyerId = parts[7];
                                item.markAsSold(buyerId);
                            }
                        }

                        items.put(itemId, item);

                        // Add to seller's listings if seller exists
                        User seller = usersById.get(sellerId);
                        if (seller != null) {
                            seller.addListing(item);
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid price
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading item file: " + e.getMessage());
            // Continue with empty items map
        }
    }

    /**
     * Removes an item from the database.
     */
    public synchronized boolean removeItem(String itemId, String requesterId) {
        Item item = items.get(itemId);
        if (item == null) {
            return false;
        }


        if (!item.getSellerId().equals(requesterId)) {
            return false;
        }

        User seller = usersById.get(requesterId);
        if (seller != null) {
            seller.removeListing(itemId);
        }

        items.remove(itemId);
        writeItemFile();
        return true;
    }

    /**
     * Gets messages between two users.
     */
    public synchronized List<Message> getMessagesBetweenUsers(String user1Id, String user2Id) {
        List<Message> userMessages = new ArrayList<>();

        for (Message message : messages) {
            String senderId = message.getSenderId();
            String receiverId = message.getReceiverId();

            if ((senderId.equals(user1Id) && receiverId.equals(user2Id)) ||
                    (senderId.equals(user2Id) && receiverId.equals(user1Id))) {
                userMessages.add(message);
            }
        }


        userMessages.sort(Comparator.comparingLong(Message::getTimestamp));

        return userMessages;
    }
}