import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import java.util.Map;

/**
 * Tests for database
 * @author Rayaan Grewal
 * @version April 6th, 2024
 */
public class DatabaseTest {

	private Database database;

	/**
	 * init  new db before each test
	 */
	@Before
	public void setUp() {
		database = new Database();
	}

	/**
	 * Tests adding a user.
	 */
	@Test
	public void testAddUser() {
		boolean added = database.addUser("aditya", "passwordABC", "Tech reviewer and part-time reseller.");
    
		assertTrue(added);
    
		boolean addedAgain = database.addUser("aditya", "passwordABC", "Duplicate username not allowed.");
		assertFalse(addedAgain);
	}

	/**
	 * Tests login .
	 */
	@Test
	public void testLogin() {
		database.addUser("rayaan", "rayaanPass", "Interested in discount Western clothing.");
		assertTrue(database.login("rayaan,rayaanPass"));
		assertFalse(database.login("rayaan,wrongPassword"));
    
		assertFalse(database.login("someoneElse,whateverPass"));
    
		assertFalse(database.login("incompleteData"));
	}

	/**
	 * Tests retrieving a user with username.
	 */
	@Test
	public void testGetUserByUsername() {
		database.addUser("arjun", "arjunPass", "Curates rare electric items from auctions.");
    
		User user = database.getUserByUsername("arjun");
		assertNotNull(user);
    
		assertEquals("arjun", user.getUsername());
	}

	/**
	 * Tests retrieving a user by ID.
	 */
	@Test
	public void testGetUserById() {
		database.addUser("anthony", "anthonyPass", "Authentic Western wear dealer.");
		User user = database.getUserByUsername("anthony");
		User sameUser = database.getUserById(user.getUserId());
    
		assertEquals(user, sameUser);
	}

	/**
	 * Tests user existence check by username.
	 */
	@Test
	public void testUserExists() {
		database.addUser("james", "jamesPass", "Loves sampling various instant foods.");
		User user = database.userExists("james");
		assertNotNull(user);
		assertEquals("james", user.getUsername());
		User notFound = database.userExists("nobody");
		assertNull(notFound);
	}

	/**
	 * Tests retrieving all users in the database.
	 */
	@Test
	public void testGetAllUsers() {
		database.addUser("mat", "matPass", "Bulk buyer of electronic accessories.");
    
		database.addUser("harshitha", "harshithaPass", "Distributor of microwavable meals.");
		List<User> allUsers = database.getAllUsers();
    
		assertEquals(2, allUsers.size());
	}

	/**
	 * Tests adding an item and retrieving it by ID.
	 */
	@Test
	public void testAddItemAndGetItemById() {
		database.addUser("aditya", "adityaSecret", "Reviews top-of-the-line gadgets.");
    
		User user = database.getUserByUsername("aditya");
    
		Item item = new Item(user.getUserId(), "Microwave Oven", "Smart oven for quick cooking.", "Electronics", 200.0);
		boolean addedItem = database.addItem(item);
    
		assertTrue(addedItem);
		Item fetchedItem = database.getItemById(item.getItemId());
    
		assertNotNull(fetchedItem);
		assertEquals("Microwave Oven", fetchedItem.getTitle());
	}

	/**
	 * Tests retrieving all and active items from the database.
	 */
	@Test
	public void testGetAllItemsAndGetActiveItems() {
		database.addUser("rayaan", "rayaanSecret", "Focus on packaged foods and Western t-shirts.");
		User user = database.getUserByUsername("rayaan");
    
		Item item1 = new Item(user.getUserId(), "Instant Pasta", "Creamy cheese pasta packet.", "Food", 5.0);
    
		Item item2 = new Item(user.getUserId(), "Denim Jacket", "Stylish Western denim jacket.", "Clothing", 60.0);
    
		Item item3 = new Item(user.getUserId(), "Smartphone", "Android phone with 5G support.", "Electronics", 700.0);
    
		database.addItem(item1);
		database.addItem(item2);
		database.addItem(item3);
    
		item2.markAsSold("buyerXYZ");
		List<Item> allItems = database.getAllItems();
		assertEquals(3, allItems.size());
    
		List<Item> activeItems = database.getActiveItems();
		assertEquals(2, activeItems.size());
    
		assertFalse(activeItems.contains(item2));
	}

