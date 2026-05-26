package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public class UITheme {

    // ── University of Kigali official colour palette ───────────────────────
    public static final Color PRIMARY        = new Color(120, 18,  40);  // UoK maroon
    public static final Color PRIMARY_DARK   = new Color( 68,  5,  18);  // dark maroon
    public static final Color PRIMARY_LIGHT  = new Color(165, 35,  62);  // lighter maroon
    public static final Color UOK_BLUE       = new Color( 25, 78, 162);  // UoK royal blue
    public static final Color UOK_BLUE_LIGHT = new Color( 50,128, 210);  // sky blue accent
    public static final Color GOLD           = new Color(196, 158,  50);  // UoK gold
    public static final Color GOLD_BRIGHT    = new Color(240, 200,  60);
    public static final Color GOLD_DARK      = new Color(140, 108,  22);
    public static final Color SUCCESS        = new Color( 39, 125,  61);  // deposit
    public static final Color DANGER         = new Color(192,  57,  43);  // withdraw
    public static final Color WARNING        = new Color(211, 105,   0);  // loans
    public static final Color PURPLE         = new Color( 90,  50, 160);  // history
    public static final Color TEAL           = new Color(  0, 119, 107);  // agent
    public static final Color BLUE_ACCENT    = new Color( 21, 101, 192);  // transfer
    public static final Color BG             = new Color(240, 243, 250);
    public static final Color CARD           = Color.WHITE;
    public static final Color TEXT           = new Color( 30,  30,  40);
    public static final Color TEXT_MUTED     = new Color(120, 125, 140);
    public static final Color BORDER         = new Color(210, 215, 228);

    // ── Typography ─────────────────────────────────────────────────────────
    public static final Font F_DISPLAY = new Font("Arial", Font.BOLD,  26);
    public static final Font F_TITLE   = new Font("Arial", Font.BOLD,  20);
    public static final Font F_HEADING = new Font("Arial", Font.BOLD,  15);
    public static final Font F_BODY    = new Font("Arial", Font.PLAIN, 13);
    public static final Font F_LABEL   = new Font("Arial", Font.BOLD,  12);
    public static final Font F_SMALL   = new Font("Arial", Font.PLAIN, 11);
    public static final Font F_MICRO   = new Font("Arial", Font.ITALIC, 10);
    public static final Font F_MONO    = new Font("Monospaced", Font.BOLD, 14);

    // ── Button factory ─────────────────────────────────────────────────────
    public static JButton btn(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = getModel().isPressed()  ? bg.darker()   :
                             getModel().isRollover() ? bg.brighter() : bg;
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

    public static JButton primaryBtn(String t) { return btn(t, PRIMARY,      Color.WHITE);  }
    public static JButton goldBtn   (String t) { return btn(t, GOLD,         PRIMARY_DARK); }
    public static JButton successBtn(String t) { return btn(t, SUCCESS,      Color.WHITE);  }
    public static JButton dangerBtn (String t) { return btn(t, DANGER,       Color.WHITE);  }
    public static JButton warningBtn(String t) { return btn(t, WARNING,      Color.WHITE);  }
    public static JButton grayBtn   (String t) { return btn(t, new Color(100, 100, 110), Color.WHITE); }
    public static JButton tealBtn   (String t) { return btn(t, TEAL,         Color.WHITE);  }
    public static JButton purpleBtn (String t) { return btn(t, PURPLE,       Color.WHITE);  }

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
    public static JPanel logoBadge(int size) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,     RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                drawUoKShield(g2, 0, 0, size);
            }
            @Override public Dimension getPreferredSize() { return new Dimension(size, size); }
            @Override public boolean   isOpaque()         { return false; }
        };
    }

    /**
     * Draws the UoK heraldic shield: gold border, gold top band with mortarboards,
     * royal-blue left half with open book, maroon right half with gold star,
     * gold bottom banner with motto text.
     */
    private static void drawUoKShield(Graphics2D g2, int ox, int oy, int size) {
        double s   = size;
        double cx  = ox + s / 2.0;
        int    pad = Math.max(3, (int)(s * 0.06));
        int    is  = size - 2 * pad;  // inner shield size
        int    ix  = ox + pad;
        int    iy  = oy + pad;

        int topH  = (int)(is * 0.30);             // top band (mortarboards)
        int bodyH = (int)(is * 0.45);             // main body (book | maroon)
        int botH  = is - topH - bodyH;            // bottom banner

        int topEndY   = iy + topH;
        int botStartY = topEndY + bodyH;
        int midX      = ix + is / 2;

        // ── Gold outer shield ──────────────────────────────────────────────
        Path2D.Double outer = shieldPath(ox, oy, size);
        g2.setColor(GOLD);
        g2.fill(outer);

        // ── Clip to inner shield, paint sections ───────────────────────────
        Path2D.Double inner = shieldPath(ix, iy, is);
        Shape savedClip = g2.getClip();
        g2.clip(inner);

        // Top band: gold
        g2.setColor(new Color(215, 178, 48));
        g2.fillRect(ix, iy, is, topH);

        // Body left: UoK royal blue
        g2.setColor(UOK_BLUE);
        g2.fillRect(ix, topEndY, is / 2 + 1, bodyH + botH + pad * 2);

        // Body right: UoK maroon
        g2.setColor(PRIMARY);
        g2.fillRect(midX, topEndY, is - is / 2 + pad, bodyH + botH + pad * 2);

        // Bottom banner: gold (overwrites bottom portion)
        g2.setColor(new Color(215, 178, 48));
        g2.fillRect(ix, botStartY, is + pad, botH + pad * 2);

        // ── Section dividers ───────────────────────────────────────────────
        g2.setColor(new Color(148, 115, 18));
        float sw = Math.max(1.2f, (float)(s * 0.022));
        g2.setStroke(new BasicStroke(sw));
        g2.drawLine(ix, topEndY,   ix + is, topEndY);
        g2.drawLine(ix, botStartY, ix + is, botStartY);
        g2.drawLine(midX, topEndY, midX,    botStartY);
        g2.setStroke(new BasicStroke(1f));

        // ── Graduation caps in top band ────────────────────────────────────
        if (size >= 36) {
            g2.setColor(new Color(22, 15, 8));
            int capW = Math.max(4, (int)(is * 0.145));
            int capH = Math.max(3, (int)(topH * 0.68));
            int capY = iy + Math.max(1, (int)(topH * 0.15));
            if (size >= 56) {
                int zone = is / 3;
                drawCap(g2, ix + zone / 2 - capW / 2,       capY, capW, capH);
                drawCap(g2, (int)cx - capW / 2,              capY, capW, capH);
                drawCap(g2, ix + is - zone / 2 - capW / 2,  capY, capW, capH);
            } else {
                drawCap(g2, (int)cx - capW / 2, capY, capW, capH);
            }
        }

        // ── Open book on left (blue) section ──────────────────────────────
        if (size >= 44) {
            int bkW = (int)(is * 0.38);
            int bkH = (int)(bodyH * 0.63);
            int bkX = ix + (int)(is * 0.03);
            int bkY = topEndY + (bodyH - bkH) / 2;
            drawBook(g2, bkX, bkY, bkW, bkH);
        }

        // ── Gold star on right (maroon) section ───────────────────────────
        if (size >= 56) {
            g2.setColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 210));
            drawStar(g2,
                midX + (int)(is * 0.13),
                topEndY + (int)(bodyH * 0.43),
                Math.max(3, (int)(is * 0.07)), 5);
        }

        // ── Banner text ────────────────────────────────────────────────────
        g2.setColor(new Color(48, 18, 6));
        if (size >= 52) {
            int fs = Math.max(4, (int)(s * 0.087));
            g2.setFont(new Font("Arial", Font.BOLD, fs));
            FontMetrics fm = g2.getFontMetrics();
            String txt = "Labour for the Future";
            while (fm.stringWidth(txt) > is * 0.89 && fs > 4) {
                g2.setFont(new Font("Arial", Font.BOLD, --fs));
                fm = g2.getFontMetrics();
            }
            g2.drawString(txt,
                (int)(cx - fm.stringWidth(txt) / 2.0),
                (int)(botStartY + botH * 0.68));
        } else if (size >= 36) {
            int fs = Math.max(5, (int)(s * 0.175));
            g2.setFont(new Font("Arial", Font.BOLD, fs));
            FontMetrics fm = g2.getFontMetrics();
            String txt = "UoK";
            g2.drawString(txt,
                (int)(cx - fm.stringWidth(txt) / 2.0),
                (int)(botStartY + botH * 0.70));
        }

        g2.setClip(savedClip);
    }

    private static void drawCap(Graphics2D g2, int x, int y, int w, int h) {
        // Wide board on top
        int boardW = w + w / 2;
        int boardH = Math.max(2, (int)(h * 0.30));
        g2.fillRect(x - w / 4, y, boardW, boardH);
        // Cap body
        int bodyW = (int)(w * 0.68);
        int bodyH = (int)(h * 0.55);
        g2.fillRect(x + (w - bodyW) / 2, y + boardH, bodyW, bodyH);
        // Tassel bead
        int bead = Math.max(1, (int)(w * 0.14));
        g2.fillOval(x - w / 4 + boardW - bead, y, bead * 2, bead * 2);
    }

    private static void drawBook(Graphics2D g2, int x, int y, int w, int h) {
        // Page fill
        g2.setColor(new Color(242, 225, 180));
        g2.fillRect(x, y, w, h);
        g2.setColor(new Color(248, 232, 192));
        g2.fillRect(x + 1, y + 1, w / 2 - 2, h - 2);
        // Cover and spine
        g2.setColor(new Color(72, 40, 10));
        g2.setStroke(new BasicStroke(Math.max(1f, w * 0.05f)));
        g2.drawRect(x, y, w, h);
        g2.drawLine(x + w / 2, y, x + w / 2, y + h);
        g2.setStroke(new BasicStroke(1f));
        // Text lines
        g2.setColor(new Color(130, 88, 30, 160));
        g2.setStroke(new BasicStroke(Math.max(0.5f, w * 0.025f)));
        int lines = Math.min(4, Math.max(2, (int)(h / (w * 0.26))));
        for (int i = 1; i <= lines; i++) {
            int ly = y + (int)(h * i / (lines + 1.0));
            g2.drawLine(x + 3, ly, x + w / 2 - 3, ly);
            g2.drawLine(x + w / 2 + 3, ly, x + w - 3, ly);
        }
        g2.setStroke(new BasicStroke(1f));
    }

    private static void drawStar(Graphics2D g2, int cx, int cy, int r, int pts) {
        int ri = (int)(r * 0.42);
        int[] xs = new int[pts * 2], ys = new int[pts * 2];
        for (int i = 0; i < pts * 2; i++) {
            double a = i * Math.PI / pts - Math.PI / 2;
            double d = (i % 2 == 0) ? r : ri;
            xs[i] = (int)(cx + d * Math.cos(a));
            ys[i] = (int)(cy + d * Math.sin(a));
        }
        g2.fillPolygon(xs, ys, pts * 2);
    }

    private static Path2D.Double shieldPath(int x, int y, int size) {
        double s  = size;
        double l  = x, r = x + s, t = y, b = y + s;
        double mx = x + s / 2.0;
        Path2D.Double p = new Path2D.Double();
        p.moveTo(l, t);
        p.lineTo(r, t);
        p.lineTo(r, t + s * 0.62);
        p.curveTo(r, t + s * 0.87, mx, b, mx, b);
        p.curveTo(mx, b, l, t + s * 0.87, l, t + s * 0.62);
        p.closePath();
        return p;
    }

    // ── App window icon ────────────────────────────────────────────────────
    public static Image appIcon() {
        int s = 64;
        BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,     RenderingHints.VALUE_ANTIALIAS_ON);
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
        JLabel sub = new JLabel(subtitle);
        sub.setFont(F_SMALL);  sub.setForeground(new Color(255, 205, 195));
        textCol.add(t); textCol.add(sub);
        left.add(textCol);

        panel.add(left, BorderLayout.WEST);
        return panel;
    }

    // ── Footer ─────────────────────────────────────────────────────────────
    public static JPanel footer() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(245, 235, 237));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 180, 185)),
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
        table.getTableHeader().setBackground(new Color(245, 228, 232));
        table.getTableHeader().setForeground(PRIMARY_DARK);
        table.setRowHeight(28);
        table.setGridColor(BORDER);
        table.setSelectionBackground(new Color(240, 210, 215));
        table.setSelectionForeground(PRIMARY_DARK);
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);
    }

    // ── Divider ────────────────────────────────────────────────────────────
    public static JSeparator divider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
}
