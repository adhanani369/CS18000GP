public interface PaymentProcessingInterface {
    boolean addFunds(String userId, double amount);

    boolean withdrawFunds(String userId, double amount);

    boolean processPurchase(String buyerId, String itemId);
}
