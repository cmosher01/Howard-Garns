/*
 * Created on Sep 24, 2005
 */
package nu.mine.mosher.sudoku.gui;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;

import nu.mine.mosher.sudoku.state.GameManager;

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
