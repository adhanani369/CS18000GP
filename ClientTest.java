import static org.junit.Assert.*;
import org.junit.*;
import java.io.*;
import java.net.*;

public class ClientTest {
	private ServerSocket serverSocket;
	private Thread serverThread;
	private Client client;

	@Before
	public void setUp() throws Exception {

		serverSocket = new ServerSocket(1234);
		serverThread = new Thread(() -> {
			try {
				Socket sock = serverSocket.accept();
				BufferedReader in = new BufferedReader(
						new InputStreamReader(sock.getInputStream()));
				BufferedWriter out = new BufferedWriter(
						new OutputStreamWriter(sock.getOutputStream()));
				String line;
				while ((line = in.readLine()) != null) {
					String cmd = line.split(",")[0];
					String response;
					switch (cmd) {
						case "REGISTER":
						case "DELETE_ACCOUNT":
						case "ADD_ITEM":
						case "GET_ITEM":
						case "SEARCH_ITEMS":
						case "GET_USER_LISTINGS":
						case "MARK_SOLD":
						case "REMOVE_ITEM":
						case "SEND_MESSAGE":
						case "GET_MESSAGES":
						case "GET_CONVERSATIONS":
						case "ADD_FUNDS":
						case "WITHDRAW_FUNDS":
						case "PROCESS_PURCHASE":
						case "RATE_SELLER":
						case "GET_RATING":
						case "GET_ALL_USERS":
						case "GET_ACTIVE_SELLERS":
						case "GET_MY_RATING":
							response = cmd + ",SUCCESS,ok";
							break;

						case "LOGIN":
							response = "LOGIN,SUCCESS,user123";
							break;

						case "GET_BALANCE":
							response = "GET_BALANCE,SUCCESS,50.0";
							break;

						default:
							response = "UNKNOWN,FAIL";
					}
					out.write(response);
					out.newLine();
					out.flush();
				}
			} catch (IOException ignored) {
			}
		});
		serverThread.start();

		// connect client
		client = new Client();
		assertTrue(client.connect());
	}

	@After
	public void tearDown() throws Exception {
		client.disconnect();
		serverSocket.close();
		serverThread.interrupt();
	}

	@Test
	public void testRegister() {
		String resp = client.register("user","pass","bio");
		assertEquals("REGISTER,SUCCESS,ok", resp);
	}

	@Test
	public void testLogin() {
		String resp = client.login("user","pass");
		assertEquals("LOGIN,SUCCESS,user123", resp);
		assertEquals("user123", client.getCurrentUserId());
	}

	@Test
	public void testDeleteAccount() {
		String resp = client.deleteAccount("userId");
		assertEquals("DELETE_ACCOUNT,SUCCESS,ok", resp);
	}

	@Test
	public void testAddItem() {
		String resp = client.addItem("seller","title","desc","cat",9.99);
		assertEquals("ADD_ITEM,SUCCESS,ok", resp);
	}

	@Test
	public void testGetItem() {
		String resp = client.getItem("itemId");
		assertEquals("GET_ITEM,SUCCESS,ok", resp);
	}

	@Test
	public void testSearchItems() {
		String resp = client.searchItems("q","cat",5);
		assertEquals("SEARCH_ITEMS,SUCCESS,ok", resp);
	}

	@Test
	public void testGetUserListings() {
		String resp = client.getUserListings("userId", true);
		assertEquals("GET_USER_LISTINGS,SUCCESS,ok", resp);
	}

	@Test
	public void testMarkSold() {
		String resp = client.markSold("itemId","buyerId");
		assertEquals("MARK_SOLD,SUCCESS,ok", resp);
	}

	@Test
	public void testRemoveItem() {
		String resp = client.removeItem("itemId","sellerId");
		assertEquals("REMOVE_ITEM,SUCCESS,ok", resp);
	}

	@Test
	public void testSendMessageToUser() {
		String resp = client.sendMessageToUser("sender","receiver","hi","none");
		assertEquals("SEND_MESSAGE,SUCCESS,ok", resp);
	}

	@Test
	public void testGetMessages() {
		String resp = client.getMessages("buyer","seller");
		assertEquals("GET_MESSAGES,SUCCESS,ok", resp);
	}

	@Test
	public void testGetConversations() {
		String resp = client.getConversations("userId");
		assertEquals("GET_CONVERSATIONS,SUCCESS,ok", resp);
	}

	@Test
	public void testAddFunds() {
		String resp = client.addFunds("userId", 20.0);
		assertEquals("ADD_FUNDS,SUCCESS,ok", resp);
	}

	@Test
	public void testWithdrawFunds() {
		String resp = client.withdrawFunds("userId", 10.0);
		assertEquals("WITHDRAW_FUNDS,SUCCESS,ok", resp);
	}

	@Test
	public void testProcessPurchase() {
		String resp = client.processPurchase("buyer","itemId");
		assertEquals("PROCESS_PURCHASE,SUCCESS,ok", resp);
	}

	@Test
	public void testRateSeller() {
		String resp = client.rateSeller("seller",4.5);
		assertEquals("RATE_SELLER,SUCCESS,ok", resp);
	}

	@Test
	public void testGetRating() {
		String resp = client.getRating("seller");
		assertEquals("GET_RATING,SUCCESS,ok", resp);
	}

	@Test
	public void testGetAllUsers() {
		String resp = client.getAllUsers();
		assertEquals("GET_ALL_USERS,SUCCESS,ok", resp);
	}

	@Test
	public void testGetActiveSellers() {
		String resp = client.getActiveSellers();
		assertEquals("GET_ACTIVE_SELLERS,SUCCESS,ok", resp);
	}

	@Test
	public void testGetBalance() {
		String resp = client.sendMessage("GET_BALANCE,uid");
		assertEquals("GET_BALANCE,SUCCESS,50.0", resp);
	}

	@Test
	public void testGetMyRating() {
		String resp = client.getMyRating("userId");
		assertEquals("GET_MY_RATING,SUCCESS,ok", resp);
	}

	@Test
	public void testSetAndGetCurrentUserId() {
		client.setCurrentUserId("abc");
		assertEquals("abc", client.getCurrentUserId());
	}

	@Test
	public void testDisconnectAndSendMessageError() {
		client.disconnect();
		String resp = client.sendMessage("PING");
		assertTrue(resp.startsWith("ERROR"));
	}
}
