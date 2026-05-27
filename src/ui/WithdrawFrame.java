package ui;

import dao.AccountDAO;
import dao.TransactionDAO;
import model.Account;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;

public class WithdrawFrame extends JFrame {

    private static final DecimalFormat FRW = new DecimalFormat("#,##0.00");

    private final AccountDAO     accDao  = new AccountDAO();
    private final TransactionDAO txnDao  = new TransactionDAO();
    private final Account        account;
    private final DashboardFrame dash;

    private JTextField     txtAmount;
    private JPasswordField txtPin;

    public WithdrawFrame(Account account, DashboardFrame dash) {
        this.account = account;
        this.dash    = dash;
        setTitle("Withdraw — UoK Bank");
        setSize(420, 420);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(UITheme.appIcon());
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        build();
        setVisible(true);
    }

    private void build() {
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout());

        add(UITheme.pageHeader("Withdraw Cash", "Confirm with your PIN"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.CARD);
        form.setBorder(new EmptyBorder(28, 32, 28, 32));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        // Balance chip
        g.gridy = 0; g.gridx = 0; g.gridwidth = 2; g.insets = new Insets(0, 0, 18, 0);
        JPanel chip = balanceChip();
        form.add(chip, g);

        g.insets = new Insets(6, 0, 2, 0);
        g.gridy = 1; form.add(UITheme.sectionLabel("AMOUNT TO WITHDRAW (FRW)"), g);
        g.gridy = 2; g.insets = new Insets(4, 0, 14, 0);
        txtAmount = UITheme.field(); txtAmount.setPreferredSize(new Dimension(0, 42));
        form.add(txtAmount, g);

        g.gridy = 3; g.insets = new Insets(6, 0, 2, 0);
        form.add(UITheme.sectionLabel("CONFIRM YOUR PIN"), g);
        g.gridy = 4; g.insets = new Insets(4, 0, 24, 0);
        txtPin = UITheme.pinField(); txtPin.setPreferredSize(new Dimension(0, 42));
        txtPin.addActionListener(e -> withdraw());
        form.add(txtPin, g);

        g.gridy = 5; g.insets = new Insets(0, 0, 10, 0);
        JButton btnOk = UITheme.dangerBtn("Confirm Withdrawal");
        btnOk.setPreferredSize(new Dimension(0, 44));
        btnOk.addActionListener(e -> withdraw());
        form.add(btnOk, g);

        g.gridy = 6; g.insets = new Insets(0, 0, 0, 0);
        JButton btnCancel = UITheme.grayBtn("Cancel");
        btnCancel.setPreferredSize(new Dimension(0, 40));
        btnCancel.addActionListener(e -> dispose());
        form.add(btnCancel, g);

        add(form, BorderLayout.CENTER);
    }

    private JPanel balanceChip() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(255, 243, 243));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255,180,180)),
            new EmptyBorder(10, 14, 10, 14)));
        JLabel lbl = new JLabel("Available Balance: FRW " + FRW.format(account.getBalance()));
        lbl.setFont(UITheme.F_LABEL);
        lbl.setForeground(UITheme.DANGER);
        p.add(lbl);
        return p;
    }

    private void withdraw() {
        String pin = new String(txtPin.getPassword()).trim();
        if (!account.getPin().equals(pin)) { err("Incorrect PIN."); return; }

        try {
            double amount = Double.parseDouble(txtAmount.getText().trim().replace(",", ""));
            if (amount <= 0) { err("Amount must be greater than zero."); return; }

            Account fresh = accDao.findByPhone(account.getPhone());
            if (amount > fresh.getBalance()) {
                err("Insufficient balance. Available: FRW " + FRW.format(fresh.getBalance()));
                return;
            }

            accDao.updateBalance(account.getPhone(), fresh.getBalance() - amount);
            txnDao.record(account.getPhone(), null, "WITHDRAW", amount, "Cash withdrawal");

            JOptionPane.showMessageDialog(this,
                "FRW " + FRW.format(amount) + " withdrawn successfully.",
                "Withdrawal Successful", JOptionPane.INFORMATION_MESSAGE);
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
