package VisualLogic.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/**
 * A styled vertical separator for use in toolbars.
 * Renders as a thin vertical line with padding.
 */
public class ToolBarSeparator extends JComponent {

    private static final Color SEPARATOR_COLOR = new Color(160, 170, 185);
    private static final int WIDTH = 9;
    private static final int HEIGHT = 24;

    public ToolBarSeparator() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setMaximumSize(new Dimension(WIDTH, HEIGHT));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = getWidth() / 2;
        int y1 = 4;
        int y2 = getHeight() - 4;

        g2.setColor(SEPARATOR_COLOR);
        g2.drawLine(x, y1, x, y2);

        g2.dispose();
    }
}
