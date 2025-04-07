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
		item1 = new Item("seller1", "Denim Jacket", "Cool denim jacket", "Clothing", 50.0);
		item2 = new Item("seller2", "Leather Jacket", "Stylish leather jacket", "Clothing", 80.0);
		item3 = new Item("seller3", "Smartphone", "Latest smartphone with advanced features", "Electronics", 700.0);
		item4 = new Item("seller4", "Denim Shorts", "Comfortable denim shorts", "Clothing", 30.0);

    
		db.addItem(item1);
		db.addItem(item2);
		db.addItem(item3);
		db.addItem(item4);
	}

	/**  search without category. */
	@Test
	public void testSearchWithoutCategory() {
		List<Item> results = searchService.search("denim");
		assertEquals(2, results.size());
    
		assertTrue(results.contains(item1));
		assertTrue(results.contains(item4));
    
	}

	/**  search with a category filter. */
	@Test
	public void testSearchWithCategory() {
		List<Item> results = searchService.search("jacket", "Clothing");
		assertEquals(2, results.size());
    
		assertTrue(results.contains(item1));
		assertTrue(results.contains(item2));
    
		results = searchService.search("jacket", "Electronics");
		assertTrue(results.isEmpty());
	}

	/**  search with a max results limit. */
	@Test
	public void testSearchWithMaxResults() {
		List<Item> results = searchService.search("jacket", 1);
    
		assertEquals(1, results.size());
	}

	/** Test that search results are ordered by descending score. */
	@Test
	public void testSearchScoringOrder() {
		Item item5 = new Item("seller5", "Denim Shorts", "denim denim comfortable denim shorts", "Clothing", 30.0);
    
		db.addItem(item5);
		List<Item> results = searchService.search("denim");
    
		assertEquals(3, results.size());
		assertEquals(item5, results.get(0));
	}

	/**  search with no matching results. */
	@Test
	public void testNoMatches() {
		List<Item> results = searchService.search("nonexistentkeyword");
    
		assertTrue(results.isEmpty());
	}
}
