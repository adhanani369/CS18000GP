# Marketplace Application

## Introduction
This project implements a marketplace application that allows users to buy and sell items. Users can create accounts, list items for sale, communicate with other users, manage their funds, and conduct transactions. The application uses a file-based persistence system to store user data, item listings, and message history.

## How to Run the Application
1. Ensure you have JDK 8 or higher installed
2. Create the necessary data files in the project root directory:
   - `stopword.txt` - Contains common words to exclude from tag extraction
   - `special_characters.txt` - Contains special characters to be cleaned from tags
3. Compile all Java files:
   ```
   javac *.java
   ```
4. Run the application (implementation of main class is required)

## Class Descriptions

### Database.java
The core data management class that handles all persistent storage operations.

**Functionality:**
- Manages user accounts, items, and messages
- Stores data in memory using HashMaps and ArrayLists
- Provides synchronized methods for thread safety
- Persists data to files (`users.txt`, `items.txt`, `messages.txt`)
- Handles conversation tracking between buyers and sellers
- Implements CRUD operations for all entity types

**Key Methods:**
- User management: `addUser()`, `login()`, `getUserByUsername()`, `getUserById()`
- Item management: `addItem()`, `getItemById()`, `getAllItems()`, `getActiveItems()`, `removeItem()`
- Message handling: `addMessage()`, `getMessagesBetweenBuyerAndSeller()`, `getMessagesBetweenUsers()`
- File operations: `readUserFile()`, `writeUserFile()`, `readItemFile()`, `writeItemFile()`, `readMessageFiles()`



### User.java
Represents a user in the marketplace with functionality for both buyers and sellers.

**Functionality:**
- Stores user profile information (username, password, bio)
- Manages user balance for transactions
- Tracks items listed for sale, purchased items, and sold items
- Handles fund deposits and withdrawals

**Key Methods:**
- Account management: `getUserId()`, `getUsername()`, `validatePassword()`
- Financial operations: `depositFunds()`, `withdrawFunds()`, `getBalance()`
- Item tracking: `addListing()`, `removeListing()`, `addToPurchaseHistory()`, `recordItemSold()`
- Collection getters: `getActiveListings()`, `getPurchaseHistory()`, `getSoldItems()`



### Item.java
Represents an item listing in the marketplace.

**Functionality:**
- Stores item details (title, description, category, price)
- Manages item status (available or sold)
- Handles tag extraction from item descriptions
- Tracks seller and buyer information
- Supports item rating system

**Key Methods:**
- Information retrieval: `getItemId()`, `getSellerId()`, `getTitle()`, `getDescription()`, `getCategory()`
- Tag management: `getTags()`, `extractTags()`, `cleanWord()`
- Status handling: `isSold()`, `markAsSold()`, `getBuyerId()`
- Rating system: `getRating()`, `updateRating()`



### Message.java
Handles communication between users.

**Functionality:**
- Stores message content, sender/receiver information, and timestamps
- Tracks message status (read/unread)
- Provides unique message identification

**Key Methods:**
- Information retrieval: `getMessageId()`, `getSenderId()`, `getReceiverId()`, `getContent()`, `getTimestamp()`
- Status management: `isRead()`, `markAsRead()`
- Formatting: `toString()`



### PaymentProcessing.java
Manages financial transactions between users.

**Functionality:**
- Handles fund transfers between buyers and sellers
- Processes item purchases
- Updates user balances and item ownership

**Key Methods:**
- Financial operations: `addFunds()`, `withdrawFunds()`
- Transaction processing: `processPurchase()`



### SearchService.java
Provides advanced search capabilities for finding items.

**Functionality:**
- Searches items by keywords across titles, descriptions, and tags
- Assigns relevance scores to search results
- Supports filtering by category
- Limits number of results
- Sorts results by relevance

**Key Methods:**
- Search operations: Multiple overloaded `search()` methods with different parameters



### SearchBar.java
Offers functionality for finding sellers.

**Functionality:**
- Searches for sellers by user ID
- Finds sellers with partial ID matches
- Lists all active sellers

**Key Methods:**
- Seller search: `findSellerById()`, `searchSellersByPartialId()`, `getAllActiveSellers()`


## Data Storage


### File Formats
- `users.txt`: User records in format `username,password,bio,balance,userId,activeListings,purchaseHistory,soldItems`
- `items.txt`: Item listings in format `itemId,sellerId,title,description,category,price,isSold,buyerId`
- `messages.txt`: Message history
- Conversation files: Named as `buyer_[buyerId]_seller_[sellerId].txt` with content in format `senderId:messageContent`


### Required Configuration Files
- `stopword.txt`: Contains common words to exclude from tag extraction
- `special_characters.txt`: Contains special characters to be cleaned from tags


## Key Operations


### User Registration Process
1. User provides username, password, and bio
2. System validates username uniqueness
3. New user is created with a UUID
4. User data is stored in memory and persisted to file


### Item Listing Process
1. User creates an item with title, description, category, and price
2. System generates a UUID for the item
3. Tags are extracted from the item description
4. Item is added to the database and seller's active listings
5. Item data is persisted to file


### Messaging System
1. User selects another user to message
2. System identifies buyer/seller roles
3. Message is created with content and timestamp
4. Message is stored in memory and persisted to the appropriate conversation file


### Purchase Transaction
1. Buyer initiates purchase of an item
2. System validates availability and funds
3. Funds are transferred from buyer to seller
4. Item is marked as sold with buyer's ID
5. Transaction data is updated in memory and persisted to files


### Search Implementation
1. User provides search query and optional parameters
2. System searches items by keywords in titles, descriptions, and tags
3. Results are scored based on match relevance
4. Results are sorted by score and returned to the user


## Testing
Each class has a corresponding test class (e.g., `DatabaseTest.java`, `UserTest.java`) that verifies the functionality of all methods using JUnit. The tests ensure that:
- All operations work correctly with valid inputs
- Error conditions are handled properly
- Data persistence functions correctly
- Business logic maintains consistency


## Interface Implementation
Each class implements a corresponding interface (e.g., `UserInterface`, `ItemInterface`) that defines the expected behavior. This ensures consistency and facilitates testing.
