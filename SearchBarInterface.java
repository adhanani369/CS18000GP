import java.util.List;

public interface SearchBarInterface {
    User findSellerById(String userId);

    List<User> searchSellersByPartialId(String searchTerm);

    List<User> getAllActiveSellers();
}
