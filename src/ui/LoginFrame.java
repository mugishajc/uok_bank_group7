package ui;

import dao.AccountDAO;
import model.Account;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final AccountDAO dao = new AccountDAO();
    private JTextField     txtPhone;
    private JPasswordField txtPin;

    public LoginFrame() {
        setTitle("UoK Bank — Login");
        setSize(460, 620);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage(UITheme.appIcon());
        build();
        setVisible(true);
    }

    private void build() {
        // Full UoK navy gradient background
        JPanel root = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, UITheme.PRIMARY_DARK,
                                             getWidth(), getHeight(), UITheme.PRIMARY));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // decorative circles
                g2.setColor(new Color(255,255,255,12));
                g2.fillOval(-60, -60, 220, 220);
                g2.fillOval(getWidth()-120, getHeight()-120, 240, 240);
            }
        };
        setContentPane(root);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(30, 40, 24, 40));
        card.setPreferredSize(new Dimension(380, 540));

        // ── UoK Shield Logo ───────────────────────────────────────────────
        JPanel logoRow = centred();
        logoRow.add(UITheme.logoBadge(80));
        card.add(logoRow);
        card.add(Box.createVerticalStrut(14));

        // ── App title ─────────────────────────────────────────────────────
        JLabel lblApp = new JLabel("UoK Bank");
        lblApp.setFont(new Font("Arial", Font.BOLD, 28));
        lblApp.setForeground(UITheme.PRIMARY);
        lblApp.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblApp);

        JLabel lblUni = new JLabel("University of Kigali");
        lblUni.setFont(new Font("Arial", Font.BOLD, 12));
        lblUni.setForeground(UITheme.GOLD_DARK);
        lblUni.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblUni);

        JLabel lblTag = new JLabel("Banking & Mobile Money Simulator");
        lblTag.setFont(new Font("Arial", Font.ITALIC, 11));
        lblTag.setForeground(UITheme.TEXT_MUTED);
        lblTag.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTag);
        card.add(Box.createVerticalStrut(20));

        card.add(UITheme.divider());
        card.add(Box.createVerticalStrut(18));

        // ── Phone ─────────────────────────────────────────────────────────
        fieldLabel(card, "PHONE NUMBER");
        txtPhone = UITheme.field();
        txtPhone.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));
        txtPhone.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(txtPhone);
        card.add(Box.createVerticalStrut(14));

        // ── PIN ───────────────────────────────────────────────────────────
        fieldLabel(card, "PIN (5 digits)");
        txtPin = UITheme.pinField();
        txtPin.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));
        txtPin.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPin.addActionListener(e -> login());
        card.add(txtPin);
        card.add(Box.createVerticalStrut(24));

        // ── Buttons (UoK navy + gold — NOT green) ─────────────────────────
        JButton btnLogin = UITheme.primaryBtn("Login to My Account");
        btnLogin.setMaximumSize(new Dimension(Short.MAX_VALUE, 44));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> login());
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(10));

        JButton btnReg = UITheme.goldBtn("Open New Account");
        btnReg.setMaximumSize(new Dimension(Short.MAX_VALUE, 44));
        btnReg.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnReg.addActionListener(e -> { dispose(); new RegisterFrame(); });
        card.add(btnReg);
        card.add(Box.createVerticalStrut(16));

        card.add(UITheme.divider());
        card.add(Box.createVerticalStrut(8));

        JLabel hint = new JLabel("Admin: 0700000000 / PIN: 00000");
        hint.setFont(UITheme.F_MICRO);
        hint.setForeground(UITheme.TEXT_MUTED);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(hint);

        root.add(card);
    }

    private void login() {
        String phone = txtPhone.getText().trim();
        String pin   = new String(txtPin.getPassword()).trim();

        if (phone.isEmpty() || pin.isEmpty()) {
            err("Enter your phone number and PIN."); return;
        }
        Account a = dao.findByPhone(phone);
        if (a == null)                { err("No account found for this phone number."); return; }
        if (!a.getPin().equals(pin))  { err("Incorrect PIN. Please try again."); return; }
        if (a.isFrozen())             { err("Your account is frozen. Contact the admin."); return; }

        dispose();
        if ("ADMIN".equals(a.getRole())) new AdminFrame(a);
        else                             new DashboardFrame(a);
    }

    private void fieldLabel(JPanel p, String text) {
        JLabel l = UITheme.sectionLabel(text);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(Box.createVerticalStrut(5));
    }

    private JPanel centred() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        return p;
    }

    private void err(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Login Failed", JOptionPane.ERROR_MESSAGE);
    }
}
