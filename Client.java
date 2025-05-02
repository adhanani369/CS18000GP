import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Client class that handles communication with the server.
 * Now includes GUI integration.
 * @author Ayush Dhanani
 * @version April 29, 2025
 */
public class Client {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String currentUserId;
    private final String SERVER_ADDRESS = "localhost";
    private final int SERVER_PORT = 1234;

    // GUI components
    private static MarketPlaceGUI gui;
    private static boolean guiMode = false;

    /**
     * Creates a new Client instance.
     */
    public Client() {
        this.socket = null;
        this.reader = null;
        this.writer = null;
        this.currentUserId = null;
        System.out.println("Client created");
    }

    /**
     * Connects to the server.
     * @return true if connection was successful, false otherwise
     */
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

    /**
     * Disconnects from the server.
     */
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

    /**
     * Sends a message to the server and receives the response.
     * @param message The message to send
     * @return The response from the server
     */
    public String sendMessage(String message) {
        try {
            System.out.println("Sending: " + message);

            writer.write(message);
            writer.newLine();
            writer.flush();

            String response = reader.readLine();
            System.out.println("Received: " + response);

            if (response == null) {
                System.out.println("ERROR: Received null response from server");
                // Check if connection is still alive
                if (socket != null && !socket.isClosed()) {
                    System.out.println("Socket is still connected");
                } else {
                    System.out.println("Socket connection is closed");
                }
            }

            return response;
        } catch (IOException e) {
            System.out.println("Communication error: " + e.getMessage());
            e.printStackTrace(); // Add stack trace for better debugging
            return "ERROR,Communication failure";
        }
    }

    /**
     * Registers a new user account.
     * @param username The username for the new account
     * @param password The password for the new account
     * @param bio The bio for the new account
     * @return The response from the server
     */
    public String register(String username, String password, String bio) {
        System.out.println("Registering user: " + username);
        String message = "REGISTER," + username + "," + password + "," + bio;
        return sendMessage(message);
    }

    /**
     * Logs in a user.
     * @param username The username
     * @param password The password
     * @return The response from the server
     */
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

    /**
     * Deletes a user account.
     * @param userId The ID of the user account to delete
     * @return The response from the server
     */
    public String deleteAccount(String userId) {
        System.out.println("Deleting account: " + userId);
        String message = "DELETE_ACCOUNT," + userId;
        return sendMessage(message);
    }

    /**
     * Adds a new item listing.
     * @param sellerId The ID of the seller
     * @param title The title of the item
     * @param description The description of the item
     * @param category The category of the item
     * @param price The price of the item
     * @return The response from the server
     */
    public String addItem(String sellerId, String title, String description, String category, double price) {
        System.out.println("Adding item: " + title + " (Price: $" + price + ")");
        String message = "ADD_ITEM," + sellerId + "," + title + "," + description + "," + category + "," + price;
        return sendMessage(message);
    }

    /**
     * Gets an item by ID.
     * @param itemId The ID of the item
     * @return The response from the server
     */
    public String getItem(String itemId) {
        System.out.println("Getting item: " + itemId);
        String message = "GET_ITEM," + itemId;
        return sendMessage(message);
    }

    /**
     * Searches for items.
     * @param query The search query
     * @param category The category to search in (optional)
     * @param maxResults The maximum number of results to return
     * @return The response from the server
     */
    public String searchItems(String query, String category, int maxResults) {
        System.out.println("Searching for items: " + query);
        String message = "SEARCH_ITEMS," + query + "," + (category == null ? "" : category) + "," + maxResults;
        return sendMessage(message);
    }

    /**
     * Gets user listings.
     * @param userId The ID of the user
     * @param activeOnly Whether to only include active listings
     * @return The response from the server
     */
    public String getUserListings(String userId, boolean activeOnly) {
        System.out.println("Getting " + (activeOnly ? "active" : "all") + " listings for user: " + userId);
        String message = "GET_USER_LISTINGS," + userId + "," + activeOnly;
        return sendMessage(message);
    }

