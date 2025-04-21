import java.io.IOException;
/**
 * Interface for Server class
 * @author Rayaan Grewal
 * @version April 6, 2024
 */
public interface ServerInterface {
    void start(int port) throws IOException;
    void stop() throws IOException;
    String handleRequest(String request);
}
