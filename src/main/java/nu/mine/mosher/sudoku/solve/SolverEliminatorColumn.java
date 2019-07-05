/*
 * Created on Oct 8, 2005
 */
package nu.mine.mosher.sudoku.solve;

import nu.mine.mosher.sudoku.state.GameManager;
import nu.mine.mosher.sudoku.state.MoveAutomationType;
import nu.mine.mosher.sudoku.util.Converter;

class SolverEliminatorColumn implements Solver {
	private final GameManager game;

	public SolverEliminatorColumn(final GameManager gameToSolve) {
		this.game = gameToSolve;
	}

	@Override
	public boolean solve() {
		boolean changed = false;

		for (int sbox = 0; sbox < 9; ++sbox) {
			for (int square = 0; square < 9; ++square) {
				if (eliminate(sbox, square)) {
					changed = true;
				}
			}
		}

		return changed;
	}

	private boolean eliminate(int sbox, int square) {
		boolean changed = false;

		if (!this.game.hasAnswer(sbox, square)) {
			return changed;
		}

		final int imposs = this.game.getAnswer(sbox, square);

		final int row = Converter.rowOf(sbox, square);
		final int col = Converter.colOf(sbox, square);

		for (int iRow = 0; iRow < 9; ++iRow) {
			if (iRow != row) {
				final int isbox = Converter.sboxOf(iRow, col);
				final int isquare = Converter.squareOf(iRow, col);
				if (!this.game.isEliminated(isbox, isquare, imposs)) {
					this.game.toggle(isbox, isquare, imposs, MoveAutomationType.AUTOMATIC);
					changed = true;
				}
			}
		}

		return changed;
	}
}
