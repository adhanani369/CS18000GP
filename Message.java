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
        // Return messageId
    }
    
    /**
     * Gets the ID of the message sender.
     */
    public String getSenderId() {
        // Return senderId
    }
    
    /**
     * Gets the ID of the message receiver.
     */
    public String getReceiverId() {
        // Return receiverId
    }
    
    /**
     * Gets the message content.
     */
    public String getContent() {
        // Return content
    }
    
    /**
     * Gets the message timestamp.
     */
    public long getTimestamp() {
        // Return timestamp
    }
}
