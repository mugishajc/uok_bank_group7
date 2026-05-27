package ui;

import dao.TransactionDAO;
import model.Account;
import model.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class HistoryFrame extends JFrame {

    private static final DecimalFormat FRW = new DecimalFormat("#,##0.00");

    private final TransactionDAO dao = new TransactionDAO();
    private final Account        account;

    public HistoryFrame(Account account) {
        this.account = account;
        setTitle("Transaction History — UoK Bank");
        setSize(760, 520);
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

        add(UITheme.pageHeader("Transaction History",
            "All activity for " + account.getFullName()), BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────────────────
        String[] cols = {"#", "Type", "From", "To", "Amount (FRW)", "Date & Time", "Note"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<Transaction> txns = dao.getByPhone(account.getPhone());
        int i = 1;
        for (Transaction t : txns) {
            model.addRow(new Object[]{
                i++,
                t.getType(),
                t.getSenderPhone()   == null ? "—" : t.getSenderPhone(),
                t.getReceiverPhone() == null ? "—" : t.getReceiverPhone(),
                FRW.format(t.getAmount()),
                t.getTimestamp(),
                t.getNote() == null ? "" : t.getNote()
            });
        }

        JTable table = new JTable(model);
        UITheme.styleTable(table);

        // Colour-code by type
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

        // Column widths
        int[] widths = {35, 120, 115, 115, 110, 135, 120};
        for (int j = 0; j < widths.length; j++)
            table.getColumnModel().getColumn(j).setPreferredWidth(widths[j]);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(10, 14, 0, 14));
        scroll.getViewport().setBackground(UITheme.CARD);
        add(scroll, BorderLayout.CENTER);

        // ── Footer ────────────────────────────────────────────────────────────
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(UITheme.BG);
        bottom.setBorder(new EmptyBorder(8, 14, 10, 14));

        JLabel count = new JLabel("Total: " + txns.size() + " transaction(s)");
        count.setFont(UITheme.F_SMALL);
        count.setForeground(UITheme.TEXT_MUTED);
        bottom.add(count, BorderLayout.WEST);

        JButton btnClose = UITheme.grayBtn("Close");
        btnClose.setPreferredSize(new Dimension(100, 34));
        btnClose.addActionListener(e -> dispose());
        bottom.add(btnClose, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }
}
