import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration tests that exercise the {@link Server} socket protocol end‑to‑end.
 * @version April, 2025
 * @author Rayaan , Fayiz, Sena, Ayush
 */
public class ServerTest {

    private static Thread serverThread;
    private static Server  server;

    @BeforeClass
    public static void startServer() throws Exception {
        server = new Server();
        serverThread = new Thread(server::startServer);
        serverThread.start();
        
        Thread.sleep(300);
    }

    @AfterClass
    public static void stopServer() throws Exception {
        server.closeServer();
        serverThread.join();
    }


    private String tx(final String line) throws Exception {
        try (Socket socket = new Socket("localhost", 1234);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            writer.write(line);
            writer.newLine();
            writer.flush();
            return reader.readLine();
        }
    }


    private String makeUser(final String prefix) throws Exception {
        String uname = prefix + UUID.randomUUID().toString().substring(0, 6);
        assertEquals("REGISTER,SUCCESS", tx("REGISTER," + uname + ",p,bio"));
        String login = tx("LOGIN," + uname + ",p");
        assertTrue(login.startsWith("LOGIN,SUCCESS,"));
        return login.split(",")[2];
    }

    @Test
    public void registerAndLogin() throws Exception {
        assertNotNull(makeUser("alice"));
    }

    @Test
    public void addItemAndFetchIt() throws Exception {
        String sellerId = makeUser("seller");
        String add = tx("ADD_ITEM," + sellerId + ",Lamp,Nice desk lamp,Home,15.0");
        assertTrue(add.startsWith("ADD_ITEM,SUCCESS,"));
        String itemId = add.split(",")[2];
        String get = tx("GET_ITEM," + itemId);
        assertTrue(get.startsWith("GET_ITEM,SUCCESS," + itemId + "," + sellerId + ",Lamp"));
    }

    @Test
    public void addAndWithdrawFundsHappyPath() throws Exception {
        String uid = makeUser("cash");
        assertEquals("GET_BALANCE,SUCCESS,0.0", tx("GET_BALANCE," + uid));
        assertEquals("ADD_FUNDS,SUCCESS", tx("ADD_FUNDS," + uid + ",40"));
        assertEquals("GET_BALANCE,SUCCESS,40.0", tx("GET_BALANCE," + uid));
        assertEquals("WITHDRAW_FUNDS,SUCCESS", tx("WITHDRAW_FUNDS," + uid + ",15"));
        assertEquals("GET_BALANCE,SUCCESS,25.0", tx("GET_BALANCE," + uid));
    }

    @Test
    public void withdrawFailsOnInsufficientBalance() throws Exception {
        String uid = makeUser("poor");
        tx("ADD_FUNDS," + uid + ",5");
        assertEquals("WITHDRAW_FUNDS,FAILURE", tx("WITHDRAW_FUNDS," + uid + ",10"));
        assertEquals("GET_BALANCE,SUCCESS,5.0", tx("GET_BALANCE," + uid));
    }

    @Test
    public void multipleDepositsAccumulate() throws Exception {
        String uid = makeUser("stack");
        tx("ADD_FUNDS," + uid + ",10");
        tx("ADD_FUNDS," + uid + ",15");
        assertEquals("GET_BALANCE,SUCCESS,25.0", tx("GET_BALANCE," + uid));
    }

    @Test
    public void getActiveSellersAfterSale() throws Exception {
        String sellerId = makeUser("seller");
        String buyerId = makeUser("buyer");
        tx("ADD_FUNDS," + buyerId + ",50");
        String add = tx("ADD_ITEM," + sellerId + ",Pen,Fountain pen,Stationery,20");
        String itemId = add.split(",")[2];
        tx("PROCESS_PURCHASE," + buyerId + "," + itemId);
        String resp = tx("GET_ACTIVE_SELLERS");
        assertTrue(resp.startsWith("GET_ACTIVE_SELLERS,SUCCESS,"));
        assertTrue(resp.contains(sellerId));
    }

    @Test
    public void searchItemsByQueryAndCategory() throws Exception {
        String sellerId = makeUser("search");
        String add = tx("ADD_ITEM," + sellerId + ",Guitar,Acoustic guitar,Music,120.0");
        String guitarId = add.split(",")[2];
        tx("ADD_ITEM," + sellerId + ",Lamp,Bedside lamp,Home,25.0");
        String resp = tx("SEARCH_ITEMS,guitar,Music,10");
        assertTrue(resp.startsWith("SEARCH_ITEMS,SUCCESS,"));
        assertTrue(resp.contains(guitarId));
    }

    @Test
    public void sellerCanRemoveOwnItem() throws Exception {
        String sellerId = makeUser("rem");
        String add = tx("ADD_ITEM," + sellerId + ",Mug,Coffee mug,Home,8.0");
        String itemId = add.split(",")[2];
        assertEquals("REMOVE_ITEM,SUCCESS", tx("REMOVE_ITEM," + itemId + "," + sellerId));
        assertTrue(tx("GET_ITEM," + itemId).startsWith("GET_ITEM,FAILURE"));
    }

    @Test
    public void markSoldUpdatesItemStatus() throws Exception {
        String sellerId = makeUser("msell");
        String buyerId = makeUser("mbuy");
        String add = tx("ADD_ITEM," + sellerId + ",Phone,Smart phone,Electronics,200.0");
        String itemId = add.split(",")[2];
        assertEquals("MARK_SOLD,SUCCESS", tx("MARK_SOLD," + itemId + "," + buyerId));
        String[] fields = tx("GET_ITEM," + itemId).split(",");
        assertTrue(Boolean.parseBoolean(fields[8]));
    }

    @Test
    public void getRatingBeforeAnySalesIsZero() throws Exception {
        String sellerId = makeUser("nobiz");
        assertEquals("GET_RATING,SUCCESS,0.0", tx("GET_RATING," + sellerId));
    }

    @Test
    public void fullPurchaseFlowProducesRating() throws Exception {
        String sellerId = makeUser("seller");
        String buyerId = makeUser("buyer");
        tx("ADD_FUNDS," + buyerId + ",100");
        String add = tx("ADD_ITEM," + sellerId + ",Book,Cool read,Books,30");
        String itemId = add.split(",")[2];
        tx("PROCESS_PURCHASE," + buyerId + "," + itemId);
        assertEquals("RATE_SELLER,SUCCESS", tx("RATE_SELLER," + sellerId + ",4.5"));
        assertTrue(tx("GET_RATING," + sellerId).startsWith("GET_RATING,SUCCESS,4.5"));
    }

    @Test
    public void concurrentClientsStress() throws Exception {
        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    String id = makeUser("user");
                    tx("ADD_FUNDS," + id + ",10");
                } catch (Exception ignored) {
                    // deliberately ignored for stress test
                }
                latch.countDown();
            }).start();
        }
        latch.await();
        String list = tx("GET_ALL_USERS");
        int total = Integer.parseInt(list.split(",")[2]);
        assertTrue(total >= threadCount);
    }

    @Test
    public void deleteAccountRemovesListings() throws Exception {
        String uid = makeUser("temp");
        String add = tx("ADD_ITEM," + uid + ",Chair,Wooden chair,Furniture,20");
        String item = add.split(",")[2];
        tx("DELETE_ACCOUNT," + uid);
        assertTrue(tx("GET_ITEM," + item).startsWith("GET_ITEM,FAILURE"));
    }
}
