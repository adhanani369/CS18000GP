import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;

/**
 * Dialog for displaying detailed information about an item.
 * @author Ayush Dhanani
 * @version
 */
public class ItemDetailDialog extends JDialog {
    private Client client;
    private String itemId;
    private String sellerId;
    private String title;
    private String description;
    private String category;
    private double price;
    private boolean sold;
    private String buyerId;

    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();


    public ItemDetailDialog(Frame parent, Client client, String itemId) {
        super(parent, "Item Details", true);
        this.client = client;
        this.itemId = itemId;

        // Load item details
        if (!loadItemDetails()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load item details",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            dispose();
            return;
        }

        initializeUI();

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    /**
     * Loads the item details from the server.
     *
     * @return true if successful, false otherwise
     */
    private boolean loadItemDetails() {
        String response = client.getItem(itemId);
        String[] parts = response.split(",");

        if (parts.length >= 7 && parts[1].equals("SUCCESS")) {
            itemId = parts[2];
            sellerId = parts[3];
            title = parts[4];
            description = parts[5];
            category = parts[6];
            price = Double.parseDouble(parts[7]);
            sold = Boolean.parseBoolean(parts[8]);
            buyerId = (parts.length > 9) ? parts[9] : "";
            return true;
        }

        return false;
    }

    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());

        // Title panel at top
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel);

        // Content panel in center
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Category
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(new JLabel(category), gbc);

        // Price
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        contentPanel.add(new JLabel("Price:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        contentPanel.add(new JLabel(currencyFormat.format(price)), gbc);

        // Seller
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        contentPanel.add(new JLabel("Seller:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;

        // Get seller username if possible
        String sellerName = sellerId;
        String usersResponse = client.getAllUsers();
        String[] userParts = usersResponse.split(",");

        if (userParts.length >= 3 && userParts[1].equals("SUCCESS")) {
            int userCount = Integer.parseInt(userParts[2]);

            for (int i = 0; i < userCount; i++) {
                String userId = userParts[3 + 2*i];
                String username = userParts[4 + 2*i];

                if (userId.equals(sellerId)) {
                    sellerName = username + " (" + sellerId + ")";
                    break;
                }
            }
        }

        contentPanel.add(new JLabel(sellerName), gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        contentPanel.add(new JLabel("Status:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel statusLabel = new JLabel(sold ? "Sold" : "Available");
        statusLabel.setForeground(sold ? Color.RED : Color.GREEN.darker());
        contentPanel.add(statusLabel, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        contentPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        JTextArea descArea = new JTextArea(description);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBackground(new Color(240, 240, 240));
        descArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane descScrollPane = new JScrollPane(descArea);
        descScrollPane.setPreferredSize(new Dimension(400, 150));
        contentPanel.add(descScrollPane, gbc);

        // Button panel at bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Add action buttons based on item status and user role
        if (!sold && client.getCurrentUserId() != null) {
            // Buyer can purchase
            if (!client.getCurrentUserId().equals(sellerId)) {
                JButton buyButton = new JButton("Buy Now");
                buyButton.addActionListener(e -> {
                    handleBuyItem();
                });
                buttonPanel.add(buyButton);

                JButton messageButton = new JButton("Message Seller");
                messageButton.addActionListener(e -> {
                    handleMessageSeller();
                });
                buttonPanel.add(messageButton);
            }
            // Seller can remove
            else {
                JButton removeButton = new JButton("Remove Listing");
                removeButton.addActionListener(e -> {
                    handleRemoveItem();
                });
                buttonPanel.add(removeButton);
            }
        }

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        // Add panels to dialog
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Handles buying the item.
     */
    private void handleBuyItem() {
        // Check if the user is trying to buy their own item
        if (client.getCurrentUserId().equals(sellerId)) {
            JOptionPane.showMessageDialog(
                    this,
                    "You cannot buy your own item.",
                    "Purchase Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "Do you want to buy " + title + " for " + currencyFormat.format(price) + "?",
                "Confirm Purchase",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            String response = client.processPurchase(client.getCurrentUserId(), itemId);
            String[] parts = response.split(",");

            if (parts.length >= 2 && parts[1].equals("SUCCESS")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Purchase successful!",
                        "Purchase Complete",
                        JOptionPane.INFORMATION_MESSAGE
                );
                dispose();
            } else {
                // Parse the error message if available
                String errorMsg = "Purchase failed. You may not have enough funds.";
                if (parts.length > 2 && parts[2].equals("Cannot buy your own item")) {
                    errorMsg = "You cannot buy your own item.";
                }

                JOptionPane.showMessageDialog(
                        this,
                        errorMsg,
                        "Purchase Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    /**
     * Handles sending a message to the seller.
     */
    private void handleMessageSeller() {
        JDialog messageDialog = new JDialog(this, "Message Seller", true);
        messageDialog.setSize(400, 300);
        messageDialog.setLocationRelativeTo(this);
        messageDialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel instructionLabel = new JLabel("Enter your message to the seller:");

        JTextArea messageArea = new JTextArea();
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);

        formPanel.add(instructionLabel, BorderLayout.NORTH);
        formPanel.add(messageScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton sendButton = new JButton("Send");

        buttonPanel.add(cancelButton);
        buttonPanel.add(sendButton);

        messageDialog.add(formPanel, BorderLayout.CENTER);
        messageDialog.add(buttonPanel, BorderLayout.SOUTH);

        cancelButton.addActionListener(e -> messageDialog.dispose());

        sendButton.addActionListener(e -> {
            String message = messageArea.getText().trim();

            if (!message.isEmpty()) {
                String response = client.sendMessageToUser(
                        client.getCurrentUserId(),
                        sellerId,
                        message,
                        itemId
                );

                String[] parts = response.split(",");

                if (parts.length >= 2 && parts[1].equals("SUCCESS")) {
                    JOptionPane.showMessageDialog(
                            messageDialog,
                            "Message sent successfully!",
                            "Message Sent",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    messageDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(
                            messageDialog,
                            "Failed to send message",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                JOptionPane.showMessageDialog(
                        messageDialog,
                        "Message cannot be empty",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        messageDialog.setVisible(true);
    }

    /**
     * Handles removing the item.
     */
    private void handleRemoveItem() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove " + title + "?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            String response = client.removeItem(itemId, client.getCurrentUserId());
            String[] parts = response.split(",");

            if (parts.length >= 2 && parts[1].equals("SUCCESS")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Item removed successfully!",
                        "Item Removed",
                        JOptionPane.INFORMATION_MESSAGE
                );
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to remove item",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
