import java.time.Instant;
import java.util.UUID;

public class Message implements MessageInterface {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String content;
    private long timestamp;
    private boolean read;
    /**
     * Creates a new message with time stamps.
     */
    public Message(String senderId, String receiverId, String content) {
        this.messageId = UUID.randomUUID().toString();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
        this.read = false;
    }

    /**
     * Creates a message with specified timestamp and read status.
     */
    public Message(String senderId, String receiverId, String content,
                   long timestamp, boolean read) {
        this.messageId = UUID.randomUUID().toString();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
        this.read = read;
    }
    
    /**
     * Gets the message's unique identifier.
     */
    @Override
    public String getMessageId() {
        // TODO Return messageId
        return messageId;
    }
    
    /**
     * Gets the ID of the message sender.
     */
    @Override
    public String getSenderId() {
        // TODO Return senderId
        return senderId;
    }
    
    /**
     * Gets the ID of the message receiver.
     */
    @Override
    public String getReceiverId() {
        // TODO: Return receiverId
        return receiverId;
    }
    
    /**
     * Gets the message content.
     */
    @Override
    public String getContent() {
        // TODO: Return content
        return content;
    }
    
    /**
     * Gets the message timestamp.
     */
    @Override
    public long getTimestamp() {
        // TODO Return timestamp
        return timestamp;
    }

    /**
     * Checks if the message has been read.
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Marks the message as read.
     */
    public void markAsRead() {
        this.read = true;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + senderId +
                ", to=" + receiverId +
                ", content='" + content + '\'' +
                ", sent=" + Instant.ofEpochMilli(timestamp) +
                ", read=" + read +
                '}';
    }
}
