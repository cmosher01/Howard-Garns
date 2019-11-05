/*
 * Created on Sep 24, 2005
 */
package nu.mine.mosher.sudoku.gui;

import nu.mine.mosher.sudoku.state.GameManager;

import javax.swing.JPanel;
import java.awt.*;

class SBox extends JPanel {
    public SBox(final GameManager game, int iSbox) {
        super(new GridLayout(3, 3, 1, 1));

        setBackground(Color.LIGHT_GRAY);

        for (int i = 0; i < 9; ++i) {
            add(new Square(game, iSbox, i));
        }
    }
}
