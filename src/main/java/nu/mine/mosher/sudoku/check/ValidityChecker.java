/*
 * Created on Dec 28, 2005
 */
package nu.mine.mosher.sudoku.check;

import nu.mine.mosher.sudoku.state.GameManager;

class ValidityChecker {
	private final GameManager game;

	/**
	 * Initializes this checker to check the given game.
	 * 
	 * @param gameToCheck
	 */
	public ValidityChecker(final GameManager gameToCheck) {
		this.game = gameToCheck;
	}

	/**
	 * Checks the game (passed into the constructor) to make sure it has no
	 * invalid squares. An invalid square is one that has all of its possibilities
	 * eliminated.
	 * 
	 * @return true if the game has no "invalid" squares
	 */
	public boolean check() {
		// find first square without answer
		for (int sbox = 0; sbox < 9; ++sbox) {
			for (int square = 0; square < 9; ++square) {
				if (!possible(sbox, square)) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean possible(int sbox, int square) {
		for (int iPoss = 0; iPoss < 9; ++iPoss) {
			if (!this.game.isEliminated(sbox, square, iPoss)) {
				return true;
			}
		}
		return false;
	}
}
