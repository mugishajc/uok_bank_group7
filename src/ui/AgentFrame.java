package ui;

import dao.AccountDAO;
import dao.TransactionDAO;
import model.Account;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;

public class AgentFrame extends JFrame {

    private static final DecimalFormat FRW = new DecimalFormat("#,##0.00");

    private final AccountDAO     accDao = new AccountDAO();
    private final TransactionDAO txnDao = new TransactionDAO();
    private final Account        agent;
    private final DashboardFrame dash;

    private JTextField     txtCustomerPhone, txtAmount;
    private JPasswordField txtAgentPin;
    private JLabel         lblCustomerName;

    public AgentFrame(Account agent, DashboardFrame dash) {
        this.agent = agent;
        this.dash  = dash;
        setTitle("Agent Services — UoK Bank");
        setSize(460, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        build();
        setVisible(true);
    }

    private void build() {
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout());

        add(UITheme.pageHeader("Agent Services", "MoMo Cash-In & Cash-Out operations"),
            BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.CARD);
        form.setBorder(new EmptyBorder(24, 32, 24, 32));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        // Agent info banner
        g.gridy = 0; g.gridx = 0; g.gridwidth = 2; g.insets = new Insets(0, 0, 18, 0);
        JPanel agentInfo = new JPanel(new BorderLayout());
        agentInfo.setBackground(new Color(232, 245, 233));
        agentInfo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 210, 160)),
            new EmptyBorder(10, 14, 10, 14)));
        JLabel lblAgent = new JLabel("Agent: " + agent.getFullName() + "   (" + agent.getPhone() + ")");
        lblAgent.setFont(UITheme.F_LABEL);
        lblAgent.setForeground(new Color(27, 94, 32));
        agentInfo.add(lblAgent);
        form.add(agentInfo, g);

        // Customer phone + verify
        g.gridy = 1; g.insets = new Insets(6, 0, 2, 0);
        form.add(UITheme.sectionLabel("CUSTOMER PHONE NUMBER"), g);

        g.gridy = 2; g.gridwidth = 1; g.weightx = 1.0; g.insets = new Insets(4, 0, 0, 8);
        txtCustomerPhone = UITheme.field();
        txtCustomerPhone.setPreferredSize(new Dimension(0, 42));
        form.add(txtCustomerPhone, g);

        g.gridx = 1; g.weightx = 0; g.insets = new Insets(4, 0, 0, 0);
        JButton btnVerify = UITheme.primaryBtn("Verify");
        btnVerify.setPreferredSize(new Dimension(90, 42));
        btnVerify.addActionListener(e -> lookupCustomer());
        form.add(btnVerify, g);

        g.gridy = 3; g.gridx = 0; g.gridwidth = 2; g.weightx = 1.0; g.insets = new Insets(6, 0, 14, 0);
        lblCustomerName = new JLabel("   ");
        lblCustomerName.setFont(UITheme.F_LABEL);
        lblCustomerName.setForeground(UITheme.SUCCESS);
        form.add(lblCustomerName, g);

        g.gridy = 4; g.insets = new Insets(6, 0, 2, 0);
        form.add(UITheme.sectionLabel("AMOUNT (FRW)"), g);
        g.gridy = 5; g.insets = new Insets(4, 0, 14, 0);
        txtAmount = UITheme.field(); txtAmount.setPreferredSize(new Dimension(0, 42));
        form.add(txtAmount, g);

        g.gridy = 6; g.insets = new Insets(6, 0, 2, 0);
        form.add(UITheme.sectionLabel("YOUR AGENT PIN"), g);
        g.gridy = 7; g.insets = new Insets(4, 0, 22, 0);
        txtAgentPin = UITheme.pinField(); txtAgentPin.setPreferredSize(new Dimension(0, 42));
        form.add(txtAgentPin, g);

        // Cash In / Cash Out buttons side by side
        g.gridy = 8; g.gridwidth = 1; g.weightx = 0.5; g.insets = new Insets(0, 0, 10, 6);
        JButton btnCashIn = UITheme.successBtn("Cash-In");
        btnCashIn.setPreferredSize(new Dimension(0, 44));
        btnCashIn.addActionListener(e -> cashIn());
        form.add(btnCashIn, g);

        g.gridx = 1; g.insets = new Insets(0, 6, 10, 0);
        JButton btnCashOut = UITheme.dangerBtn("Cash-Out");
        btnCashOut.setPreferredSize(new Dimension(0, 44));
        btnCashOut.addActionListener(e -> cashOut());
        form.add(btnCashOut, g);

        g.gridy = 9; g.gridx = 0; g.gridwidth = 2; g.weightx = 1.0; g.insets = new Insets(0, 0, 0, 0);
        JButton btnCancel = UITheme.grayBtn("Close");
        btnCancel.setPreferredSize(new Dimension(0, 40));
        btnCancel.addActionListener(e -> dispose());
        form.add(btnCancel, g);

        add(form, BorderLayout.CENTER);
    }

    private void lookupCustomer() {
        String phone = txtCustomerPhone.getText().trim();
        if (phone.isEmpty()) { lblCustomerName.setText("Enter a phone number."); return; }
        Account c = accDao.findByPhone(phone);
        if (c == null) {
            lblCustomerName.setForeground(UITheme.DANGER);
            lblCustomerName.setText("No account found for " + phone);
        } else if (c.isFrozen()) {
            lblCustomerName.setForeground(UITheme.DANGER);
            lblCustomerName.setText("Account is frozen: " + c.getFullName());
        } else {
            lblCustomerName.setForeground(UITheme.SUCCESS);
            lblCustomerName.setText("Verified: " + c.getFullName() +
                "  —  Balance: FRW " + FRW.format(c.getBalance()));
        }
    }

    private void cashIn() {
        if (!validatePin()) return;
        Account customer = getCustomer(); if (customer == null) return;
        double amount = getAmount();      if (amount <= 0) return;

        accDao.updateBalance(customer.getPhone(), customer.getBalance() + amount);
        txnDao.record(agent.getPhone(), customer.getPhone(), "AGENT_CASH_IN", amount,
            "Cash-in via agent " + agent.getFullName());

        JOptionPane.showMessageDialog(this,
            "Cash-in of FRW " + FRW.format(amount) + " to " + customer.getFullName() + " successful.",
            "Cash-In Done", JOptionPane.INFORMATION_MESSAGE);
        lookupCustomer();
    }

    private void cashOut() {
        if (!validatePin()) return;
        Account customer = getCustomer(); if (customer == null) return;
        double amount = getAmount();      if (amount <= 0) return;

        if (amount > customer.getBalance()) {
            err("Customer has insufficient balance. Available: FRW " + FRW.format(customer.getBalance()));
            return;
        }

        accDao.updateBalance(customer.getPhone(), customer.getBalance() - amount);
        txnDao.record(customer.getPhone(), agent.getPhone(), "AGENT_CASH_OUT", amount,
            "Cash-out via agent " + agent.getFullName());

        JOptionPane.showMessageDialog(this,
            "Cash-out of FRW " + FRW.format(amount) + " from " + customer.getFullName() + " successful.",
            "Cash-Out Done", JOptionPane.INFORMATION_MESSAGE);
        lookupCustomer();
    }

    private boolean validatePin() {
        String pin = new String(txtAgentPin.getPassword()).trim();
        if (!agent.getPin().equals(pin)) { err("Incorrect agent PIN."); return false; }
        return true;
    }

    private Account getCustomer() {
        String phone = txtCustomerPhone.getText().trim();
        Account c = accDao.findByPhone(phone);
        if (c == null)    { err("Customer account not found."); return null; }
        if (c.isFrozen()) { err("Customer account is frozen."); return null; }
        return c;
    }

    private double getAmount() {
        try {
            double a = Double.parseDouble(txtAmount.getText().trim().replace(",", ""));
            if (a <= 0) { err("Amount must be greater than zero."); return 0; }
            return a;
        } catch (NumberFormatException e) {
            err("Please enter a valid amount."); return 0;
        }
    }

    private void err(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
