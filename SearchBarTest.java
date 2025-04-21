import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SearchBarTest {
    private Database db;
    private SearchBar searchBar;

    @Before
    public void setUp() {
        db = new Database();
        searchBar = new SearchBar(db);
    }

    @Test
    public void testFindSellerById_ValidUserId() {
        db.addUser("user1", "pass", "bio");
        User u = db.getUserByUsername("user1");
        Item item = new Item(u.getUserId(), "Book A", "Desc", "Books", 10.0);
        assertTrue(db.addItem(item));

        User found = searchBar.findSellerById(u.getUserId());
        assertNotNull(found);
        assertEquals(u.getUserId(), found.getUserId());
    }

    @Test
    public void testFindSellerById_NoActiveListings() {
        db.addUser("user2", "pass", "bio");
        User u = db.getUserByUsername("user2");

        assertNull(searchBar.findSellerById(u.getUserId()));
    }

    @Test
    public void testFindSellerById_DoesNotExist() {
        assertNull(searchBar.findSellerById("nonexistent"));
    }

    @Test
    public void testSearchSellersByPartialId_SingleMatch() {
        db.addUser("userA", "pass", "bio");
        db.addUser("userB", "pass", "bio");

        User ua = db.getUserByUsername("userA");
        User ub = db.getUserByUsername("userB");
        // Add an item to each so they have active listings
        db.addItem(new Item(ua.getUserId(), "I1", "d", "Cat", 1.0));
        db.addItem(new Item(ub.getUserId(), "I2", "d", "Cat", 2.0));

        // Use a substring of ua's userId
        String partial = ua.getUserId().substring(0, 8).toLowerCase();
        List<User> results = searchBar.searchSellersByPartialId(partial);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.contains(ua));
        assertFalse(results.contains(ub));
    }

    @Test
    public void testSearchSellersByPartialId_NoMatch() {
        db.addUser("userC", "pass", "bio");
        // add an item so C is active
        User uc = db.getUserByUsername("userC");
        db.addItem(new Item(uc.getUserId(), "I3", "d", "Cat", 3.0));

        // search with unrelated substring
        List<User> results = searchBar.searchSellersByPartialId("zzzz");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testGetAllActiveSellers() {
        db.addUser("u1", "pass", "bio");
        db.addUser("u2", "pass", "bio");
        db.addUser("u3", "pass", "bio");

        User u1 = db.getUserByUsername("u1");
        User u2 = db.getUserByUsername("u2");
        User u3 = db.getUserByUsername("u3");

        db.addItem(new Item(u1.getUserId(), "A", "d", "C", 5.0));
        db.addItem(new Item(u3.getUserId(), "B", "d", "C", 6.0));

        List<User> active = searchBar.getAllActiveSellers();
        assertEquals(2, active.size());
        assertTrue(active.contains(u1));
        assertTrue(active.contains(u3));
        assertFalse(active.contains(u2));
    }

    @Test
    public void testGetAllActiveSellers_NoMatch() {
        db.addUser("u4", "pass", "bio");
        db.addUser("u5", "pass", "bio");

        List<User> active = searchBar.getAllActiveSellers();
        assertNotNull(active);
        assertTrue(active.isEmpty());
    }
}
