package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

/**
 * Centralised design system for UoK Bank.
 * Colours, fonts, components — every screen pulls from here.
 */
public class UITheme {

    // ── University of Kigali official colour palette ───────────────────────
    public static final Color PRIMARY       = new Color(  0,  46, 110);  // UoK navy blue
    public static final Color PRIMARY_DARK  = new Color(  0,  25,  70);
    public static final Color PRIMARY_LIGHT = new Color(  0,  80, 170);
    public static final Color GOLD          = new Color(196, 158,  50);  // UoK gold
    public static final Color GOLD_BRIGHT   = new Color(240, 200,  60);
    public static final Color GOLD_DARK     = new Color(150, 115,  25);
    public static final Color SUCCESS       = new Color( 39, 125,  61);  // semantic only (deposit)
    public static final Color DANGER        = new Color(192,  57,  43);  // semantic only (withdraw)
    public static final Color WARNING       = new Color(211, 105,   0);  // loans
    public static final Color PURPLE        = new Color( 90,  50, 160);  // history
    public static final Color TEAL          = new Color(  0, 119, 107);  // agent
    public static final Color BLUE_ACCENT   = new Color( 21, 101, 192);  // transfer
    public static final Color BG            = new Color(240, 243, 250);
    public static final Color CARD          = Color.WHITE;
    public static final Color TEXT          = new Color( 30,  30,  40);
    public static final Color TEXT_MUTED    = new Color(120, 125, 140);
    public static final Color BORDER        = new Color(210, 215, 228);

    // ── Typography ─────────────────────────────────────────────────────────
    public static final Font F_DISPLAY = new Font("Arial", Font.BOLD, 26);
    public static final Font F_TITLE   = new Font("Arial", Font.BOLD, 20);
    public static final Font F_HEADING = new Font("Arial", Font.BOLD, 15);
    public static final Font F_BODY    = new Font("Arial", Font.PLAIN, 13);
    public static final Font F_LABEL   = new Font("Arial", Font.BOLD, 12);
    public static final Font F_SMALL   = new Font("Arial", Font.PLAIN, 11);
    public static final Font F_MICRO   = new Font("Arial", Font.ITALIC, 10);
    public static final Font F_MONO    = new Font("Monospaced", Font.BOLD, 14);

