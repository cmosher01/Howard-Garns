/*
 * Created on Sep 24, 2005
 */
package nu.mine.mosher.sudoku.gui;

import nu.mine.mosher.sudoku.state.GameManager;

import javax.swing.JPanel;
import java.awt.*;

class Board extends JPanel {
    public Board(final GameManager game) {
        super(new GridLayout(3, 3, 3, 3));

        setBackground(Color.ORANGE);
        setOpaque(true);
        addNotify();

        for (int i = 0; i < 9; ++i) {
            add(new SBox(game, i));
        }
    }
}
