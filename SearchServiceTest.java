import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

/** Tests for the SearchService class.
 * @version April 6th, 2024
 * @author Rayaan Grewal
 */
public class SearchServiceTest {
	private Database db;
	private SearchService searchService;
	private Item item1, item2, item3, item4;

	/** start database and search service with test items. */
	@Before
	public void setup() {
		db = new Database();
		searchService = new SearchService(db);


		db.addUser("seller1", "password1", "Seller 1 bio");
		db.addUser("seller2", "password2", "Seller 2 bio");
		db.addUser("seller3", "password3", "Seller 3 bio");
		db.addUser("seller4", "password4", "Seller 4 bio");
		db.addUser("seller5", "password5", "Seller 5 bio");


		User seller1 = db.getUserByUsername("seller1");
		User seller2 = db.getUserByUsername("seller2");

		User seller3 = db.getUserByUsername("seller3");

		User seller4 = db.getUserByUsername("seller4");


		item1 = new Item(seller1.getUserId(), "Denim Jacket", "Cool denim jacket", "Clothing", 50.0);
		item2 = new Item(seller2.getUserId(), "Leather Jacket", "Stylish leather jacket", "Clothing", 80.0);
		item3 = new Item(seller3.getUserId(), "Smartphone", "Latest smartphone with advanced features", "Electronics", 700.0);
		item4 = new Item(seller4.getUserId(), "Denim Shorts", "Comfortable denim shorts", "Clothing", 30.0);


		boolean added1 = db.addItem(item1);
		boolean added2 = db.addItem(item2);
		boolean added3 = db.addItem(item3);

		boolean added4 = db.addItem(item4);


		assertTrue("Failed to add item1", added1);
		assertTrue("Failed to add item2", added2);

		assertTrue("Failed to add item3", added3);
		assertTrue("Failed to add item4", added4);

		// Verify items can be retrieved from the database
		assertNotNull("Item1 not found in database", db.getItemById(item1.getItemId()));
		assertNotNull("Item2 not found in database", db.getItemById(item2.getItemId()));

		assertNotNull("Item3 not found in database", db.getItemById(item3.getItemId()));
		assertNotNull("Item4 not found in database", db.getItemById(item4.getItemId()));
	}

	/**  search without category. */
	@Test
	public void testSearchWithoutCategory() {
		List<Item> results = searchService.search("denim");
		assertEquals("Should find 2 items with 'denim'", 2, results.size());

		assertTrue("Results should contain denim jacket", results.contains(item1));

		assertTrue("Results should contain denim shorts", results.contains(item4));
	}

	/**  search with a category filter. */
	@Test
	public void testSearchWithCategory() {
		List<Item> results = searchService.search("jacket", "Clothing");
		assertEquals("Should find 2 jackets in Clothing category", 2, results.size());

		assertTrue("Results should contain denim jacket", results.contains(item1));

		assertTrue("Results should contain leather jacket", results.contains(item2));

		results = searchService.search("jacket", "Electronics");
		assertTrue("Should not find jackets in Electronics category", results.isEmpty());
	}

	/**  search with a max results limit. */
	@Test
	public void testSearchWithMaxResults() {
		List<Item> results = searchService.search("jacket", 1);

		assertEquals("Should limit results to 1 item", 1, results.size());
	}

	/** Test that search results are ordered by descending score. */
	@Test
	public void testSearchScoringOrder() {
		// Get seller5 from database
		User seller5 = db.getUserByUsername("seller5");
		if (seller5 == null) {

			db.addUser("seller5", "password5", "Seller 5 bio");
			seller5 = db.getUserByUsername("seller5");
		}

		Item item5 = new Item(seller5.getUserId(), "Denim Shorts", "denim denim comfortable denim shorts", "Clothing", 30.0);
		boolean added5 = db.addItem(item5);
		assertTrue("Failed to add item5", added5);

		List<Item> results = searchService.search("denim");

		assertEquals("Should find 3 items with 'denim'", 3, results.size());
		assertEquals("Item with most 'denim' mentions should be first", item5, results.get(0));
	}

	/**  search with no matching results. */
	@Test
	public void testNoMatches() {
		List<Item> results = searchService.search("nonexistentkeyword");

		assertTrue("Should return empty list for nonexistent keyword", results.isEmpty());
	}
}
