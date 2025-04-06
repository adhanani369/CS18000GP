import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
public class ItemTest {
    
    // @Test
    // public void getItemIdTest() {
    //     Item item = new Item("3", "The Secret of Emberwood", "In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows-waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", "Book", 0);
    //     Item item2 = new Item(null, null, null, null, 0);
    //     Item item3 = new Item(null, null, null, null, 0);
    //     Item item4 = new Item(null, null, null, null, 0);
    //     assertEquals("1", item.getItemId());
    //     assertEquals("3", item3.getItemId());
    //     assertEquals("4", item4.getItemId());
    // }

    /*
     * Checks if it returns the right sellerId
     */
    @Test
    public void getSellerId() {
        Item item = new Item("3", "The Secret of Emberwood", "In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows-waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", "Book", 0);
        assertEquals("3", item.getSellerId());
    }

    /*
     * Checks if it returns the right title
     */
    @Test
    public void getTitle() {
        Item item = new Item("3", "The Secret of Emberwood", "In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows-waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", "Book", 0);
        assertEquals("The Secret of Emberwood", item.getTitle());
    }

    /*
     * Checks if it returns the right description
     */
    @Test
    public void getDescription() {
        Item item = new Item("3", "The Secret of Emberwood", "In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows-waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", "Book", 0);
        assertEquals("In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows-waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", item.getDescription());
    }

    /*
     * Checks if it returns the right price
     */
    @Test
    public void getPrice() {
        Item item = new Item("3", "The Secret of Emberwood", "In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows-waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", "Book", 0);
        assertEquals(0.0, item.getPrice(), 0.0001f);
    }

    /*
     * Checks if the updateRating and getRating methods works correctly
     */
    @Test
    public void ratingTest() {
        Item item = new Item("3", "The Secret of Emberwood", "In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows-waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", "Book", 0);
        for (int i = 1; i <= 7; i++) {
            item.updateRating(i);
        }
        assertEquals(3.0, item.getRating(), 0.0001f);
    } 

    /*
     * Checks the getBuyerId, isSold, and markAsSold methods
     */
    @Test
    public void soldTest() {
        Item item = new Item("3", "The Secret of Emberwood", "In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows-waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", "Book", 0);
        assertEquals(null, item.getBuyerId());
        assertEquals(false, item.isSold());
        assertEquals(true, item.markAsSold("654"));
        assertEquals(false, item.markAsSold("654"));
        assertEquals(true, item.isSold());
        assertEquals("654", item.getBuyerId());
    }

    /*
     * Checks if the getStopwords reads the files correctly and returns the right values
     */
    @Test
    public void getStopwords() {
        Item item = new Item("3", "The Secret of Emberwood", "In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows-waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", "Book", 0);
        List<String> stopWords = Arrays.asList("a", "an", "and", "are", "as", "at", 
        "be", "but", "by", "for", "if", "in", 
        "into", "is", "it", "no", "not", "of", 
        "on", "or", "such", "that", "the", 
        "their", "then", "there", "these", 
        "they", "this", "to", "was", "will", 
        "with");
        assertEquals(stopWords, item.getStopwords());
           
    }

    /*
     * Checks if the extractTags method converts the description into the correct tags and returns them
     */
    @Test
    public void extractTags() {
        Item item = new Item("3", "The Secret of Emberwood", "In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows-waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", "Book", 0);
        assertEquals(Arrays.asList(
            "heart", "Emberwood", "young", "Elara", "stumbles", "upon", "ancient",
            "key", "buried", "beneath", "roots", "enchanted", "tree", "When", "she",
            "unlocks", "hidden", "doorway", "she", "discovers", "forgotten", "world",
            "brimming", "talking", "animals", "lost", "spells", "secret", "could",
            "change", "everything", "deeper", "she", "explores", "more", "she",
            "realizes", "something", "dark", "lurking", "shadows", "waiting", "help",
            "her", "mischievous", "fox", "companion", "Finn", "Elara", "must", "race",
            "against", "time", "unravel", "mystery", "before", "Emberwood", "falls",
            "darkness", "forever"
        ) , item.getTags());
    }

    /*
     * Checks if the cleanWord method correctly cleans the word from special characters and returns them
     */
    @Test
    public void cleanWord() {
        String[] wordsWithoutSpecialCharacters = {
            "spark", "ember", "mystic", "twilight", "star", "shadow",
            "whisper", "dusk", "echo", "raven", "storm", "flame",
            "frost", "glimmer", "nightfall", "dawn", "phantom",
            "solace", "nova", "serene"
        };

        String[] randomWords = {
            "@spark", "ember$", "#mystic", "twilight!", "star*", "&shadow",
            "whisper@", "%dusk", "echo#", "raven!", "$storm", "!flame", 
            "frost&", "glimmer%", "#nightfall", "dawn*", "phantom#", 
            "&solace", "nova!", "!serene"
        };
        
        Item item = new Item("3", "The Secret of Emberwood", "In the heart of Emberwood, young Elara stumbles upon an ancient key buried beneath the roots of an enchanted tree. When she unlocks a hidden doorway, she discovers a forgotten world brimming with talking animals, lost spells, and a secret that could change everything. But the deeper she explores, the more she realizes that something dark is lurking in the shadows—waiting. With the help of her mischievous fox companion, Finn, Elara must race against time to unravel the mystery before Emberwood falls into darkness forever.", "Book", 0);
   
        for (int i = 0; i < randomWords.length; i++) {
            System.out.println(item.cleanWord(randomWords[i]));
            assertEquals(wordsWithoutSpecialCharacters[i], item.cleanWord(randomWords[i]));
        }

    }

}
