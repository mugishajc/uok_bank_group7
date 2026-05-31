package ui;

import dao.AccountDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private final AccountDAO dao = new AccountDAO();
    private JTextField     txtName, txtPhone;
    private JPasswordField txtPin, txtConfirm;
    private JComboBox<String> cmbType;

    public RegisterFrame() {
        setTitle("UoK Bank — Open Account");
        setSize(520, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage(UITheme.appIcon());
        build();
        setVisible(true);
    }

    private void build() {
        // Full gradient background — same as LoginFrame
        JPanel root = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, UITheme.PRIMARY_DARK,
                                             getWidth(), getHeight(), UITheme.PRIMARY));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillOval(-60, -60, 220, 220);
                g2.fillOval(getWidth() - 120, getHeight() - 120, 240, 240);
            }
        };
        setContentPane(root);

        // ── White card ────────────────────────────────────────────────────
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(24, 40, 24, 40));
        card.setPreferredSize(new Dimension(400, 640));
        card.setMaximumSize(new Dimension(400, 800));

        // Logo + title
        JPanel logoRow = centred();
        logoRow.add(UITheme.logoBadge(60));
        card.add(logoRow);
        card.add(Box.createVerticalStrut(10));

        JLabel lblApp = new JLabel("Open New Account");
        lblApp.setFont(new Font("Arial", Font.BOLD, 22));
        lblApp.setForeground(UITheme.PRIMARY);
        lblApp.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblApp);

        JLabel lblSub = new JLabel("University of Kigali Banking Simulator");
        lblSub.setFont(UITheme.F_MICRO);
        lblSub.setForeground(UITheme.TEXT_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblSub);
        card.add(Box.createVerticalStrut(16));
        card.add(UITheme.divider());
        card.add(Box.createVerticalStrut(12));

        // ── Form fields ───────────────────────────────────────────────────
        txtName    = UITheme.field();
        txtPhone   = UITheme.field();
        cmbType    = UITheme.combo("MoMo Wallet", "Savings Account", "Current Account");
        txtPin     = UITheme.pinField();
        txtConfirm = UITheme.pinField();

        addField(card, "FULL NAME",      txtName);
        addField(card, "PHONE NUMBER",   txtPhone);
        addField(card, "ACCOUNT TYPE",   cmbType);
        addField(card, "PIN (5 digits)", txtPin);
        addField(card, "CONFIRM PIN",    txtConfirm);

        card.add(Box.createVerticalStrut(18));

        // ── Buttons ───────────────────────────────────────────────────────
        JButton btnCreate = UITheme.primaryBtn("Create Account");
        btnCreate.setMaximumSize(new Dimension(Short.MAX_VALUE, 46));
        btnCreate.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCreate.addActionListener(e -> register());
        card.add(btnCreate);
        card.add(Box.createVerticalStrut(10));

        JButton btnBack = UITheme.grayBtn("Back to Login");
        btnBack.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));
        btnBack.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnBack.addActionListener(e -> goBack());
        card.add(btnBack);

        root.add(card);
    }

    private void addField(JPanel card, String label, JComponent field) {
        JLabel lbl = UITheme.sectionLabel(label);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(lbl);
        card.add(Box.createVerticalStrut(4));
        field.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(field);
        card.add(Box.createVerticalStrut(10));
    }

    private JPanel centred() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        return p;
    }

    private void goBack() {
        setVisible(false);
        dispose();
        SwingUtilities.invokeLater(LoginFrame::new);
    }

    private void register() {
        String name    = txtName.getText().trim();
        String phone   = txtPhone.getText().trim();
        String pin     = new String(txtPin.getPassword()).trim();
        String confirm = new String(txtConfirm.getPassword()).trim();
        String type    = switch ((String) cmbType.getSelectedItem()) {
            case "Savings Account" -> "SAVINGS";
            case "Current Account" -> "CURRENT";
            default                -> "MOMO";
        };

        if (name.isEmpty() || phone.isEmpty()) { err("Name and phone are required.");          return; }
        if (pin.length() != 5)                 { err("PIN must be exactly 5 digits.");         return; }
        if (!pin.equals(confirm))              { err("PINs do not match.");                    return; }
        if (!phone.matches("07\\d{8}"))        { err("Enter a valid Rwandan phone (07XXXXXXXX)."); return; }

        if (dao.create(phone, name, pin, type)) {
            JOptionPane.showMessageDialog(this,
                "Account created!\nYou can now log in.",
                "Account Opened", JOptionPane.INFORMATION_MESSAGE);
            goBack();
        } else {
            err("This phone number is already registered.");
        }
    }

    private void err(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Registration Error", JOptionPane.ERROR_MESSAGE);
    }
}
