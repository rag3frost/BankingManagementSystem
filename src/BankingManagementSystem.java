import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
import java.awt.geom.RoundRectangle2D;

public class BankingManagementSystem extends JFrame {
    private Connection conn;
    private JTextField accountField, amountField;
    private JPasswordField pinField;
    private JLabel balanceLabel;
    private JPanel sidebarPanel;
    private JPanel mainPanel;
    private JPanel cardsPanel;
    private JPanel paymentsPanel;
    private JPanel transferHistoryPanel;
    private Color primaryColor = new Color(67, 97, 238);
    private Color accentColor = new Color(171, 71, 188);
    private Color backgroundColor = new Color(245, 245, 250);
    private Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font headerFont = new Font("Segoe UI", Font.BOLD, 24);
    
    public BankingManagementSystem() {
        // Database connection
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/bank_db", "root", "1234");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
        
        // UI Setup
        setTitle("Banking Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(backgroundColor);
        
        // Main layout
        setLayout(new BorderLayout(20, 20));
        ((JPanel)getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));
        
        createSidebar();
        createMainContent();
    }
    
    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
        sidebarPanel.setBackground(primaryColor);
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(new EmptyBorder(20, 15, 20, 15));
        
        // Add logo
        JLabel logoLabel = new JLabel("MyBank");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(logoLabel);
        sidebarPanel.add(Box.createVerticalStrut(40));
        
        // Add menu items
        String[] menuItems = {
            "Dashboard", "Transfer", "History", 
            "Cards", "Settings"
        };
        
        for (int i = 0; i < menuItems.length; i++) {
            addMenuItem(menuItems[i], i == 0);
            sidebarPanel.add(Box.createVerticalStrut(10));
        }
        
