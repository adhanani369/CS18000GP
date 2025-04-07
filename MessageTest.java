import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
public class MessageTest {
    /*
     * get message id
     * expect return: message id
     */
    @Test
    public void testGetMessageId() {
        String senderId = "user1";
        String receiverId = "user2";
        String content = "Test message content";

        Message message = new Message(senderId, receiverId, content);
        String messageId = message.getMessageId();

        assertNotNull(messageId);
        assertFalse(messageId.isEmpty());
    }

    /*
     * get sender id
     * expect return: sender id
     */
    @Test
    public void testGetSenderId() {
        String senderId = "user1";
        String receiverId = "user2";
        String content = "Test message content";

        Message message = new Message(senderId, receiverId, content);
        String retrievedSenderId = message.getSenderId();

        assertEquals(senderId, retrievedSenderId);
    }

    /*
     * get receiver id
     * expect return: receiver id
     */
    @Test
    public void testGetReceiverId() {
        String senderId = "user1";
        String receiverId = "user2";
        String content = "Test message content";

        Message message = new Message(senderId, receiverId, content);
        String retrievedReceiverId = message.getReceiverId();

        assertEquals(receiverId, retrievedReceiverId);
    }

    /*
     * get content id
     * expect return: content id
     */
    @Test
    public void testGetContent() {
        String senderId = "user1";
        String receiverId = "user2";
        String content = "Test message content";

        Message message = new Message(senderId, receiverId, content);
        String retrievedContent = message.getContent();

        assertEquals(content, retrievedContent);
    }

    /*
     * get timestamp
     * expect return: timestamp
     */
    @Test
    public void testGetTimestamp() {
        String senderId = "user1";
        String receiverId = "user2";
        String content = "Test message content";

        long beforeCreationTime = System.currentTimeMillis();
        Message message = new Message(senderId, receiverId, content);
        long messageTimestamp = message.getTimestamp();

        assertTrue(messageTimestamp >= beforeCreationTime);
        assertTrue(messageTimestamp <= System.currentTimeMillis());
    }

    /*
     * Checks status of a message before and after read.
     *
     */
    @Test
    public void testIsRead() {
        String senderId = "user1";
        String receiverId = "user2";
        String content = "Test message content";

        Message message = new Message(senderId, receiverId, content);

        assertFalse(message.isRead());
        message.markAsRead();
        assertTrue(message.isRead());
    }

    /*
     * Change status of a message after read.
     *
     */
    @Test
    public void testMarkAsRead() {
        String senderId = "user1";
        String receiverId = "user2";
        String content = "Test message content";

        Message message = new Message(senderId, receiverId, content);

        assertFalse(message.isRead());
        message.markAsRead();
        assertTrue(message.isRead());
    }

    /*
     * test to string
     *
     */
    @Test
    public void testToString() {
        String senderId = "user1";
        String receiverId = "user2";
        String content = "Test message content";

        Message message = new Message(senderId, receiverId, content);
        String messageString = message.toString();

        assertTrue(messageString.contains(senderId));
        assertTrue(messageString.contains(receiverId));
        assertTrue(messageString.contains(content));
    }
}
