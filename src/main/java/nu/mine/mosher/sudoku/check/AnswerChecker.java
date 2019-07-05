/*
 * Created on Oct 8, 2005
 */
package nu.mine.mosher.sudoku.check;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import nu.mine.mosher.sudoku.state.GameManager;
import nu.mine.mosher.sudoku.util.Converter;

/**
 * Checks a given (finished) game to see if it has been correctly solved
 * according to the rules of sudoku.
 * 
 * @author Chris Mosher
 */
class AnswerChecker {
	private static final Set<Integer> ALL_NINE;
	static {
		final Set<Integer> all_nine = new HashSet<Integer>(9, 1.0f);
		for (int i = 0; i < 9; ++i) {
			all_nine.add(Integer.valueOf(i));
		}
		ALL_NINE = Collections.<Integer> unmodifiableSet(all_nine);
	}

	private final GameManager game;

	/**
	 * Initializes this checker to check the given game.
	 * 
	 * @param gameToCheck
	 */
	public AnswerChecker(final GameManager gameToCheck) {
		this.game = gameToCheck;
	}

	/**
	 * Checks the game (passed into the constructor) to see if it has been
	 * correctly solved.
	 * 
	 * @return true if the game has been solved correctly
	 */
	public boolean check() {
		for (int row = 0; row < 9; ++row) {
			if (!checkRow(row)) {
				return false;
			}
		}
		for (int col = 0; col < 9; ++col) {
			if (!checkCol(col)) {
				return false;
			}
		}
		for (int sbox = 0; sbox < 9; ++sbox) {
			if (!checkSBox(sbox)) {
				return false;
			}
		}
		return true;
	}

	private boolean checkSBox(int sbox) {
		final Set<Integer> s = new HashSet<Integer>(ALL_NINE);

		for (int iSquare = 0; iSquare < 9; ++iSquare) {
			if (!this.game.hasAnswer(sbox, iSquare)) {
				return false;
			}
			final int ans = this.game.getAnswer(sbox, iSquare);
			if (!s.remove(Integer.valueOf(ans))) {
				return false;
			}
		}

		if (s.size() > 0) {
			return false;
		}

		return true;
	}

	private boolean checkCol(int col) {
		final Set<Integer> s = new HashSet<Integer>(ALL_NINE);

		for (int row = 0; row < 9; ++row) {
			final int square = Converter.squareOf(row, col);
			final int sbox = Converter.sboxOf(row, col);
			if (!this.game.hasAnswer(sbox, square)) {
				return false;
			}
			final int ans = this.game.getAnswer(sbox, square);
			if (!s.remove(Integer.valueOf(ans))) {
				return false;
			}
		}

		if (s.size() > 0) {
			return false;
		}

		return true;
	}

	private boolean checkRow(int row) {
		final Set<Integer> s = new HashSet<Integer>(ALL_NINE);

		for (int col = 0; col < 9; ++col) {
			final int square = Converter.squareOf(row, col);
			final int sbox = Converter.sboxOf(row, col);
			if (!this.game.hasAnswer(sbox, square)) {
				return false;
			}
			final int ans = this.game.getAnswer(sbox, square);
			if (!s.remove(Integer.valueOf(ans))) {
				return false;
			}
		}

		if (s.size() > 0) {
			return false;
		}

		return true;
	}
}