        add(sidebarPanel, BorderLayout.WEST);
    }
    
    private void addMenuItem(String text, boolean selected) {
        JPanel menuItem = new RoundedPanel(15);
        menuItem.setLayout(new BorderLayout());
        menuItem.setMaximumSize(new Dimension(220, 45));
        menuItem.setBorder(new EmptyBorder(8, 15, 8, 15));
        
        if (selected) {
            menuItem.setBackground(new Color(255, 255, 255, 40));
        } else {
            menuItem.setBackground(new Color(0, 0, 0, 0));
        }
        
        JLabel label = new JLabel(text);
        label.setFont(mainFont);
        label.setForeground(Color.WHITE);
        menuItem.add(label);
        
        menuItem.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!selected) {
                    menuItem.setBackground(new Color(255, 255, 255, 20));
                }
            }
            
            public void mouseExited(MouseEvent e) {
                if (!selected) {
                    menuItem.setBackground(new Color(0, 0, 0, 0));
                }
            }
            
            public void mouseClicked(MouseEvent e) {
                showPanel(text);
            }
        });
        
        sidebarPanel.add(menuItem);
    }
    
    private void createMainContent() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBackground(backgroundColor);
        
        // Welcome header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(backgroundColor);
        JLabel welcomeLabel = new JLabel("Welcome back!");
        welcomeLabel.setFont(headerFont);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        // Main content area
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(backgroundColor);
        
        // Add account section
        createAccountSection(contentPanel);
        
        // Add balance section
        createBalanceSection(contentPanel);
        
        // Add transaction section
        createTransactionSection(contentPanel);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void createAccountSection(JPanel parent) {
        JPanel panel = new RoundedPanel(20);
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 10);
        
        // Account input
        accountField = new JTextField(15);
        addFormField(panel, "Account ID:", accountField, gbc);
        
        // PIN input
        pinField = new JPasswordField(15);
        gbc.gridy++;
        addFormField(panel, "PIN:", pinField, gbc);
        
        // Create Account button
        JButton createButton = createStyledButton("Create Account");
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(createButton, gbc);
        
        parent.add(panel);
        parent.add(Box.createVerticalStrut(20));
    }
    
    private void createBalanceSection(JPanel parent) {
        JPanel panel = new RoundedPanel(20);
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBackground(accentColor);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        balanceLabel = new JLabel("Balance: $0.00");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        balanceLabel.setForeground(Color.WHITE);
        panel.add(balanceLabel, BorderLayout.NORTH);
        
        JButton checkButton = createStyledButton("Check Balance");
        panel.add(checkButton, BorderLayout.CENTER);
        
        parent.add(panel);
        parent.add(Box.createVerticalStrut(20));
    }
    
    private void createTransactionSection(JPanel parent) {
        JPanel panel = new RoundedPanel(20);
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 10, 10);
        
        // Amount input
        amountField = new JTextField(15);
        addFormField(panel, "Amount:", amountField, gbc);
        
        // Transaction buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton depositButton = createStyledButton("Deposit");
        JButton withdrawButton = createStyledButton("Withdraw");
        
        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);
        
        parent.add(panel);
    }
    
    private void addFormField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        label.setFont(mainFont);
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
        
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        
        button.setFont(mainFont);
        button.setForeground(Color.WHITE);
        button.setBackground(primaryColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> {
            try {
                switch(text) {
                    case "Create Account":
                        createAccount();
                        break;
                    case "Check Balance":
                        checkBalance();
                        break;
                    case "Deposit":
                        updateBalance(true);
                        break;
                    case "Withdraw":
                        updateBalance(false);
                        break;
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
        
        return button;
    }
    
    private void createAccount() throws SQLException {
        if (accountField.getText().trim().isEmpty() || 
            new String(pinField.getPassword()).trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Account ID and PIN are required!");
            return;
        }
        
        String sql = "INSERT INTO accounts (id, pin, balance) VALUES (?, ?, 0)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountField.getText().trim());
            stmt.setString(2, new String(pinField.getPassword()).trim());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Account created successfully!");
            clearFields();
        }
    }
    
    private void checkBalance() throws SQLException {
        if (accountField.getText().trim().isEmpty() || 
            new String(pinField.getPassword()).trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Account ID and PIN are required!");
            return;
        }
        
        String sql = "SELECT balance FROM accounts WHERE id = ? AND pin = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountField.getText().trim());
            stmt.setString(2, new String(pinField.getPassword()).trim());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                balanceLabel.setText(String.format("Balance: $%.2f", rs.getDouble("balance")));
            } else {
                JOptionPane.showMessageDialog(this, "Invalid account ID or PIN!");
            }
        }
    }
    
    private void updateBalance(boolean isDeposit) throws SQLException {
        if (accountField.getText().trim().isEmpty() || 
            new String(pinField.getPassword()).trim().isEmpty() ||
            amountField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount!");
            return;
        }
        
        String sql = "UPDATE accounts SET balance = balance " + (isDeposit ? "+" : "-") + 
                    " ? WHERE id = ? AND pin = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.setString(2, accountField.getText().trim());
            stmt.setString(3, new String(pinField.getPassword()).trim());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                checkBalance();
                amountField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid account ID or PIN!");
            }
        }
    }
    
    private void clearFields() {
        accountField.setText("");
        pinField.setText("");
        amountField.setText("");
        balanceLabel.setText("Balance: $0.00");
    }
    
    private void showPanel(String panelName) {
        mainPanel.removeAll();
        switch(panelName) {
            case "Dashboard":
                createMainContent();
                break;
            case "Transfer":
                paymentsPanel = new JPanel();
                mainPanel.add(paymentsPanel);
                break;
            case "History":
                transferHistoryPanel = new JPanel();
                mainPanel.add(transferHistoryPanel);
                break;
            case "Cards":
                cardsPanel = new JPanel();
                mainPanel.add(cardsPanel);
                break;
        }
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    // Custom rounded panel class
    private class RoundedPanel extends JPanel {
        private int radius;
        
        RoundedPanel(int radius) {
            super();
            this.radius = radius;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new BankingManagementSystem().setVisible(true);
        });
    }
} 