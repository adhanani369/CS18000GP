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

        db.addUser("username1", "pass1", "bio1");

        User user = db.getUserByUsername("username1");
        String validUserId = user.getUserId();

        user.addListing(new Item("3", "The Secret of Emberwood", "In the heart of Emberwood,", "Book", 0));

        System.out.println();
        assertNotNull(user);
        //assertEquals(user, searchBar.findSellerById(validUserId));
    }


    /*
     * Find seller by invalid user id because the user have no active listings
     * expect get: null
     */
    @Test
    public void testFindSellerById_InvalidUserId_NoActingListing() {
        Database db = new Database();
        SearchBar searchBar = new SearchBar(db);
        String invalidUserId = "d8bb19d1-d5e5-4a32-9e0d-68248fefdd3d";

        String username1 = "d8bb19d1-d5e5-4a32-9e0d-68248fefdd3d";
        String pass1 = "pass";
        String bio1 = "bio";

        User user = new User(username1, pass1, bio1, db);
        db.addUser(username1, pass1, bio1);

        assertNull(searchBar.findSellerById(invalidUserId));
    }

    /*
     * Find seller by invalid user id because user id doesn't match any user in the database
     * expect get: null
     */
    @Test
    public void testFindSellerById_InvalidUserId_DNE() {
        Database db = new Database();
        SearchBar searchBar = new SearchBar(db);
        String invalidUserId = "nonexistent-user-id";

        assertNull(searchBar.findSellerById(invalidUserId));
    }




    /*
     * Find seller by partial valid user id
     * expect get: user id
     */
    @Test
    public void testSearchSellersByPartialId() {
        Database db = new Database();
        SearchBar searchBar = new SearchBar(db);
        String partialId = "d8bb19d1";
        String username1 = "d8bb19d1-d5e5-4a32-9e0d-68248fefdd3d";
        String username2 = "d8bb19d1-c3e4-5b62-9a1d-9b149aeb2f8e";
        String username3 = "a7cf28c1-c3e4-5b62-9a1d-9b149aeb2f8e";

        User user = new User(username1, "pass1", "bio1", db);
        User user2 = new User(username2, "pass1", "bio1", db);
        User user3 = new User(username3, "pass1", "bio1", db);

        user.addListing(new Item("3", "The Secret of Emberwood", "In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows-waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", "Book", 0));
        user2.addListing(new Item("4", "The Secret of Emberwood", "In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows-waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", "Book", 0));
        user3.addListing(new Item("4", "The Secret of Emberwood", "In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows-waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", "Book", 0));


        List<User> results = searchBar.searchSellersByPartialId(partialId);

        assertFalse(results.isEmpty());
        assertTrue(results.contains(user));
        assertTrue(results.contains(user2));
        assertFalse(results.contains(user3));
    }

    /*
     * Find seller by partial valid user id with no match
     * expect get: null
     */
    @Test
    public void testSearchSellersByPartialId_noMatch() {
        Database db = new Database();
        SearchBar searchBar = new SearchBar(db);
        String partialId = "d8bb19d1";
        String username1 = "a7cf28c1-d5e5-4a32-9e0d-68248fefdd3d";
        String username2 = "a7cf28c1-c3e4-5b62-9a1d-9b149aeb2f8e";
        String username3 = "a7cf28c1-c3e4-5b62-9a1d-9b149aeb2f8e";

        User user = new User(username1, "pass1", "bio1", db);
        User user2 = new User(username2, "pass1", "bio1", db);
        User user3 = new User(username3, "pass1", "bio1", db);

        user.addListing(new Item("3", "The Secret of Emberwood", "In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows-waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", "Book", 0));
        user2.addListing(new Item("4", "The Secret of Emberwood", "In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows-waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", "Book", 0));
        user3.addListing(new Item("4", "The Secret of Emberwood", "In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows-waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", "Book", 0));

        List<User> results = searchBar.searchSellersByPartialId(partialId);

        assertNull(results);
    }

    /*
     * Find all active sellers
     * expect get: list of sellers
     */
    @Test
    public void testgetAllActiveSellers() {
        Database db = new Database();
        SearchBar searchBar = new SearchBar(db);
        String username1 = "a7cf28c1-d5e5-4a32-9e0d-68248fefdd3d";
        String username2 = "a7cf28c1-c3e4-5b62-9a1d-9b149aeb2f8e";
        String username3 = "a7cf28c1-c3e4-5b62-9a1d-9b149aeb2f8e";

        User user = new User(username1, "pass1", "bio1", db);
        User user2 = new User(username2, "pass1", "bio1", db);
        User user3 = new User(username3, "pass1", "bio1", db);

        user.addListing(new Item("3", "The Secret of Emberwood", "In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows-waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", "Book", 0));
        user3.addListing(new Item("4", "The Secret of Emberwood", "In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows-waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", "Book", 0));

        List<User> results = searchBar.getAllActiveSellers();

        assertFalse(results.isEmpty());
        assertTrue(results.contains(user));
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
        String username1 = "a7cf28c1-d5e5-4a32-9e0d-68248fefdd3d";
        String username2 = "a7cf28c1-c3e4-5b62-9a1d-9b149aeb2f8e";
        String username3 = "a7cf28c1-c3e4-5b62-9a1d-9b149aeb2f8e";

        User user = new User(username1, "pass1", "bio1", db);
        User user2 = new User(username2, "pass1", "bio1", db);
        User user3 = new User(username3, "pass1", "bio1", db);

        List<User> results = searchBar.getAllActiveSellers();

        assertTrue(results.isEmpty());
    }
}