    /**
     * Marks an item as sold.
     * @param itemId The ID of the item
     * @param buyerId The ID of the buyer
     * @return The response from the server
     */
    public String markSold(String itemId, String buyerId) {
        System.out.println("Marking item sold: " + itemId + " to buyer " + buyerId);
        String message = "MARK_SOLD," + itemId + "," + buyerId;
        return sendMessage(message);
    }

    /**
     * Removes an item listing.
     * @param itemId The ID of the item
     * @param sellerId The ID of the seller
     * @return The response from the server
     */
    public String removeItem(String itemId, String sellerId) {
        System.out.println("Removing item: " + itemId);
        String message = "REMOVE_ITEM," + itemId + "," + sellerId;
        return sendMessage(message);
    }

    /**
     * Sends a message to another user.
     * @param senderId The ID of the sender
     * @param receiverId The ID of the receiver
     * @param content The message content
     * @param itemId The ID of the related item (or "none" if none)
     * @return The response from the server
     */
    public String sendMessageToUser(String senderId, String receiverId, String content, String itemId) {
        System.out.println("Sending message to user " + receiverId);
        String message = "SEND_MESSAGE," + senderId + "," + receiverId + "," + content + "," + itemId;
        return sendMessage(message);
    }

    /**
     * Gets messages between two users.
     * @param buyerId The ID of the buyer
     * @param sellerId The ID of the seller
     * @return The response from the server
     */
    public String getMessages(String buyerId, String sellerId) {
        System.out.println("Getting messages between " + buyerId + " and " + sellerId);
        String message = "GET_MESSAGES," + buyerId + "," + sellerId;
        return sendMessage(message);
    }

    /**
     * Gets conversations for a user.
     * @param userId The ID of the user
     * @return The response from the server
     */
    public String getConversations(String userId) {
        System.out.println("Getting conversations for user " + userId);
        String message = "GET_CONVERSATIONS," + userId;
        return sendMessage(message);
    }

    /**
     * Adds funds to a user's account.
     * @param userId The ID of the user
     * @param amount The amount to add
     * @return The response from the server
     */
    public String addFunds(String userId, double amount) {
        System.out.println("Adding funds: $" + amount + " to user " + userId);
        String message = "ADD_FUNDS," + userId + "," + amount;
        return sendMessage(message);
    }

    /**
     * Withdraws funds from a user's account.
     * @param userId The ID of the user
     * @param amount The amount to withdraw
     * @return The response from the server
     */
    public String withdrawFunds(String userId, double amount) {
        System.out.println("Withdrawing funds: $" + amount + " from user " + userId);
        String message = "WITHDRAW_FUNDS," + userId + "," + amount;
        return sendMessage(message);
    }

    /**
     * Processes a purchase transaction.
     * @param buyerId The ID of the buyer
     * @param itemId The ID of the item
     * @return The response from the server
     */
    public String processPurchase(String buyerId, String itemId) {
        System.out.println("Processing purchase of item " + itemId + " by buyer " + buyerId);
        String message = "PROCESS_PURCHASE," + buyerId + "," + itemId;
        return sendMessage(message);
    }

    /**
     * Rates a seller.
     * @param sellerId The ID of the seller
     * @param rating The rating (1-5)
     * @return The response from the server
     */
    public String rateSeller(String sellerId, double rating) {
        System.out.println("Rating seller " + sellerId + " with " + rating + " stars");
        String message = "RATE_SELLER," + sellerId + "," + rating;
        return sendMessage(message);
    }

    /**
     * Gets a seller's rating.
     * @param sellerId The ID of the seller
     * @return The response from the server
     */
    public String getRating(String sellerId) {
        System.out.println("Getting rating for seller " + sellerId);
        String message = "GET_RATING," + sellerId;
        return sendMessage(message);
    }

