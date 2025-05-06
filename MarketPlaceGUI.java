import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

/**
 * GUI implementation for the Marketplace application.
 * @author Ayush Dhanani
 * @version
 */
public class MarketPlaceGUI extends JFrame {
    // Client instance
    private Client client;

    // Main panels
    private JPanel loginPanel;
    private JPanel mainPanel;
    private JPanel registerPanel;

    // Cards layout for switching between panels
    private CardLayout cardLayout;
    private JPanel contentPanel;

    // Main dashboard components
    private JTabbedPane tabbedPane;
    private JPanel browsePanel;
    private JPanel myListingsPanel;
    private JPanel messagesPanel;
    private JPanel accountPanel;

    // Status components
    private JLabel statusLabel;
    private JLabel balanceLabel;
    private JLabel usernameLabel;

    // Common UI elements
    private JButton logoutButton;
    private JButton refreshButton;

    // Current user info
    private String currentUsername;
    private double currentBalance;

    // Formatters
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    /**
     * Constructor for the MarketPlaceGUI.
     */
    public MarketPlaceGUI() {
        // Initialize client
        client = new Client();

        // Connect to server
        if (!client.connect()) {
            JOptionPane.showMessageDialog(this,
                    "Failed to connect to server. Please try again later.",
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Set up the JFrame
        setTitle("Marketplace Application");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Add window listener for clean disconnect
        addWindowListener();

        // Initialize layout
        initializeLayout();

        // Show the frame
        setVisible(true);
    }

    /**
     * Initializes the main layout with card layout for different screens.
     */
    private void initializeLayout() {
        // Create card layout and content panel
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Initialize panels
        initializeLoginPanel();
        initializeRegisterPanel();
        initializeMainPanel();

        // Add panels to content panel
        contentPanel.add(loginPanel, "LOGIN");
        contentPanel.add(registerPanel, "REGISTER");
        contentPanel.add(mainPanel, "MAIN");

        // Add content panel to frame
        add(contentPanel);

        // Show login panel by default
        cardLayout.show(contentPanel, "LOGIN");
    }

    /**
     * Initializes the login panel.
     */
    private void initializeLoginPanel() {
        loginPanel = new JPanel(new BorderLayout());
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create center panel with login form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Login to Marketplace");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(passwordField, gbc);

        // Login button
        JButton loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(loginButton, gbc);

        // Register link
        JButton registerLink = new JButton("Don't have an account? Register");
        registerLink.setBorderPainted(false);
        registerLink.setContentAreaFilled(false);
        registerLink.setForeground(Color.BLUE);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(registerLink, gbc);

        // Status message
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);

        // Add form panel to center
        loginPanel.add(formPanel, BorderLayout.CENTER);

        // Add action listeners
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Username and password cannot be empty");
                return;
            }

            String response = client.login(username, password);
            String[] parts = response.split(",");

            if (parts.length >= 3 && parts[1].equals("SUCCESS")) {
                currentUsername = username;
                // Get balance
                String balanceResponse = client.sendMessage("GET_BALANCE," + client.getCurrentUserId());
                String[] balanceParts = balanceResponse.split(",");
                if (balanceParts.length >= 3 && balanceParts[1].equals("SUCCESS")) {
                    currentBalance = Double.parseDouble(balanceParts[2]);
                }

                statusLabel.setText("");
                cardLayout.show(contentPanel, "MAIN");
                updateUserInfo();
                refreshAllPanels();
            } else {
                statusLabel.setText("Login failed. Please check your credentials.");
            }
        });

        registerLink.addActionListener(e -> {
            cardLayout.show(contentPanel, "REGISTER");
        });

        // Enter key to login
        ActionListener loginAction = e -> loginButton.doClick();
        passwordField.addActionListener(loginAction);
    }

    /**
     * Initializes the registration panel.
     */
    private void initializeRegisterPanel() {
        registerPanel = new JPanel(new BorderLayout());
        registerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create center panel with registration form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Register New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(titleLabel, gbc);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(passwordField, gbc);

        // Bio
        JLabel bioLabel = new JLabel("Bio:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(bioLabel, gbc);

        JTextArea bioArea = new JTextArea(4, 20);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        JScrollPane bioScrollPane = new JScrollPane(bioArea);
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(bioScrollPane, gbc);

        // Register button
        JButton registerButton = new JButton("Register");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(registerButton, gbc);

        // Back to login link
        JButton loginLink = new JButton("Already have an account? Login");
        loginLink.setBorderPainted(false);
        loginLink.setContentAreaFilled(false);
        loginLink.setForeground(Color.BLUE);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(loginLink, gbc);

        // Status message
        JLabel statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);

        // Add form panel to center
        registerPanel.add(formPanel, BorderLayout.CENTER);

        // Add action listeners
        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String bio = bioArea.getText();

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Username and password cannot be empty");
                return;
            }

            String response = client.register(username, password, bio);
            String[] parts = response.split(",");

            if (parts.length >= 2 && parts[1].equals("SUCCESS")) {
                JOptionPane.showMessageDialog(this,
                        "Registration successful! You can now login.",
                        "Registration Complete", JOptionPane.INFORMATION_MESSAGE);

                usernameField.setText("");
                passwordField.setText("");
                bioArea.setText("");
                statusLabel.setText("");

                cardLayout.show(contentPanel, "LOGIN");
            } else {
                statusLabel.setText("Registration failed. Username may already exist.");
            }
        });

        loginLink.addActionListener(e -> {
            cardLayout.show(contentPanel, "LOGIN");
        });
    }

    /**
     * Initializes the main panel with tabbed interface.
     */
    private void initializeMainPanel() {
        mainPanel = new JPanel(new BorderLayout());

        // Top panel with user info and logout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        usernameLabel = new JLabel("Welcome, Guest");
        balanceLabel = new JLabel("Balance: $0.00");
        userInfoPanel.add(usernameLabel);
        userInfoPanel.add(new JLabel(" | "));
        userInfoPanel.add(balanceLabel);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("Refresh");
        logoutButton = new JButton("Logout");
        actionPanel.add(refreshButton);
        actionPanel.add(logoutButton);

        topPanel.add(userInfoPanel, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);

        // Status bar at bottom
        JPanel bottomPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Ready");
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Tabbed pane in center
        tabbedPane = new JTabbedPane();

        // Initialize tab panels
        initializeBrowsePanel();
        initializeMyListingsPanel();
        initializeMessagesPanel();
        initializeAccountPanel();

        // Add tabs
        tabbedPane.addTab("Browse Items", browsePanel);
        tabbedPane.addTab("My Listings", myListingsPanel);
        tabbedPane.addTab("Messages", messagesPanel);
        tabbedPane.addTab("Account", accountPanel);

        // Add panels to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add action listeners
        logoutButton.addActionListener(e -> handleLogout());
        refreshButton.addActionListener(e -> refreshAllPanels());

        // Tab change listener to refresh the selected tab
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            switch (selectedIndex) {
                case 0: // Browse Items
                    refreshBrowsePanel();
                    break;
                case 1: // My Listings
                    refreshMyListingsPanel();
                    break;
                case 2: // Messages
                    refreshMessagesPanel();
                    break;
                case 3: // Account
                    refreshAccountPanel();
                    break;
            }
        });
    }

    /**
     * Initializes the browse panel for searching and viewing items.
     */
    private void initializeBrowsePanel() {
        browsePanel = new JPanel(new BorderLayout());
        browsePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel at top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField(20);
        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<String> categoryComboBox = new JComboBox<>(new String[] {
                "", "Electronics", "Clothing", "Books", "Home", "Food", "Other"
        });
        JButton searchButton = new JButton("Search");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(categoryLabel);
        searchPanel.add(categoryComboBox);
        searchPanel.add(searchButton);

        // Results panel in center
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Available Items"));

        // Table model for items
        DefaultTableModel itemsTableModel = new DefaultTableModel(
                new Object[][] {},
                new String[] {"ID", "Title", "Category", "Price", "Seller", "Rating", "Actions"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 6; // Make both Rating and Actions columns editable
            }
        };

        JTable itemsTable = new JTable(itemsTableModel);
        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        itemsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        itemsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        itemsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        itemsTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Rating column
        itemsTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        // Set up button renderer and editor for the actions column
        itemsTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        itemsTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        itemsTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        itemsTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(itemsTable);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        // Add panels to browse panel
        browsePanel.add(searchPanel, BorderLayout.NORTH);
        browsePanel.add(resultsPanel, BorderLayout.CENTER);

        // Add action listeners
        searchButton.addActionListener(e -> {
            String query = searchField.getText();
            String category = (String) categoryComboBox.getSelectedItem();

            // Clear the table
            while (itemsTableModel.getRowCount() > 0) {
                itemsTableModel.removeRow(0);
            }

            // Search for items
            String response = client.searchItems(query, category, 100);
            String[] parts = response.split(",");

            if (parts.length >= 3 && parts[1].equals("SUCCESS")) {
                int count = Integer.parseInt(parts[2]);

                for (int i = 0; i < count; i++) {
                    String itemId = parts[3 + 2*i];
                    String title = parts[4 + 2*i];

                    // Get more details about the item
                    String itemResponse = client.getItem(itemId);
                    String[] itemParts = itemResponse.split(",");

                    if (itemParts.length >= 7 && itemParts[1].equals("SUCCESS")) {
                        String sellerId = itemParts[3];
                        String itemTitle = itemParts[4];
                        String description = itemParts[5];
                        String itemCategory = itemParts[6];
                        double price = Double.parseDouble(itemParts[7]);
                        boolean sold = Boolean.parseBoolean(itemParts[8]);

                        if (!sold) {
                            // Add row to table
                            itemsTableModel.addRow(new Object[] {
                                    itemId,
                                    itemTitle,
                                    itemCategory,
                                    currencyFormat.format(price),
                                    sellerId,
                                    "Rating",
                                    "Buy"
                            });
                        }
                    }
                }

                statusLabel.setText("Found " + count + " items matching your search.");
            } else {
                statusLabel.setText("No items found.");
            }
        });

        // Button click handler for both Rating and Actions columns
        itemsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = itemsTable.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / itemsTable.getRowHeight();

                if (row < itemsTable.getRowCount() && row >= 0) {
                    if (column == 5) {  // Rating column
                        String rating = (String) itemsTable.getValueAt(row, 5);
                        if (!rating.equals("N/A")) {
                            String sellerId = getSellerId(itemsTable.getValueAt(row, 4).toString());
                            handleRateSeller(sellerId);
                        }
                    } else if (column == 6) {  // Actions column
                        String action = (String) itemsTable.getValueAt(row, 6);
                        String itemId = (String) itemsTable.getValueAt(row, 0);
                        String itemTitle = (String) itemsTable.getValueAt(row, 1);
                        String priceStr = (String) itemsTable.getValueAt(row, 3);
                        String sellerId = getSellerId(itemsTable.getValueAt(row, 4).toString());

                        if (action.equals("Buy")) {
                            // Check if the user is trying to buy their own item
                            if (client.getCurrentUserId().equals(sellerId)) {
                                JOptionPane.showMessageDialog(
                                        browsePanel,
                                        "You cannot buy your own item.",
                                        "Purchase Error",
                                        JOptionPane.ERROR_MESSAGE
                                );
                                return;
                            }

                            int option = JOptionPane.showConfirmDialog(
                                    browsePanel,
                                    "Do you want to buy " + itemTitle + " for " + priceStr + "?",
                                    "Confirm Purchase",
                                    JOptionPane.YES_NO_OPTION
                            );

                            if (option == JOptionPane.YES_OPTION) {
                                String response = client.processPurchase(client.getCurrentUserId(), itemId);
                                String[] parts = response.split(",");

                                if (parts.length >= 2 && parts[1].equals("SUCCESS")) {
                                    JOptionPane.showMessageDialog(
                                            browsePanel,
                                            "Purchase successful!",
                                            "Purchase Complete",
                                            JOptionPane.INFORMATION_MESSAGE
                                    );

                                    // Refresh everything that needs updating
                                    refreshBrowsePanel();
                                    refreshMyListingsPanel();
                                    refreshAccountPanel();
                                    refreshMessagesPanel();
                                    updateBalance();
                                } else {
                                    // Parse the error message if available
                                    String errorMsg = "Purchase failed. You may not have enough funds.";
                                    if (parts.length > 2 && parts[2].equals("Cannot buy your own item")) {
                                        errorMsg = "You cannot buy your own item.";
                                    }

                                    JOptionPane.showMessageDialog(
                                            browsePanel,
                                            errorMsg,
                                            "Purchase Failed",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }
                            }
                        } else if (action.equals("View")) {
                            // Show item details
                            ItemDetailDialog itemDialog = new ItemDetailDialog(
                                    (Frame) SwingUtilities.getWindowAncestor(MarketPlaceGUI.this),
                                    client,
                                    itemId
                            );
                            itemDialog.setVisible(true);
                        }
                    }
                }
            }
        });
    }

    private void handleRateSeller(String sellerId) {
        JDialog rateDialog = new JDialog(this, "Rate Seller", true);
        rateDialog.setSize(300, 150);
        rateDialog.setLocationRelativeTo(this);
        rateDialog.setLayout(new BorderLayout());

        JPanel ratePanel = new JPanel(new FlowLayout());
        JLabel rateLabel = new JLabel("Rating (1-5):");
        JSpinner rateSpinner = new JSpinner(new SpinnerNumberModel(5.0, 1.0, 5.0, 0.5));
        JButton submitButton = new JButton("Submit");

        ratePanel.add(rateLabel);
        ratePanel.add(rateSpinner);
        ratePanel.add(submitButton);

        rateDialog.add(ratePanel, BorderLayout.CENTER);

        submitButton.addActionListener(event -> {
            double rating = (Double) rateSpinner.getValue();

            String response = client.rateSeller(sellerId, rating);
            String[] parts = response.split(",");

            if (parts.length >= 2 && parts[1].equals("SUCCESS")) {
                JOptionPane.showMessageDialog(
                        rateDialog,
                        "Rating submitted successfully!",
                        "Rating Submitted",
                        JOptionPane.INFORMATION_MESSAGE
                );
                rateDialog.dispose();
                refreshBrowsePanel(); // Refresh to show updated rating
            } else {
                JOptionPane.showMessageDialog(
                        rateDialog,
                        "Failed to submit rating",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        rateDialog.setVisible(true);
    }

    private String getSellerId(String sellerDisplay) {
        // Extract seller ID from display format (could be "username" or "username (rating)")
        int openParenIndex = sellerDisplay.indexOf(" (");
        String username = openParenIndex > 0 ? sellerDisplay.substring(0, openParenIndex) : sellerDisplay;

        // Look up the seller ID by username
        String usersResponse = client.getAllUsers();
        String[] userParts = usersResponse.split(",");

        if (userParts.length >= 3 && userParts[1].equals("SUCCESS")) {
            int userCount = Integer.parseInt(userParts[2]);
            for (int i = 0; i < userCount; i++) {
                String userId = userParts[3 + 2*i];
                String name = userParts[4 + 2*i];
                if (name.equals(username)) {
                    return userId;
                }
            }
        }

        return sellerDisplay; // Return the display name if ID not found
    }

    /**
     * Initializes the listings panel for managing user's items.
     */
    private void initializeMyListingsPanel() {
        myListingsPanel = new JPanel(new BorderLayout());
        myListingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Action panel at top
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addItemButton = new JButton("Add New Item");
        JCheckBox showSoldItemsCheckBox = new JCheckBox("Show Sold Items");

        actionPanel.add(addItemButton);
        actionPanel.add(showSoldItemsCheckBox);

        // Listings panel in center
        JPanel listingsPanel = new JPanel(new BorderLayout());
        listingsPanel.setBorder(BorderFactory.createTitledBorder("My Listings"));

        // Table model for listings
        DefaultTableModel listingsTableModel = new DefaultTableModel(
                new Object[][] {},
                new String[] {"ID", "Title", "Category", "Price", "Status", "Actions"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only actions column is editable
            }
        };

        JTable listingsTable = new JTable(listingsTableModel);
        listingsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        listingsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        listingsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        listingsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        listingsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        listingsTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        // Set up button renderer and editor for the actions column
        listingsTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        listingsTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(listingsTable);
        listingsPanel.add(scrollPane, BorderLayout.CENTER);

        // Add panels to my listings panel
        myListingsPanel.add(actionPanel, BorderLayout.NORTH);
        myListingsPanel.add(listingsPanel, BorderLayout.CENTER);

        // Add action listeners
        addItemButton.addActionListener(e -> {
            // Create dialog for adding new item
            JDialog addItemDialog = new JDialog(this, "Add New Item", true);
            addItemDialog.setSize(400, 350);
            addItemDialog.setLocationRelativeTo(this);
            addItemDialog.setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Title
            JLabel titleLabel = new JLabel("Title:");
            gbc.gridx = 0;
            gbc.gridy = 0;
            formPanel.add(titleLabel, gbc);

            JTextField titleField = new JTextField(20);
            gbc.gridx = 1;
            gbc.gridy = 0;
            formPanel.add(titleField, gbc);

            // Description
            JLabel descLabel = new JLabel("Description:");
            gbc.gridx = 0;
            gbc.gridy = 1;
            formPanel.add(descLabel, gbc);

            JTextArea descArea = new JTextArea(4, 20);
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            JScrollPane descScrollPane = new JScrollPane(descArea);
            gbc.gridx = 1;
            gbc.gridy = 1;
            formPanel.add(descScrollPane, gbc);

            // Category
            JLabel catLabel = new JLabel("Category:");
            gbc.gridx = 0;
            gbc.gridy = 2;
            formPanel.add(catLabel, gbc);

            JComboBox<String> catComboBox = new JComboBox<>(new String[] {
                    "Electronics", "Clothing", "Books", "Home", "Food", "Other"
            });
            gbc.gridx = 1;
            gbc.gridy = 2;
            formPanel.add(catComboBox, gbc);

            // Price
            JLabel priceLabel = new JLabel("Price:");
            gbc.gridx = 0;
            gbc.gridy = 3;
            formPanel.add(priceLabel, gbc);

            JSpinner priceSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10000.0, 0.01));
            JSpinner.NumberEditor editor = new JSpinner.NumberEditor(priceSpinner, "0.00");
            priceSpinner.setEditor(editor);
            gbc.gridx = 1;
            gbc.gridy = 3;
            formPanel.add(priceSpinner, gbc);

            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancelButton = new JButton("Cancel");
            JButton saveButton = new JButton("Save");
            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);

            addItemDialog.add(formPanel, BorderLayout.CENTER);
            addItemDialog.add(buttonPanel, BorderLayout.SOUTH);

            // Add action listeners
            cancelButton.addActionListener(event -> addItemDialog.dispose());

            saveButton.addActionListener(event -> {
                String title = titleField.getText();
                String description = descArea.getText();
                String category = (String) catComboBox.getSelectedItem();
                double price = (Double) priceSpinner.getValue();

                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            addItemDialog,
                            "Title cannot be empty",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                String response = client.addItem(
                        client.getCurrentUserId(),
                        title,
                        description,
                        category,
                        price
                );

                String[] parts = response.split(",");

                if (parts.length >= 2 && parts[1].equals("SUCCESS")) {
                    JOptionPane.showMessageDialog(
                            addItemDialog,
                            "Item added successfully!",
                            "Item Added",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    addItemDialog.dispose();
                    refreshMyListingsPanel();
                } else {
                    JOptionPane.showMessageDialog(
                            addItemDialog,
                            "Failed to add item",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });

            addItemDialog.setVisible(true);
        });

        showSoldItemsCheckBox.addActionListener(e -> {
            refreshMyListingsPanel();
        });

        // Button click handler for the "Remove" button
        listingsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = listingsTable.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / listingsTable.getRowHeight();

                if (row < listingsTable.getRowCount() && row >= 0 && column == 5) {
                    String action = (String) listingsTable.getValueAt(row, 5);
                    if (action == null || action.isEmpty()) {
                        return; // No action available
                    }

                    String itemId = (String) listingsTable.getValueAt(row, 0);
                    String itemTitle = (String) listingsTable.getValueAt(row, 1);
                    String status = (String) listingsTable.getValueAt(row, 4);

                    if (status.equals("Available") && action.equals("Remove")) {
                        int option = JOptionPane.showConfirmDialog(
                                myListingsPanel,
                                "Do you want to remove " + itemTitle + "?",
                                "Confirm Removal",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (option == JOptionPane.YES_OPTION) {
                            String response = client.removeItem(itemId, client.getCurrentUserId());
                            String[] parts = response.split(",");

                            if (parts.length >= 2 && parts[1].equals("SUCCESS")) {
                                JOptionPane.showMessageDialog(
                                        myListingsPanel,
                                        "Item removed successfully!",
                                        "Item Removed",
                                        JOptionPane.INFORMATION_MESSAGE
                                );
                                refreshMyListingsPanel();
                            } else {
                                JOptionPane.showMessageDialog(
                                        myListingsPanel,
                                        "Failed to remove item",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }
                        }
                    } else if (action.equals("View")) {
                        // Show item details
                        ItemDetailDialog itemDialog = new ItemDetailDialog(
                                (Frame) SwingUtilities.getWindowAncestor(MarketPlaceGUI.this),
                                client,
                                itemId
                        );
                        itemDialog.setVisible(true);
                    }
                }
            }
        });
    }

    /**
     * Initializes the messages panel for user communication.
     */
    private void initializeMessagesPanel() {
        messagesPanel = new JPanel(new BorderLayout());
        messagesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Split pane with conversations on left and messages on right
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);

        // Conversations panel
        JPanel conversationsPanel = new JPanel(new BorderLayout());
        conversationsPanel.setBorder(BorderFactory.createTitledBorder("Conversations"));

        // Add a button to start a new conversation
        JButton newConversationButton = new JButton("New Conversation");
        conversationsPanel.add(newConversationButton, BorderLayout.NORTH);

        DefaultListModel<ConversationInfo> conversationsListModel = new DefaultListModel<>();
        JList<ConversationInfo> conversationsList = new JList<>(conversationsListModel);
        conversationsList.setCellRenderer(new ConversationCellRenderer());
        JScrollPane conversationsScrollPane = new JScrollPane(conversationsList);

        conversationsPanel.add(conversationsScrollPane, BorderLayout.CENTER);

        // Messages panel
        JPanel messagesContentPanel = new JPanel(new BorderLayout());
        messagesContentPanel.setBorder(BorderFactory.createTitledBorder("Messages"));

        JTextArea messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        messagesArea.setLineWrap(true);
        messagesArea.setWrapStyleWord(true);
        JScrollPane messagesScrollPane = new JScrollPane(messagesArea);

        JPanel sendPanel = new JPanel(new BorderLayout());
        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("Send");
        sendPanel.add(messageField, BorderLayout.CENTER);
        sendPanel.add(sendButton, BorderLayout.EAST);

        messagesContentPanel.add(messagesScrollPane, BorderLayout.CENTER);
        messagesContentPanel.add(sendPanel, BorderLayout.SOUTH);

        // Add panels to split pane
        splitPane.setLeftComponent(conversationsPanel);
        splitPane.setRightComponent(messagesContentPanel);

        // Add split pane to messages panel
        messagesPanel.add(splitPane, BorderLayout.CENTER);

        // Add action listeners
        // Button to start a new conversation
        newConversationButton.addActionListener(e -> {
            // Show dialog to select a user
            JDialog selectUserDialog = new JDialog(this, "Select User", true);
            selectUserDialog.setSize(400, 300);
            selectUserDialog.setLocationRelativeTo(this);
            selectUserDialog.setLayout(new BorderLayout());

            // Get all users
            String response = client.getAllUsers();
            String[] parts = response.split(",");

            if (parts.length >= 3 && parts[1].equals("SUCCESS")) {
                int count = Integer.parseInt(parts[2]);

                // Create list model and list
                DefaultListModel<UserInfo> userListModel = new DefaultListModel<>();

                for (int i = 0; i < count; i++) {
                    String userId = parts[3 + 2*i];
                    String username = parts[4 + 2*i];

                    // Don't add current user
                    if (!userId.equals(client.getCurrentUserId())) {
                        userListModel.addElement(new UserInfo(userId, username));
                    }
                }

                JList<UserInfo> userList = new JList<>(userListModel);
                userList.setCellRenderer(new UserCellRenderer());
                JScrollPane scrollPane = new JScrollPane(userList);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton cancelButton = new JButton("Cancel");
                JButton selectButton = new JButton("Select");

                buttonPanel.add(cancelButton);
                buttonPanel.add(selectButton);

                selectUserDialog.add(scrollPane, BorderLayout.CENTER);
                selectUserDialog.add(buttonPanel, BorderLayout.SOUTH);

                cancelButton.addActionListener(event -> selectUserDialog.dispose());

                selectButton.addActionListener(event -> {
                    UserInfo selectedUser = userList.getSelectedValue();
                    if (selectedUser != null) {
                        // Create conversation info and add to list
                        ConversationInfo newConversation = new ConversationInfo(
                                selectedUser.getUserId(), selectedUser.getUsername());

                        // Check if this conversation already exists
                        boolean exists = false;
                        for (int i = 0; i < conversationsListModel.size(); i++) {
                            ConversationInfo existing = conversationsListModel.getElementAt(i);
                            if (existing.getUserId().equals(newConversation.getUserId())) {
                                exists = true;
                                conversationsList.setSelectedIndex(i);
                                break;
                            }
                        }

                        if (!exists) {
                            conversationsListModel.addElement(newConversation);
                            conversationsList.setSelectedValue(newConversation, true);
                        }

                        selectUserDialog.dispose();

                        // Clear message area for new conversation
                        messagesArea.setText("No messages yet. Start a conversation!");
                    }
                });

                selectUserDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to retrieve user list",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        conversationsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ConversationInfo selected = conversationsList.getSelectedValue();
                if (selected != null) {
                    // Load messages between users
                    String response = client.getMessages(client.getCurrentUserId(), selected.getUserId());
                    System.out.println("Messages response: " + response);

                    // Clear message area
                    messagesArea.setText("");

                    // Parse the response
                    String[] parts = response.split(",");

                    if (parts.length >= 3 && parts[1].equals("SUCCESS")) {
                        // Check if there are any messages
                        if (parts[2].equals("0")) {
                            messagesArea.append("No messages yet. Start a conversation!");
                            return;
                        }

                        // Extract and display messages
                        if (parts[2].startsWith("messages.size()")) {
                            // Handle old format with "messages.size()" literal
                            String messagesData = response.substring(response.indexOf("messages.size()") + "messages.size()".length());

                            // Find all UUIDs (message IDs) - they mark the start of each message
                            List<Integer> uuidPositions = new ArrayList<>();

                            // UUID pattern regex
                            String uuidPattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
                            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(uuidPattern);
                            java.util.regex.Matcher matcher = pattern.matcher(messagesData);

                            // Find all positions where UUIDs start
                            while (matcher.find()) {
                                uuidPositions.add(matcher.start());
                            }

                            // Process each message
                            for (int i = 0; i < uuidPositions.size(); i++) {
                                int start = uuidPositions.get(i);

                                // Find the parts in this message segment
                                String segment;
                                if (i < uuidPositions.size() - 1) {
                                    segment = messagesData.substring(start, uuidPositions.get(i + 1));
                                } else {
                                    segment = messagesData.substring(start);
                                }

                                // Split the segment by commas to get each part
                                String[] messageParts = segment.split(",", 5); // Limit to 5 parts

                                if (messageParts.length >= 5) {
                                    String messageId = messageParts[0];
                                    String senderId = messageParts[1];
                                    String receiverId = messageParts[2];
                                    String timestamp = messageParts[3];
                                    String content = messageParts[4];

                                    // If this isn't the last message, extract just the content before the next UUID
                                    if (i < uuidPositions.size() - 1) {
                                        // Find the position of the next UUID within this content
                                        matcher = pattern.matcher(content);
                                        if (matcher.find()) {
                                            content = content.substring(0, matcher.start());
                                        }
                                    }

                                    boolean isSentByMe = senderId.equals(client.getCurrentUserId());
                                    String prefix = isSentByMe ? "Me: " : selected.getUsername() + ": ";

                                    messagesArea.append(prefix + content + "\n\n");
                                }
                            }
                        } else {
                            // Handle standard format with message count
                            try {
                                int messageCount = Integer.parseInt(parts[2]);

                                for (int i = 0; i < messageCount; i++) {
                                    int baseIndex = 3 + (i * 5); // Each message has 5 parts

                                    if (baseIndex + 4 < parts.length) {
                                        String messageId = parts[baseIndex];
                                        String senderId = parts[baseIndex + 1];
                                        String receiverId = parts[baseIndex + 2];
                                        String timestamp = parts[baseIndex + 3];
                                        String content = parts[baseIndex + 4];

                                        boolean isSentByMe = senderId.equals(client.getCurrentUserId());
                                        String prefix = isSentByMe ? "Me: " : selected.getUsername() + ": ";

                                        messagesArea.append(prefix + content + "\n\n");
                                    }
                                }
                            } catch (NumberFormatException ex) {
                                messagesArea.append("Error parsing messages. Please try again.");
                                System.out.println("Error parsing message count: " + ex.getMessage());
                            }
                        }

                        // Scroll to the bottom
                        messagesArea.setCaretPosition(messagesArea.getDocument().getLength());
                    } else {
                        messagesArea.append("No messages yet. Start a conversation!");
                    }
                }
            }
        });

        sendButton.addActionListener(e -> {
            ConversationInfo selected = conversationsList.getSelectedValue();
            String message = messageField.getText().trim();

            if (selected != null && !message.isEmpty()) {
                String response = client.sendMessageToUser(
                        client.getCurrentUserId(),
                        selected.getUserId(),
                        message,
                        "none"
                );

                String[] parts = response.split(",");

                if (parts.length >= 2 && parts[1].equals("SUCCESS")) {
                    messageField.setText("");

                    // Add the sent message to the messages area immediately
                    messagesArea.append("Me: " + message + "\n\n");

                    // Force scrolling to bottom of message area
                    messagesArea.setCaretPosition(messagesArea.getDocument().getLength());
                } else {
                    JOptionPane.showMessageDialog(
                            messagesPanel,
                            "Failed to send message",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        // Enter key to send message
        messageField.addActionListener(e -> sendButton.doClick());
    }

    /**
     * Class to represent user information.
     */
    private class UserInfo {
        private String userId;
        private String username;

        public UserInfo(String userId, String username) {
            this.userId = userId;
            this.username = username;
        }

        public String getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        @Override
        public String toString() {
            return username;
        }
    }

    /**
     * Custom renderer for user cells.
     */
    private class UserCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            if (value instanceof UserInfo) {
                UserInfo info = (UserInfo) value;
                label.setText(info.getUsername());
            }

            return label;
        }
    }

    /**
     * Initializes the account panel for managing user account.
     */
    private void initializeAccountPanel() {
        accountPanel = new JPanel(new BorderLayout());
        accountPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Account management panel
        JPanel accountManagementPanel = new JPanel(new GridBagLayout());
        accountManagementPanel.setBorder(BorderFactory.createTitledBorder("Account Management"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Current balance
        JLabel currentBalanceLabel = new JLabel("Current Balance:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        accountManagementPanel.add(currentBalanceLabel, gbc);

        JLabel balanceValueLabel = new JLabel(currencyFormat.format(currentBalance));
        balanceValueLabel.setName("balanceValueLabel"); // Add name for easy lookup
        gbc.gridx = 1;
        gbc.gridy = 0;
        accountManagementPanel.add(balanceValueLabel, gbc);

        // Add funds section
        JLabel addFundsLabel = new JLabel("Add Funds:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        accountManagementPanel.add(addFundsLabel, gbc);

        JSpinner addFundsSpinner = new JSpinner(new SpinnerNumberModel(10.0, 1.0, 1000.0, 1.0));
        JSpinner.NumberEditor addEditor = new JSpinner.NumberEditor(addFundsSpinner, "0.00");
        addFundsSpinner.setEditor(addEditor);
        gbc.gridx = 1;
        gbc.gridy = 1;
        accountManagementPanel.add(addFundsSpinner, gbc);

        JButton addFundsButton = new JButton("Add Funds");
        gbc.gridx = 2;
        gbc.gridy = 1;
        accountManagementPanel.add(addFundsButton, gbc);

        // Withdraw funds section
        JLabel withdrawFundsLabel = new JLabel("Withdraw Funds:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        accountManagementPanel.add(withdrawFundsLabel, gbc);

        JSpinner withdrawFundsSpinner = new JSpinner(new SpinnerNumberModel(10.0, 1.0, 1000.0, 1.0));
        JSpinner.NumberEditor withdrawEditor = new JSpinner.NumberEditor(withdrawFundsSpinner, "0.00");
        withdrawFundsSpinner.setEditor(withdrawEditor);
        gbc.gridx = 1;
        gbc.gridy = 2;
        accountManagementPanel.add(withdrawFundsSpinner, gbc);

        JButton withdrawFundsButton = new JButton("Withdraw Funds");
        gbc.gridx = 2;
        gbc.gridy = 2;
        accountManagementPanel.add(withdrawFundsButton, gbc);





        JLabel myRatingLabel = new JLabel("My Rating:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        accountManagementPanel.add(myRatingLabel, gbc);

        // Initially set to loading, will be updated in refreshAccountPanel()
        JLabel myRatingValueLabel = new JLabel("Loading...");
        myRatingValueLabel.setName("myRatingValueLabel"); // Add name for easy lookup
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        accountManagementPanel.add(myRatingValueLabel, gbc);








        // Purchase history section
        JPanel purchaseHistoryPanel = new JPanel(new BorderLayout());
        purchaseHistoryPanel.setBorder(BorderFactory.createTitledBorder("Purchase History"));

        DefaultTableModel purchaseTableModel = new DefaultTableModel(
                new Object[][] {},
                new String[] {"ID", "Title", "Category", "Price", "Seller", "Rate"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only rate column is editable
            }
        };

        JTable purchaseTable = new JTable(purchaseTableModel);
        purchaseTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        purchaseTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        purchaseTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        purchaseTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        purchaseTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        purchaseTable.getColumnModel().getColumn(5).setPreferredWidth(80);

        // Set up button renderer and editor for the rate column
        purchaseTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        purchaseTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane purchaseScrollPane = new JScrollPane(purchaseTable);
        purchaseHistoryPanel.add(purchaseScrollPane, BorderLayout.CENTER);

        // Account actions panel
        JPanel accountActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        accountActionsPanel.setBorder(BorderFactory.createTitledBorder("Account Actions"));

        JButton deleteAccountButton = new JButton("Delete Account");
        deleteAccountButton.setForeground(Color.RED);
        accountActionsPanel.add(deleteAccountButton);

        // Add panels to account panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(accountManagementPanel, BorderLayout.NORTH);
        topPanel.add(accountActionsPanel, BorderLayout.SOUTH);

        accountPanel.add(topPanel, BorderLayout.NORTH);
        accountPanel.add(purchaseHistoryPanel, BorderLayout.CENTER);

        // Add action listeners
        addFundsButton.addActionListener(e -> {
            double amount = (Double) addFundsSpinner.getValue();

            String response = client.addFunds(client.getCurrentUserId(), amount);
            String[] parts = response.split(",");

            if (parts.length >= 2 && parts[1].equals("SUCCESS")) {
                JOptionPane.showMessageDialog(
                        accountPanel,
                        "Funds added successfully!",
                        "Funds Added",
                        JOptionPane.INFORMATION_MESSAGE
                );
                updateBalance();
                balanceValueLabel.setText(currencyFormat.format(currentBalance));
            } else {
                JOptionPane.showMessageDialog(
                        accountPanel,
                        "Failed to add funds",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        withdrawFundsButton.addActionListener(e -> {
            double amount = (Double) withdrawFundsSpinner.getValue();

            if (amount > currentBalance) {
                JOptionPane.showMessageDialog(
                        accountPanel,
                        "Insufficient funds",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            String response = client.withdrawFunds(client.getCurrentUserId(), amount);
            String[] parts = response.split(",");

            if (parts.length >= 2 && parts[1].equals("SUCCESS")) {
                JOptionPane.showMessageDialog(
                        accountPanel,
                        "Funds withdrawn successfully!",
                        "Funds Withdrawn",
                        JOptionPane.INFORMATION_MESSAGE
                );
                updateBalance();
                balanceValueLabel.setText(currencyFormat.format(currentBalance));
            } else {
                JOptionPane.showMessageDialog(
                        accountPanel,
                        "Failed to withdraw funds",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        deleteAccountButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                    accountPanel,
                    "Are you sure you want to delete your account? This action cannot be undone.",
                    "Confirm Account Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (option == JOptionPane.YES_OPTION) {
                String response = client.deleteAccount(client.getCurrentUserId());
                String[] parts = response.split(",");

                if (parts.length >= 2 && parts[1].equals("SUCCESS")) {
                    JOptionPane.showMessageDialog(
                            accountPanel,
                            "Account deleted successfully!",
                            "Account Deleted",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    handleLogout();
                } else {
                    JOptionPane.showMessageDialog(
                            accountPanel,
                            "Failed to delete account",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        // Button click handler for the "Rate" button
        purchaseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = purchaseTable.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / purchaseTable.getRowHeight();

                if (row < purchaseTable.getRowCount() && row >= 0 && column == 5) {
                    String sellerId = (String) purchaseTable.getValueAt(row, 4);

                    JDialog rateDialog = new JDialog(MarketPlaceGUI.this, "Rate Seller", true);
                    rateDialog.setSize(300, 150);
                    rateDialog.setLocationRelativeTo(MarketPlaceGUI.this);
                    rateDialog.setLayout(new BorderLayout());

                    JPanel ratePanel = new JPanel(new FlowLayout());
                    JLabel rateLabel = new JLabel("Rating (1-5):");
                    JSpinner rateSpinner = new JSpinner(new SpinnerNumberModel(5.0, 1.0, 5.0, 0.5));
                    JButton submitButton = new JButton("Submit");

                    ratePanel.add(rateLabel);
                    ratePanel.add(rateSpinner);
                    ratePanel.add(submitButton);

                    rateDialog.add(ratePanel, BorderLayout.CENTER);

                    submitButton.addActionListener(event -> {
                        double rating = (Double) rateSpinner.getValue();

                        String response = client.rateSeller(sellerId, rating);
                        String[] parts = response.split(",");

                        if (parts.length >= 2 && parts[1].equals("SUCCESS")) {
                            JOptionPane.showMessageDialog(
                                    rateDialog,
                                    "Rating submitted successfully!",
                                    "Rating Submitted",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                            rateDialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(
                                    rateDialog,
                                    "Failed to submit rating",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    });

                    rateDialog.setVisible(true);
                }
            }
        });
    }

    private void refreshBrowsePanel() {
        if (browsePanel != null) {
            // Find the table model
            JPanel resultsPanel = (JPanel) browsePanel.getComponent(1);
            JScrollPane scrollPane = (JScrollPane) resultsPanel.getComponent(0);
            JTable itemsTable = (JTable) scrollPane.getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) itemsTable.getModel();

            // Clear the table
            while (model.getRowCount() > 0) {
                model.removeRow(0);
            }

            // Get mapping of user IDs to usernames
            Map<String, String> userIdToName = new HashMap<>();
            String usersResponse = client.getAllUsers();
            String[] userParts = usersResponse.split(",");

            if (userParts.length >= 3 && userParts[1].equals("SUCCESS")) {
                int userCount = Integer.parseInt(userParts[2]);
                for (int i = 0; i < userCount; i++) {
                    String userId = userParts[3 + 2*i];
                    String username = userParts[4 + 2*i];
                    userIdToName.put(userId, username);
                }
            }

            // Search for items
            String response = client.searchItems("", "", 100);
            String[] parts = response.split(",");

            if (parts.length >= 3 && parts[1].equals("SUCCESS")) {
                int count = Integer.parseInt(parts[2]);
                int displayedCount = 0;

                for (int i = 0; i < count; i++) {
                    String itemId = parts[3 + 2*i];

                    // Get more details about the item
                    String itemResponse = client.getItem(itemId);
                    String[] itemParts = itemResponse.split(",");

                    if (itemParts.length >= 7 && itemParts[1].equals("SUCCESS")) {
                        String sellerId = itemParts[3];
                        String itemTitle = itemParts[4];
                        String category = itemParts[6];
                        double price = Double.parseDouble(itemParts[7]);
                        boolean sold = Boolean.parseBoolean(itemParts[8]);

                        // Only display items that are not sold and not owned by the current user
                        if (!sellerId.equals(client.getCurrentUserId())) {
                            // Convert seller ID to username if available
                            String sellerDisplay = userIdToName.getOrDefault(sellerId, sellerId);

                            // Get seller rating
                            String ratingResponse = client.getRating(sellerId);
                            String[] ratingParts = ratingResponse.split(",");
                            String ratingDisplay = "No ratings";
                            String ratingAction = "Rate";

                            if (ratingParts.length >= 3 && ratingParts[1].equals("SUCCESS")) {
                                double rating = Double.parseDouble(ratingParts[2]);
                                if (rating > 0) {
                                    ratingDisplay = String.format("%.1f★", rating);
                                }
                            }

                            // Add row to table with a separate rating column
                            model.addRow(new Object[] {
                                    itemId,
                                    itemTitle,
                                    category,
                                    currencyFormat.format(price),
                                    sellerDisplay,
                                    sold ? ratingDisplay : "N/A",
                                    sold ? "View" : "Buy"
                            });
                            displayedCount++;
                        }
                    }
                }

                statusLabel.setText("Showing " + displayedCount + " available items.");
            } else {
                statusLabel.setText("No items found.");
            }
        }
    }

    private void debugLoadMessages(String user1Id, String user2Id) {
        String response = client.getMessages(user1Id, user2Id);
        System.out.println("DEBUG - Message loading response: " + response);

        // Parse and print details to understand what's happening
        String[] parts = response.split(",");
        if (parts.length >= 3 && parts[1].equals("SUCCESS")) {
            int messageCount = Integer.parseInt(parts[2]);
            System.out.println("DEBUG - Found " + messageCount + " messages");

            for (int i = 0; i < messageCount; i++) {
                int baseIndex = 3 + (i * 5);
                if (baseIndex + 4 < parts.length) {
                    String messageId = parts[baseIndex];
                    String senderId = parts[baseIndex + 1];
                    String receiverId = parts[baseIndex + 2];
                    String timestamp = parts[baseIndex + 3];
                    String content = parts[baseIndex + 4];

                    System.out.println("DEBUG - Message " + i + ": From " + senderId +
                            " To " + receiverId + " Content: " + content);
                }
            }
        }
    }

    private void refreshMyListingsPanel() {
        if (myListingsPanel != null && client.getCurrentUserId() != null) {
            // Find the checkbox for showing sold items
            JPanel actionPanel = (JPanel) myListingsPanel.getComponent(0);
            JCheckBox showSoldItemsCheckBox = (JCheckBox) actionPanel.getComponent(1);
            boolean showSoldItems = showSoldItemsCheckBox.isSelected();

            // Find the table model
            JPanel listingsPanel = (JPanel) myListingsPanel.getComponent(1);
            JScrollPane scrollPane = (JScrollPane) listingsPanel.getComponent(0);
            JTable listingsTable = (JTable) scrollPane.getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) listingsTable.getModel();

            // Clear the table
            while (model.getRowCount() > 0) {
                model.removeRow(0);
            }

            // Get user listings
            String response = client.getUserListings(client.getCurrentUserId(), !showSoldItems);
            String[] parts = response.split(",");

            if (parts.length >= 3 && parts[1].equals("SUCCESS")) {
                int count = Integer.parseInt(parts[2]);

                for (int i = 0; i < count; i++) {
                    // Format: itemId,title,price,sold
                    String itemId = parts[3 + 4*i];
                    String title = parts[4 + 4*i];
                    double price = Double.parseDouble(parts[5 + 4*i]);
                    boolean sold = Boolean.parseBoolean(parts[6 + 4*i]);

                    // Get more details about the item
                    String itemResponse = client.getItem(itemId);
                    String[] itemParts = itemResponse.split(",");

                    if (itemParts.length >= 7 && itemParts[1].equals("SUCCESS")) {
                        String category = itemParts[6];

                        // Add row to table
                        model.addRow(new Object[] {
                                itemId,
                                title,
                                category,
                                currencyFormat.format(price),
                                sold ? "Sold" : "Available",
                                sold ? "View" : "Remove"  // Change action based on item status
                        });
                    }
                }

                statusLabel.setText("Showing " + model.getRowCount() + " listings.");
            } else {
                statusLabel.setText("No listings found.");
            }
        }
    }

    /**
     * Refreshes the messages panel with latest conversations.
     */
    private void refreshMessagesPanel() {
        if (messagesPanel != null && client.getCurrentUserId() != null) {
            // Find the conversations list
            JSplitPane splitPane = (JSplitPane) messagesPanel.getComponent(0);
            JPanel conversationsPanel = (JPanel) splitPane.getLeftComponent();
            JScrollPane conversationsScrollPane = (JScrollPane) conversationsPanel.getComponent(1);
            JList<ConversationInfo> conversationsList = (JList<ConversationInfo>) conversationsScrollPane.getViewport().getView();
            DefaultListModel<ConversationInfo> model = (DefaultListModel<ConversationInfo>) conversationsList.getModel();

            // Remember the currently selected conversation
            ConversationInfo selectedConversation = conversationsList.getSelectedValue();

            // Clear the list
            model.clear();

            // Get user conversations
            String response = client.getConversations(client.getCurrentUserId());
            System.out.println("Conversations response: " + response);
            String[] parts = response.split(",");

            if (parts.length >= 3 && parts[1].equals("SUCCESS")) {
                int count = Integer.parseInt(parts[2]);
                System.out.println("Found " + count + " conversations");

                for (int i = 0; i < count; i++) {
                    if (3 + 2*i + 1 < parts.length) { // Ensure there's enough data
                        String userId = parts[3 + 2*i];
                        String username = parts[4 + 2*i];
                        System.out.println("Adding conversation with: " + username + " (" + userId + ")");
                        model.addElement(new ConversationInfo(userId, username));
                    }
                }

                statusLabel.setText("Showing " + count + " conversations.");

                // Restore the previously selected conversation if possible
                if (selectedConversation != null) {
                    for (int i = 0; i < model.getSize(); i++) {
                        if (model.getElementAt(i).getUserId().equals(selectedConversation.getUserId())) {
                            conversationsList.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            } else {
                statusLabel.setText("No conversations found.");
            }
        }
    }

    private void refreshAccountPanel() {
        if (accountPanel != null && client.getCurrentUserId() != null) {
            // Update balance
            updateBalance();

            // Find the purchase history table model
            JPanel topPanel = (JPanel) accountPanel.getComponent(0);
            JPanel accountManagementPanel = (JPanel) topPanel.getComponent(0);
            JLabel balanceValueLabel = null;

            // Find the balance value label
            for (Component comp : accountManagementPanel.getComponents()) {
                if (comp instanceof JLabel && "balanceValueLabel".equals(comp.getName())) {
                    balanceValueLabel = (JLabel) comp;
                    break;
                }
            }

            if (balanceValueLabel != null) {
                balanceValueLabel.setText(currencyFormat.format(currentBalance));
            }

            // Update My Rating label
            String ratingResponse = client.getMyRating(client.getCurrentUserId());
            String[] ratingParts = ratingResponse.split(",");

            if (ratingParts.length >= 4 && ratingParts[1].equals("SUCCESS")) {
                double rating = Double.parseDouble(ratingParts[2]);
                int ratingCount = Integer.parseInt(ratingParts[3]);

                // Find the rating value label by its name
                for (Component comp : accountManagementPanel.getComponents()) {
                    if (comp instanceof JLabel && "myRatingValueLabel".equals(comp.getName())) {
                        JLabel myRatingValueLabel = (JLabel) comp;

                        if (ratingCount == 0) {
                            myRatingValueLabel.setText("No ratings yet");
                        } else {
                            // Display the rating with stars
                            StringBuilder starsText = new StringBuilder(String.format("%.1f/5.0 (", rating));

                            // Add full stars
                            int fullStars = (int) Math.floor(rating);
                            for (int i = 0; i < fullStars; i++) {
                                starsText.append("★");
                            }

                            // Add half star if needed
                            if (rating - fullStars >= 0.5) {
                                starsText.append("½");
                            }

                            // Add empty stars
                            int emptyStars = 5 - fullStars - (rating - fullStars >= 0.5 ? 1 : 0);
                            for (int i = 0; i < emptyStars; i++) {
                                starsText.append("☆");
                            }

                            starsText.append(String.format(") from %d ratings", ratingCount));
                            myRatingValueLabel.setText(starsText.toString());
                        }
                        break;
                    }
                }
            }

            // Find the purchase history table
            JPanel purchaseHistoryPanel = (JPanel) accountPanel.getComponent(1);
            JScrollPane purchaseScrollPane = (JScrollPane) purchaseHistoryPanel.getComponent(0);
            JTable purchaseTable = (JTable) purchaseScrollPane.getViewport().getView();
            DefaultTableModel model = (DefaultTableModel) purchaseTable.getModel();

            // Clear the table
            while (model.getRowCount() > 0) {
                model.removeRow(0);
            }

            System.out.println("Refreshing purchase history for user: " + client.getCurrentUserId());

            // Request all items from the server
            String response = client.searchItems("", "", 100);
            String[] parts = response.split(",");

            if (parts.length >= 3 && parts[1].equals("SUCCESS")) {
                int count = Integer.parseInt(parts[2]);
                System.out.println("Found " + count + " items to check for purchase history");
                int purchaseCount = 0;

                for (int i = 0; i < count; i++) {
                    String itemId = parts[3 + 2*i];

                    // Get details for each item
                    String itemResponse = client.getItem(itemId);
                    String[] itemParts = itemResponse.split(",");

                    System.out.println("Checking item: " + itemId + ", response length: " + itemParts.length);

                    if (itemParts.length >= 9 && itemParts[1].equals("SUCCESS")) {
                        String sellerId = itemParts[3];
                        String itemTitle = itemParts[4];
                        String category = itemParts[6];
                        double price = Double.parseDouble(itemParts[7]);
                        boolean sold = Boolean.parseBoolean(itemParts[8]);

                        // Check for buyer ID - this is the critical part for purchase history
                        String buyerId = "";
                        if (itemParts.length > 9) {
                            buyerId = itemParts[9];
                            System.out.println("Item " + itemId + " has buyerId: " + buyerId);
                        }

                        // Add to purchase history if this user bought it
                        if (sold && buyerId.equals(client.getCurrentUserId())) {
                            // Get seller username instead of ID if possible
                            String sellerName = sellerId;
                            String usersResponse = client.getAllUsers();
                            String[] userParts = usersResponse.split(",");

                            if (userParts.length >= 3 && userParts[1].equals("SUCCESS")) {
                                int userCount = Integer.parseInt(userParts[2]);

                                for (int j = 0; j < userCount; j++) {
                                    String userId = userParts[3 + 2*j];
                                    String username = userParts[4 + 2*j];

                                    if (userId.equals(sellerId)) {
                                        sellerName = username;
                                        break;
                                    }
                                }
                            }

                            // Get seller rating
                            String sellerRatingResponse = client.getRating(sellerId);
                            String[] sellerRatingParts = sellerRatingResponse.split(",");
                            String ratingDisplay = "";

                            if (sellerRatingParts.length >= 3 && sellerRatingParts[1].equals("SUCCESS")) {
                                double sellerRating = Double.parseDouble(sellerRatingParts[2]);
                                if (sellerRating > 0) {
                                    ratingDisplay = String.format(" (%.1f★)", sellerRating);
                                }
                            }

                            sellerName += ratingDisplay;

                            model.addRow(new Object[] {
                                    itemId,
                                    itemTitle,
                                    category,
                                    currencyFormat.format(price),
                                    sellerName,
                                    "Rate"
                            });
                            purchaseCount++;
                            System.out.println("Added item to purchase history: " + itemTitle);
                        }
                    }
                }

                statusLabel.setText("Showing " + purchaseCount + " purchased items.");
            } else {
                statusLabel.setText("No purchase history found.");
            }
        }
    }



    /**
     * Refreshes all panels with latest data.
     */
    private void refreshAllPanels() {
        refreshBrowsePanel();
        refreshMyListingsPanel();
        refreshMessagesPanel();
        refreshAccountPanel();
    }

    /**
     * Updates the user information display.
     */
    private void updateUserInfo() {
        if (usernameLabel != null) {
            usernameLabel.setText("Welcome, " + currentUsername);
        }

        updateBalance();
    }

    private void updateBalance() {
        // Get latest balance
        String response = client.sendMessage("GET_BALANCE," + client.getCurrentUserId());
        String[] parts = response.split(",");

        if (parts.length >= 3 && parts[1].equals("SUCCESS")) {
            currentBalance = Double.parseDouble(parts[2]);

            if (balanceLabel != null) {
                balanceLabel.setText("Balance: " + currencyFormat.format(currentBalance));
            }

            // Update balance in account panel if it exists and is visible
            if (accountPanel != null) {
                // Find all components in the account panel
                JPanel topPanel = (JPanel) accountPanel.getComponent(0);
                JPanel accountManagementPanel = (JPanel) topPanel.getComponent(0);

                // Find the balance value label by its name
                for (Component comp : accountManagementPanel.getComponents()) {
                    if (comp instanceof JLabel && "balanceValueLabel".equals(comp.getName())) {
                        JLabel balanceValueLabel = (JLabel) comp;
                        balanceValueLabel.setText(currencyFormat.format(currentBalance));
                        break;
                    }
                }
            }
        }
    }

    /**
     * Handles the logout process.
     */
    private void handleLogout() {
        client.setCurrentUserId(null);
        cardLayout.show(contentPanel, "LOGIN");
        currentUsername = null;
        currentBalance = 0.0;
        statusLabel.setText("Logged out successfully");
    }

    /**
     * Adds window listener for proper disconnection when closing.
     */
    private void addWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client != null) {
                    client.disconnect();
                }
                System.exit(0);
            }
        });
    }

    /**
     * Class to represent conversation information.
     */
    private class ConversationInfo {
        private String userId;
        private String username;

        public ConversationInfo(String userId, String username) {
            this.userId = userId;
            this.username = username;
        }

        public String getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        @Override
        public String toString() {
            return username;
        }
    }

    /**
     * Custom renderer for conversation cells.
     */
    private class ConversationCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            if (value instanceof ConversationInfo) {
                ConversationInfo info = (ConversationInfo) value;
                label.setText(info.getUsername());
            }

            return label;
        }
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
