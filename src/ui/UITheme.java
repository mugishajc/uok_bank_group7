package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Centralised design tokens and component factory for UoK Bank.
 * Every frame uses this class so the UI stays consistent throughout.
 */
public class UITheme {

    // ── Colour palette ────────────────────────────────────────────────────────
    public static final Color PRIMARY       = new Color(0,  48, 135);   // UoK navy
    public static final Color PRIMARY_DARK  = new Color(0,  25,  80);
    public static final Color PRIMARY_LIGHT = new Color(30, 90, 180);
    public static final Color GOLD          = new Color(200, 169,  81); // UoK gold
    public static final Color SUCCESS       = new Color(27,  153,  83);
    public static final Color DANGER        = new Color(211,  47,  47);
    public static final Color WARNING       = new Color(230, 120,   0);
    public static final Color PURPLE        = new Color(103,  58, 183);
    public static final Color TEAL          = new Color(  0, 137, 123);
    public static final Color BLUE_ACCENT   = new Color( 33, 150, 243);
    public static final Color BG            = new Color(241, 244, 249);
    public static final Color CARD          = Color.WHITE;
    public static final Color TEXT          = new Color( 33,  33,  33);
    public static final Color TEXT_MUTED    = new Color(120, 120, 130);
    public static final Color BORDER        = new Color(218, 222, 230);
    public static final Color ROW_ALT       = new Color(248, 250, 255);

    // ── Typography ────────────────────────────────────────────────────────────
    public static final Font F_DISPLAY = new Font("Arial", Font.BOLD, 26);
    public static final Font F_TITLE   = new Font("Arial", Font.BOLD, 20);
    public static final Font F_HEADING = new Font("Arial", Font.BOLD, 15);
    public static final Font F_BODY    = new Font("Arial", Font.PLAIN, 13);
    public static final Font F_LABEL   = new Font("Arial", Font.BOLD, 12);
    public static final Font F_SMALL   = new Font("Arial", Font.PLAIN, 11);
    public static final Font F_MICRO   = new Font("Arial", Font.ITALIC, 10);
    public static final Font F_MONO    = new Font("Monospaced", Font.BOLD, 14);

    // ── Button factory ────────────────────────────────────────────────────────
    /** Core button with rounded paint — works on all platforms regardless of L&F. */
    public static JButton btn(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = getModel().isPressed()  ? bg.darker()   :
                             getModel().isRollover() ? bg.brighter() : bg;
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(fg);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
            @Override public boolean isOpaque() { return false; }
        };
        b.setFont(F_LABEL);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(200, 40));
        return b;
    }

    public static JButton primaryBtn(String t) { return btn(t, PRIMARY,     Color.WHITE); }
    public static JButton successBtn(String t) { return btn(t, SUCCESS,     Color.WHITE); }
    public static JButton dangerBtn (String t) { return btn(t, DANGER,      Color.WHITE); }
    public static JButton warningBtn(String t) { return btn(t, WARNING,     Color.WHITE); }
    public static JButton grayBtn   (String t) { return btn(t, new Color(100,100,100), Color.WHITE); }
    public static JButton tealBtn   (String t) { return btn(t, TEAL,        Color.WHITE); }
    public static JButton purpleBtn (String t) { return btn(t, PURPLE,      Color.WHITE); }
    public static JButton goldBtn   (String t) { return btn(t, GOLD,        PRIMARY_DARK); }

    /** Convenience: button with inline listener. */
    public static JButton actionBtn(String text, Color bg, ActionListener l) {
        JButton b = btn(text, bg, Color.WHITE);
        b.addActionListener(l);
        return b;
    }

    // ── Input fields ──────────────────────────────────────────────────────────
    public static JTextField field() {
        JTextField f = new JTextField();
        f.setFont(F_BODY);
        f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(8, 11, 8, 11)));
        return f;
    }

    /** PIN field: max 5 digits, monospace, masked. */
    public static JPasswordField pinField() {
        JPasswordField f = new JPasswordField() {
            @Override public Dimension getPreferredSize() {
                return new Dimension(super.getPreferredSize().width, 42);
            }
        };
        f.setFont(F_MONO);
        f.setDocument(new javax.swing.text.PlainDocument() {
            @Override
            public void insertString(int off, String s, javax.swing.text.AttributeSet a)
                    throws javax.swing.text.BadLocationException {
                if (s == null) return;
                String digits = s.replaceAll("[^0-9]", "");
                if (getLength() + digits.length() <= 5) super.insertString(off, digits, a);
            }
        });
        f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(8, 11, 8, 11)));
        return f;
    }

    public static JComboBox<String> combo(String... items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(F_BODY);
        cb.setBackground(Color.WHITE);
        return cb;
    }

    // ── Labels ────────────────────────────────────────────────────────────────
    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_LABEL);
        l.setForeground(TEXT);
        return l;
    }

    public static JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(new Font("Arial", Font.BOLD, 10));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    // ── UoK logo badge (pure Java2D, no image file needed) ───────────────────
    public static JPanel logoBadge(int size) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gold outer ring
                g2.setColor(GOLD);
                g2.fillOval(0, 0, size, size);
                // Navy fill
                g2.setColor(PRIMARY);
                g2.fillOval(4, 4, size - 8, size - 8);
                // "UoK" text in gold
                g2.setColor(GOLD);
                int fs = Math.max(size / 4, 10);
                g2.setFont(new Font("Arial", Font.BOLD, fs));
                FontMetrics fm = g2.getFontMetrics();
                String t = "UoK";
                g2.drawString(t, (size - fm.stringWidth(t)) / 2,
                              (size + fm.getAscent() - fm.getDescent()) / 2);
            }
            @Override public Dimension getPreferredSize() { return new Dimension(size, size); }
            @Override public boolean isOpaque()           { return false; }
        };
    }

    // ── Page header bar (gradient, badge, title/subtitle) ────────────────────
    public static JPanel pageHeader(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, PRIMARY, getWidth(), 0, PRIMARY_DARK));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(600, 68));
        panel.setBorder(new EmptyBorder(0, 16, 0, 16));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        left.setOpaque(false);
        left.add(logoBadge(44));

        JPanel textCol = new JPanel(new GridLayout(2, 1, 0, 3));
        textCol.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(F_HEADING); t.setForeground(Color.WHITE);
        JLabel s = new JLabel(subtitle);
        s.setFont(F_SMALL);   s.setForeground(new Color(175, 205, 255));
        textCol.add(t); textCol.add(s);
        left.add(textCol);

        panel.add(left, BorderLayout.WEST);
        return panel;
    }

    // ── Card / surface panel ──────────────────────────────────────────────────
    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(16, 20, 16, 20)));
        return p;
    }

    // ── Styled table ─────────────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setFont(F_BODY);
        table.getTableHeader().setFont(F_LABEL);
        table.getTableHeader().setBackground(new Color(230, 235, 248));
        table.getTableHeader().setForeground(PRIMARY_DARK);
        table.setRowHeight(28);
        table.setGridColor(BORDER);
        table.setSelectionBackground(new Color(210, 225, 255));
        table.setSelectionForeground(PRIMARY_DARK);
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);
    }

    // ── Divider ───────────────────────────────────────────────────────────────
    public static JSeparator divider() {
        JSeparator s = new JSeparator();
        s.setForeground(BORDER);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }
}
