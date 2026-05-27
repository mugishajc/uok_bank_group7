import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Standalone utility — generates the UoK Bank app icon as PNG files.
 * Run from BankingSimulator/: javac tools/GenerateIcon.java && java -cp tools GenerateIcon
 */
public class GenerateIcon {

    // UoK colours
    static final Color PRIMARY      = new Color(120, 18,  40);
    static final Color PRIMARY_DARK = new Color( 68,  5,  18);
    static final Color UOK_BLUE     = new Color( 25, 78, 162);
    static final Color GOLD         = new Color(196,158,  50);
    static final Color GOLD_DARK    = new Color(140,108,  22);

    public static void main(String[] args) throws Exception {
        new File("tools/icons").mkdirs();
        for (int size : new int[]{16, 32, 48, 64, 128, 256}) {
            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,     RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
            drawShield(g2, 0, 0, size);
            g2.dispose();
            ImageIO.write(img, "PNG", new File("tools/icons/icon_" + size + ".png"));
            System.out.println("Generated icon_" + size + ".png");
        }
        // Also write the 256px version to icon.png for ICO conversion
        BufferedImage big = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = big.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,     RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
        drawShield(g2, 0, 0, 256);
        g2.dispose();
        ImageIO.write(big, "PNG", new File("icon.png"));
        System.out.println("Generated icon.png (256px) — ready for ICO conversion");
    }

    static void drawShield(Graphics2D g2, int ox, int oy, int size) {
        double s  = size;
        int    pad = Math.max(2, (int)(s * 0.05));
        int    is  = size - 2 * pad;
        int    ix  = ox + pad;
        int    iy  = oy + pad;
        int    topH     = (int)(is * 0.30);
        int    bodyH    = (int)(is * 0.45);
        int    botH     = is - topH - bodyH;
        int    topEndY  = iy + topH;
        int    botStart = topEndY + bodyH;
        int    midX     = ix + is / 2;
        double cx       = ox + s / 2.0;

        // Outer gold shield
        Path2D.Double outer = shieldPath(ox, oy, size);
        g2.setColor(GOLD);
        g2.fill(outer);

        // Clip to inner shield
        Path2D.Double inner = shieldPath(ix, iy, is);
        Shape saved = g2.getClip();
        g2.clip(inner);

        // Top band — gold
        g2.setColor(new Color(215, 178, 48));
        g2.fillRect(ix, iy, is, topH);
        // Left body — blue
        g2.setColor(UOK_BLUE);
        g2.fillRect(ix, topEndY, is / 2 + 1, bodyH + botH + pad * 2);
        // Right body — maroon
        g2.setColor(PRIMARY);
        g2.fillRect(midX, topEndY, is / 2 + pad * 2, bodyH + botH + pad * 2);
        // Bottom banner — gold
        g2.setColor(new Color(215, 178, 48));
        g2.fillRect(ix, botStart, is + pad * 2, botH + pad * 2);

        // Divider lines
        g2.setColor(new Color(140, 108, 18));
        float sw = Math.max(1f, (float)(s * 0.020));
        g2.setStroke(new BasicStroke(sw));
        g2.drawLine(ix, topEndY,  ix + is, topEndY);
        g2.drawLine(ix, botStart, ix + is, botStart);
        g2.drawLine(midX, topEndY, midX, botStart);
        g2.setStroke(new BasicStroke(1f));

        // Grad caps in top band
        if (size >= 32) {
            g2.setColor(new Color(22, 15, 8));
            int cw = Math.max(3, (int)(is * 0.14));
            int ch = Math.max(2, (int)(topH * 0.65));
            int cy = iy + Math.max(1, (int)(topH * 0.17));
            if (size >= 64) {
                int zone = is / 3;
                drawCap(g2, ix + zone/2 - cw/2,           cy, cw, ch);
                drawCap(g2, (int)cx - cw/2,                cy, cw, ch);
                drawCap(g2, ix + is - zone/2 - cw/2,      cy, cw, ch);
            } else {
                drawCap(g2, (int)cx - cw/2, cy, cw, ch);
            }
        }

        // Book (left/blue section)
        if (size >= 40) {
            int bw = (int)(is * 0.36);
            int bh = (int)(bodyH * 0.60);
            int bx = ix + (int)(is * 0.03);
            int by = topEndY + (bodyH - bh) / 2;
            drawBook(g2, bx, by, bw, bh);
        }

        // Star (right/maroon section)
        if (size >= 48) {
            g2.setColor(new Color(GOLD.getRed(), GOLD.getGreen(), GOLD.getBlue(), 220));
            drawStar(g2, midX + (int)(is * 0.13), topEndY + (int)(bodyH * 0.44),
                     Math.max(3, (int)(is * 0.072)), 5);
        }

        // Banner text
        g2.setColor(new Color(48, 18, 6));
        if (size >= 48) {
            int fs = Math.max(4, (int)(s * 0.082));
            g2.setFont(new Font("Arial", Font.BOLD, fs));
            FontMetrics fm = g2.getFontMetrics();
            String txt = size >= 100 ? "Labour for the Future" : "UoK";
            while (fm.stringWidth(txt) > is * 0.88 && fs > 4) {
                g2.setFont(new Font("Arial", Font.BOLD, --fs));
                fm = g2.getFontMetrics();
            }
            g2.drawString(txt, (int)(cx - fm.stringWidth(txt)/2.0),
                (int)(botStart + botH * 0.68));
        }

        g2.setClip(saved);
    }

