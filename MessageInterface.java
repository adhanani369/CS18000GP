public interface MessageInterface {
    String getMessageId();

    String getSenderId();

    String getReceiverId();

    String getContent();

    long getTimestamp();
}
