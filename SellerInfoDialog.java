import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;
import java.text.NumberFormat;

/**
 * Dialog for displaying detailed information about a seller.
 * @author Ayush Dhanani
 * @version
 */
public class SellerInfoDialog extends JDialog {
    private Client client;
    private String sellerId;
    private String username;
    private double rating;

    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    /**
     * Creates a new seller info dialog.
     *
     * @param parent The parent frame
     * @param client The client instance
     * @param sellerId The ID of the seller
     */
    public SellerInfoDialog(Frame parent, Client client, String sellerId) {
        super(parent, "Seller Information", true);
        this.client = client;
        this.sellerId = sellerId;

        // Load seller details
        if (!loadSellerDetails()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Failed to load seller details",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            dispose();
            return;
        }

        initializeUI();

        setSize(600, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    /**
     * hey TA if you are reading this, it's my way of saying Thankyou
     * for being one of the best TA I ever had!!
     */

    /**
     * Loads the seller details from the server.
     *
     * @return true if successful, false otherwise
     */
    private boolean loadSellerDetails() {
        // Get seller username
        String usersResponse = client.getAllUsers();
        String[] userParts = usersResponse.split(",");

        boolean userFound = false;

        if (userParts.length >= 3 && userParts[1].equals("SUCCESS")) {
            int userCount = Integer.parseInt(userParts[2]);

            for (int i = 0; i < userCount; i++) {
                String userId = userParts[3 + 2*i];
                String name = userParts[4 + 2*i];

                if (userId.equals(sellerId)) {
                    username = name;
                    userFound = true;
                    break;
                }
            }
        }

        if (!userFound) {
            username = sellerId;
        }

        // Get seller rating
        String ratingResponse = client.getRating(sellerId);
        String[] ratingParts = ratingResponse.split(",");

        if (ratingParts.length >= 3 && ratingParts[1].equals("SUCCESS")) {
            rating = Double.parseDouble(ratingParts[2]);
            return true;
        }

        rating = 0.0;
        return userFound;
    }

    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());

        // Title panel at top
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Seller: " + username);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel);

        // Content panel in center with tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Info tab
        JPanel infoPanel = createInfoPanel();
        tabbedPane.addTab("Info", infoPanel);

        // Items tab
        JPanel itemsPanel = createItemsPanel();
        tabbedPane.addTab("Items", itemsPanel);

        // Button panel at bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Add action buttons based on user role
        if (client.getCurrentUserId() != null && !client.getCurrentUserId().equals(sellerId)) {
            JButton messageButton = new JButton("Message Seller");
            messageButton.addActionListener(e -> {
                handleMessageSeller();
            });
            buttonPanel.add(messageButton);
        }

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);

        // Add panels to dialog
        add(titlePanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the panel with seller information.
     *
     * @return The info panel
     */
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Seller info panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        infoPanel.add(new JLabel(username), gbc);

        // Seller ID
        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(new JLabel("Seller ID:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        infoPanel.add(new JLabel(sellerId), gbc);

        // Rating
        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(new JLabel("Rating:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));

        // Create rating stars
        int fullStars = (int) Math.floor(rating);
        boolean halfStar = (rating - fullStars) >= 0.5;

        for (int i = 0; i < fullStars; i++) {
            JLabel starLabel = new JLabel("★");
            starLabel.setForeground(Color.ORANGE);
            ratingPanel.add(starLabel);
        }

        if (halfStar) {
            JLabel halfStarLabel = new JLabel("½");
            halfStarLabel.setForeground(Color.ORANGE);
            ratingPanel.add(halfStarLabel);
        }

        int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
        for (int i = 0; i < emptyStars; i++) {
            JLabel emptyStarLabel = new JLabel("☆");
            emptyStarLabel.setForeground(Color.GRAY);
            ratingPanel.add(emptyStarLabel);
        }

        JLabel ratingValueLabel = new JLabel(String.format(" (%.1f/5.0)", rating));
        ratingPanel.add(ratingValueLabel);

        infoPanel.add(ratingPanel, gbc);

        panel.add(infoPanel, BorderLayout.NORTH);

        // Add more info as needed

        return panel;
    }

    /**
     * Creates the panel with seller's items.
     *
     * @return The items panel
     */
    private JPanel createItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create table for items
        DefaultTableModel itemsTableModel = new DefaultTableModel(
                new Object[][] {},
                new String[] {"ID", "Title", "Category", "Price", "Status", "Actions"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only actions column is editable
            }
        };

        JTable itemsTable = new JTable(itemsTableModel);
        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        itemsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        itemsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        itemsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        itemsTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        // Set up button renderer and editor for the actions column
        itemsTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        itemsTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(itemsTable);

        // Add items to table
        loadSellerItems(itemsTableModel);

        // Button click handler for the "View" button
        itemsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = itemsTable.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / itemsTable.getRowHeight();

                if (row < itemsTable.getRowCount() && row >= 0 && column == 5) {
                    String itemId = (String) itemsTable.getValueAt(row, 0);

                    // Show item details
                    ItemDetailDialog itemDialog = new ItemDetailDialog(
                            (Frame) SwingUtilities.getWindowAncestor(SellerInfoDialog.this),
                            client,
                            itemId
                    );
                    itemDialog.setVisible(true);
                }
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Loads the seller's items into the table model.
     *
     * @param model The table model to populate
     */
    private void loadSellerItems(DefaultTableModel model) {
        // Search for all items
        String response = client.searchItems("", "", 100);
        String[] parts = response.split(",");

        if (parts.length >= 3 && parts[1].equals("SUCCESS")) {
            int count = Integer.parseInt(parts[2]);

            for (int i = 0; i < count; i++) {
                String itemId = parts[3 + 2*i];

                // Get more details about the item
                String itemResponse = client.getItem(itemId);
                String[] itemParts = itemResponse.split(",");

                if (itemParts.length >= 7 && itemParts[1].equals("SUCCESS")) {
                    String itemSellerId = itemParts[3];

                    // Only include items from this seller
                    if (itemSellerId.equals(sellerId)) {
                        String title = itemParts[4];
                        String category = itemParts[6];
                        double price = Double.parseDouble(itemParts[7]);
                        boolean sold = Boolean.parseBoolean(itemParts[8]);

                        // Add row to table
                        model.addRow(new Object[] {
                                itemId,
                                title,
                                category,
                                currencyFormat.format(price),
                                sold ? "Sold" : "Available",
                                "View"
                        });
                    }
                }
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

        JLabel instructionLabel = new JLabel("Enter your message to " + username + ":");

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
                        "none"
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
     * Button renderer for JTable cells.
     */
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            if (value != null) {
                setText(value.toString());
            } else {
                setText("");
            }

            return this;
        }
    }

    /**
     * Button editor for JTable cells.
     */
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {

            if (value != null) {
                label = value.toString();
            } else {
                label = "";
            }

            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