    static void drawCap(Graphics2D g2, int x, int y, int w, int h) {
        int bw = w + w/2, bh = Math.max(2,(int)(h*0.30));
        g2.fillRect(x - w/4, y, bw, bh);
        int byw = (int)(w*0.68), byh = (int)(h*0.55);
        g2.fillRect(x + (w-byw)/2, y+bh, byw, byh);
        int bead = Math.max(1,(int)(w*0.14));
        g2.fillOval(x - w/4 + bw - bead, y, bead*2, bead*2);
    }

    static void drawBook(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(new Color(242,225,180));
        g2.fillRect(x,y,w,h);
        g2.setColor(new Color(248,232,192));
        g2.fillRect(x+1,y+1,w/2-2,h-2);
        g2.setColor(new Color(72,40,10));
        g2.setStroke(new BasicStroke(Math.max(1f, w*0.05f)));
        g2.drawRect(x,y,w,h);
        g2.drawLine(x+w/2,y,x+w/2,y+h);
        g2.setStroke(new BasicStroke(1f));
        g2.setColor(new Color(130,88,30,160));
        g2.setStroke(new BasicStroke(Math.max(0.5f, w*0.025f)));
        int lines = Math.min(4, Math.max(2,(int)(h/(w*0.26))));
        for (int i=1; i<=lines; i++) {
            int ly = y + (int)(h*i/(lines+1.0));
            g2.drawLine(x+3,ly,x+w/2-3,ly);
            g2.drawLine(x+w/2+3,ly,x+w-3,ly);
        }
        g2.setStroke(new BasicStroke(1f));
    }

    static void drawStar(Graphics2D g2, int cx, int cy, int r, int pts) {
        int ri = (int)(r*0.42);
        int[] xs = new int[pts*2], ys = new int[pts*2];
        for (int i=0; i<pts*2; i++) {
            double a = i*Math.PI/pts - Math.PI/2;
            double d = (i%2==0)?r:ri;
            xs[i]=(int)(cx+d*Math.cos(a));
            ys[i]=(int)(cy+d*Math.sin(a));
        }
        g2.fillPolygon(xs,ys,pts*2);
    }

    static Path2D.Double shieldPath(int x, int y, int size) {
        double s=size, l=x, r=x+s, t=y, b=y+s, mx=x+s/2.0;
        Path2D.Double p = new Path2D.Double();
        p.moveTo(l,t); p.lineTo(r,t); p.lineTo(r,t+s*0.62);
        p.curveTo(r,t+s*0.87,mx,b,mx,b);
        p.curveTo(mx,b,l,t+s*0.87,l,t+s*0.62);
        p.closePath();
        return p;
    }
}
