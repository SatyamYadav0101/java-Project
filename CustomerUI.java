import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CustomerUI extends JFrame {

    private DefaultListModel<String> cartModel;
    private JLabel subtotalLabel, gstLabel, totalLabel, statusLabel;
    private Map<String, Integer> cart;
    private int subtotal = 0;

    public CustomerUI() {

        cartModel = new DefaultListModel<>();
        cart = new HashMap<>();

        setTitle("McDonald's | Customer Panel");

        // 🔥 FULL SCREEN & UNCHANGEABLE
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ================= HEADER =================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(200, 16, 46));
        header.setPreferredSize(new Dimension(100, 80));

        ImageIcon logoIcon = new ImageIcon("images/logo.png");
        JLabel logo = new JLabel(new ImageIcon(
                logoIcon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH)));

        JLabel title = new JLabel("McDonald's Food Ordering System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));

        header.add(logo, BorderLayout.WEST);
        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ================= LEFT TABS =================
        JTabbedPane tabs = new JTabbedPane();
        tabs.setPreferredSize(new Dimension(250, 0));

        tabs.add("Profile", profilePanel());
        tabs.add("Order Details", orderDetailsPanel());
        tabs.add("Order Status", orderStatusPanel());

        add(tabs, BorderLayout.WEST);

        // ================= MENU (NO SIDE SCROLL) =================
        JPanel menuGrid = new JPanel(new GridLayout(0, 3, 20, 20));
        menuGrid.setBackground(new Color(245, 245, 245));

        menuGrid.add(foodCard("Burger", 120, "images/burger.png"));
        menuGrid.add(foodCard("Fries", 80, "images/fries.png"));
        menuGrid.add(foodCard("Coke", 50, "images/coke.png"));

        JPanel menuWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        menuWrapper.setBackground(new Color(245, 245, 245));
        menuWrapper.add(menuGrid);

        JScrollPane menuScroll = new JScrollPane(menuWrapper);
        menuScroll.setBorder(null);
        menuScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        menuScroll.getVerticalScrollBar().setUnitIncrement(16);

        add(menuScroll, BorderLayout.CENTER);

        // ================= BILLING =================
        JPanel billPanel = new JPanel(new BorderLayout());
        billPanel.setPreferredSize(new Dimension(250, 0));
        billPanel.setBorder(BorderFactory.createTitledBorder("Billing"));

        JList<String> cartList = new JList<>(cartModel);
        cartList.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel totalsPanel = new JPanel(new GridLayout(3, 1));
        subtotalLabel = new JLabel("Subtotal: ₹0");
        gstLabel = new JLabel("GST (18%): ₹0");
        totalLabel = new JLabel("Total: ₹0");

        subtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gstLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        totalsPanel.add(subtotalLabel);
        totalsPanel.add(gstLabel);
        totalsPanel.add(totalLabel);

        JButton orderBtn = new JButton("Place Order");
        orderBtn.setBackground(new Color(255, 199, 44));
        orderBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        orderBtn.addActionListener(e -> placeOrder());

        billPanel.add(new JScrollPane(cartList), BorderLayout.CENTER);
        billPanel.add(totalsPanel, BorderLayout.NORTH);
        billPanel.add(orderBtn, BorderLayout.SOUTH);

        add(billPanel, BorderLayout.EAST);
    }

    // ================= FOOD CARD =================
    private JPanel foodCard(String name, int price, String imagePath) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(230, 220));
        panel.setBackground(new Color(255, 245, 230));
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        ImageIcon icon = new ImageIcon(imagePath);
        JLabel img = new JLabel(new ImageIcon(
                icon.getImage().getScaledInstance(180, 90, Image.SCALE_SMOOTH)));
        img.setHorizontalAlignment(JLabel.CENTER);

        JLabel label = new JLabel(name + "  ₹" + price, JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        JButton addBtn = new JButton("Add to Cart");
        addBtn.setBackground(new Color(255, 199, 44));
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.setFocusPainted(false);

        addBtn.addActionListener(e -> {
            int qty = (int) qtySpinner.getValue();
            cart.put(name, cart.getOrDefault(name, 0) + qty);
            updateBill();
        });

        JPanel bottom = new JPanel(new GridLayout(3, 1, 4, 4));
        bottom.setOpaque(false);
        bottom.add(new JLabel("Qty", JLabel.CENTER));
        bottom.add(qtySpinner);
        bottom.add(addBtn);

        panel.add(img, BorderLayout.NORTH);
        panel.add(label, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    // ================= BILL LOGIC =================
    private void updateBill() {
        cartModel.clear();
        subtotal = 0;

        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            int itemTotal = entry.getValue() * getPrice(entry.getKey());
            subtotal += itemTotal;
            cartModel.addElement(entry.getKey() + " x " + entry.getValue() + " = ₹" + itemTotal);
        }

        double gst = subtotal * 0.18;
        double total = subtotal + gst;

        subtotalLabel.setText("Subtotal: ₹" + subtotal);
        gstLabel.setText("GST (18%): ₹" + String.format("%.2f", gst));
        totalLabel.setText("Total: ₹" + String.format("%.2f", total));
    }

    private int getPrice(String item) {
        switch (item) {
            case "Burger": return 120;
            case "Fries": return 80;
            case "Coke": return 50;
            default: return 0;
        }
    }

    private void placeOrder() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }
        statusLabel.setText("Status: Order Placed");
        JOptionPane.showMessageDialog(this, "Order Placed Successfully!");
    }

    // ================= LEFT PANELS =================
    private JPanel profilePanel() {
        JPanel p = new JPanel(new GridLayout(4, 1, 5, 5));
        p.add(new JLabel("Name: Satyam"));
        p.add(new JLabel("Phone: 9XXXXXXXXX"));
        p.add(new JLabel("Address: City, State"));
        return p;
    }

    private JPanel orderDetailsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(new JList<>(cartModel)));
        return p;
    }

    private JPanel orderStatusPanel() {
        JPanel p = new JPanel();
        statusLabel = new JLabel("Status: Not Ordered");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        p.add(statusLabel);
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerUI().setVisible(true));
    }
}
