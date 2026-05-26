package ui;

import dao.AccountDAO;
import model.Account;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final AccountDAO dao = new AccountDAO();
    private JTextField    txtPhone;
    private JPasswordField txtPin;

    public LoginFrame() {
        setTitle("UoK Bank — Login");
        setSize(460, 580);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        build();
        setVisible(true);
    }

    private void build() {
        // Full gradient background
        JPanel root = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, UITheme.PRIMARY_DARK, 0, getHeight(), UITheme.PRIMARY_LIGHT));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(root);

        // White card
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(32, 40, 32, 40));
        card.setPreferredSize(new Dimension(380, 490));

        // ── Logo ──────────────────────────────────────────────────────────
        JPanel logoRow = centred();
        logoRow.add(UITheme.logoBadge(72));
        card.add(logoRow);
        card.add(Box.createVerticalStrut(12));

        // ── App name ──────────────────────────────────────────────────────
        JLabel lblApp = new JLabel("UoK Bank");
        lblApp.setFont(UITheme.F_DISPLAY);
        lblApp.setForeground(UITheme.PRIMARY);
        lblApp.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblApp);

        JLabel lblUni = new JLabel("University of Kigali");
        lblUni.setFont(UITheme.F_SMALL);
        lblUni.setForeground(UITheme.TEXT_MUTED);
        lblUni.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblUni);
        card.add(Box.createVerticalStrut(6));

        JLabel lblTag = new JLabel("Rwanda's Campus Banking Simulator");
        lblTag.setFont(new Font("Arial", Font.ITALIC, 11));
        lblTag.setForeground(new Color(UITheme.GOLD.getRed(), UITheme.GOLD.getGreen(), UITheme.GOLD.getBlue()));
        lblTag.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTag);
        card.add(Box.createVerticalStrut(22));

        card.add(UITheme.divider());
        card.add(Box.createVerticalStrut(18));

        // ── Phone ─────────────────────────────────────────────────────────
        addFieldBlock(card, "PHONE NUMBER", null);
        txtPhone = UITheme.field();
        txtPhone.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));
        txtPhone.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(txtPhone);
        card.add(Box.createVerticalStrut(14));

        // ── PIN ───────────────────────────────────────────────────────────
        addFieldBlock(card, "PIN (5 digits)", null);
        txtPin = UITheme.pinField();
        txtPin.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));
        txtPin.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPin.addActionListener(e -> login());
        card.add(txtPin);
        card.add(Box.createVerticalStrut(24));

        // ── Buttons ───────────────────────────────────────────────────────
        JButton btnLogin = UITheme.successBtn("Login to My Account");
        btnLogin.setMaximumSize(new Dimension(Short.MAX_VALUE, 44));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> login());
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(10));

        JButton btnReg = UITheme.primaryBtn("Open New Account");
        btnReg.setMaximumSize(new Dimension(Short.MAX_VALUE, 44));
        btnReg.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnReg.addActionListener(e -> { dispose(); new RegisterFrame(); });
        card.add(btnReg);
        card.add(Box.createVerticalStrut(16));

        card.add(UITheme.divider());
        card.add(Box.createVerticalStrut(10));

        JLabel hint = new JLabel("Admin login: 0700000000 / PIN: 00000");
        hint.setFont(UITheme.F_MICRO);
        hint.setForeground(UITheme.TEXT_MUTED);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(hint);

        root.add(card);
    }

    private void login() {
        String phone = txtPhone.getText().trim();
        String pin   = new String(txtPin.getPassword()).trim();

        if (phone.isEmpty() || pin.isEmpty()) { err("Enter your phone number and PIN."); return; }

        Account a = dao.findByPhone(phone);
        if (a == null)               { err("No account found for this phone number."); return; }
        if (!a.getPin().equals(pin)) { err("Incorrect PIN. Please try again."); return; }
        if (a.isFrozen())            { err("Your account has been frozen. Contact the admin."); return; }

        dispose();
        if ("ADMIN".equals(a.getRole())) new AdminFrame(a);
        else                             new DashboardFrame(a);
    }

    private void addFieldBlock(JPanel parent, String labelText, String hint) {
        JLabel lbl = UITheme.sectionLabel(labelText);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createVerticalStrut(5));
    }

    private JPanel centred() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Short.MAX_VALUE, 90));
        return p;
    }

    private void err(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Login Failed", JOptionPane.ERROR_MESSAGE);
    }
}
