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
        setSize(480, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setIconImage(UITheme.appIcon());
        build();
        setVisible(true);
    }

    private void build() {
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout());

        add(UITheme.pageHeader("Open New Account", "University of Kigali Banking Simulator"),
            BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.CARD);
        form.setBorder(new EmptyBorder(28, 36, 28, 36));

        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.insets  = new Insets(6, 0, 6, 0);
        g.weightx = 1.0;

        int row = 0;
        row = addRow(form, g, row, "FULL NAME",      txtName    = UITheme.field());
        row = addRow(form, g, row, "PHONE NUMBER",   txtPhone   = UITheme.field());
        cmbType = UITheme.combo("MoMo Wallet", "Savings Account", "Current Account");
        row = addRow(form, g, row, "ACCOUNT TYPE",   cmbType);
        row = addRow(form, g, row, "PIN (5 digits)", txtPin     = UITheme.pinField());
        row = addRow(form, g, row, "CONFIRM PIN",    txtConfirm = UITheme.pinField());

        // Primary action = UoK navy blue
        g.gridy = row; g.gridx = 0; g.gridwidth = 2; g.insets = new Insets(18, 0, 8, 0);
        JButton btnCreate = UITheme.primaryBtn("Create Account");
        btnCreate.setPreferredSize(new Dimension(Short.MAX_VALUE, 44));
        btnCreate.addActionListener(e -> register());
        form.add(btnCreate, g);

        g.gridy = row + 1; g.insets = new Insets(0, 0, 0, 0);
        JButton btnBack = UITheme.grayBtn("Back to Login");
        btnBack.setPreferredSize(new Dimension(Short.MAX_VALUE, 40));
        btnBack.addActionListener(e -> { dispose(); new LoginFrame(); });
        form.add(btnBack, g);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UITheme.CARD);
        add(scroll, BorderLayout.CENTER);

        add(UITheme.footer(), BorderLayout.SOUTH);
    }

    private int addRow(JPanel p, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridy = row; g.gridx = 0; g.gridwidth = 2; g.insets = new Insets(10, 0, 0, 0);
        p.add(UITheme.sectionLabel(label), g);
        g.gridy = row + 1; g.insets = new Insets(4, 0, 0, 0);
        field.setPreferredSize(new Dimension(Short.MAX_VALUE, 42));
        p.add(field, g);
        return row + 2;
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

        if (name.isEmpty() || phone.isEmpty()) { err("Name and phone are required."); return; }
        if (pin.length() != 5)                 { err("PIN must be exactly 5 digits."); return; }
        if (!pin.equals(confirm))              { err("PINs do not match."); return; }
        if (!phone.matches("07\\d{8}"))        { err("Enter a valid Rwandan phone (07XXXXXXXX)."); return; }

        if (dao.create(phone, name, pin, type)) {
            JOptionPane.showMessageDialog(this,
                "Account created successfully!\nYou can now log in.",
                "Account Opened", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new LoginFrame();
        } else {
            err("This phone number is already registered.");
        }
    }

    private void err(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Registration Error", JOptionPane.ERROR_MESSAGE);
    }
}
