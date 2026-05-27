package ui;

import dao.AccountDAO;
import dao.TransactionDAO;
import model.Account;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;

public class DepositFrame extends JFrame {

    private static final DecimalFormat FRW = new DecimalFormat("#,##0.00");

    private final AccountDAO     accDao  = new AccountDAO();
    private final TransactionDAO txnDao  = new TransactionDAO();
    private final Account        account;
    private final DashboardFrame dash;

    private JTextField txtAmount, txtNote;

    public DepositFrame(Account account, DashboardFrame dash) {
        this.account = account;
        this.dash    = dash;
        setTitle("Deposit — UoK Bank");
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int)(screen.width * 0.55), (int)(screen.height * 0.70));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(UITheme.appIcon());
        setLocationRelativeTo(null);
        setResizable(true);
        build();
        setVisible(true);
    }

    private void build() {
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout());

        add(UITheme.pageHeader("Deposit Funds", "Add money to your account"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.CARD);
        form.setBorder(new EmptyBorder(28, 32, 28, 32));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        // Current balance info
        g.gridy = 0; g.gridx = 0; g.gridwidth = 2; g.insets = new Insets(0, 0, 18, 0);
        JPanel info = new JPanel(new BorderLayout());
        info.setBackground(new Color(240, 247, 255));
        info.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 210, 255)),
            new EmptyBorder(10, 14, 10, 14)));
        JLabel lblInfo = new JLabel("Current Balance: FRW " + FRW.format(account.getBalance()));
        lblInfo.setFont(UITheme.F_LABEL);
        lblInfo.setForeground(UITheme.PRIMARY);
        info.add(lblInfo);
        form.add(info, g);

        g.insets = new Insets(6, 0, 2, 0);

        g.gridy = 1; form.add(UITheme.sectionLabel("AMOUNT (FRW)"), g);
        g.gridy = 2; g.insets = new Insets(4, 0, 14, 0);
        txtAmount = UITheme.field(); txtAmount.setPreferredSize(new Dimension(0, 42));
        form.add(txtAmount, g);

        g.gridy = 3; g.insets = new Insets(6, 0, 2, 0);
        form.add(UITheme.sectionLabel("NOTE (OPTIONAL)"), g);
        g.gridy = 4; g.insets = new Insets(4, 0, 24, 0);
        txtNote = UITheme.field(); txtNote.setPreferredSize(new Dimension(0, 42));
        form.add(txtNote, g);

        g.gridy = 5; g.insets = new Insets(0, 0, 10, 0);
        JButton btnOk = UITheme.successBtn("Confirm Deposit");
        btnOk.setPreferredSize(new Dimension(0, 44));
        btnOk.addActionListener(e -> deposit());
        form.add(btnOk, g);

        g.gridy = 6; g.insets = new Insets(0, 0, 0, 0);
        JButton btnCancel = UITheme.grayBtn("Cancel");
        btnCancel.setPreferredSize(new Dimension(0, 40));
        btnCancel.addActionListener(e -> dispose());
        form.add(btnCancel, g);

        add(form, BorderLayout.CENTER);
    }

    private void deposit() {
        try {
            double amount = Double.parseDouble(txtAmount.getText().trim().replace(",", ""));
            if (amount <= 0) { err("Amount must be greater than zero."); return; }

            Account fresh = accDao.findByPhone(account.getPhone());
            accDao.updateBalance(account.getPhone(), fresh.getBalance() + amount);

            String note = txtNote.getText().trim().isEmpty() ? "Cash deposit" : txtNote.getText().trim();
            txnDao.record(null, account.getPhone(), "DEPOSIT", amount, note);

            JOptionPane.showMessageDialog(this,
                "FRW " + FRW.format(amount) + " deposited successfully.",
                "Deposit Successful", JOptionPane.INFORMATION_MESSAGE);
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
