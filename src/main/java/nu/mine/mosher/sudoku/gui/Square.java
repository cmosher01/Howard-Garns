/*
 * Created on Sep 24, 2005
 */
package nu.mine.mosher.sudoku.gui;

import nu.mine.mosher.sudoku.state.GameManager;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.geom.Rectangle2D;

class Square extends JPanel {
    public Square(final GameManager game, final int iSbox, final int iSquare) {
        super(new BorderLayout());
        this.game = game;
        this.iSbox = iSbox;
        this.iSquare = iSquare;

        setBackground(Color.WHITE);

        final Font font = getFont();
        float sizeFont = font.getSize2D();
        sizeFont *= 3;
        setFont(font.deriveFont(sizeFont));

        add(new PossibilitiesPanel(this.game, this.iSbox, this.iSquare), BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        if (!this.game.hasAnswer(this.iSbox, this.iSquare)) {
            return;
        }
        final int answer = this.game.getAnswer(this.iSbox, this.iSquare) + 1;
        final String sAnswer = Integer.toString(answer);

        drawCentered(sAnswer, g);
    }
    private final GameManager game;
    private final int iSbox;
    private final int iSquare;

    private void drawCentered(final String sAnswer, final Graphics g) {
        final FontMetrics fontMetrics = g.getFontMetrics();

        /*
         * Calculate the height and width of the number.
         */
        final Rectangle2D bounds = fontMetrics.getStringBounds(sAnswer, g);
        final int textHeight = (int) (bounds.getHeight());
        final int textWidth = (int) (bounds.getWidth());

        /*
         * Calculate coordinates that will center the number (horizontally and
         * vertically).
         */
        final int x = (getWidth() - textWidth) / 2;
        final int y = (getHeight() - textHeight) / 2 + fontMetrics.getAscent();

        g.drawString(sAnswer, x, y);
    }
}
