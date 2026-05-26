package ui;

import dao.AccountDAO;
import dao.LoanDAO;
import dao.TransactionDAO;
import model.Account;
import model.Loan;
import model.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class AdminFrame extends JFrame {

    private static final DecimalFormat FRW = new DecimalFormat("#,##0.00");

    private final AccountDAO     accDao  = new AccountDAO();
    private final LoanDAO        loanDao = new LoanDAO();
    private final TransactionDAO txnDao  = new TransactionDAO();
    private final Account        admin;

    public AdminFrame(Account admin) {
        this.admin = admin;
        setTitle("Admin Dashboard — UoK Bank");
        setSize(880, 620);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        build();
        setVisible(true);
    }

    private void build() {
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout(0, 0));

        // ── Top nav ───────────────────────────────────────────────────────────
        JPanel nav = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0,0, UITheme.PRIMARY_DARK, getWidth(),0, new Color(20,60,140)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        nav.setPreferredSize(new Dimension(880, 56));
        nav.setBorder(new EmptyBorder(0, 18, 0, 18));

        JPanel navLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 12));
        navLeft.setOpaque(false);
        navLeft.add(UITheme.logoBadge(34));
        JLabel navTitle = new JLabel("UoK Bank — Admin Dashboard");
        navTitle.setFont(UITheme.F_HEADING); navTitle.setForeground(Color.WHITE);
        navLeft.add(navTitle);

        JLabel adminBadge = new JLabel("  ADMIN: " + admin.getFullName() + "  ");
        adminBadge.setFont(new Font("Arial", Font.BOLD, 11));
        adminBadge.setForeground(UITheme.PRIMARY_DARK);
        adminBadge.setBackground(UITheme.GOLD);
        adminBadge.setOpaque(true);
        adminBadge.setBorder(new EmptyBorder(3, 8, 3, 8));
        navLeft.add(adminBadge);

        nav.add(navLeft, BorderLayout.WEST);

        JPanel navRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        navRight.setOpaque(false);
        JButton btnLogout = UITheme.btn("Logout", new Color(180,30,30), Color.WHITE);
        btnLogout.setPreferredSize(new Dimension(90, 32));
        btnLogout.addActionListener(e -> { dispose(); new LoginFrame(); });
        navRight.add(btnLogout);
        nav.add(navRight, BorderLayout.EAST);
        add(nav, BorderLayout.NORTH);

        // ── Tabs ──────────────────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.F_LABEL);
        tabs.setBackground(UITheme.BG);
        tabs.setForeground(UITheme.TEXT);
        tabs.addTab("  Accounts  ",  buildAccountsTab());
        tabs.addTab("  Loans     ",  buildLoansTab());
        tabs.addTab("  All Transactions  ", buildTransactionsTab());
        add(tabs, BorderLayout.CENTER);
    }

    // ── Accounts tab ──────────────────────────────────────────────────────────
    private JPanel buildAccountsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG);

        String[] cols = {"Phone", "Full Name", "Type", "Balance (FRW)", "Role", "Frozen"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<Account> accounts = accDao.getAll();
        for (Account a : accounts) {
            model.addRow(new Object[]{
                a.getPhone(), a.getFullName(), a.getAccountType(),
                FRW.format(a.getBalance()), a.getRole(), a.isFrozen() ? "YES" : "NO"
            });
        }

        JTable table = new JTable(model);
        UITheme.styleTable(table);

        // Colour "Frozen" column
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel,
                                                           boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setForeground("YES".equals(val) ? UITheme.DANGER : UITheme.SUCCESS);
                setFont(UITheme.F_LABEL);
                return this;
            }
        });

        int[] widths = {120, 180, 90, 130, 80, 70};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(10, 14, 0, 14));
        scroll.getViewport().setBackground(UITheme.CARD);
        panel.add(scroll, BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        btnRow.setBackground(UITheme.BG);

        JButton btnFreeze = UITheme.dangerBtn("Freeze / Unfreeze");
        btnFreeze.setPreferredSize(new Dimension(180, 36));
        btnFreeze.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { msg("Select an account first."); return; }
            String phone = (String) model.getValueAt(row, 0);
            Account acc  = accDao.findByPhone(phone);
            if (acc == null) return;
            boolean nowFrozen = !acc.isFrozen();
            accDao.setFrozen(phone, nowFrozen);
            model.setValueAt(nowFrozen ? "YES" : "NO", row, 5);
            msg("Account " + (nowFrozen ? "frozen" : "unfrozen") + " successfully.");
        });
        btnRow.add(btnFreeze);
        panel.add(btnRow, BorderLayout.SOUTH);
        return panel;
    }

    // ── Loans tab ─────────────────────────────────────────────────────────────
    private JPanel buildLoansTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG);

        String[] cols = {"ID", "Phone", "Amount (FRW)", "Status", "Requested At"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<Loan> loans = loanDao.getAll();
        for (Loan l : loans) {
            model.addRow(new Object[]{
                l.getId(), l.getPhone(), FRW.format(l.getAmount()),
                l.getStatus(), l.getRequestedAt()
            });
        }

        JTable table = new JTable(model);
        UITheme.styleTable(table);

        // Colour status column
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel,
                                                           boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String s = val == null ? "" : val.toString();
                setForeground(switch (s) {
                    case "APPROVED" -> UITheme.SUCCESS;
                    case "REJECTED" -> UITheme.DANGER;
                    case "REPAID"   -> UITheme.PRIMARY;
                    default         -> UITheme.WARNING;
                });
                setFont(UITheme.F_LABEL);
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(10, 14, 0, 14));
        scroll.getViewport().setBackground(UITheme.CARD);
        panel.add(scroll, BorderLayout.CENTER);

        // Approve / Reject buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        btnRow.setBackground(UITheme.BG);

        JButton btnApprove = UITheme.successBtn("Approve & Disburse");
        btnApprove.setPreferredSize(new Dimension(180, 36));
        btnApprove.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { msg("Select a loan first."); return; }
            if (!"PENDING".equals(model.getValueAt(row, 3))) {
                msg("Only PENDING loans can be approved.");
                return;
            }
            int loanId = (int) model.getValueAt(row, 0);
            String phone = (String) model.getValueAt(row, 1);
            double amount = loans.stream().filter(l -> l.getId() == loanId)
                .mapToDouble(Loan::getAmount).findFirst().orElse(0);
            Account acc = accDao.findByPhone(phone);
            if (acc != null && amount > 0) {
                accDao.updateBalance(phone, acc.getBalance() + amount);
                txnDao.record(null, phone, "LOAN_DISBURSEMENT", amount,
                    "Loan approved and disbursed");
            }
            loanDao.updateStatus(loanId, "APPROVED");
            model.setValueAt("APPROVED", row, 3);
            msg("Loan approved and FRW " + FRW.format(amount) + " disbursed to " +
                (acc != null ? acc.getFullName() : phone) + ".");
        });
        btnRow.add(btnApprove);

        JButton btnReject = UITheme.dangerBtn("Reject");
        btnReject.setPreferredSize(new Dimension(130, 36));
        btnReject.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { msg("Select a loan first."); return; }
            if (!"PENDING".equals(model.getValueAt(row, 3))) {
                msg("Only PENDING loans can be rejected.");
                return;
            }
            int loanId = (int) model.getValueAt(row, 0);
            loanDao.updateStatus(loanId, "REJECTED");
            model.setValueAt("REJECTED", row, 3);
            msg("Loan rejected.");
        });
        btnRow.add(btnReject);
        panel.add(btnRow, BorderLayout.SOUTH);
        return panel;
    }

    // ── All Transactions tab ──────────────────────────────────────────────────
    private JPanel buildTransactionsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG);

        String[] cols = {"#", "Type", "From", "To", "Amount (FRW)", "Date & Time", "Note"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        int i = 1;
        for (Transaction t : txnDao.getAll()) {
            model.addRow(new Object[]{
                i++, t.getType(),
                t.getSenderPhone()   == null ? "—" : t.getSenderPhone(),
                t.getReceiverPhone() == null ? "—" : t.getReceiverPhone(),
                FRW.format(t.getAmount()), t.getTimestamp(),
                t.getNote() == null ? "" : t.getNote()
            });
        }

        JTable table = new JTable(model);
        UITheme.styleTable(table);

        // Colour type column
        table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel,
                                                           boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String type = val == null ? "" : val.toString();
                setForeground(switch (type) {
                    case "DEPOSIT", "AGENT_CASH_IN", "LOAN_DISBURSEMENT" -> UITheme.SUCCESS;
                    case "WITHDRAW", "AGENT_CASH_OUT", "LOAN_REPAY"      -> UITheme.DANGER;
                    case "TRANSFER"                                        -> UITheme.BLUE_ACCENT;
                    default                                                -> UITheme.TEXT;
                });
                setFont(UITheme.F_LABEL);
                return this;
            }
        });

        int[] widths = {35, 130, 110, 110, 110, 135, 120};
        for (int j = 0; j < widths.length; j++)
            table.getColumnModel().getColumn(j).setPreferredWidth(widths[j]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(10, 14, 10, 14));
        scroll.getViewport().setBackground(UITheme.CARD);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void msg(String m) {
        JOptionPane.showMessageDialog(this, m, "Admin Action", JOptionPane.INFORMATION_MESSAGE);
    }
}
