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
        setResizable(false);
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
        navSub.setForeground(new Color(160, 195, 255));
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
        JPanel balanceCard = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Navy-to-blue gradient
                g2.setPaint(new GradientPaint(0, 0, UITheme.PRIMARY,
                                             getWidth(), getHeight(), UITheme.PRIMARY_LIGHT));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                // Gold accent stripe at top
                g2.setColor(UITheme.GOLD);
                g2.fillRoundRect(0, 0, getWidth(), 6, 6, 6);
                // Decorative gold ring (background)
                g2.setColor(new Color(255,255,255,15));
                g2.fillOval(getWidth()-100, -30, 160, 160);
                g2.fillOval(getWidth()-40, getHeight()-50, 100, 100);
                // UoK shield (watermark)
                Graphics2D g2w = (Graphics2D) g2.create();
                g2w.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f));
                // draw a small shield watermark at right side
                g2w.dispose();
            }
        };
        balanceCard.setOpaque(false);
        balanceCard.setPreferredSize(new Dimension(460, 145));

        // Labels positioned absolutely inside card
        JLabel lblBank = new JLabel("UNIVERSITY OF KIGALI BANK");
        lblBank.setFont(new Font("Arial", Font.BOLD, 9));
        lblBank.setForeground(UITheme.GOLD);
        lblBank.setBounds(20, 16, 280, 14);
        balanceCard.add(lblBank);

        JLabel lblName = new JLabel(account.getFullName().toUpperCase());
        lblName.setFont(new Font("Arial", Font.BOLD, 15));
        lblName.setForeground(Color.WHITE);
        lblName.setBounds(20, 34, 340, 20);
        balanceCard.add(lblName);

        JLabel lblPhoneType = new JLabel(account.getPhone() + "   ·   " + account.getAccountType() + " Account");
        lblPhoneType.setFont(UITheme.F_SMALL);
        lblPhoneType.setForeground(new Color(180, 210, 255));
        lblPhoneType.setBounds(20, 58, 340, 16);
        balanceCard.add(lblPhoneType);

        JLabel lblBalLabel = new JLabel("AVAILABLE BALANCE");
        lblBalLabel.setFont(new Font("Arial", Font.BOLD, 9));
        lblBalLabel.setForeground(new Color(180, 210, 255));
        lblBalLabel.setBounds(20, 82, 200, 12);
        balanceCard.add(lblBalLabel);

        lblBalance = new JLabel("FRW  " + FRW.format(account.getBalance()));
        lblBalance.setFont(new Font("Arial", Font.BOLD, 24));
        lblBalance.setForeground(UITheme.GOLD_BRIGHT);
        lblBalance.setBounds(20, 96, 360, 32);
        balanceCard.add(lblBalance);

        // Mini UoK badge on card
        JPanel miniLogo = UITheme.logoBadge(38);
        miniLogo.setBounds(408, 52, 38, 38);
        balanceCard.add(miniLogo);

        JPanel cardWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 14));
        cardWrapper.setBackground(UITheme.BG);
        cardWrapper.add(balanceCard);
        add(cardWrapper, BorderLayout.CENTER);

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

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(UITheme.BG);
        bottom.add(grid, BorderLayout.CENTER);
        bottom.add(UITheme.footer(), BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
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
