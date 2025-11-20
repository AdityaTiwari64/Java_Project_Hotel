import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;

public class HotelReservationSystem extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "aditya";
    private Connection connection;
    private JTabbedPane tabbedPane;
    private DefaultTableModel reservationTableModel;
    private JTable reservationTable;
    // Room management
    private static final int TOTAL_ROOMS = 50;
    private Map<Integer, JButton> roomButtons = new HashMap<>();
    private int selectedRoomNumber = -1;
    // Booking Tab Components
    private JTextField guestNameField, contactField;
    private JTextArea addressArea;
    private JComboBox<String> roomTypeCombo;
    private JLabel selectedRoomLabel, priceLabel;
    private JButton reserveButton, clearButton;
    // Check-in and Check-out date components (using JSpinner)
    private JSpinner checkInDateSpinner, checkOutDateSpinner;
    // View Tab Components
    private JButton refreshButton, exportButton;
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    // Update/Delete Tab Components
    private JTextField searchIdField;
    private JTextField updateGuestField, updateContactField;
    private JTextArea updateAddressArea;
    private JLabel currentRoomLabel;
    private JButton searchButton, updateButton, deleteButton, clearManageButton;
    // Update check-in and check-out date components (using JSpinner)
    private JSpinner updateCheckInDateSpinner, updateCheckOutDateSpinner;
    // Color scheme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color DARK_BG = new Color(44, 62, 80);
    private final Color LIGHT_BG = new Color(236, 240, 241);
    private final Color OCCUPIED_ROOM = new Color(231, 76, 60);
    private final Color AVAILABLE_ROOM = new Color(46, 204, 113);

    public HotelReservationSystem() {
        setTitle("Hotel Management System - Premium Edition");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Connect to database
        connectDatabase();
        createTables();
        // Set modern Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_BG);
        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(LIGHT_BG);
        tabbedPane.addTab("New Reservation", createBookingPanel());
        tabbedPane.addTab("All Reservations", createViewPanel());
        tabbedPane.addTab("Room Status", createRoomStatusPanel());
        tabbedPane.addTab("Manage Booking", createManagePanel());
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
        
        // Add tab change listener to update room status when switching tabs
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 2) { // Room Status tab index
                updateRoomStatusDashboard();
            }
        });
    }

    private void connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connected successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed:\n" + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void createTables() {
        try {
            Statement stmt = connection.createStatement();
            // Check if address column exists, if not add it
            try {
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet columns = metaData.getColumns(null, null, "reservations", "address");
                if (!columns.next()) {
                    String alterTable = "ALTER TABLE reservations ADD COLUMN address TEXT AFTER contact_number";
                    stmt.execute(alterTable);
                    System.out.println("Added 'address' column to reservations table");
                }
            } catch (SQLException e) {
                // Table might not exist, will be created below
            }
            // Reservations table with enhanced fields
            String createReservationsTable = "CREATE TABLE IF NOT EXISTS reservations (" +
                    "reservation_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "guest_name VARCHAR(100) NOT NULL, " +
                    "room_number INT NOT NULL, " +
                    "contact_number VARCHAR(20) NOT NULL, " +
                    "address TEXT, " +
                    "room_type VARCHAR(50), " +
                    "price DECIMAL(10,2), " +
                    "reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "check_in_date DATE, " +
                    "check_out_date DATE, " +
                    "status VARCHAR(20) DEFAULT 'Active'" +
                    ")";
            stmt.execute(createReservationsTable);
            System.out.println("Tables verified/created successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating/updating tables:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DARK_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        // Title
        JLabel titleLabel = new JLabel("GRAND HOTEL MANAGEMENT SYSTEM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        // Subtitle
        JLabel subtitleLabel = new JLabel("Premium Reservation & Management Solution");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(189, 195, 199));
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(DARK_BG);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBackground(DARK_BG);
        JLabel statusLabel = new JLabel("System Active");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(SUCCESS_COLOR);
        statusPanel.add(statusLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(statusPanel, BorderLayout.EAST);
        return headerPanel;
    }

    // Helper method to create a styled date spinner
    private JSpinner createDateSpinner() {
        JSpinner spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "yyyy-MM-dd");
        spinner.setEditor(editor);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        spinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        spinner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return spinner;
    }

    private JPanel createBookingPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(LIGHT_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // Left panel - Guest Information
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        JLabel guestInfoTitle = new JLabel("Guest Information");
        guestInfoTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        guestInfoTitle.setForeground(PRIMARY_COLOR);
        guestInfoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(guestInfoTitle);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        // Guest Name
        leftPanel.add(createStyledLabel("Guest Name:"));
        guestNameField = createStyledTextField();
        leftPanel.add(guestNameField);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        // Contact Number
        leftPanel.add(createStyledLabel("Contact Number:"));
        contactField = createStyledTextField();
        leftPanel.add(contactField);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        // Address
        leftPanel.add(createStyledLabel("Address:"));
        addressArea = new JTextArea(4, 20);
        addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(addressScroll);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        // Room Type
        leftPanel.add(createStyledLabel("Room Type:"));
        String[] roomTypes = {"Standard - Rs.2000/night", "Deluxe - Rs.3500/night", "Suite - Rs.5000/night", "Presidential - Rs.10000/night"};
        roomTypeCombo = new JComboBox<>(roomTypes);
        roomTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roomTypeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        roomTypeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        roomTypeCombo.addActionListener(e -> updatePrice());
        leftPanel.add(roomTypeCombo);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Check-in Date
        leftPanel.add(createStyledLabel("Check-in Date:"));
        checkInDateSpinner = createDateSpinner();
        checkInDateSpinner.setValue(new Date()); // Set default to today
        leftPanel.add(checkInDateSpinner);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Check-out Date
        leftPanel.add(createStyledLabel("Check-out Date:"));
        checkOutDateSpinner = createDateSpinner();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        checkOutDateSpinner.setValue(tomorrow.getTime()); // Set default to tomorrow
        leftPanel.add(checkOutDateSpinner);
        leftPanel.add(Box.createVerticalGlue());
        
        // Right panel - Room Selection
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        JLabel roomSelectionTitle = new JLabel("Select Room");
        roomSelectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        roomSelectionTitle.setForeground(PRIMARY_COLOR);
        rightPanel.add(roomSelectionTitle, BorderLayout.NORTH);
        // Room grid
        JPanel roomGridPanel = new JPanel(new GridLayout(5, 10, 8, 8));
        roomGridPanel.setBackground(Color.WHITE);
        roomGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        for (int i = 1; i <= TOTAL_ROOMS; i++) {
            JButton roomBtn = new JButton(String.valueOf(i));
            roomBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            roomBtn.setFocusPainted(false);
            roomBtn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            roomBtn.setPreferredSize(new Dimension(60, 40));
            final int roomNum = i;
            roomBtn.addActionListener(e -> selectRoom(roomNum));
            roomButtons.put(i, roomBtn);
            roomGridPanel.add(roomBtn);
        }
        updateRoomAvailability();
        JScrollPane roomScrollPane = new JScrollPane(roomGridPanel);
        roomScrollPane.setBorder(null);
        rightPanel.add(roomScrollPane, BorderLayout.CENTER);
        // Legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        legendPanel.setBackground(Color.WHITE);
        JLabel availableLabel = new JLabel("Available");
        availableLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        availableLabel.setForeground(AVAILABLE_ROOM);
        JLabel occupiedLabel = new JLabel("Occupied");
        occupiedLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        occupiedLabel.setForeground(OCCUPIED_ROOM);
        JLabel selectedLabel = new JLabel("Selected");
        selectedLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        selectedLabel.setForeground(WARNING_COLOR);
        legendPanel.add(availableLabel);
        legendPanel.add(occupiedLabel);
        legendPanel.add(selectedLabel);
        rightPanel.add(legendPanel, BorderLayout.SOUTH);
        // Bottom panel - Booking Summary
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 5));
        summaryPanel.setBackground(Color.WHITE);
        selectedRoomLabel = new JLabel("Selected Room: None");
        selectedRoomLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        selectedRoomLabel.setForeground(PRIMARY_COLOR);
        priceLabel = new JLabel("Price: Rs.0");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setForeground(SUCCESS_COLOR);
        summaryPanel.add(selectedRoomLabel);
        summaryPanel.add(priceLabel);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        clearButton = createStyledButton("Clear", DANGER_COLOR);
        clearButton.addActionListener(e -> clearBookingForm());
        reserveButton = createStyledButton("Confirm Reservation", SUCCESS_COLOR);
        reserveButton.addActionListener(e -> reserveRoom());
        buttonPanel.add(clearButton);
        buttonPanel.add(reserveButton);
        bottomPanel.add(summaryPanel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        // Layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.35);
        splitPane.setDividerSize(5);
        splitPane.setBackground(LIGHT_BG);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    private JPanel createViewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        // Top Panel with search
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        JLabel titleLabel = new JLabel("All Reservations");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_COLOR);
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(Color.WHITE);
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        String[] searchTypes = {"Guest Name", "Room Number", "Contact"};
        searchTypeCombo = new JComboBox<>(searchTypes);
        searchTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JButton searchBtn = createStyledButton("Search", PRIMARY_COLOR);
        searchBtn.addActionListener(e -> searchReservations());
        refreshButton = createStyledButton("Refresh", SECONDARY_COLOR);
        refreshButton.addActionListener(e -> loadReservations());
        searchPanel.add(searchLabel);
        searchPanel.add(searchTypeCombo);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(refreshButton);
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        // Table
        String[] columnNames = {"ID", "Guest Name", "Room", "Contact", "Address", "Type", "Price", "Check-in", "Check-out", "Date", "Status"};
        reservationTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reservationTable = new JTable(reservationTableModel);
        reservationTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        reservationTable.setRowHeight(30);
        reservationTable.setSelectionBackground(new Color(52, 152, 219));
        reservationTable.setSelectionForeground(Color.WHITE);
        reservationTable.setGridColor(new Color(189, 195, 199));
        // Header styling
        reservationTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        reservationTable.getTableHeader().setBackground(PRIMARY_COLOR);
        reservationTable.getTableHeader().setForeground(Color.WHITE);
        reservationTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        // Center align columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < reservationTable.getColumnCount(); i++) {
            reservationTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        JScrollPane scrollPane = new JScrollPane(reservationTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        // Load initial data
        loadReservations();
        return panel;
    }

    private JPanel createRoomStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(DARK_BG);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        JLabel titleLabel = new JLabel("ROOM STATUS DASHBOARD");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Add refresh button
        JButton refreshRoomStatusBtn = createStyledButton("Refresh Status", SECONDARY_COLOR);
        refreshRoomStatusBtn.addActionListener(e -> updateRoomStatusDashboard());
        titlePanel.add(refreshRoomStatusBtn);
        
        // Room grid with details
        JPanel roomPanel = new JPanel(new GridLayout(5, 10, 10, 10));
        roomPanel.setBackground(LIGHT_BG);
        roomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (int i = 1; i <= TOTAL_ROOMS; i++) {
            JPanel roomCard = createRoomCard(i);
            roomPanel.add(roomCard);
        }
        JScrollPane scrollPane = new JScrollPane(roomPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        // Stats panel
        JPanel statsPanel = createStatsPanel();
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(statsPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createRoomCard(int roomNumber) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
        card.setPreferredSize(new Dimension(100, 80));
        boolean isOccupied = isRoomOccupied(roomNumber);
        JLabel roomNumLabel = new JLabel(String.valueOf(roomNumber), SwingConstants.CENTER);
        roomNumLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        roomNumLabel.setForeground(isOccupied ? OCCUPIED_ROOM : AVAILABLE_ROOM);
        JLabel statusLabel = new JLabel(isOccupied ? "OCCUPIED" : "AVAILABLE", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(isOccupied ? OCCUPIED_ROOM : AVAILABLE_ROOM);
        card.add(roomNumLabel, BorderLayout.CENTER);
        card.add(statusLabel, BorderLayout.SOUTH);
        // Click to view details
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showRoomDetails(roomNumber);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 3));
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 2));
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        return card;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(LIGHT_BG);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        int[] stats = getRoomStatistics();
        statsPanel.add(createStatCard("Total Rooms", String.valueOf(TOTAL_ROOMS), PRIMARY_COLOR, "HOTEL"));
        statsPanel.add(createStatCard("Available", String.valueOf(stats[0]), SUCCESS_COLOR, "AVAILABLE"));
        statsPanel.add(createStatCard("Occupied", String.valueOf(stats[1]), DANGER_COLOR, "OCCUPIED"));
        statsPanel.add(createStatCard("Occupancy Rate", stats[2] + "%", WARNING_COLOR, "STATS"));
        return statsPanel;
    }

    private JPanel createStatCard(String title, String value, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 3),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        iconLabel.setForeground(color);
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(127, 140, 141));
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(color);
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(titleLabel);
        textPanel.add(valueLabel);
        card.add(iconLabel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createManagePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        JLabel searchTitle = new JLabel("Find Reservation");
        searchTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        searchTitle.setForeground(PRIMARY_COLOR);
        searchTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchPanel.add(searchTitle);
        searchPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        searchInputPanel.setBackground(Color.WHITE);
        searchInputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel searchIdLabel = new JLabel("Reservation ID:");
        searchIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchIdField = new JTextField(15);
        searchIdField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchButton = createStyledButton("Search", PRIMARY_COLOR);
        searchButton.addActionListener(e -> searchReservationForManage());
        searchInputPanel.add(searchIdLabel);
        searchInputPanel.add(searchIdField);
        searchInputPanel.add(searchButton);
        searchPanel.add(searchInputPanel);
        // Update Panel
        JPanel updatePanel = new JPanel();
        updatePanel.setLayout(new BoxLayout(updatePanel, BoxLayout.Y_AXIS));
        updatePanel.setBackground(Color.WHITE);
        updatePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        JLabel updateTitle = new JLabel("Update Details");
        updateTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        updateTitle.setForeground(PRIMARY_COLOR);
        updateTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        updatePanel.add(updateTitle);
        updatePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        // Current Room Display
        currentRoomLabel = new JLabel("Current Room: --");
        currentRoomLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        currentRoomLabel.setForeground(WARNING_COLOR);
        currentRoomLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        updatePanel.add(currentRoomLabel);
        updatePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        // Update fields
        updatePanel.add(createStyledLabel("Guest Name:"));
        updateGuestField = createStyledTextField();
        updatePanel.add(updateGuestField);
        updatePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        updatePanel.add(createStyledLabel("Contact Number:"));
        updateContactField = createStyledTextField();
        updatePanel.add(updateContactField);
        updatePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        updatePanel.add(createStyledLabel("Address:"));
        updateAddressArea = new JTextArea(3, 20);
        updateAddressArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        updateAddressArea.setLineWrap(true);
        updateAddressArea.setWrapStyleWord(true);
        updateAddressArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane updateAddressScroll = new JScrollPane(updateAddressArea);
        updateAddressScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        updatePanel.add(updateAddressScroll);
        updatePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Update Check-in Date
        updatePanel.add(createStyledLabel("Check-in Date:"));
        updateCheckInDateSpinner = createDateSpinner();
        updatePanel.add(updateCheckInDateSpinner);
        updatePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Update Check-out Date
        updatePanel.add(createStyledLabel("Check-out Date:"));
        updateCheckOutDateSpinner = createDateSpinner();
        updatePanel.add(updateCheckOutDateSpinner);
        updatePanel.add(Box.createVerticalGlue());
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        updateButton = createStyledButton("Update", WARNING_COLOR);
        updateButton.addActionListener(e -> updateReservation());
        deleteButton = createStyledButton("Delete", DANGER_COLOR);
        deleteButton.addActionListener(e -> deleteReservation());
        clearManageButton = createStyledButton("Clear", SECONDARY_COLOR);
        clearManageButton.addActionListener(e -> clearManageForm());
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearManageButton);
        updatePanel.add(buttonPanel);
        // Layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, searchPanel, updatePanel);
        splitPane.setResizeWeight(0.3);
        splitPane.setDividerSize(5);
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(LIGHT_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // Title
        JLabel titleLabel = new JLabel("Dashboard & Analytics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        // Statistics Panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        statsPanel.setBackground(LIGHT_BG);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        int[] stats = getRoomStatistics();
        int totalBookings = getTotalBookings();
        double revenue = getTotalRevenue();
        statsPanel.add(createDashboardCard("Total Rooms", String.valueOf(TOTAL_ROOMS), "HOTEL", PRIMARY_COLOR));
        statsPanel.add(createDashboardCard("Available Rooms", String.valueOf(stats[0]), "AVAILABLE", SUCCESS_COLOR));
        statsPanel.add(createDashboardCard("Occupied Rooms", String.valueOf(stats[1]), "OCCUPIED", DANGER_COLOR));
        statsPanel.add(createDashboardCard("Total Bookings", String.valueOf(totalBookings), "LIST", SECONDARY_COLOR));
        statsPanel.add(createDashboardCard("Occupancy Rate", stats[2] + "%", "STATS", WARNING_COLOR));
        statsPanel.add(createDashboardCard("Total Revenue", "Rs." + String.format("%.2f", revenue), "MONEY", SUCCESS_COLOR));
        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        JLabel infoTitle = new JLabel("System Information");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        infoTitle.setForeground(PRIMARY_COLOR);
        infoTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel dbInfo = new JLabel("Database: hotel_db (MySQL 8.0)");
        dbInfo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dbInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel versionInfo = new JLabel("Version: 2.0 Premium Edition");
        versionInfo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        versionInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel authorInfo = new JLabel("Author: Aditya Anil Tiwari");
        authorInfo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        authorInfo.setForeground(PRIMARY_COLOR);
        authorInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel features = new JLabel("<html><div style='text-align: center;'>" +
                "<br>Real-time room availability<br>" +
                "Advanced booking management<br>" +
                "Interactive room selection<br>" +
                "Comprehensive analytics<br>" +
                "Modern & intuitive interface</div></html>");
        features.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        features.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(infoTitle);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        infoPanel.add(dbInfo);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(versionInfo);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        infoPanel.add(authorInfo);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        infoPanel.add(features);
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createDashboardCard(String title, String value, String icon, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 3),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        iconLabel.setForeground(color);
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(127, 140, 141));
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(color);
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        textPanel.add(valueLabel);
        card.add(iconLabel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    // Helper methods for creating styled components
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(DARK_BG);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    // Business Logic Methods
    private void selectRoom(int roomNum) {
        if (isRoomOccupied(roomNum)) {
            JOptionPane.showMessageDialog(this, "Room " + roomNum + " is already occupied!", "Room Unavailable", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Reset previous selection
        if (selectedRoomNumber != -1) {
            JButton prevBtn = roomButtons.get(selectedRoomNumber);
            prevBtn.setBackground(AVAILABLE_ROOM);
            prevBtn.setForeground(Color.WHITE);
        }
        // Set new selection
        selectedRoomNumber = roomNum;
        JButton btn = roomButtons.get(roomNum);
        btn.setBackground(WARNING_COLOR);
        btn.setForeground(Color.WHITE);
        selectedRoomLabel.setText("Selected Room: " + roomNum);
        updatePrice();
    }

    private void updatePrice() {
        if (selectedRoomNumber == -1) {
            priceLabel.setText("Price: Rs.0");
            return;
        }
        String roomType = (String) roomTypeCombo.getSelectedItem();
        double price = 0;
        if (roomType.contains("Standard")) price = 2000;
        else if (roomType.contains("Deluxe")) price = 3500;
        else if (roomType.contains("Suite")) price = 5000;
        else if (roomType.contains("Presidential")) price = 10000;
        
        // Calculate total price based on number of nights
        Date checkIn = (Date) checkInDateSpinner.getValue();
        Date checkOut = (Date) checkOutDateSpinner.getValue();
        
        if (checkIn != null && checkOut != null) {
            long diff = checkOut.getTime() - checkIn.getTime();
            int nights = (int) (diff / (1000 * 60 * 60 * 24));
            if (nights > 0) {
                price *= nights;
            }
        }
        
        priceLabel.setText("Price: Rs." + String.format("%.2f", price));
    }

    private void updateRoomAvailability() {
        try {
            String sql = "SELECT room_number FROM reservations WHERE status = 'Active'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            Set<Integer> occupiedRooms = new HashSet<>();
            while (rs.next()) {
                occupiedRooms.add(rs.getInt("room_number"));
            }
            for (Map.Entry<Integer, JButton> entry : roomButtons.entrySet()) {
                JButton btn = entry.getValue();
                int roomNum = entry.getKey();
                if (occupiedRooms.contains(roomNum)) {
                    btn.setBackground(OCCUPIED_ROOM);
                    btn.setForeground(Color.WHITE);
                    btn.setEnabled(true);
                } else {
                    btn.setBackground(AVAILABLE_ROOM);
                    btn.setForeground(Color.WHITE);
                    btn.setEnabled(true);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isRoomOccupied(int roomNumber) {
        try {
            String sql = "SELECT COUNT(*) FROM reservations WHERE room_number = ? AND status = 'Active'";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, roomNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showRoomDetails(int roomNumber) {
        try {
            String sql = "SELECT * FROM reservations WHERE room_number = ? AND status = 'Active'";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, roomNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String details = "Room Number: " + roomNumber + "\n" +
                        "Guest Name: " + rs.getString("guest_name") + "\n" +
                        "Contact: " + rs.getString("contact_number") + "\n" +
                        "Room Type: " + rs.getString("room_type") + "\n" +
                        "Price: Rs." + rs.getDouble("price") + "\n" +
                        "Check-in: " + rs.getDate("check_in_date") + "\n" +
                        "Check-out: " + rs.getDate("check_out_date") + "\n" +
                        "Reservation ID: " + rs.getInt("reservation_id");
                JOptionPane.showMessageDialog(this, details, "Room " + roomNumber + " Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Room " + roomNumber + " is available!", "Room Status", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int[] getRoomStatistics() {
        int available = 0;
        int occupied = 0;
        try {
            String sql = "SELECT COUNT(*) FROM reservations WHERE status = 'Active'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                occupied = rs.getInt(1);
            }
            available = TOTAL_ROOMS - occupied;
            int occupancyRate = (int) ((occupied * 100.0) / TOTAL_ROOMS);
            return new int[]{available, occupied, occupancyRate};
        } catch (SQLException e) {
            e.printStackTrace();
            return new int[]{TOTAL_ROOMS, 0, 0};
        }
    }

    private int getTotalBookings() {
        try {
            String sql = "SELECT COUNT(*) FROM reservations";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private double getTotalRevenue() {
        try {
            String sql = "SELECT SUM(price) FROM reservations WHERE status = 'Active'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private void reserveRoom() {
        String guestName = guestNameField.getText().trim();
        String contact = contactField.getText().trim();
        String address = addressArea.getText().trim();
        
        // Validate dates
        Date checkIn = (Date) checkInDateSpinner.getValue();
        Date checkOut = (Date) checkOutDateSpinner.getValue();
        
        if (guestName.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in Guest Name and Contact Number!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (checkIn == null || checkOut == null) {
            JOptionPane.showMessageDialog(this, "Please select check-in and check-out dates!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (checkIn.after(checkOut)) {
            JOptionPane.showMessageDialog(this, "Check-in date must be before check-out date!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedRoomNumber == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            String roomType = (String) roomTypeCombo.getSelectedItem();
            String type = roomType.split(" - ")[0];
            double price = 0;
            if (roomType.contains("Standard")) price = 2000;
            else if (roomType.contains("Deluxe")) price = 3500;
            else if (roomType.contains("Suite")) price = 5000;
            else if (roomType.contains("Presidential")) price = 10000;
            
            // Calculate total price based on number of nights
            long diff = checkOut.getTime() - checkIn.getTime();
            int nights = (int) (diff / (1000 * 60 * 60 * 24));
            if (nights > 0) {
                price *= nights;
            }
            
            // Convert dates to SQL Date
            java.sql.Date sqlCheckIn = new java.sql.Date(checkIn.getTime());
            java.sql.Date sqlCheckOut = new java.sql.Date(checkOut.getTime());
            
            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number, address, room_type, price, check_in_date, check_out_date, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Active')";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, guestName);
            pstmt.setInt(2, selectedRoomNumber);
            pstmt.setString(3, contact);
            pstmt.setString(4, address);
            pstmt.setString(5, type);
            pstmt.setDouble(6, price);
            pstmt.setDate(7, sqlCheckIn);
            pstmt.setDate(8, sqlCheckOut);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Reservation Successful!\n\n" +
                        "Guest: " + guestName + "\n" +
                        "Room: " + selectedRoomNumber + "\n" +
                        "Type: " + type + "\n" +
                        "Check-in: " + sqlCheckIn + "\n" +
                        "Check-out: " + sqlCheckOut + "\n" +
                        "Price: Rs." + price, "Success", JOptionPane.INFORMATION_MESSAGE);
                clearBookingForm();
                updateRoomAvailability();
                loadReservations();
                if (tabbedPane.getSelectedIndex() == 2) {
                    updateRoomStatusDashboard();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearBookingForm() {
        guestNameField.setText("");
        contactField.setText("");
        addressArea.setText("");
        roomTypeCombo.setSelectedIndex(0);
        
        // Reset dates to defaults
        checkInDateSpinner.setValue(new Date());
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        checkOutDateSpinner.setValue(tomorrow.getTime());
        
        if (selectedRoomNumber != -1) {
            JButton btn = roomButtons.get(selectedRoomNumber);
            btn.setBackground(AVAILABLE_ROOM);
            btn.setForeground(Color.WHITE);
        }
        selectedRoomNumber = -1;
        selectedRoomLabel.setText("Selected Room: None");
        priceLabel.setText("Price: Rs.0");
    }

    private void loadReservations() {
        reservationTableModel.setRowCount(0);
        try {
            String sql = "SELECT * FROM reservations ORDER BY reservation_id DESC";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("reservation_id"),
                        rs.getString("guest_name"),
                        rs.getInt("room_number"),
                        rs.getString("contact_number"),
                        rs.getString("address"),
                        rs.getString("room_type"),
                        "Rs." + rs.getDouble("price"),
                        rs.getDate("check_in_date") != null ? sdf.format(rs.getDate("check_in_date")) : "N/A",
                        rs.getDate("check_out_date") != null ? sdf.format(rs.getDate("check_out_date")) : "N/A",
                        sdf.format(rs.getTimestamp("reservation_date")),
                        rs.getString("status")
                };
                reservationTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading reservations: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchReservations() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            loadReservations();
            return;
        }
        reservationTableModel.setRowCount(0);
        try {
            String searchType = (String) searchTypeCombo.getSelectedItem();
            String sql = "";
            if (searchType.equals("Guest Name")) {
                sql = "SELECT * FROM reservations WHERE guest_name LIKE ? ORDER BY reservation_id DESC";
            } else if (searchType.equals("Room Number")) {
                sql = "SELECT * FROM reservations WHERE room_number = ? ORDER BY reservation_id DESC";
            } else if (searchType.equals("Contact")) {
                sql = "SELECT * FROM reservations WHERE contact_number LIKE ? ORDER BY reservation_id DESC";
            }
            PreparedStatement pstmt = connection.prepareStatement(sql);
            if (searchType.equals("Room Number")) {
                pstmt.setInt(1, Integer.parseInt(searchText));
            } else {
                pstmt.setString(1, "%" + searchText + "%");
            }
            ResultSet rs = pstmt.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("reservation_id"),
                        rs.getString("guest_name"),
                        rs.getInt("room_number"),
                        rs.getString("contact_number"),
                        rs.getString("address"),
                        rs.getString("room_type"),
                        "Rs." + rs.getDouble("price"),
                        rs.getDate("check_in_date") != null ? sdf.format(rs.getDate("check_in_date")) : "N/A",
                        rs.getDate("check_out_date") != null ? sdf.format(rs.getDate("check_out_date")) : "N/A",
                        sdf.format(rs.getTimestamp("reservation_date")),
                        rs.getString("status")
                };
                reservationTableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchReservationForManage() {
        String idText = searchIdField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Reservation ID!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int id = Integer.parseInt(idText);
            String sql = "SELECT * FROM reservations WHERE reservation_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                updateGuestField.setText(rs.getString("guest_name"));
                updateContactField.setText(rs.getString("contact_number"));
                updateAddressArea.setText(rs.getString("address"));
                currentRoomLabel.setText("Current Room: " + rs.getInt("room_number"));
                
                // Set dates in the spinners
                if (rs.getDate("check_in_date") != null) {
                    updateCheckInDateSpinner.setValue(rs.getDate("check_in_date"));
                } else {
                    updateCheckInDateSpinner.setValue(new Date());
                }
                if (rs.getDate("check_out_date") != null) {
                    updateCheckOutDateSpinner.setValue(rs.getDate("check_out_date"));
                } else {
                    Calendar tomorrow = Calendar.getInstance();
                    tomorrow.add(Calendar.DAY_OF_MONTH, 1);
                    updateCheckOutDateSpinner.setValue(tomorrow.getTime());
                }
                
                JOptionPane.showMessageDialog(this, "Reservation found!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No reservation found with ID: " + id, "Not Found", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Reservation ID must be a number!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateReservation() {
        String idText = searchIdField.getText().trim();
        String guestName = updateGuestField.getText().trim();
        String contact = updateContactField.getText().trim();
        String address = updateAddressArea.getText().trim();
        
        // Get dates from spinners
        Date checkIn = (Date) updateCheckInDateSpinner.getValue();
        Date checkOut = (Date) updateCheckOutDateSpinner.getValue();
        
        if (idText.isEmpty() || guestName.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (checkIn == null || checkOut == null) {
            JOptionPane.showMessageDialog(this, "Please select check-in and check-out dates!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (checkIn.after(checkOut)) {
            JOptionPane.showMessageDialog(this, "Check-in date must be before check-out date!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to update this reservation?", "Confirm Update", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        
        try {
            int id = Integer.parseInt(idText);
            
            // Convert dates to SQL Date
            java.sql.Date sqlCheckIn = new java.sql.Date(checkIn.getTime());
            java.sql.Date sqlCheckOut = new java.sql.Date(checkOut.getTime());
            
            String sql = "UPDATE reservations SET guest_name = ?, contact_number = ?, address = ?, check_in_date = ?, check_out_date = ? WHERE reservation_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, guestName);
            pstmt.setString(2, contact);
            pstmt.setString(3, address);
            pstmt.setDate(4, sqlCheckIn);
            pstmt.setDate(5, sqlCheckOut);
            pstmt.setInt(6, id);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Reservation updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearManageForm();
                loadReservations();
            } else {
                JOptionPane.showMessageDialog(this, "No reservation found with this ID!", "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteReservation() {
        String idText = searchIdField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Reservation ID!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this reservation?\nThis action cannot be undone!", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        
        try {
            int id = Integer.parseInt(idText);
            String sql = "DELETE FROM reservations WHERE reservation_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Reservation deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearManageForm();
                updateRoomAvailability();
                loadReservations();
                if (tabbedPane.getSelectedIndex() == 2) {
                    updateRoomStatusDashboard();
                }
            } else {
                JOptionPane.showMessageDialog(this, "No reservation found with this ID!", "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearManageForm() {
        searchIdField.setText("");
        updateGuestField.setText("");
        updateContactField.setText("");
        updateAddressArea.setText("");
        currentRoomLabel.setText("Current Room: --");
        
        // Reset spinners to defaults
        updateCheckInDateSpinner.setValue(new Date());
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        updateCheckOutDateSpinner.setValue(tomorrow.getTime());
    }
    
    // Method to update the room status dashboard
    private void updateRoomStatusDashboard() {
        Component roomStatusPanel = tabbedPane.getComponentAt(2);
        if (roomStatusPanel instanceof JPanel) {
            JPanel panel = (JPanel) roomStatusPanel;
            JScrollPane scrollPane = (JScrollPane) panel.getComponent(1); // Index 1 is the scroll pane
            JPanel roomGridPanel = (JPanel) scrollPane.getViewport().getView();
            roomGridPanel.removeAll();
            for (int i = 1; i <= TOTAL_ROOMS; i++) {
                JPanel roomCard = createRoomCard(i);
                roomGridPanel.add(roomCard);
            }
            roomGridPanel.revalidate();
            roomGridPanel.repaint();
            
            JPanel statsPanel = (JPanel) panel.getComponent(2); // Index 2 is the stats panel
            statsPanel.removeAll();
            int[] stats = getRoomStatistics();
            statsPanel.add(createStatCard("Total Rooms", String.valueOf(TOTAL_ROOMS), PRIMARY_COLOR, "HOTEL"));
            statsPanel.add(createStatCard("Available", String.valueOf(stats[0]), SUCCESS_COLOR, "AVAILABLE"));
            statsPanel.add(createStatCard("Occupied", String.valueOf(stats[1]), DANGER_COLOR, "OCCUPIED"));
            statsPanel.add(createStatCard("Occupancy Rate", stats[2] + "%", WARNING_COLOR, "STATS"));
            statsPanel.revalidate();
            statsPanel.repaint();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new HotelReservationSystem();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to start application:\n" + e.getMessage(), "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
