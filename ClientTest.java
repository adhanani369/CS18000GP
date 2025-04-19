import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.io.*;
import java.net.*;

/**
 * Tests for Client class
 * Covers all command methods via dummy server on port 1234
 * @author Rayaan Grewal
 * @version April 18th, 2025
 */
public class ClientTest {
	private Client client;
	private ServerSocket serverSocket;
	private Thread serverThread;

	@Before
	public void setUp() throws Exception {
		serverSocket = new ServerSocket(1234);
		serverThread = new Thread(() -> {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					Socket sock = serverSocket.accept();
					new Thread(() -> {
						try (
								BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
								BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()))
						) {
							String line;
							while ((line = in.readLine()) != null) {
								String response;
								if (line.startsWith("PING")) {
									response = "PONG";
								} else if (line.startsWith("REGISTER,")) {
									response = "REGISTER,SUCCESS,FAKE_ID";
								} else if (line.startsWith("LOGIN,")) {
									response = "LOGIN,SUCCESS,FAKE_ID";
								} else if (line.startsWith("DELETE_ACCOUNT,")) {
									response = "DELETE_ACCOUNT,SUCCESS";
								} else if (line.startsWith("ADD_ITEM,")) {
									response = "ADD_ITEM,SUCCESS,FAKE_ITEM";
								} else if (line.startsWith("GET_ITEM,")) {
									response = "GET_ITEM,ITEM_DATA";
								} else if (line.startsWith("SEARCH_ITEMS,")) {
									response = "SEARCH_ITEMS,RESULT1;RESULT2";
								} else if (line.startsWith("MARK_SOLD,")) {
									response = "MARK_SOLD,SUCCESS";
								} else if (line.startsWith("REMOVE_ITEM,")) {
									response = "REMOVE_ITEM,SUCCESS";
								} else if (line.startsWith("SEND_MESSAGE,")) {
									response = "SEND_MESSAGE,SUCCESS";
								} else if (line.startsWith("GET_MESSAGES,")) {
									response = "GET_MESSAGES,msg1|msg2";
								} else if (line.startsWith("GET_CONVERSATIONS,")) {
									response = "GET_CONVERSATIONS,userA;userB";
								} else if (line.startsWith("ADD_FUNDS,")) {
									response = "ADD_FUNDS,SUCCESS";
								} else if (line.startsWith("WITHDRAW_FUNDS,")) {
									response = "WITHDRAW_FUNDS,SUCCESS";
								} else if (line.startsWith("PROCESS_PURCHASE,")) {
									response = "PROCESS_PURCHASE,SUCCESS";
								} else if (line.startsWith("RATE_SELLER,")) {
									response = "RATE_SELLER,SUCCESS";
								} else if (line.startsWith("GET_RATING,")) {
									response = "GET_RATING,4.5";
								} else {
									response = "UNKNOWN";
								}
								out.write(response);
								out.newLine();
								out.flush();
							}
						} catch (IOException e) {
							//pollution
						}
					}).start();
				}
			} catch (IOException e) {
				//pollution
			}
		});
		serverThread.start();

		client = new Client();
		Thread.sleep(50);
	}

	@After
	public void tearDown() throws Exception {
		if (client != null) client.disconnect();
		if (serverSocket != null && !serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} catch (IOException e) {

			}
		}
		if (serverThread != null && serverThread.isAlive()) {
			serverThread.interrupt();
		}
	}

	@Test public void testConnect() { assertTrue(client.connect()); }
	@Test public void testPing() { client.connect(); assertEquals("PONG", client.sendMessage("PING")); }
	@Test public void testRegister() { client.connect(); assertEquals("REGISTER,SUCCESS,FAKE_ID", client.register("u","p","b")); }
	@Test public void testLogin() { client.connect(); assertEquals("LOGIN,SUCCESS,FAKE_ID", client.login("u","p")); assertEquals("FAKE_ID", client.getCurrentUserId()); }
	@Test public void testDeleteAccount() { client.connect(); assertEquals("DELETE_ACCOUNT,SUCCESS", client.deleteAccount("u")); }
	@Test public void testAddItem() { client.connect(); assertEquals("ADD_ITEM,SUCCESS,FAKE_ITEM", client.addItem("s","t","d","c",1.23)); }
	@Test public void testGetItem() { client.connect(); assertEquals("GET_ITEM,ITEM_DATA", client.getItem("item1")); }
	@Test public void testSearchItems() { client.connect(); assertEquals("SEARCH_ITEMS,RESULT1;RESULT2", client.searchItems("q","c",2)); }
	@Test public void testMarkSold() { client.connect(); assertEquals("MARK_SOLD,SUCCESS", client.markSold("item","buyer")); }
	@Test public void testRemoveItem() { client.connect(); assertEquals("REMOVE_ITEM,SUCCESS", client.removeItem("item","seller")); }
	@Test public void testSendMessageToUser() { client.connect(); assertEquals("SEND_MESSAGE,SUCCESS", client.sendMessageToUser("s","r","c","i")); }
	@Test public void testGetMessages() { client.connect(); assertEquals("GET_MESSAGES,msg1|msg2", client.getMessages("b","s")); }
	@Test public void testGetConversations() { client.connect(); assertEquals("GET_CONVERSATIONS,userA;userB", client.getConversations("u")); }
	@Test public void testAddFunds() { client.connect(); assertEquals("ADD_FUNDS,SUCCESS", client.addFunds("u",5.0)); }
	@Test public void testWithdrawFunds() { client.connect(); assertEquals("WITHDRAW_FUNDS,SUCCESS", client.withdrawFunds("u",2.0)); }
	@Test public void testProcessPurchase() { client.connect(); assertEquals("PROCESS_PURCHASE,SUCCESS", client.processPurchase("b","i")); }
	@Test public void testRateSeller() { client.connect(); assertEquals("RATE_SELLER,SUCCESS", client.rateSeller("s",4.0)); }
	@Test public void testGetRating() { client.connect(); assertEquals("GET_RATING,4.5", client.getRating("s")); }
	@Test public void testCurrentUserId() { client.setCurrentUserId("XYZ"); assertEquals("XYZ", client.getCurrentUserId()); }
}