    /**
     * Gets all users.
     * @return The response from the server
     */
    public String getAllUsers() {
        System.out.println("Getting all users");
        String message = "GET_ALL_USERS";
        return sendMessage(message);
    }

    /**
     * Gets all active sellers.
     * @return The response from the server
     */
    public String getActiveSellers() {
        System.out.println("Getting all active sellers");
        String message = "GET_ACTIVE_SELLERS";
        return sendMessage(message);
    }

    /**
     * Gets the current user ID.
     * @return The current user ID
     */
    public String getCurrentUserId() {
        return currentUserId;
    }

    /**
     * Sets the current user ID.
     * @param userId The user ID to set
     */
    public void setCurrentUserId(String userId) {
        this.currentUserId = userId;
    }

    /**
     * Gets the user's own rating.
     * @param userId The ID of the user
     * @return The response from the server
     */
    public String getMyRating(String userId) {
        System.out.println("Getting my rating: " + userId);
        String message = "GET_MY_RATING," + userId;
        return sendMessage(message);
    }

    /**
     * Runs the application in GUI mode.
     */
    public static void startGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Show splash screen
        SplashScreen splashScreen = new SplashScreen();
        splashScreen.setVisible(true);

        // Start application in a separate thread to allow splash screen to be shown
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Simulate loading time
                Thread.sleep(1500);
                return null;
            }

            @Override
            protected void done() {
                splashScreen.dispose();
                SwingUtilities.invokeLater(() -> gui = new MarketPlaceGUI());
            }
        };
        worker.execute();
    }

    /**
     * Runs the application in terminal mode. For now, I am keeping it in case we need to use it
     * for debugging purposes but can remove later.
     * I just don't want to remove it given the fact we had spend so much time on
     * this beautiful piece of code
     */
    public static void startTerminal() {
        Client client = new Client();
        if (!client.connect()) return;
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\nMenu:");
            System.out.println("1 Register");
            System.out.println("2 Login");
            System.out.println("3 List All Items & Buy");
            System.out.println("4 Search Items");
            System.out.println("5 Add Item");
            System.out.println("6 Send Message");
            System.out.println("7 View Messages");
            System.out.println("8 Add Funds");
            System.out.println("9 Withdraw Funds");
            System.out.println("10 View Balance");
            System.out.println("11 Rate Seller");
            System.out.println("12 View My Listings");
            System.out.println("13 View My Rating");
            System.out.println("14 Delete My Account");
            System.out.println("15 Logout");
            System.out.println("0 Exit");
            System.out.print("Choice: ");

            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        System.out.print("Username: ");
                        String u = sc.nextLine();
                        System.out.print("Password: ");
                        String p = sc.nextLine();
                        System.out.print("Bio: ");
                        String b = sc.nextLine();
                        System.out.println(client.register(u, p, b));
                        break;

                    case "2":
                        System.out.print("Username: ");
                        String lu = sc.nextLine();
                        System.out.print("Password: ");
                        String lp = sc.nextLine();
                        System.out.println(client.login(lu, lp));
                        break;

                    case "3":
                        String rawList = client.searchItems("", "", 100);
                        String[] parts = rawList.split(",");
                        int count = Integer.parseInt(parts[2]);
                        System.out.println("Available Items (" + count + "):");
                        for (int i = 0; i < count; i++) {
                            String itemId = parts[3 + 2*i];
                            String title  = parts[4 + 2*i];
                            System.out.printf("[%d] %s (ID: %s)%n", i+1, title, itemId);
                        }
                        if (count > 0) {
                            System.out.print("Buy an item? (y/n): ");
                            if (sc.nextLine().trim().equalsIgnoreCase("y")) {
                                while (true) {
                                    System.out.print("Enter item number: ");
                                    String numStr = sc.nextLine().trim();
                                    int sel;
                                    try {
                                        sel = Integer.parseInt(numStr);
                                    } catch (NumberFormatException ex) {
                                        System.out.println("Invalid input. Please enter a number between 1 and " + count);
                                        continue;
                                    }
                                    if (sel < 1 || sel > count) {
                                        System.out.println("Number out of range. Please enter between 1 and " + count);
                                        continue;
                                    }
                                    String chosenId = parts[3 + 2*(sel-1)];
                                    System.out.println(client.processPurchase(client.getCurrentUserId(), chosenId));
                                    break;
                                }
                            }
                        }
                        break;

                    case "4":
                        System.out.print("Query: ");
                        String q = sc.nextLine();
                        System.out.print("Category: ");
                        String cat = sc.nextLine();
                        System.out.print("Max results: ");
                        int mr = Integer.parseInt(sc.nextLine());
                        String raw = client.searchItems(q, cat, mr);
                        String[] sp = raw.split(",");
                        int cnt = Integer.parseInt(sp[2]);
                        System.out.println("Search results (" + cnt + "):");
                        for (int i = 0; i < cnt; i++) {
                            String id = sp[3 + 2*i];
                            String title2 = sp[4 + 2*i];
                            System.out.printf("[%d] %s (ID: %s)%n", i+1, title2, id);
                        }
                        break;

                    case "5":
                        if (client.getCurrentUserId() == null) {
                            System.out.println("Please login first!");
                            break;
                        }
                        System.out.print("Title: ");
                        String t = sc.nextLine();
                        System.out.print("Description: ");
                        String d = sc.nextLine();
                        System.out.print("Category: ");
                        String c = sc.nextLine();
                        System.out.print("Price: ");
                        double pr = Double.parseDouble(sc.nextLine());
                        System.out.println(client.addItem(client.getCurrentUserId(), t, d, c, pr));
                        break;

                    case "6":
                        if (client.getCurrentUserId() == null) {
                            System.out.println("Please login first!");
                            break;
                        }

                        // Get all users from the server
                        String usersResponse = client.getAllUsers();
                        String[] userParts = usersResponse.split(",");

                        if (userParts.length > 1 && userParts[1].equals("SUCCESS")) {
                            int userCount = Integer.parseInt(userParts[2]);
                            System.out.println("\nAvailable Users (" + userCount + "):");

                            for (int i = 0; i < userCount; i++) {
                                String userId = userParts[3 + 2*i];
                                String username = userParts[4 + 2*i];

                                // Don't show the current user
                                if (!userId.equals(client.getCurrentUserId())) {
                                    System.out.printf("[%d] %s (ID: %s)%n", i+1, username, userId);
                                }
                            }

                            System.out.print("\nSelect user number or enter user ID directly: ");
                            String userInput = sc.nextLine();

                            String receiverId;
                            try {
                                int userSelection = Integer.parseInt(userInput);
                                if (userSelection > 0 && userSelection <= userCount) {
                                    receiverId = userParts[3 + 2*(userSelection-1)];
                                } else {
                                    System.out.println("Invalid selection. Please enter a valid user ID:");
                                    receiverId = sc.nextLine();
                                }
                            } catch (NumberFormatException e) {
                                // User entered a direct ID
                                receiverId = userInput;
                            }

                            System.out.print("Content: ");
                            String cont = sc.nextLine();
                            System.out.print("Item ID (or leave blank): ");
                            String itid = sc.nextLine();
                            if (itid.trim().isEmpty()) {
                                itid = "none"; // Use a placeholder if no item ID is provided
                            }

                            System.out.println(client.sendMessageToUser(client.getCurrentUserId(), receiverId, cont, itid));
                        } else {
                            System.out.println("Failed to retrieve user list. Enter user ID manually:");
                            String rid = sc.nextLine();
                            System.out.print("Content: ");
                            String cont = sc.nextLine();
                            System.out.print("Item ID: ");
                            String itid = sc.nextLine();
                            System.out.println(client.sendMessageToUser(client.getCurrentUserId(), rid, cont, itid));
                        }
                        break;

                    case "7":
                        if (client.getCurrentUserId() == null) {
                            System.out.println("Please login first!");
                            break;
                        }

                        // Get all users from the server
                        String convUsersResponse = client.getAllUsers();
                        String[] convUserParts = convUsersResponse.split(",");

                        if (convUserParts.length > 1 && convUserParts[1].equals("SUCCESS")) {
                            int userCount = Integer.parseInt(convUserParts[2]);
                            System.out.println("\nSelect User to View Messages With:");

                            for (int i = 0; i < userCount; i++) {
                                String userId = convUserParts[3 + 2*i];
                                String username = convUserParts[4 + 2*i];

                                // Don't show the current user
                                if (!userId.equals(client.getCurrentUserId())) {
                                    System.out.printf("[%d] %s (ID: %s)%n", i+1, username, userId);
                                }
                            }

                            System.out.print("\nSelect user number or enter user ID directly: ");
                            String userInput = sc.nextLine();

                            String otherUserId;
                            try {
                                int userSelection = Integer.parseInt(userInput);
                                if (userSelection > 0 && userSelection <= userCount) {
                                    otherUserId = convUserParts[3 + 2*(userSelection-1)];
                                } else {
                                    System.out.println("Invalid selection. Please enter a valid user ID:");
                                    otherUserId = sc.nextLine();
                                }
                            } catch (NumberFormatException e) {
                                // User entered a direct ID
                                otherUserId = userInput;
                            }

                            System.out.println(client.getMessages(client.getCurrentUserId(), otherUserId));
                        } else {
                            System.out.println("Failed to retrieve user list. Enter user ID manually:");
                            String ou = sc.nextLine();
                            System.out.println(client.getMessages(client.getCurrentUserId(), ou));
                        }
                        break;

                    case "8":
                        if (client.getCurrentUserId() == null) {
                            System.out.println("Please login first!");
                            break;
                        }
                        System.out.print("Amount: ");
                        double af = Double.parseDouble(sc.nextLine());
                        System.out.println(client.addFunds(client.getCurrentUserId(), af));
                        break;

                    case "9":
                        if (client.getCurrentUserId() == null) {
                            System.out.println("Please login first!");
                            break;
                        }
                        System.out.print("Amount: ");
                        double wf = Double.parseDouble(sc.nextLine());
                        System.out.println(client.withdrawFunds(client.getCurrentUserId(), wf));
                        break;

                    case "10":
                        // View balance
                        if (client.getCurrentUserId() == null) {
                            System.out.println("Please login first!");
                        } else {
                            // Send GET_BALANCE command to server
                            String balanceResp = client.sendMessage("GET_BALANCE," + client.getCurrentUserId());
                            String[] balParts = balanceResp.split(",");
                            if (balParts.length >= 3 && balParts[1].equals("SUCCESS")) {
                                System.out.println("Current balance: $" + balParts[2]);
                            } else {
                                System.out.println("Failed to retrieve balance.");
                            }
                        }
                        break;

                    case "11":
                        if (client.getCurrentUserId() == null) {
                            System.out.println("Please login first!");
                            break;
                        }

                        // Get active sellers from the server
                        String sellersResponse = client.getActiveSellers();
                        String[] sellerParts = sellersResponse.split(",");

                        if (sellerParts.length > 1 && sellerParts[1].equals("SUCCESS")) {
                            int sellerCount = Integer.parseInt(sellerParts[2]);
                            if (sellerCount == 0) {
                                System.out.println("No active sellers found to rate.");
                                break;
                            }

                            System.out.println("\nActive Sellers (" + sellerCount + "):");
                            for (int i = 0; i < sellerCount; i++) {
                                String sellerId = sellerParts[3 + 2*i];
                                String username = sellerParts[4 + 2*i];

                                // Don't show the current user
                                if (!sellerId.equals(client.getCurrentUserId())) {
                                    System.out.printf("[%d] %s (ID: %s)%n", i+1, username, sellerId);
                                }
                            }

                            System.out.print("\nSelect seller number or enter seller ID directly: ");
                            String sellerInput = sc.nextLine();

                            String sellerId;
                            try {
                                int sellerSelection = Integer.parseInt(sellerInput);
                                if (sellerSelection > 0 && sellerSelection <= sellerCount) {
                                    sellerId = sellerParts[3 + 2*(sellerSelection-1)];
                                } else {
                                    System.out.println("Invalid selection. Please enter a valid seller ID:");
                                    sellerId = sc.nextLine();
                                }
                            } catch (NumberFormatException e) {
                                // User entered a direct ID
                                sellerId = sellerInput;
                            }

                            System.out.print("Rating (1-5): ");
                            double rating = Double.parseDouble(sc.nextLine());

                            // Validate rating
                            if (rating < 1 || rating > 5) {
                                System.out.println("Invalid rating. Please enter a value between 1 and 5.");
                                break;
                            }

                            System.out.println(client.rateSeller(sellerId, rating));
                        } else {
                            System.out.println("Failed to retrieve seller list. Enter seller ID manually:");
                            String sid = sc.nextLine();
                            System.out.print("Rating (1-5): ");
                            double rt = Double.parseDouble(sc.nextLine());
                            System.out.println(client.rateSeller(sid, rt));
                        }
                        break;

                    case "12":
                        // View my listings
                        if (client.getCurrentUserId() == null) {
                            System.out.println("Please login first!");
                            break;
                        }

                        System.out.println("\nListing Options:");
                        System.out.println("1. View All My Listings");
                        System.out.println("2. View Only My Active Listings");
                        System.out.print("Choose option: ");
                        String listingOption = sc.nextLine();

                        boolean activeOnly = false;
                        if (listingOption.equals("2")) {
                            activeOnly = true;
                        }

                        String userListingsResponse = client.getUserListings(client.getCurrentUserId(), activeOnly);
                        String[] listingParts = userListingsResponse.split(",");

                        if (listingParts.length > 1 && listingParts[1].equals("SUCCESS")) {
                            int listingCount = Integer.parseInt(listingParts[2]);
                            System.out.println("\n" + (activeOnly ? "Active" : "All") + " Listings (" + listingCount + "):");

                            if (listingCount == 0) {
                                System.out.println("No listings found.");
                                break;
                            }

                            for (int i = 0; i < listingCount; i++) {
                                // Format depends on response structure
                                // Assuming response format: itemId,title,price,sold
                                String itemId = listingParts[3 + 4*i];
                                String title = listingParts[4 + 4*i];
                                String price = listingParts[5 + 4*i];
                                String sold = listingParts[6 + 4*i];

                                System.out.printf("[%d] %s - $%s (%s)%n",
                                        i+1, title, price, sold.equals("true") ? "SOLD" : "AVAILABLE");
                            }
                        } else {
                            System.out.println("Failed to retrieve listings: " +
                                    (listingParts.length > 2 ? listingParts[2] : "Unknown error"));
                        }
                        break;

                    case "13":
                        // View my rating
                        if (client.getCurrentUserId() == null) {
                            System.out.println("Please login first!");
                            break;
                        }

                        String ratingResponse = client.getMyRating(client.getCurrentUserId());
                        String[] ratingParts = ratingResponse.split(",");

                        if (ratingParts.length > 1 && ratingParts[1].equals("SUCCESS")) {
                            double rating = Double.parseDouble(ratingParts[2]);
                            int ratingCount = Integer.parseInt(ratingParts[3]);

                            if (ratingCount == 0) {
                                System.out.println("\nYou have not received any ratings yet.");
                            } else {
                                System.out.printf("\nYour current rating is: %.1f/5.0 (based on %d ratings)%n",
                                        rating, ratingCount);

                                // Display rating as stars
                                System.out.print("Rating: ");
                                int fullStars = (int)Math.floor(rating);
                                boolean halfStar = (rating - fullStars) >= 0.5;

                                for (int i = 0; i < fullStars; i++) {
                                    System.out.print("★");
                                }
                                if (halfStar) {
                                    System.out.print("½");
                                }
                                int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
                                for (int i = 0; i < emptyStars; i++) {
                                    System.out.print("☆");
                                }
                                System.out.println();
                            }
                        } else {
                            System.out.println("Failed to retrieve rating: " +
                                    (ratingParts.length > 2 ? ratingParts[2] : "Unknown error"));
                        }
                        break;

                    case "14":
                        // Delete account
                        if (client.getCurrentUserId() == null) {
                            System.out.println("Please login first!");
                            break;
                        }

                        System.out.println("\n⚠️ WARNING: This will permanently delete your account and all data!");
                        System.out.print("Are you sure? (type 'yes' to confirm): ");
                        String confirmation = sc.nextLine();

                        if (confirmation.toLowerCase().equals("yes")) {
                            String deleteResponse = client.deleteAccount(client.getCurrentUserId());
                            String[] deleteParts = deleteResponse.split(",");

                            if (deleteParts.length > 1 && deleteParts[1].equals("SUCCESS")) {
                                System.out.println("Account deleted successfully.");
                                client.setCurrentUserId(null); // Log out after deletion
                            } else {
                                System.out.println("Failed to delete account: " +
                                        (deleteParts.length > 2 ? deleteParts[2] : "Unknown error"));
                            }
                        } else {
                            System.out.println("Account deletion cancelled.");
                        }
                        break;

                    case "15":
                        client.setCurrentUserId(null);
                        System.out.println("Logged out.");
                        break;

                    case "0":
                        running = false;
                        break;

                    default:
                        System.out.println("Invalid choice");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        client.disconnect();
    }


    public static void main(String[] args) {
        // Here we go!!!
        startGUI();

    }

    /**
     * Simple splash screen aka the screen for the application.
     */
    private static class SplashScreen extends JWindow {
        public SplashScreen() {
            // Set size and center on screen
            setSize(500, 300);
            setLocationRelativeTo(null);

            // Create content panel with gradient background
            JPanel content = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    GradientPaint gp = new GradientPaint(0, 0, new Color(0, 102, 204),
                            w, h, new Color(0, 51, 102));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, w, h);
                }
            };
            content.setLayout(new BorderLayout());

            // Add title label
            JLabel titleLabel = new JLabel("Dbay");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setHorizontalAlignment(JLabel.CENTER);
            content.add(titleLabel, BorderLayout.NORTH);

            // Add version and copyright label
            JLabel versionLabel = new JLabel("Version: THE BEST EVER");
            versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            versionLabel.setForeground(Color.WHITE);
            versionLabel.setHorizontalAlignment(JLabel.CENTER);
            content.add(versionLabel, BorderLayout.SOUTH);

            // Add loading animation (simulated with progress bar)
            JProgressBar progress = new JProgressBar();
            progress.setIndeterminate(true);
            progress.setStringPainted(false);
            progress.setBorderPainted(false);
            progress.setForeground(Color.WHITE);
            progress.setBackground(new Color(0, 51, 102));

            JPanel progressPanel = new JPanel(new BorderLayout());
            progressPanel.setOpaque(false);
            progressPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
            progressPanel.add(progress, BorderLayout.SOUTH);

            // Add logo (simulated with text)
            JLabel logoLabel = new JLabel("MARKETPLACE");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 36));
            logoLabel.setForeground(Color.WHITE);
            logoLabel.setHorizontalAlignment(JLabel.CENTER);
            progressPanel.add(logoLabel, BorderLayout.CENTER);

            content.add(progressPanel, BorderLayout.CENTER);

            // Set content pane
            setContentPane(content);
        }
    }
}
