package ui;

import dao.AccountDAO;
import dao.TransactionDAO;
import model.Account;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;

public class TransferFrame extends JFrame {

    private static final DecimalFormat FRW = new DecimalFormat("#,##0.00");

    private final AccountDAO     accDao = new AccountDAO();
    private final TransactionDAO txnDao = new TransactionDAO();
    private final Account        sender;
    private final DashboardFrame dash;

    private JTextField     txtReceiver, txtAmount, txtNote;
    private JPasswordField txtPin;
    private JLabel         lblReceiverName;

    public TransferFrame(Account sender, DashboardFrame dash) {
        this.sender = sender;
        this.dash   = dash;
        setTitle("Transfer — UoK Bank");
        setSize(440, 520);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(UITheme.appIcon());
        setLocationRelativeTo(null);
        setResizable(false);
        build();
        setVisible(true);
    }

    private void build() {
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout());

        add(UITheme.pageHeader("Send Money", "MoMo-style transfer to any account"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.CARD);
        form.setBorder(new EmptyBorder(24, 32, 24, 32));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        // Receiver phone + lookup
        g.gridy = 0; g.gridx = 0; g.gridwidth = 2; g.insets = new Insets(0, 0, 2, 0);
        form.add(UITheme.sectionLabel("RECEIVER PHONE NUMBER"), g);

        g.gridy = 1; g.gridwidth = 1; g.weightx = 1.0; g.insets = new Insets(4, 0, 0, 8);
        txtReceiver = UITheme.field(); txtReceiver.setPreferredSize(new Dimension(0, 42));
        form.add(txtReceiver, g);

        g.gridx = 1; g.weightx = 0; g.insets = new Insets(4, 0, 0, 0);
        JButton btnLookup = UITheme.primaryBtn("Verify");
        btnLookup.setPreferredSize(new Dimension(90, 42));
        btnLookup.addActionListener(e -> lookupReceiver());
        form.add(btnLookup, g);

        g.gridy = 2; g.gridx = 0; g.gridwidth = 2; g.weightx = 1.0; g.insets = new Insets(6, 0, 14, 0);
        lblReceiverName = new JLabel("   ");
        lblReceiverName.setFont(UITheme.F_LABEL);
        lblReceiverName.setForeground(UITheme.SUCCESS);
        form.add(lblReceiverName, g);

        g.gridy = 3; g.insets = new Insets(6, 0, 2, 0);
        form.add(UITheme.sectionLabel("AMOUNT (FRW)"), g);
        g.gridy = 4; g.insets = new Insets(4, 0, 14, 0);
        txtAmount = UITheme.field(); txtAmount.setPreferredSize(new Dimension(0, 42));
        form.add(txtAmount, g);

        g.gridy = 5; g.insets = new Insets(6, 0, 2, 0);
        form.add(UITheme.sectionLabel("NOTE / REASON"), g);
        g.gridy = 6; g.insets = new Insets(4, 0, 14, 0);
        txtNote = UITheme.field(); txtNote.setPreferredSize(new Dimension(0, 42));
        form.add(txtNote, g);

        g.gridy = 7; g.insets = new Insets(6, 0, 2, 0);
        form.add(UITheme.sectionLabel("YOUR PIN"), g);
        g.gridy = 8; g.insets = new Insets(4, 0, 24, 0);
        txtPin = UITheme.pinField(); txtPin.setPreferredSize(new Dimension(0, 42));
        txtPin.addActionListener(e -> transfer());
        form.add(txtPin, g);

        g.gridy = 9; g.insets = new Insets(0, 0, 10, 0);
        JButton btnSend = UITheme.btn("Send Money", UITheme.BLUE_ACCENT, Color.WHITE);
        btnSend.setPreferredSize(new Dimension(0, 44));
        btnSend.addActionListener(e -> transfer());
        form.add(btnSend, g);

        g.gridy = 10; g.insets = new Insets(0, 0, 0, 0);
        JButton btnCancel = UITheme.grayBtn("Cancel");
        btnCancel.setPreferredSize(new Dimension(0, 40));
        btnCancel.addActionListener(e -> dispose());
        form.add(btnCancel, g);

        add(form, BorderLayout.CENTER);
    }

    private void lookupReceiver() {
        String phone = txtReceiver.getText().trim();
        if (phone.isEmpty()) { lblReceiverName.setText("Enter a phone number first."); return; }
        Account r = accDao.findByPhone(phone);
        if (r == null) {
            lblReceiverName.setForeground(UITheme.DANGER);
            lblReceiverName.setText("Account not found.");
        } else if (r.getPhone().equals(sender.getPhone())) {
            lblReceiverName.setForeground(UITheme.DANGER);
            lblReceiverName.setText("Cannot transfer to yourself.");
        } else {
            lblReceiverName.setForeground(UITheme.SUCCESS);
            lblReceiverName.setText("Verified: " + r.getFullName());
        }
    }

    private void transfer() {
        String receiverPhone = txtReceiver.getText().trim();
        String pin           = new String(txtPin.getPassword()).trim();

        if (!sender.getPin().equals(pin)) { err("Incorrect PIN."); return; }
        if (receiverPhone.equals(sender.getPhone())) { err("Cannot transfer to yourself."); return; }

        Account receiver = accDao.findByPhone(receiverPhone);
        if (receiver == null)       { err("Receiver account not found."); return; }
        if (receiver.isFrozen())    { err("Receiver account is frozen."); return; }

        try {
            double amount = Double.parseDouble(txtAmount.getText().trim().replace(",", ""));
            if (amount <= 0) { err("Amount must be greater than zero."); return; }

            Account freshSender   = accDao.findByPhone(sender.getPhone());
            Account freshReceiver = accDao.findByPhone(receiverPhone);
            if (freshSender == null || freshReceiver == null) {
                err("Account lookup failed. Please try again."); return;
            }
            if (amount > freshSender.getBalance()) {
                err("Insufficient balance. Available: FRW " + FRW.format(freshSender.getBalance()));
                return;
            }

            // Debit sender, credit receiver (both from freshly-fetched balances)
            accDao.updateBalance(sender.getPhone(), freshSender.getBalance()   - amount);
            accDao.updateBalance(receiverPhone,     freshReceiver.getBalance() + amount);

            String note = txtNote.getText().trim().isEmpty() ? "MoMo Transfer" : txtNote.getText().trim();
            txnDao.record(sender.getPhone(), receiverPhone, "TRANSFER", amount, note);

            JOptionPane.showMessageDialog(this,
                "FRW " + FRW.format(amount) + " sent to " + receiver.getFullName() + " successfully.",
                "Transfer Successful", JOptionPane.INFORMATION_MESSAGE);
            dash.refreshBalance();
            dispose();
        } catch (NumberFormatException ex) {
            err("Please enter a valid amount.");
        }
    }

    private void err(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
