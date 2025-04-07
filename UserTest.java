import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;

/** Tests for the User class. 
 * @version April 6th, 2024
 * @author Rayaan Grewal
 */
public class UserTest {

	/** Tests that the bio is correctly returned. */
	@Test
	public void testGetBio() {
		Database db = new Database();
		User user = new User("aditya", "password123", "I love reviewing modern gadgets and apparel.", db);
		assertEquals("I love reviewing modern gadgets and apparel.", user.getBio());
	}

	/** Tests that the user ID is generated and is not empty. */
	@Test
	public void testGetUserId() {
		Database db = new Database();
		User user = new User("rayaan", "secretPass", "Passionate about healthy processed food options.", db);
		assertNotNull(user.getUserId());
		assertFalse(user.getUserId().isEmpty());
	}

	/** Tests that the username is returned correctly. */
	@Test
	public void testGetUsername() {
		Database db = new Database();
		User user = new User("arjun", "arjunPass", "Always on the lookout for the latest electronics.", db);
		assertEquals("arjun", user.getUsername());
	}

	/** Tests password validation. */
	@Test
	public void testValidatePassword() {
		Database db = new Database();
		User user = new User("anthony", "anthonyPass", "A curator of imported Western wear.", db);
    
		assertTrue(user.validatePassword("anthonyPass"));
		assertFalse(user.validatePassword("wrongPass"));
	}

	/** Tests depositing funds into the user's account. */
	@Test
	public void testDepositFunds() {
		Database db = new Database();
		User user = new User("james", "jamesPass", "Researching advanced tech from various brands.", db);
    
		user.depositFunds(100.0);
		assertEquals(100.0, user.getBalance(), 0.0001);
    
		user.depositFunds(-50.0);
    
		assertEquals(100.0, user.getBalance(), 0.0001);
	}

	/** Tests withdrawing funds from the user's account. */
	@Test
	public void testWithdrawFunds() {
		Database db = new Database();
		User user = new User("mat", "matPass", "Interested in bulk-buying processed snacks.", db);
    
		user.depositFunds(200.0);
    
		assertTrue(user.withdrawFunds(50.0));
    
		assertEquals(150.0, user.getBalance(), 0.0001);
		assertFalse(user.withdrawFunds(-10.0));
    
		assertEquals(150.0, user.getBalance(), 0.0001);
    
		assertFalse(user.withdrawFunds(300.0));
		assertEquals(150.0, user.getBalance(), 0.0001);
    
	}

	/** Tests adding listings and removing them from the user. */
	@Test
	public void testAddListingAndRemoveListing() {
		Database db = new Database();
    
		User user = new User("harshitha", "harshithaPass", "Selling top-tier Western clothing lines.", db);
		Item item1 = new Item(user.getUserId(), "Leather Jacket", "Genuine leather jacket with a sleek design.", "Clothing", 120.0);
    
		Item item2 = new Item(user.getUserId(), "Wireless Earbuds", "High-fidelity earbuds with noise cancellation.", "Electronics", 80.0);
    
		db.addItem(item1);
		db.addItem(item2);
		assertTrue(user.getActiveListings().contains(item1));
		assertTrue(user.getActiveListings().contains(item2));
    
		user.removeListing(item1.getItemId());
    
    
		assertFalse(user.getActiveListings().contains(item1));
		assertTrue(user.getActiveListings().contains(item2));
	}

	/** Tests adding an item to the users purchase history. */
	@Test
	public void testAddToPurchaseHistory() {
		Database db = new Database();
		User user = new User("arjun", "arjun2Pass", "Collects exclusive tech items.", db);
    
		Item purchasedItem = new Item("someSellerId", "Instant Noodles", "Pack of spicy noodles ready to cook.", "Food", 2.99);
    
		db.addItem(purchasedItem);
		user.addToPurchaseHistory(purchasedItem);
		purchasedItem.markAsSold(user.getUserId());
		assertTrue(user.getPurchaseHistory().contains(purchasedItem));
	}

	/** Tests an item as sold. */
	@Test
	public void testRecordItemSold() {
		Database db = new Database();
		User user = new User("anthony", "anthony2Pass", "Dropshipping Western jackets and pants.", db);
    
		Item itemForSale = new Item(user.getUserId(), "Denim Jeans", "Slim-fit denim with a vintage look.", "Clothing", 45.0);
		db.addItem(itemForSale);
		itemForSale.markAsSold("someBuyerId");
    
		user.recordItemSold(itemForSale);
		assertFalse(user.getActiveListings().contains(itemForSale));
    
		assertTrue(user.getSoldItems().contains(itemForSale));
	}

	/** Tests retrieving the users active listings. */
	@Test
	public void testGetActiveListings() {
		Database db = new Database();
		User user = new User("james", "james2Pass", "Reviews processed foods and sells them occasionally.", db);
    
		Item item1 = new Item(user.getUserId(), "Microwave Oven", "Smart oven with multiple cooking modes.", "Electronics", 150.0);
    
		Item item2 = new Item(user.getUserId(), "Frozen Pizza", "Ready-to-bake pizza pack.", "Food", 8.0);
    
		Item item3 = new Item(user.getUserId(), "Leather Boots", "Comfortable boots for outdoor wear.", "Clothing", 75.0);
    
		db.addItem(item1);
		db.addItem(item2);
		db.addItem(item3);
    
		item2.markAsSold("buyerId");
		ArrayList<Item> active = user.getActiveListings();
    
		assertTrue(active.contains(item1));
		assertFalse(active.contains(item2));
    
		assertTrue(active.contains(item3));
	}

	/** Tests retrieving the users purchase history */
	@Test
	public void testGetPurchaseHistory() {
		Database db = new Database();
		User user = new User("mat", "mat2Pass", "Loves trying different packaged meals.", db);
    
		Item itemPurchased = new Item("sellerXYZ", "Chocolate Bar", "Dark chocolate bar with roasted almonds.", "Food", 3.5);
    
    
		db.addItem(itemPurchased);
		itemPurchased.markAsSold(user.getUserId());
    
		ArrayList<Item> history = user.getPurchaseHistory();
		assertTrue(history.contains(itemPurchased));
	}

	/** Tests retrieving the users sold items */
	@Test
	public void testGetSoldItems() {
		Database db = new Database();
		User user = new User("harshitha", "harshitha2Pass", "Focus on premium electronic accessories.", db);
    
		Item itemSold = new Item(user.getUserId(), "Laptop", "High-end gaming laptop with RGB lighting.", "Electronics", 1200.0);
    
		db.addItem(itemSold);
		itemSold.markAsSold("anotherUser");
		ArrayList<Item> soldItems = user.getSoldItems();
    
		assertTrue(soldItems.contains(itemSold));
	}
}
