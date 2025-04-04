public class Message {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String content;
    private long timestamp;
    
    /**
     * Creates a new message.
     */
    public Message(String senderId, String receiverId, String content) {
        // Initialize message with sender, receiver, content
        // Generate a simple ID
        // Set timestamp to current time
    }
    
    /**
     * Gets the message's unique identifier.
     */
    public String getMessageId() {
        // TODO Return messageId
        return null;
    }
    
    /**
     * Gets the ID of the message sender.
     */
    public String getSenderId() {
        // TODO Return senderId
        return null;
    }
    
    /**
     * Gets the ID of the message receiver.
     */
    public String getReceiverId() {
        // TODO: Return receiverId
        return null;
    }
    
    /**
     * Gets the message content.
     */
    public String getContent() {
        // TODO: Return content
        return null;
    }
    
    /**
     * Gets the message timestamp.
     */
    public long getTimestamp() {
        // TODO Return timestamp
        return 0;
    }
}
