package VisualLogic.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * A modern toolbar button with hover and pressed visual effects.
 */
public class ModernToolBarButton extends JButton {

    private static final Color HOVER_COLOR = new Color(185, 209, 234);
    private static final Color PRESSED_COLOR = new Color(140, 180, 220);
    private static final Color BORDER_HOVER_COLOR = new Color(120, 160, 200);
    private static final int CORNER_RADIUS = 4;

    private boolean isHovered = false;
    private boolean isPressed = false;

    public ModernToolBarButton(String caption) {
        super(caption);
        init();
    }

    public ModernToolBarButton(Icon icon) {
        super(icon);
        init();
    }

    public ModernToolBarButton(String caption, Icon icon) {
        super(caption, icon);
        init();
    }

    private void init() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setFocusable(false);
        setBorderPainted(false);
        setOpaque(false);
        setPreferredSize(new Dimension(32, 28));
        setMinimumSize(new Dimension(28, 24));
        setMaximumSize(new Dimension(40, 32));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        setMargin(new java.awt.Insets(2, 4, 2, 4));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                isPressed = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        RoundRectangle2D shape = new RoundRectangle2D.Float(1, 1, w - 3, h - 3, CORNER_RADIUS, CORNER_RADIUS);

        if (isPressed) {
            g2.setColor(PRESSED_COLOR);
            g2.fill(shape);
            g2.setColor(BORDER_HOVER_COLOR);
            g2.draw(shape);
        } else if (isHovered) {
            g2.setColor(HOVER_COLOR);
            g2.fill(shape);
            g2.setColor(BORDER_HOVER_COLOR);
            g2.draw(shape);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
