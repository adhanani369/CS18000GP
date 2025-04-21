import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class SearchBarTest {

     /*
     * Find seller by valid user id
     * expect return: user
     */
    @Test
    public void testFindSellerById_ValidUserId() {
        Database db = new Database();
        SearchBar searchBar = new SearchBar(db);
        db.addUser("user1", "pass", "bio");
        User u = db.getUserByUsername("user1");
        Item item = new Item(u.getUserId(), "Book A", "Desc", "Books", 10.0);
        assertTrue(db.addItem(item));

        User found = searchBar.findSellerById(u.getUserId());
        assertNotNull(found);
        assertEquals(u.getUserId(), found.getUserId());
    }


    /*
     * Find seller by invalid user id because the user have no active listings
     * expect get: null
     */
    @Test
    public void testFindSellerById_InvalidUserId_NoActingListing() {
        Database db = new Database();
        SearchBar searchBar = new SearchBar(db);
        db.addUser("user2", "pass", "bio");
        User u = db.getUserByUsername("user2");

        assertNull(searchBar.findSellerById(u.getUserId()));
    }

    /*
     * Find seller by invalid user id because user id doesn't match any user in the database
     * expect get: null
     */
    @Test
    public void testFindSellerById_DoesNotExist() {
        Database db = new Database();
        SearchBar searchBar = new SearchBar(db);
        assertNull(searchBar.findSellerById("nonexistent"));
    }

    /*
     * Find seller by partial valid user id
     * expect get: user id
     */
    @Test
    public void testSearchSellersByPartialId() {
        Database db = new Database();
        SearchBar searchBar = new SearchBar(db);
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

    /*
     * Find seller by partial valid user id with no match
     * expect get: null
     */
    @Test
    public void testSearchSellersByPartialId_noMatch() {
        Database db = new Database();
        SearchBar searchBar = new SearchBar(db);
        db.addUser("userC", "pass", "bio");
        // add an item so C is active
        User uc = db.getUserByUsername("userC");
        db.addItem(new Item(uc.getUserId(), "I3", "d", "Cat", 3.0));

        // search with unrelated substring
        List<User> results = searchBar.searchSellersByPartialId("zzzz");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    /*
     * Find all active sellers
     * expect get: list of sellers
     */
    @Test
    public void testgetAllActiveSellers() {
        Database db = new Database();
        SearchBar searchBar = new SearchBar(db);
        db.addUser("user1", "pass1", "bio1");
        db.addUser("user2", "pass1", "bio1");
        db.addUser("user3", "pass1", "bio1");

        User user1 = db.getUserByUsername("user1");
        User user2 = db.getUserByUsername("user2");
        User user3 = db.getUserByUsername("user3");

        db.addItem(new Item(user1.getUserId(), "A", "d", "C", 5.0));
        db.addItem(new Item(user3.getUserId(), "B", "d", "C", 6.0));

        List<User> results = searchBar.getAllActiveSellers();

        assertFalse(results.isEmpty());
        assertTrue(results.contains(user1));
        assertTrue(results.contains(user3));
        assertFalse(results.contains(user2));
    }

    /*
     * Find none active sellers
     * expect get: empty list
     */
    @Test
    public void testgetAllActiveSellers_noMatch() {
        Database db = new Database();
        SearchBar searchBar = new SearchBar(db);
        db.addUser("u4", "pass", "bio");
        db.addUser("u5", "pass", "bio");

        List<User> active = searchBar.getAllActiveSellers();
        assertNotNull(active);
        assertTrue(active.isEmpty());
    }
}
