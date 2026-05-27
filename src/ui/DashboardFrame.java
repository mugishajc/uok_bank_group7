package ui;

import dao.AccountDAO;
import model.Account;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class DashboardFrame extends JFrame {

    private static final DecimalFormat FRW = new DecimalFormat("#,##0.00");

    private final AccountDAO dao = new AccountDAO();
    private Account account;
    private JLabel lblBalance;

    public DashboardFrame(Account account) {
        this.account = account;
        setTitle("UoK Bank — " + account.getFullName());
        setSize(520, 620);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage(UITheme.appIcon());
        build();
        setVisible(true);
    }

    private void build() {
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout(0, 0));

        // ── Top nav bar ───────────────────────────────────────────────────
        JPanel nav = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, UITheme.PRIMARY_DARK,
                                             getWidth(), 0, UITheme.PRIMARY));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        nav.setPreferredSize(new Dimension(520, 52));
        nav.setBorder(new EmptyBorder(0, 14, 0, 14));

        JPanel navLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        navLeft.setOpaque(false);
        navLeft.add(UITheme.logoBadge(32));
        JLabel navTitle = new JLabel("UoK Bank");
        navTitle.setFont(UITheme.F_HEADING);
        navTitle.setForeground(Color.WHITE);
        navLeft.add(navTitle);
        JLabel navSub = new JLabel("| University of Kigali");
        navSub.setFont(UITheme.F_SMALL);
        navSub.setForeground(new Color(255, 210, 195));
        navLeft.add(navSub);
        nav.add(navLeft, BorderLayout.WEST);

        JPanel navRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        navRight.setOpaque(false);
        JButton btnLogout = UITheme.btn("Logout", UITheme.DANGER, Color.WHITE);
        btnLogout.setPreferredSize(new Dimension(90, 32));
        btnLogout.addActionListener(e -> { dispose(); new LoginFrame(); });
        navRight.add(btnLogout);
        nav.add(navRight, BorderLayout.EAST);
        add(nav, BorderLayout.NORTH);

        // ── Balance card ──────────────────────────────────────────────────
        JPanel balanceCard = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // UoK maroon → royal blue gradient (bank card look)
                g2.setPaint(new GradientPaint(0, 0, UITheme.PRIMARY_DARK,
                                             getWidth(), getHeight(), UITheme.UOK_BLUE));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                // Gold top stripe
                g2.setColor(UITheme.GOLD);
                g2.fillRoundRect(0, 0, getWidth(), 6, 6, 6);
                // Subtle highlight circles
                g2.setColor(new Color(255, 255, 255, 14));
                g2.fillOval(getWidth() - 110, -35, 170, 170);
                g2.fillOval(getWidth() - 45,  getHeight() - 55, 110, 110);
            }
        };
        balanceCard.setOpaque(false);
        balanceCard.setPreferredSize(new Dimension(0, 160));
        balanceCard.setBorder(new EmptyBorder(14, 20, 14, 18));

        // Text column (left side of card)
        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));

        JLabel lblBank = new JLabel("UNIVERSITY OF KIGALI BANK");
        lblBank.setFont(new Font("Arial", Font.BOLD, 9));
        lblBank.setForeground(UITheme.GOLD);
        textStack.add(lblBank);
        textStack.add(Box.createVerticalStrut(4));

        JLabel lblName = new JLabel(account.getFullName().toUpperCase());
        lblName.setFont(new Font("Arial", Font.BOLD, 15));
        lblName.setForeground(Color.WHITE);
        textStack.add(lblName);
        textStack.add(Box.createVerticalStrut(3));

        JLabel lblPhoneType = new JLabel(account.getAccountType() + " Account");
        lblPhoneType.setFont(UITheme.F_SMALL);
        lblPhoneType.setForeground(new Color(255, 205, 190));
        textStack.add(lblPhoneType);
        textStack.add(Box.createVerticalGlue());

        JLabel lblBalLabel = new JLabel("AVAILABLE BALANCE");
        lblBalLabel.setFont(new Font("Arial", Font.BOLD, 9));
        lblBalLabel.setForeground(new Color(255, 205, 190));
        textStack.add(lblBalLabel);
        textStack.add(Box.createVerticalStrut(2));

        lblBalance = new JLabel("FRW  " + FRW.format(account.getBalance()));
        lblBalance.setFont(new Font("Arial", Font.BOLD, 24));
        lblBalance.setForeground(UITheme.GOLD_BRIGHT);
        textStack.add(lblBalance);

        balanceCard.add(textStack, BorderLayout.CENTER);

        // Mini UoK badge (right side of card)
        JPanel logoWrapper = new JPanel(new GridBagLayout());
        logoWrapper.setOpaque(false);
        logoWrapper.add(UITheme.logoBadge(42));
        balanceCard.add(logoWrapper, BorderLayout.EAST);

        // ── Action grid ───────────────────────────────────────────────────
        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 12));
        grid.setBackground(UITheme.BG);
        grid.setBorder(new EmptyBorder(0, 20, 8, 20));

        grid.add(tile("Deposit",  "Add funds",       UITheme.SUCCESS,     e -> new DepositFrame(account, this)));
        grid.add(tile("Withdraw", "Take cash",       UITheme.DANGER,      e -> new WithdrawFrame(account, this)));
        grid.add(tile("Transfer", "Send money",      UITheme.BLUE_ACCENT, e -> new TransferFrame(account, this)));
        grid.add(tile("History",  "View statements", UITheme.PURPLE,      e -> new HistoryFrame(account)));
        grid.add(tile("Loan",     "Request credit",  UITheme.WARNING,     e -> new LoanFrame(account, this)));
        grid.add(tile("Agent",    "Cash in / out",   UITheme.TEAL,        e -> new AgentFrame(account, this)));

        // Center: balance card + tile grid stacked, grid expands to fill
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(UITheme.BG);
        GridBagConstraints gc = new GridBagConstraints();

        gc.gridx = 0; gc.gridy = 0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0; gc.weighty = 0.0;
        gc.insets = new Insets(14, 20, 0, 20);
        center.add(balanceCard, gc);

        gc.gridy = 1;
        gc.fill = GridBagConstraints.BOTH;
        gc.weighty = 1.0;
        gc.insets = new Insets(12, 0, 0, 0);
        center.add(grid, gc);

        add(center, BorderLayout.CENTER);
        add(UITheme.footer(), BorderLayout.SOUTH);
    }

    public void refreshBalance() {
        Account fresh = dao.findByPhone(account.getPhone());
        if (fresh != null) {
            account = fresh;
            lblBalance.setText("FRW  " + FRW.format(fresh.getBalance()));
        }
    }

    private JPanel tile(String title, String sub, Color accent, ActionListener onClick) {
        JPanel tile = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                // Left accent bar
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 5, getHeight(), 4, 4);
                // Top accent strip
                g2.fillRoundRect(0, 0, getWidth(), 3, 3, 3);
            }
            @Override public boolean isOpaque() { return false; }
        };
        tile.setBorder(new EmptyBorder(12, 14, 12, 12));
        tile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lTitle = new JLabel(title);
        lTitle.setFont(UITheme.F_LABEL);
        lTitle.setForeground(UITheme.TEXT);
        JLabel lSub = new JLabel(sub);
        lSub.setFont(UITheme.F_MICRO);
        lSub.setForeground(UITheme.TEXT_MUTED);

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setOpaque(false);
        text.add(lTitle); text.add(lSub);
        tile.add(text, BorderLayout.CENTER);

        // Coloured dot indicator
        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accent);
                g2.fillOval(2, 2, 16, 16);
                g2.setColor(new Color(255,255,255,90));
                g2.fillOval(4, 4, 6, 6);
            }
            @Override public Dimension getPreferredSize() { return new Dimension(20, 20); }
            @Override public boolean   isOpaque()         { return false; }
        };
        tile.add(dot, BorderLayout.EAST);

        tile.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                onClick.actionPerformed(null);
            }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                tile.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accent, 1),
                    new EmptyBorder(11, 13, 11, 11)));
                tile.repaint();
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                tile.setBorder(new EmptyBorder(12, 14, 12, 12));
                tile.repaint();
            }
        });
        return tile;
    }
}