    // ── Button factory ─────────────────────────────────────────────────────
    public static JButton btn(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = getModel().isPressed()  ? bg.darker()    :
                             getModel().isRollover() ? bg.brighter()  : bg;
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
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

    // Named button shortcuts — UoK colours are PRIMARY and GOLD
    public static JButton primaryBtn(String t) { return btn(t, PRIMARY,      Color.WHITE);      }
    public static JButton goldBtn   (String t) { return btn(t, GOLD,         PRIMARY_DARK);     }
    public static JButton successBtn(String t) { return btn(t, SUCCESS,      Color.WHITE);      }
    public static JButton dangerBtn (String t) { return btn(t, DANGER,       Color.WHITE);      }
    public static JButton warningBtn(String t) { return btn(t, WARNING,      Color.WHITE);      }
    public static JButton grayBtn   (String t) { return btn(t, new Color(100,100,110), Color.WHITE); }
    public static JButton tealBtn   (String t) { return btn(t, TEAL,         Color.WHITE);      }
    public static JButton purpleBtn (String t) { return btn(t, PURPLE,       Color.WHITE);      }

    public static JButton actionBtn(String text, Color bg, ActionListener l) {
        JButton b = btn(text, bg, Color.WHITE);
        b.addActionListener(l);
        return b;
    }

    // ── Input fields ───────────────────────────────────────────────────────
    public static JTextField field() {
        JTextField f = new JTextField();
        f.setFont(F_BODY);
        f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(8, 11, 8, 11)));
        return f;
    }

    public static JPasswordField pinField() {
        JPasswordField f = new JPasswordField();
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

    // ── Labels ─────────────────────────────────────────────────────────────
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

    // ── University of Kigali Shield Logo (pure Java2D) ─────────────────────
    /**
     * Draws a university-style shield badge with torch, book, and "UoK" lettering.
     * No external image files required.
     */
    public static JPanel logoBadge(int size) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                drawUoKShield(g2, 0, 0, size);
            }
            @Override public Dimension getPreferredSize() { return new Dimension(size, size); }
            @Override public boolean   isOpaque()         { return false; }
        };
    }

    /** Renders the UoK shield crest at the given position and size. */
    private static void drawUoKShield(Graphics2D g2, int ox, int oy, int size) {
        double s  = size;
        double cx = ox + s / 2.0;

        // ── Shield outline (gold fill then navy fill for border effect) ──
        Path2D.Double shield = shieldPath(ox, oy, size);
        g2.setColor(GOLD);
        g2.fill(shield);

        Path2D.Double innerShield = shieldPath((int)(ox + s*0.07), (int)(oy + s*0.07),
                                               (int)(s * 0.86));
        g2.setColor(PRIMARY);
        g2.fill(innerShield);

        // ── Torch flame (top) ───────────────────────────────────────────
        g2.setColor(GOLD_BRIGHT);
        int fW = (int)(s * 0.12); int fH = (int)(s * 0.14);
        int fX = (int)(cx - fW / 2.0); int fY = (int)(oy + s * 0.10);
        // Flame shape: teardrop
        g2.fillOval(fX, fY + fH/3, fW, fH * 2/3);
        int[] px = {fX, (int)cx, fX + fW};
        int[] py = {fY + fH/2, fY, fY + fH/2};
        g2.fillPolygon(px, py, 3);

        // Torch handle
        g2.setColor(GOLD);
        int tW = (int)(s * 0.07); int tH = (int)(s * 0.14);
        int tX = (int)(cx - tW / 2.0); int tY = fY + fH - 2;
        g2.fillRoundRect(tX, tY, tW, tH, 3, 3);

        // ── Horizontal divider ──────────────────────────────────────────
        g2.setColor(GOLD);
        int divY  = (int)(oy + s * 0.52);
        int divX1 = (int)(ox + s * 0.18);
        int divX2 = (int)(ox + s * 0.82);
        g2.setStroke(new BasicStroke(Math.max(1.5f, (float)(s * 0.025))));
        g2.drawLine(divX1, divY, divX2, divY);
        g2.setStroke(new BasicStroke(1f));

        // ── "UoK" text ──────────────────────────────────────────────────
        g2.setColor(GOLD);
        int fs = Math.max((int)(s * 0.22), 8);
        g2.setFont(new Font("Arial", Font.BOLD, fs));
        FontMetrics fm = g2.getFontMetrics();
        String uok = "UoK";
        g2.drawString(uok, (int)(cx - fm.stringWidth(uok) / 2.0),
                      (int)(oy + s * 0.76));

        // ── Small star dots (decorative) ────────────────────────────────
        if (size >= 48) {
            g2.setColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 180));
            int dotR = Math.max(2, (int)(s * 0.04));
            g2.fillOval((int)(cx - s*0.28) - dotR, (int)(oy + s*0.62) - dotR, dotR*2, dotR*2);
            g2.fillOval((int)(cx + s*0.24) - dotR, (int)(oy + s*0.62) - dotR, dotR*2, dotR*2);
        }
    }

    /** Builds a classic heraldic shield Path2D that fits in (x,y,size,size). */
    private static Path2D.Double shieldPath(int x, int y, int size) {
        double s = size;
        double l = x, r = x + s, t = y, b = y + s;
        double mx = x + s / 2.0;
        Path2D.Double p = new Path2D.Double();
        p.moveTo(l, t);
        p.lineTo(r, t);
        p.lineTo(r, t + s * 0.62);
        // Curve down to bottom point
        p.curveTo(r, t + s * 0.87, mx, b, mx, b);
        p.curveTo(mx, b, l, t + s * 0.87, l, t + s * 0.62);
        p.closePath();
        return p;
    }

    // ── App window icon (BufferedImage for JFrame.setIconImage) ────────────
    public static Image appIcon() {
        int s = 64;
        BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        drawUoKShield(g2, 0, 0, s);
        g2.dispose();
        return img;
    }

    // ── Page header bar ────────────────────────────────────────────────────
    public static JPanel pageHeader(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, PRIMARY_DARK, getWidth(), 0, PRIMARY));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(600, 70));
        panel.setBorder(new EmptyBorder(0, 16, 0, 16));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 13));
        left.setOpaque(false);
        left.add(logoBadge(44));

        JPanel textCol = new JPanel(new GridLayout(2, 1, 0, 3));
        textCol.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(F_HEADING); t.setForeground(Color.WHITE);
        JLabel s = new JLabel(subtitle);
        s.setFont(F_SMALL);   s.setForeground(new Color(175, 210, 255));
        textCol.add(t); textCol.add(s);
        left.add(textCol);

        panel.add(left, BorderLayout.WEST);
        return panel;
    }

    // ── Footer — every screen uses this ────────────────────────────────────
    public static JPanel footer() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(228, 233, 245));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            new EmptyBorder(5, 14, 5, 14)));

        JLabel left = new JLabel("MSc.IT — Advanced Programming with Java");
        left.setFont(F_MICRO);
        left.setForeground(PRIMARY);

        JLabel right = new JLabel("University of Kigali  ·  Group 7  ·  2026");
        right.setFont(F_MICRO);
        right.setForeground(TEXT_MUTED);

        p.add(left,  BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    // ── Card / surface panel ───────────────────────────────────────────────
    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(16, 20, 16, 20)));
        return p;
    }

    // ── Table styling ──────────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setFont(F_BODY);
        table.getTableHeader().setFont(F_LABEL);
        table.getTableHeader().setBackground(new Color(220, 228, 248));
        table.getTableHeader().setForeground(PRIMARY_DARK);
        table.setRowHeight(28);
        table.setGridColor(BORDER);
        table.setSelectionBackground(new Color(200, 218, 255));
        table.setSelectionForeground(PRIMARY_DARK);
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);
    }

    // ── Divider ────────────────────────────────────────────────────────────
    public static JSeparator divider() {
        JSeparator s = new JSeparator();
        s.setForeground(BORDER);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }
}
