/*
 * Created on Oct 1, 2005
 */
package nu.mine.mosher.sudoku.gui;

import java.awt.GridLayout;

import javax.swing.JPanel;

import nu.mine.mosher.sudoku.state.GameManager;

class PossibilitiesPanel extends JPanel {
	public PossibilitiesPanel(final GameManager game, final int iSbox, final int iSquare) {
		super(new GridLayout(3, 3));

		setOpaque(false);

		for (int i = 0; i < 9; ++i) {
			add(new PossibililtyPanel(game, iSbox, iSquare, i));
		}
	}
}
