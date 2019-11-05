/*
 * Created on Oct 1, 2005
 */
package nu.mine.mosher.sudoku.gui;

import nu.mine.mosher.sudoku.state.GameManager;

import javax.swing.JPanel;
import java.awt.GridLayout;

class PossibilitiesPanel extends JPanel {
    public PossibilitiesPanel(final GameManager game, final int iSbox, final int iSquare) {
        super(new GridLayout(3, 3));

        setOpaque(false);

        for (int i = 0; i < 9; ++i) {
            add(new PossibililtyPanel(game, iSbox, iSquare, i));
        }
    }
}
