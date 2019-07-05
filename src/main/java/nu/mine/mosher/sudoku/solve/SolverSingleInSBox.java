/*
 * Created on Oct 8, 2005
 */
package nu.mine.mosher.sudoku.solve;

import nu.mine.mosher.sudoku.state.GameManager;
import nu.mine.mosher.sudoku.state.MoveAutomationType;

class SolverSingleInSBox implements Solver {
	private final GameManager game;

	public SolverSingleInSBox(final GameManager gameToSolve) {
		this.game = gameToSolve;
	}

	@Override
	public boolean solve() {
		boolean changed = false;

		for (int sbox = 0; sbox < 9; ++sbox) {
			if (affirm(sbox)) {
				changed = true;
			}
		}

		return changed;
	}

	private boolean affirm(int sbox) {
		boolean changed = false;

		for (int poss = 0; poss < 9; ++poss) {
			int cPoss = 0;
			int iPossSquare = -1;
			for (int iSquare = 0; iSquare < 9; ++iSquare) {
				if (!this.game.isEliminated(sbox, iSquare, poss)) {
					++cPoss;
					iPossSquare = iSquare;
				}
			}
			if (cPoss == 1) {
				if (!this.game.hasAnswer(sbox, iPossSquare)) {
					this.game.keep(sbox, iPossSquare, poss, MoveAutomationType.AUTOMATIC);
					changed = true;
				}
			}
		}

		return changed;
	}
}