	/**
	 * Tests adding messages and retrieving conversations.
	 */
	@Test
	public void testAddMessageAndGetMessagesBetweenBuyerAndSeller() {
		database.addUser("arjun", "arjun1Pass", "Trader of leftover electric stock.");
    
		database.addUser("harshitha", "harshitha1Pass", "Specializes in packaged meals.");
    
		User buyer = database.getUserByUsername("arjun");
		User seller = database.getUserByUsername("harshitha");
		Item item = new Item(seller.getUserId(), "Laptop", "Premium laptop for design work.", "Electronics", 1000.0);
    
		database.addItem(item);
		Message message1 = new Message(buyer.getUserId(), seller.getUserId(), "Is this laptop still available?");
    
		database.addMessage(message1, item.getItemId());
    
		Message message2 = new Message(seller.getUserId(), buyer.getUserId(), "Yes, it is brand new.");
		database.addMessage(message2, item.getItemId());
    
		List<Message> conversation = database.getMessagesBetweenBuyerAndSeller(buyer.getUserId(), seller.getUserId());
    
		assertEquals(2, conversation.size());
		assertEquals("Is this laptop still available?", conversation.get(0).getContent());
    
		assertEquals("Yes, it is brand new.", conversation.get(1).getContent());
	}

	/**
	 * Tests removing an item from the database.
	 */
	@Test
	public void testRemoveItem() {
		database.addUser("mat", "matSecret", "Interested in wholesale microwavable snacks.");
    
		User user = database.getUserByUsername("mat");
		Item item = new Item(user.getUserId(), "Frozen Burger Patty", "Pack of four burger patties.", "Food", 10.0);
    
		database.addItem(item);
		boolean removed = database.removeItem(item.getItemId(), user.getUserId());
    
		assertTrue(removed);
		assertNull(database.getItemById(item.getItemId()));
	}

	/**
	 * Tests retrieving messages between two users.
	 */
	@Test
	public void testGetMessagesBetweenUsers() {
		database.addUser("anthony", "anthonyX", "Distributor of phone accessories.");
    
		database.addUser("james", "jamesX", "Buys jackets in bulk for reselling.");
		User anthony = database.getUserByUsername("anthony");
		User james = database.getUserByUsername("james");
		Message m1 = new Message(anthony.getUserId(), james.getUserId(), "Looking for leather jackets. Can you supply?");
		Message m2 = new Message(james.getUserId(), anthony.getUserId(), "Yes, I have a fresh stock of them.");
    
		database.addMessage(m1);
		database.addMessage(m2);
		List<Message> messages = database.getMessagesBetweenUsers(anthony.getUserId(), james.getUserId());
		assertEquals(2, messages.size());
    
		assertEquals("Looking for leather jackets. Can you supply?", messages.get(0).getContent());
		assertEquals("Yes, I have a fresh stock of them.", messages.get(1).getContent());
	}

	/**
	 * Tests role-based conversations and retrieving all conversation partners.
	 */
	@Test
	public void testGetUserRoleBasedConversationsAndGetAllConversationPartners() {
		database.addUser("harshitha", "harshithaX", "Supplies various Western tops.");
    
		database.addUser("aditya", "adityaX", "Buys electronics to modify.");
		User harshitha = database.getUserByUsername("harshitha");
    
		User aditya = database.getUserByUsername("aditya");
    
		Message m1 = new Message(harshitha.getUserId(), aditya.getUserId(), "I have new T-shirts in stock.");
    
		database.addMessage(m1);
		Map<String, List<String>> conversationMap = database.getUserRoleBasedConversations(harshitha.getUserId());
		assertNotNull(conversationMap);
    
		List<String> allPartners = database.getAllConversationPartners(harshitha.getUserId());
    
		assertEquals(1, allPartners.size());
    
		assertTrue(allPartners.contains(aditya.getUserId()));
	}
  
}
