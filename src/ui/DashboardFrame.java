package ui;

import dao.AccountDAO;
import model.Account;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;

public class DashboardFrame extends JFrame {

    private static final DecimalFormat FRW = new DecimalFormat("#,##0.00");

    private final AccountDAO dao = new AccountDAO();
    private Account account;

    private JLabel lblBalance;
    private JLabel lblName;

    public DashboardFrame(Account account) {
        this.account = account;
        setTitle("UoK Bank — " + account.getFullName());
        setSize(520, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        build();
        setVisible(true);
    }

    private void build() {
        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout(0, 0));

        // ── Top nav bar ───────────────────────────────────────────────────────
        JPanel nav = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0,0,UITheme.PRIMARY_DARK,getWidth(),0,UITheme.PRIMARY));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        nav.setPreferredSize(new Dimension(520, 52));
        nav.setBorder(new EmptyBorder(0, 16, 0, 16));

        JPanel navLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        navLeft.setOpaque(false);
        navLeft.add(UITheme.logoBadge(32));
        JLabel navTitle = new JLabel("UoK Bank");
        navTitle.setFont(UITheme.F_HEADING);
        navTitle.setForeground(Color.WHITE);
        navLeft.add(navTitle);
        nav.add(navLeft, BorderLayout.WEST);

        JButton btnLogout = UITheme.btn("Logout", new Color(180,30,30), Color.WHITE);
        btnLogout.setPreferredSize(new Dimension(90, 32));
        btnLogout.addActionListener(e -> { dispose(); new LoginFrame(); });
        JPanel navRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        navRight.setOpaque(false);
        navRight.add(btnLogout);
        nav.add(navRight, BorderLayout.EAST);
        add(nav, BorderLayout.NORTH);

        // ── Balance card ──────────────────────────────────────────────────────
        JPanel balanceCard = new JPanel(new GridLayout(1, 1)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0, UITheme.PRIMARY, getWidth(), getHeight(), new Color(0,90,200)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                // decorative circles
                g2.setColor(new Color(255,255,255,18));
                g2.fillOval(getWidth()-120, -40, 160, 160);
                g2.fillOval(getWidth()-60, getHeight()-60, 120, 120);
            }
        };
        balanceCard.setOpaque(false);
        balanceCard.setPreferredSize(new Dimension(460, 140));
        balanceCard.setBorder(new EmptyBorder(18, 24, 18, 24));

        JPanel cardInner = new JPanel(new GridLayout(4, 1, 0, 2));
        cardInner.setOpaque(false);

        JLabel lblBank = new JLabel("University of Kigali Bank");
        lblBank.setFont(UITheme.F_MICRO);
        lblBank.setForeground(new Color(175,210,255));
        cardInner.add(lblBank);

        lblName = new JLabel(account.getFullName().toUpperCase());
        lblName.setFont(UITheme.F_HEADING);
        lblName.setForeground(Color.WHITE);
        cardInner.add(lblName);

        JLabel lblPhoneType = new JLabel(account.getPhone() + "   |   " + account.getAccountType() + " Account");
        lblPhoneType.setFont(UITheme.F_SMALL);
        lblPhoneType.setForeground(new Color(175,210,255));
        cardInner.add(lblPhoneType);

        lblBalance = new JLabel("FRW  " + FRW.format(account.getBalance()));
        lblBalance.setFont(new Font("Arial", Font.BOLD, 22));
        lblBalance.setForeground(UITheme.GOLD);
        cardInner.add(lblBalance);

        balanceCard.add(cardInner);

        JPanel cardWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 14));
        cardWrapper.setBackground(UITheme.BG);
        cardWrapper.add(balanceCard);
        add(cardWrapper, BorderLayout.CENTER);

        // ── Action grid ───────────────────────────────────────────────────────
        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 12));
        grid.setBackground(UITheme.BG);
        grid.setBorder(new EmptyBorder(0, 20, 14, 20));

        grid.add(actionTile("Deposit",    UITheme.SUCCESS,    "Add funds",       e -> { new DepositFrame(account, this);  }));
        grid.add(actionTile("Withdraw",   UITheme.DANGER,     "Take cash",       e -> { new WithdrawFrame(account, this); }));
        grid.add(actionTile("Transfer",   UITheme.BLUE_ACCENT,"Send money",      e -> { new TransferFrame(account, this); }));
        grid.add(actionTile("History",    UITheme.PURPLE,     "View statements", e -> { new HistoryFrame(account);        }));
        grid.add(actionTile("Loan",       UITheme.WARNING,    "Request credit",  e -> { new LoanFrame(account, this);    }));
        grid.add(actionTile("Agent",      UITheme.TEAL,       "Cash in/out",     e -> { new AgentFrame(account, this);   }));

        // ── Page footer ───────────────────────────────────────────────────────
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(UITheme.BG);
        bottom.add(grid, BorderLayout.CENTER);

        JLabel footer = new JLabel(
            "  Advanced Computer Programming — Dr. Josbert Nteziriza  |  UoK 2026",
            SwingConstants.CENTER);
        footer.setFont(UITheme.F_MICRO);
        footer.setForeground(UITheme.TEXT_MUTED);
        footer.setBorder(new EmptyBorder(4, 0, 8, 0));
        bottom.add(footer, BorderLayout.SOUTH);

        add(bottom, BorderLayout.SOUTH);
    }

    /** Re-fetches account from DB and refreshes the balance card. */
    public void refreshBalance() {
        Account fresh = dao.findByPhone(account.getPhone());
        if (fresh != null) {
            this.account = fresh;
            lblBalance.setText("FRW  " + FRW.format(fresh.getBalance()));
        }
    }

    private JPanel actionTile(String title, Color color, String sub,
                              java.awt.event.ActionListener onClick) {
        JPanel tile = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, 6, getHeight(), 4, 4);
            }
            @Override public boolean isOpaque() { return false; }
        };
        tile.setBorder(new EmptyBorder(12, 16, 12, 12));
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

        JPanel dot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(4, 4, 14, 14);
            }
            @Override public Dimension getPreferredSize() { return new Dimension(22, 22); }
            @Override public boolean isOpaque()           { return false; }
        };
        tile.add(dot, BorderLayout.EAST);

        tile.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { onClick.actionPerformed(null); }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                tile.setBackground(new Color(245, 248, 255)); tile.repaint();
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                tile.setBackground(Color.WHITE); tile.repaint();
            }
        });
        return tile;
    }
}
