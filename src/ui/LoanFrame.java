package ui;

import dao.AccountDAO;
import dao.LoanDAO;
import dao.TransactionDAO;
import model.Account;
import model.Loan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class LoanFrame extends JFrame {

    private static final DecimalFormat FRW = new DecimalFormat("#,##0.00");

    private final LoanDAO        loanDao = new LoanDAO();
    private final AccountDAO     accDao  = new AccountDAO();
    private final TransactionDAO txnDao  = new TransactionDAO();
    private final Account        account;
    private final DashboardFrame dash;

    private JTextField txtAmount;

    public LoanFrame(Account account, DashboardFrame dash) {
        this.account = account;
        this.dash    = dash;
        setTitle("Loans — UoK Bank");
        setSize(560, 540);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(UITheme.appIcon());
        setLocationRelativeTo(null);
        build();
        setVisible(true);
    }

    private void build() {
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout(0, 0));

        add(UITheme.pageHeader("Loan Services", "Request credit — repay at any time"),
            BorderLayout.NORTH);

        // ── Request card ──────────────────────────────────────────────────────
        JPanel requestCard = new JPanel(new GridBagLayout());
        requestCard.setBackground(UITheme.CARD);
        requestCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
            new EmptyBorder(16, 24, 16, 24)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(0, 0, 0, 10);

        g.gridx = 0; g.gridy = 0; g.anchor = GridBagConstraints.WEST;
        requestCard.add(UITheme.label("Request Amount (FRW):"), g);

        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
        txtAmount = UITheme.field();
        txtAmount.setPreferredSize(new Dimension(180, 40));
        requestCard.add(txtAmount, g);

        g.gridx = 2; g.fill = GridBagConstraints.NONE; g.weightx = 0;
        JButton btnRequest = UITheme.warningBtn("Submit Request");
        btnRequest.setPreferredSize(new Dimension(150, 40));
        btnRequest.addActionListener(e -> requestLoan());
        requestCard.add(btnRequest, g);

        g.gridx = 3;
        JButton btnRepay = UITheme.successBtn("Repay Loan");
        btnRepay.setPreferredSize(new Dimension(130, 40));
        btnRepay.addActionListener(e -> repayLoan());
        requestCard.add(btnRepay, g);

        // ── Loans table ───────────────────────────────────────────────────────
        String[] cols = {"ID", "Amount (FRW)", "Status", "Requested At"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<Loan> loans = loanDao.getByPhone(account.getPhone());
        for (Loan l : loans) {
            model.addRow(new Object[]{
                l.getId(), FRW.format(l.getAmount()), l.getStatus(), l.getRequestedAt()
            });
        }

        JTable table = new JTable(model);
        UITheme.styleTable(table);

        // Colour status column
        table.getColumnModel().getColumn(2).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel,
                                                           boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String s = val == null ? "" : val.toString();
                setForeground(switch (s) {
                    case "APPROVED"   -> UITheme.SUCCESS;
                    case "REJECTED"   -> UITheme.DANGER;
                    case "REPAID"     -> UITheme.PRIMARY;
                    default           -> UITheme.WARNING;
                });
                setFont(UITheme.F_LABEL);
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(UITheme.CARD);

        // ── Assemble ──────────────────────────────────────────────────────────
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(UITheme.BG);
        center.add(requestCard, BorderLayout.NORTH);

        JPanel tableWrap = new JPanel(new BorderLayout());
        tableWrap.setBackground(UITheme.BG);
        tableWrap.setBorder(new EmptyBorder(10, 14, 0, 14));
        tableWrap.add(scroll, BorderLayout.CENTER);
        center.add(tableWrap, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 8));
        bottom.setBackground(UITheme.BG);
        JButton btnClose = UITheme.grayBtn("Close");
        btnClose.setPreferredSize(new Dimension(100, 34));
        btnClose.addActionListener(e -> dispose());
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);
    }

    private void requestLoan() {
        try {
            double amount = Double.parseDouble(txtAmount.getText().trim().replace(",", ""));
            if (amount <= 0) { err("Amount must be greater than zero."); return; }

            if (loanDao.request(account.getPhone(), amount)) {
                JOptionPane.showMessageDialog(this,
                    "Loan request of FRW " + FRW.format(amount) +
                    " submitted.\nPending admin approval.",
                    "Request Submitted", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoanFrame(account, dash);
            } else {
                err("Failed to submit loan request.");
            }
        } catch (NumberFormatException ex) {
            err("Please enter a valid amount.");
        }
    }

    private void repayLoan() {
        List<Loan> loans = loanDao.getByPhone(account.getPhone());
        Loan approved = loans.stream()
            .filter(l -> "APPROVED".equals(l.getStatus()))
            .findFirst().orElse(null);

        if (approved == null) {
            JOptionPane.showMessageDialog(this,
                "You have no approved loans to repay.",
                "No Loan", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Account fresh = accDao.findByPhone(account.getPhone());
        if (fresh.getBalance() < approved.getAmount()) {
            err("Insufficient balance. You need FRW " + FRW.format(approved.getAmount()));
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Repay FRW " + FRW.format(approved.getAmount()) + "?",
            "Confirm Repayment", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        accDao.updateBalance(account.getPhone(), fresh.getBalance() - approved.getAmount());
        loanDao.updateStatus(approved.getId(), "REPAID");
        txnDao.record(account.getPhone(), null, "LOAN_REPAY", approved.getAmount(), "Loan repayment");

        JOptionPane.showMessageDialog(this, "Loan repaid successfully.", "Done",
            JOptionPane.INFORMATION_MESSAGE);
        dash.refreshBalance();
        dispose();
        new LoanFrame(account, dash);
    }

    private void err(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
