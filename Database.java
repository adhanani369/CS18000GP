import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Database implements DatabaseInterface {
    private Map<String, User> usersByUsername;
    private Map<String, User> usersById;
    private Map<String, Item> items;
    private List<Message> messages;
    private Map<String, Map<String, String>> userConversations; // userId -> (conversationKey -> fileName)
    private Map<String, List<Double>> ratings;

    private static final String USER_FILE = "users.txt";
    private static final String ITEM_FILE = "items.txt";
    private static final String MESSAGE_FILE = "messages.txt";
    private static final String RATING_FILE = "ratings.txt";

    /**
     * Creates a new Database instance.
     */
    public Database() {
        usersByUsername = new HashMap<>();
        usersById = new HashMap<>();
        items = new HashMap<>();
        messages = new ArrayList<>();
        userConversations = new HashMap<>(); // Initialize the userConversations map
        ratings = new HashMap<>();

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
     * Adds a message with buyer/seller role identification.
     */
    public synchronized boolean addMessage(Message message, String itemId) {
        User sender = usersById.get(message.getSenderId());
        User receiver = usersById.get(message.getReceiverId());

        if (sender == null || receiver == null) {
            return false;
        }

        // Determine buyer and seller based on the item
        Item item = items.get(itemId);
        if (item == null) {
            return false;
        }

        String buyerId, sellerId;

        // If sender is seller, receiver is buyer
        if (item.getSellerId().equals(message.getSenderId())) {
            sellerId = message.getSenderId();
            buyerId = message.getReceiverId();
        } else {
            // If sender is buyer, receiver is seller
            buyerId = message.getSenderId();
            sellerId = message.getReceiverId();
        }

        // Add to messages list
        messages.add(message);

        // Save to conversation file
        String fileName = getConversationFile(buyerId, sellerId);

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            // Format: userID:message
            writer.println(message.getSenderId() + ":" + message.getContent());
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to conversation file: " + e.getMessage());
            return false;
        }
    }

    public synchronized boolean deleteUser(String username) {
        User user = usersByUsername.get(username);
        if (user == null) {
            return false;
        }

        String userId = user.getUserId();

        // Remove user's active listings
        List<Item> activeListings = new ArrayList<>(user.getActiveListings());
        for (Item item : activeListings) {
            removeItem(item.getItemId(), userId);
        }

        // Remove references to this user in conversations
        List<Message> messagesToRemove = new ArrayList<>();
        for (Message message : messages) {
            if (message.getSenderId().equals(userId) || message.getReceiverId().equals(userId)) {
                messagesToRemove.add(message);
            }
        }
        messages.removeAll(messagesToRemove);

        // Remove user from maps
        usersByUsername.remove(username);
        usersById.remove(userId);

        // Remove user's conversations
        Map<String, String> userConvs = userConversations.get(userId);
        if (userConvs != null) {
            // Delete conversation files
            for (String fileName : userConvs.values()) {
                File file = new File(fileName);
                if (file.exists()) {
                    file.delete();
                }
            }
            userConversations.remove(userId);
        }

        // Update other users' conversation maps
        for (Map<String, String> convMap : userConversations.values()) {
            List<String> keysToRemove = new ArrayList<>();
            for (String key : convMap.keySet()) {
                if (key.contains("_" + userId)) {
                    keysToRemove.add(key);
                }
            }
            for (String key : keysToRemove) {
                convMap.remove(key);
            }
        }

        // Write changes to files
        writeUserFile();
        writeItemFile();
        // Delete any ratings associated with this user
        if (ratings.containsKey(userId)) {
            ratings.remove(userId);
            writeRatingsFile(ratings);
        }

        return true;
    }

    /**
     * Adds a rating for a seller.
     * Ratings are stored in a separate ratings file.
     */
    public synchronized boolean addSellerRating(String sellerId, double rating) {
        if (rating < 1 || rating > 5) {
            return false;
        }

        User seller = usersById.get(sellerId);
        if (seller == null) {
            return false;
        }

        try {
            // Load existing ratings
            Map<String, List<Double>> ratings = readRatingsFile();

            // Add or update the seller's ratings
            List<Double> sellerRatings = ratings.getOrDefault(sellerId, new ArrayList<>());
            sellerRatings.add(rating);
            ratings.put(sellerId, sellerRatings);

            // Write ratings back to file
            writeRatingsFile(ratings);

            return true;
        } catch (Exception e) {
            System.err.println("Error adding seller rating: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets a seller's average rating.
     */
    public synchronized double getSellerRating(String sellerId) {
        try {
            // Load ratings
            Map<String, List<Double>> ratings = readRatingsFile();

            // Get seller's ratings
            List<Double> sellerRatings = ratings.getOrDefault(sellerId, new ArrayList<>());
            if (sellerRatings.isEmpty()) {
                return 0.0;
            }

            // Calculate average
            double sum = 0.0;
            for (Double r : sellerRatings) {
                sum += r;
            }
            return sum / sellerRatings.size();
        } catch (Exception e) {
            System.err.println("Error getting seller rating: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Reads the ratings file.
     */

    public Map<String, List<Double>> readRatingsFile() {

        File file = new File("ratings.txt");

        if (!file.exists()) {
            return ratings;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String sellerId = parts[0];
                    List<Double> sellerRatings = new ArrayList<>();

                    for (int i = 1; i < parts.length; i++) {
                        try {
                            double rating = Double.parseDouble(parts[i]);
                            sellerRatings.add(rating);
                        } catch (NumberFormatException e) {
                            // Skip invalid ratings
                        }
                    }

                    ratings.put(sellerId, sellerRatings);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading ratings file: " + e.getMessage());
        }

        return ratings;
    }

    /**
     * Writes the ratings file.
     */
    private void writeRatingsFile(Map<String, List<Double>> ratings) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("ratings.txt"))) {
            for (Map.Entry<String, List<Double>> entry : ratings.entrySet()) {
                StringBuilder line = new StringBuilder(entry.getKey());
                for (Double rating : entry.getValue()) {
                    line.append(",").append(rating);
                }
                writer.println(line.toString());
            }
        } catch (IOException e) {
            System.err.println("Error writing ratings file: " + e.getMessage());
        }
    }

    /**
     * Reads the ratings file.
     * This method should be called during database initialization.
     */


    /**
     * Writes the ratings file.
     * This method should be called during database shutdown.
     */
    public synchronized void writeRatingsFile() {
        // No implementation needed as writeRatingsFile(Map) is called directly when ratings are modified
    }
    /**
     * For backward compatibility - use when item ID is not known
     */
    public synchronized boolean addMessage(Message message) {
        User sender = usersById.get(message.getSenderId());
        User receiver = usersById.get(message.getReceiverId());

        if (sender == null || receiver == null) {
            return false;
        }

        // Default to assuming sender is buyer
        String buyerId = message.getSenderId();
        String sellerId = message.getReceiverId();

        // Add to messages list
        messages.add(message);

        // Save to conversation file
        String fileName = getConversationFile(buyerId, sellerId);

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName, true))) {
            // Format: userID:message
            writer.println(message.getSenderId() + ":" + message.getContent());
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to conversation file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets messages between a buyer and seller.
     */
    public synchronized List<Message> getMessagesBetweenBuyerAndSeller(String buyerId, String sellerId) {
        List<Message> conversation = new ArrayList<>();

        // Get conversation file
        String fileName = getConversationFile(buyerId, sellerId);
        File file = new File(fileName);

        if (!file.exists()) {
            return conversation;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            long timestamp = System.currentTimeMillis() - 10000; // Base timestamp

            while ((line = reader.readLine()) != null) {
                int colonIndex = line.indexOf(':');
                if (colonIndex > 0) {
                    String senderId = line.substring(0, colonIndex);
                    String content = line.substring(colonIndex + 1);

                    // Determine receiver
                    String receiverId = senderId.equals(buyerId) ? sellerId : buyerId;

                    // Create message object
                    Message message = new Message(senderId, receiverId, content, timestamp, false);
                    timestamp += 1; // Increment for order

                    conversation.add(message);
                }
            }

            return conversation;
        } catch (IOException e) {
            System.err.println("Error reading conversation file: " + e.getMessage());
            return conversation;
        }
    }

    /**
     * Gets all users a person is buying from and selling to.
     */
    public synchronized Map<String, List<String>> getUserRoleBasedConversations(String userId) {
        Map<String, List<String>> result = new HashMap<>();
        List<String> buyingFrom = new ArrayList<>();
        List<String> sellingTo = new ArrayList<>();

        Map<String, String> userConvs = userConversations.get(userId);
        if (userConvs != null) {
            for (Map.Entry<String, String> entry : userConvs.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("buying_from_")) {
                    buyingFrom.add(key.substring("buying_from_".length()));
                } else if (key.startsWith("selling_to_")) {
                    sellingTo.add(key.substring("selling_to_".length()));
                }
            }
        }

        result.put("buyingFrom", buyingFrom);
        result.put("sellingTo", sellingTo);
        return result;
    }


    /**
     * Gets all conversation partners regardless of role.
     */
    public synchronized List<String> getAllConversationPartners(String userId) {
        Set<String> partners = new HashSet<>();

        Map<String, String> userConvs = userConversations.get(userId);
        if (userConvs != null) {
            for (String key : userConvs.keySet()) {
                if (key.startsWith("buying_from_")) {
                    partners.add(key.substring("buying_from_".length()));
                } else if (key.startsWith("selling_to_")) {
                    partners.add(key.substring("selling_to_".length()));
                }
            }
        }

        return new ArrayList<>(partners);
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
        try (PrintWriter writer = new PrintWriter(new FileWriter(ITEM_FILE))) {
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
    public synchronized void readMessageFiles() {
        // Clear existing map
        userConversations.clear();

        // Find all conversation files
        File directory = new File(".");
        File[] files = directory.listFiles((dir, name) -> name.startsWith("buyer_") && name.endsWith(".txt"));

        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                // Parse "buyer_[buyerId]_seller_[sellerId].txt"
                if (fileName.contains("_seller_")) {
                    try {
                        String buyerPart = fileName.substring(fileName.indexOf("buyer_") + 6, fileName.indexOf("_seller_"));
                        String sellerPart = fileName.substring(fileName.indexOf("_seller_") + 8, fileName.lastIndexOf(".txt"));

                        String buyerId = buyerPart;
                        String sellerId = sellerPart;

                        // Add to buyer's conversations
                        ensureUserMap(buyerId);
                        userConversations.get(buyerId).put("buying_from_" + sellerId, fileName);

                        // Add to seller's conversations
                        ensureUserMap(sellerId);
                        userConversations.get(sellerId).put("selling_to_" + buyerId, fileName);

                        // Read messages into memory if needed
                        readMessagesFromFile(fileName);

                    } catch (Exception e) {
                        System.err.println("Error parsing filename: " + fileName + " - " + e.getMessage());
                    }
                }
            }
        }
    }


    /**
     * Ensures a user has a map entry in userConversations.
     */
    private void ensureUserMap(String userId) {
        if (!userConversations.containsKey(userId)) {
            userConversations.put(userId, new HashMap<>());
        }
    }

    /**
     * Gets the conversation file for a specific buyer-seller interaction.
     */
    private String getConversationFile(String buyerId, String sellerId) {
        // Check if this specific buyer-seller conversation exists
        Map<String, String> buyerConversations = userConversations.get(buyerId);
        if (buyerConversations != null) {
            String key = "buying_from_" + sellerId;
            if (buyerConversations.containsKey(key)) {
                return buyerConversations.get(key);
            }
        }

        // Create new file name with buyer first, seller second
        String fileName = "buyer_" + buyerId + "_seller_" + sellerId + ".txt";

        // Update maps for both users with role-specific keys
        ensureUserMap(buyerId);
        userConversations.get(buyerId).put("buying_from_" + sellerId, fileName);

        ensureUserMap(sellerId);
        userConversations.get(sellerId).put("selling_to_" + buyerId, fileName);

        return fileName;
    }

    /**
     * Reads messages from a file into the in-memory messages list
     */
    private void readMessagesFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            long timestamp = System.currentTimeMillis() - 10000; // Base timestamp

            while ((line = reader.readLine()) != null) {
                int colonIndex = line.indexOf(':');
                if (colonIndex > 0) {
                    String senderId = line.substring(0, colonIndex);
                    String content = line.substring(colonIndex + 1);

                    // Find the buyer and seller IDs from the filename
                    String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
                    String buyerId = fileNameWithoutExt.substring(fileNameWithoutExt.indexOf("buyer_") + 6,
                            fileNameWithoutExt.indexOf("_seller_"));
                    String sellerId = fileNameWithoutExt.substring(fileNameWithoutExt.indexOf("_seller_") + 8);

                    // Determine receiver based on sender
                    String receiverId = senderId.equals(buyerId) ? sellerId : buyerId;

                    // Create message object
                    Message message = new Message(senderId, receiverId, content, timestamp, false);
                    timestamp += 1; // Increment for order

                    messages.add(message);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading message file: " + e.getMessage());
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

                        // Use the constructor that takes an itemId, or use setItemId method
                        Item item = new Item(sellerId, title, description, category, price);
                        item.setItemId(itemId); // Use the ID from the file instead of generating a new one

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
                        System.err.println("Error parsing price for item: " + itemId);
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
