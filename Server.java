import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced server skeleton with ClientHandler for marketplace application
 */
public class Server {
    private ServerSocket serverSocket;
    private final int PORT = 1234;
    private boolean running;
    private List<ClientHandler> clientHandlers;
    private Database database;

    /**
     * Creates a new server instance
     */
    public Server() {
        this.clientHandlers = new ArrayList<>();
        this.running = false;
        System.out.println("Server created");
    }

    /**
     * Starts the server and begins listening for client connections
     */
    public void startServer() {
        try {
            this.database = new Database();
            serverSocket = new ServerSocket(PORT);
            running = true;
            System.out.println("Server started on port " + PORT);

            while (running) {
                try {
                    // Accept client connection
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                    // Create and start client handler
                    ClientHandler handler = new ClientHandler(clientSocket);
                    clientHandlers.add(handler);

                    Thread thread = new Thread(handler);
                    thread.start();

                    System.out.println("Client handler started");
                } catch (IOException e) {
                    if (running) {
                        System.out.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    /**
     * Gracefully shuts down the server
     */
    public void closeServer() {
        running = false;

        // Close all client connections
        for (ClientHandler handler : clientHandlers) {
            handler.closeEverything();
        }
        clientHandlers.clear();

        // Close server socket
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                System.out.println("Server socket closed");
            } catch (IOException e) {
                System.out.println("Error closing server socket: " + e.getMessage());
            }
        }

        System.out.println("Server shutdown complete");
    }

    /**
     * Main method to start the server
     */
    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }

    /**
     * Handler for client connections
     */
    private class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader reader;
        private BufferedWriter writer;
        private boolean closed;

        /**
         * Creates a new ClientHandler for a client connection
         */
        public ClientHandler(Socket socket) {
            this.socket = socket;
            this.closed = false;

            try {
                this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                System.out.println("Handler initialized for: " + socket.getInetAddress().getHostAddress());
            } catch (IOException e) {
                System.out.println("Error initializing handler: " + e.getMessage());
                closeEverything();
            }
        }

        /**
         * Main loop for handling client requests
         */
        @Override
        public void run() {
            String message;

            try {
                while (!closed && (message = reader.readLine()) != null) {
                    System.out.println("Received: " + message);

                    // Process client request
                    String response = processRequest(message);

                    // Send response
                    writer.write(response);
                    writer.newLine();
                    writer.flush();
                    System.out.println("Sent: " + response);
                }
            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
            } finally {
                closeEverything();
            }
        }

        /**
         * Processes a client request and returns appropriate response
         * @param request The client request as a string
         * @return The response string to send back to client
         */
        private String processRequest(String request) {
            String[] parts = request.split(",");
            String command = parts[0];

            // Process different commands
            switch (command) {
                case "REGISTER":
                    return handleRegister(parts);
                case "LOGIN":
                    return handleLogin(parts);
                case "DELETE_ACCOUNT":
                    return handleDeleteAccount(parts);
                case "ADD_ITEM":
                    return handleAddItem(parts);
                case "GET_ITEM":
                    return handleGetItem(parts);
                case "SEARCH_ITEMS":
                    return handleSearchItems(parts);
                case "MARK_SOLD":
                    return handleMarkSold(parts);
                case "REMOVE_ITEM":
                    return handleRemoveItem(parts);
                case "SEND_MESSAGE":
                    return handleSendMessage(parts);
                case "GET_MESSAGES":
                    return handleGetMessages(parts);
                case "GET_CONVERSATIONS":
                    return handleGetConversations(parts);
                case "ADD_FUNDS":
                    return handleAddFunds(parts);
                case "WITHDRAW_FUNDS":
                    return handleWithdrawFunds(parts);
                case "PROCESS_PURCHASE":
                    return handleProcessPurchase(parts);
                case "RATE_SELLER":
                    return handleRateSeller(parts);
                case "GET_RATING":
                    return handleGetRating(parts);
                default:
                    return "ERROR,Unknown command: " + command;
            }
        }


        private String handleRegister(String[] parts) {
            // Check parameters
            if (parts.length < 4) {
                return "REGISTER,FAILURE,Invalid parameters";
            }

            String username = parts[1];
            String password = parts[2];
            String bio = parts[3];

            System.out.println("Processing registration for user: " + username);

            // Simulate registration success (this would actually call database methods) 
            boolean success = database.addUser(username, password, bio); // Determines success of request

            return "REGISTER," + (success ? "SUCCESS" : "FAILURE");
        }


        private String handleLogin(String[] parts) {
            // Check parameters
            if (parts.length < 3) {
                return "LOGIN,FAILURE,Invalid parameters";
            }

            String username = parts[1];
            String password = parts[2];

            System.out.println("Processing login for user: " + username);

            // Simulate login success (this would actually call database methods)
            boolean success = database.login(username + "," + password); // Determines success of request
            String userId = "user_" + System.currentTimeMillis();

            return "LOGIN," + (success ? "SUCCESS," + userId : "FAILURE");
        }


        private String handleDeleteAccount(String[] parts) {
            // Check parameters
            if (parts.length < 2) {
                return "DELETE_ACCOUNT,FAILURE,Invalid parameters";
            }
            //
            String username = parts[1];

            System.out.println("Processing account deletion for user: " + username);

            // Simulate deletion success (this would actually call database methods)
            boolean success = true; // Determines success of request

            return "DELETE_ACCOUNT," + (success ? "SUCCESS" : "FAILURE");
        }


        private String handleAddItem(String[] parts) {
            // Check parameters
            if (parts.length < 6) {
                return "ADD_ITEM,FAILURE,Invalid parameters";
            }

            String sellerId = parts[1];
            String title = parts[2];
            String description = parts[3];
            String category = parts[4];
            double price;

            try {
                price = Double.parseDouble(parts[5]);
            } catch (NumberFormatException e) {
                return "ADD_ITEM,FAILURE,Invalid price";
            }

            System.out.println("Processing add item: " + title + " by seller " + sellerId);

            // Simulate adding item (this would actually call database methods)
            boolean success; //success of request
            success = database.addItem(new Item(sellerId, title, description, category, price)); 
            String itemId = "item_" + System.currentTimeMillis();

            return "ADD_ITEM," + (success ? "SUCCESS," + itemId : "FAILURE");
        }


        private String handleGetItem(String[] parts) {
            // Check parameters
            if (parts.length < 2) {
                return "GET_ITEM,FAILURE,Invalid parameters";
            }

            String itemId = parts[1];

            System.out.println("Processing get item: " + itemId);

            // Simulate getting item (this would actually call database methods)
            Item item = database.getItemById(itemId);
            boolean success = true; // Determines success of request
            if (item == null) {
               success = false; 
            }

            if (success) {
                // Return item details (would be from database)
                String sellerId = item.getSellerId();
                String title = item.getTitle();
                String description = item.getDescription();
                String category = item.getCategory();
                double price = item.getPrice();
                boolean sold = item.isSold();

                return "GET_ITEM,SUCCESS," + itemId + "," + sellerId + "," +
                        title + "," + description + "," + category + "," +
                        price + "," + sold;
            } else {
                return "GET_ITEM,FAILURE,Item not found";
            }
        }


        private String handleSearchItems(String[] parts) {
            // Check parameters
            if (parts.length < 2) {
                return "SEARCH_ITEMS,FAILURE,Invalid parameters";
            }

            String query = parts[1];
            String category = parts.length > 2 ? parts[2] : null;
            int maxResults = 10;

            // In case of unexpect input 
            if (parts.length > 3) {
                try {
                    maxResults = Integer.parseInt(parts[3]);
                } catch (NumberFormatException e) {
                    return "SEARCH_ITEMS,FAILURE,Invalid maxResults";
                }
            }

            System.out.println("Processing search: " + query + ", category: " + category);

            // Simulate search results (this would actually call database methods)
            // Return: SEARCH_ITEMS,SUCCESS,count,itemId1,title1,itemId2,title2,...


            SearchService searchService = new SearchService(database);
            List<Item> results = searchService.search(query, category, maxResults);
            
            StringBuilder response = new StringBuilder("SEARCH_ITEMS,SUCCESS," + results.size());

            // Add mock results
            for (Item item : results) {
                response.append(",item123,"+ item.getItemId() + "," + item.getTitle());
            }
            return response.toString();
        }


        private String handleMarkSold(String[] parts) {
            // Check parameters
            if (parts.length < 3) {
                return "MARK_SOLD,FAILURE,Invalid parameters";
            }

            String itemId = parts[1];
            String buyerId = parts[2];

            System.out.println("Processing mark sold: item " + itemId + " to buyer " + buyerId);

            // Simulate marking as sold (this would actually call database methods)

            Item item = database.getItemById(itemId);
            if (item == null) {
                return "MARK_SOLD,FAILURE,Item not found";
            }
            boolean success = item.markAsSold(buyerId); // Determines success of request

            // neccessary?
            if (success) {
                database.writeItemFile();  
            }

            return "MARK_SOLD," + (success ? "SUCCESS" : "FAILURE");
        }


        private String handleRemoveItem(String[] parts) {
            // Check parameters
            if (parts.length < 3) {
                return "REMOVE_ITEM,FAILURE,Invalid parameters";
            }

            String itemId = parts[1];
            String sellerId = parts[2];

            System.out.println("Processing remove item: " + itemId + " by seller " + sellerId);

            // Simulate removing item (this would actually call database methods)
            boolean success = database.removeItem(itemId, sellerId); // Determines success of request
            
            if (success) {
                database.writeItemFile();
            }

            return "REMOVE_ITEM," + (success ? "SUCCESS" : "FAILURE");
        }


        private String handleSendMessage(String[] parts) {
            // Check parameters
            if (parts.length < 5) {
                return "SEND_MESSAGE,FAILURE,Invalid parameters";
            }

            String senderId = parts[1];
            String receiverId = parts[2];
            String content = parts[3];
            String itemId = parts[4];

            System.out.println("Processing message from " + senderId + " to " + receiverId);

            // Simulate sending message (this would actually call database methods)
            Message message = new Message(senderId, receiverId, content);
            boolean success = database.addMessage(message); // Determines success of request

            return "SEND_MESSAGE," + (success ? "SUCCESS" : "FAILURE");
        }


        private String handleGetMessages(String[] parts) {
            // Check parameters
            if (parts.length < 3) {
                return "GET_MESSAGES,FAILURE,Invalid parameters";
            }

            String buyerId = parts[1];
            String sellerId = parts[2];

            System.out.println("Processing get messages between " + buyerId + " and " + sellerId);

            // Simulate getting messages (this would actually call database methods)
            // Return: GET_MESSAGES,SUCCESS,count,messageId1,senderId1,receiverId1,timestamp1,content1,...
            List<Message> messages = database.getMessagesBetweenUsers(buyerId, sellerId);
            if (messages == null || messages.isEmpty()) {
                return "GET_MESSAGES,SUCCESS,0";
            }
            StringBuilder response = new StringBuilder("GET_MESSAGES,SUCCESS,messages.size()");

            // Add mock messages
             for (Message message : messages){
                response.append(message.getMessageId() + "," + message.getSenderId() + "," 
                                + message.getReceiverId() + "," + message.getTimestamp() + "," + message.getContent());
            }
            return response.toString();
        }


        private String handleGetConversations(String[] parts) {
            // Check parameters
            if (parts.length < 2) {
                return "GET_CONVERSATIONS,FAILURE,Invalid parameters";
            }

            String userId = parts[1];

            System.out.println("Processing get conversations for user " + userId);

            // Simulate getting conversations (this would actually call database methods)
            // Return: GET_CONVERSATIONS,SUCCESS,count,userId1,username1,userId2,username2,...
            List<Message> allMessages = database.getMessagesBetweenUsers(userId, null);
            Set<String> conversation = new HashSet<>();

            for (Message message : allMessages) {
                String otherId = message.getSenderId().equals(userId) ? message.getReceiverId() : message.getSenderId();
                if (!otherId.equals(userId)) {
                    conversation.add(otherId);
                }
            }

            StringBuilder response = new StringBuilder("GET_CONVERSATIONS,SUCCESS," + conversation.size());

            for (String partnerId : conversation) {
                User partner = database.getUserById(partnerId);
                if (partner != null) {
                    response.append("," + partnerId + "," + partner.getUsername());
                }
            }
    
            return response.toString();
        }


        private String handleAddFunds(String[] parts) {
            // Check parameters
            if (parts.length < 3) {
                return "ADD_FUNDS,FAILURE,Invalid parameters";
            }

            String userId = parts[1];
            double amount;

            try {
                amount = Double.parseDouble(parts[2]);
            } catch (NumberFormatException e) {
                return "ADD_FUNDS,FAILURE,Invalid amount";
            }

            System.out.println("Processing add funds: $" + amount + " to user " + userId);

            User currentUser = database.getUserById(userId);
            boolean success = true; // Determines success of request
            if (currentUser == null) {
                success = false;
            } else {
                currentUser.depositFunds(amount);
            }

            if (success) {
                database.writeItemFile();
            }

            return "ADD_FUNDS," + (success ? "SUCCESS" : "FAILURE");
        }


        private String handleWithdrawFunds(String[] parts) {
            // Check parameters
            if (parts.length < 3) {
                return "WITHDRAW_FUNDS,FAILURE,Invalid parameters";
            }

            String userId = parts[1];
            double amount;

            try {
                amount = Double.parseDouble(parts[2]);
            } catch (NumberFormatException e) {
                return "WITHDRAW_FUNDS,FAILURE,Invalid amount";
            }

            System.out.println("Processing withdraw funds: $" + amount + " from user " + userId);

            // Simulate withdrawing funds (this would actually call database methods)
            boolean success = true; // Determines success of request 
            User currentUser = database.getUserById(userId); // Tracks the current user

            if (currentUser == null) {
                success = false;
            } else {
                success = currentUser.withdrawFunds(amount);
            }

            if (success) {
                database.writeItemFile();
            }

            return "WITHDRAW_FUNDS," + (success ? "SUCCESS" : "FAILURE");
        }


        private String handleProcessPurchase(String[] parts) {
            // Check parameters
            if (parts.length < 3) {
                return "PROCESS_PURCHASE,FAILURE,Invalid parameters";
            }

            String buyerId = parts[1];
            String itemId = parts[2];

            System.out.println("Processing purchase of item " + itemId + " by buyer " + buyerId);

            // Simulate processing purchase (this would actually call database methods)
            boolean success = true; // Determines success of request
            User buyer = database.getUserById(buyerId);  // Tracks the buyer
            Item item = database.getItemById(itemId); // Tracks the item
            if ((item == null)  || (item.isSold())) {
                success = false;
                return "PROCESS_PURCHASE," + (success ? "SUCCESS" : "FAILURE");
            }
            User seller = database.getUserById(item.getSellerId()); // Tracks the user
            if ((buyerId == null) || (buyer.equals(seller))) {
                success = false;
            } else {
                double itemCost = item.getPrice(); // Tracks the item cost
                if (!buyer.withdrawFunds(itemCost)) {
                    success = false;
                    return "PROCESS_PURCHASE," + (success ? "SUCCESS" : "FAILURE");
                }
                seller.depositFunds(itemCost);
                seller.removeListing(itemId);
                buyer.addToPurchaseHistory(item);
                item.markAsSold(buyerId);
            }

            if (success) {
                database.writeItemFile();
            }

            return "PROCESS_PURCHASE," + (success ? "SUCCESS" : "FAILURE");
        }


        private String handleRateSeller(String[] parts) {
            // Check parameters
            if (parts.length < 3) {
                return "RATE_SELLER,FAILURE,Invalid parameters";
            }

            String sellerId = parts[1];
            double rating;

            try {
                rating = Double.parseDouble(parts[2]);
            } catch (NumberFormatException e) {
                return "RATE_SELLER,FAILURE,Invalid rating";
            }

            System.out.println("Processing seller rating: " + rating + " for seller " + sellerId);

            // Simulate rating seller (this would actually call database methods)
            User seller = database.getUserById(sellerId);
            if (seller == null) {
                return "RATE_SELLER,FAILURE,Seller not found";
            }
        
            List<Item> soldItems = seller.getSoldItems();
            if (soldItems == null || soldItems.isEmpty()) {
                return "RATE_SELLER,FAILURE,No sold items to rate";
            }

            for (Item item : soldItems) {
                if (item.getRating() == 0.0) {
                    item.updateRating(rating);
                    return "RATE_SELLER,SUCCESS";
                }
            }
            
            return "RATE_SELLER,FAILURE,All items already rated";
        }


        private String handleGetRating(String[] parts) {
            // Check parameters
            if (parts.length < 2) {
                return "GET_RATING,FAILURE,Invalid parameters";
            }

            String sellerId = parts[1];

            System.out.println("Processing get rating for seller " + sellerId);

            // Simulate getting rating (this would actually call database methods)
            User seller = database.getUserById(sellerId);
            List<Item> soldItems = seller.getSoldItems();
            double sellerRating = 0.0;
        
            if (seller == null) {
                return "GET_RATING,FAILURE,Seller not found";
            }
            
            if (soldItems == null || soldItems.isEmpty()) {
                return "GET_RATING,SUCCESS," + sellerRating; 
            }

            int numOfRating = 0;
            for (Item item : soldItems) {
                double itemRating = item.getRating();
                if (itemRating > 0.0) {
                    sellerRating += itemRating;
                    numOfRating++;
                }
            }
            
            double averageRating = numOfRating > 0 ? sellerRating / numOfRating : 0.0;
            return "GET_RATING,SUCCESS," + averageRating;
        }

        /**
         * Closes all resources associated with this client handler
         */
        public void closeEverything() {
            if (!closed) {
                closed = true;

                try {
                    if (reader != null) {
                        reader.close();
                    }
                    if (writer != null) {
                        writer.close();
                    }
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }

                    System.out.println("Closed connection with: " + socket.getInetAddress().getHostAddress());
                } catch (IOException e) {
                    System.out.println("Error closing resources: " + e.getMessage());
                }
            }
        }
    }
}
