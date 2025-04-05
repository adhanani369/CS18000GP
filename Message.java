public class Message implements MessageInterface {
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
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        
        this.messageId = java.util.UUID.randomUUID().toString();
        this.timestamp = System.currentTimeMillis();
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
}
