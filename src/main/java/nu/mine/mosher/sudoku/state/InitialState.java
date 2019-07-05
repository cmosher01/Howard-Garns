/*
 * Created on Oct 7, 2005
 */
package nu.mine.mosher.sudoku.state;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

import nu.mine.mosher.sudoku.util.Converter;

/**
 * Represents a sudoku puzzle, in its initial state. The puzzle contains 9 boxes
 * of 9 squares each. Each square will either have an "answer" (a number 0
 * through 8 representing the answer 1 through 9) or not. Objects of this class
 * are immutable.
 * 
 * @author Chris Mosher
 */
public class InitialState {
	private final int[][] rSquare = new int[9][9];

	/**
	 * The state will be initialized with all zeroes.
	 */
	private InitialState() {
		// instantiated by static factory methods
	}

	/**
	 * Creates a new <code>InitialState</code> representing the current state of
	 * the given game.
	 * 
	 * @param stateGetFrom
	 * @return the new <code>InitialState</code>
	 */
	public static InitialState createFromGameState(final GameState stateGetFrom) {
		final InitialState ret = new InitialState();

		for (int iSBox = 0; iSBox < 9; ++iSBox) {
			for (int iSquare = 0; iSquare < 9; ++iSquare) {
				int answer = -1;
				if (stateGetFrom.hasAnswer(iSBox, iSquare)) {
					answer = stateGetFrom.getAnswer(iSBox, iSquare);
				}
				ret.rSquare[iSBox][iSquare] = answer;
			}
		}

		return ret;
	}

	/**
	 * Creates a new <code>InitialState</code> from a string of 81 numbers. Zeros
	 * or non-digts represent empty squares. Any whitespace is ignored.
	 * 
	 * @param sGetFrom
	 * @return the new <code>InitialState</code>
	 */
	public static InitialState createFromString(final String sGetFrom) {
		final InitialState ret = new InitialState();

		final String sNoWhitespace = sGetFrom.replaceAll("\\p{javaWhitespace}", "");
		for (int iChar = 0; iChar < 9 * 9; ++iChar) {
			int numericValue = -1;
			if (iChar < sNoWhitespace.length()) {
				final char c = sNoWhitespace.charAt(iChar);
				if (Character.isDigit(c)) {
					numericValue = Character.getNumericValue(c) - 1;
					// valid values:
					// -1 (meaning an empty square), or 0 to 8

					if (numericValue < 0 || 9 <= numericValue) {
						numericValue = -1;
					}
				}
			}
			ret.rSquare[Converter.sbox(iChar)][Converter.square(iChar)] = numericValue;
		}

		return ret;
	}

	/**
	 * String representation of this <code>InitialState</code>, as 81 numbers,
	 * with zeroes representing empty squares.
	 * 
	 * @return String of 81 numbers
	 */
	@Override
	public String toString() {
		return asString('0', false);
	}

	/**
	 * String representation of this <code>InitialState</code>, as 81 numbers,
	 * with <code>charEmptySquare</code> representing empty squares. The string
	 * will have newlines separating the 9 rows.
	 * 
	 * @param charEmptySquare
	 *          the character to use to represent an empty square
	 * @param multiLine
	 *          true if the string should have rows separated with newlines.
	 * @return String representation of the puzzle
	 */
	public String asString(final char charEmptySquare, final boolean multiLine) {
		final StringWriter ret = new StringWriter(128);

		try {
			final BufferedWriter out = new BufferedWriter(ret);
			appendString(charEmptySquare, multiLine, out);
			out.flush();
		} catch (final IOException cantHappen) {
			throw new IllegalStateException(cantHappen);
		}

		return ret.toString();
	}

	/**
	 * Checks if this state has an answer in the given square. Square's are
	 * numbered 0-8 in row-major order, with a box ("SBox"), which are also
	 * numbered 0-8 in row-major order.
	 * 
	 * @param sbox
	 * @param square
	 * @return true if the square has an answer
	 */
	public boolean hasAnswer(int sbox, int square) {
		return this.rSquare[sbox][square] >= 0;
	}

	/**
	 * Gets the answer in the given square. Throws an assertion exception is the
	 * given square does not have an answer. Square's are numbered 0-8 in
	 * row-major order, with a box ("SBox"), which are also numbered 0-8 in
	 * row-major order.
	 * 
	 * @param sbox
	 * @param square
	 * @return true if the square has an answer
	 */
	public int getAnswer(int sbox, int square) {
		assert hasAnswer(sbox, square);
		return this.rSquare[sbox][square];
	}

	private void appendString(final char charEmptySquare, final boolean multiLine, final BufferedWriter out) throws IOException {
		int col = 0;
		for (int i = 0; i < 9 * 9; ++i) {
			final int answer = this.rSquare[Converter.sbox(i)][Converter.square(i)];
			if (answer < 0) {
				out.write(charEmptySquare);
			} else {
				out.write(Integer.toString(answer + 1));
			}

			if (multiLine) {
				++col;
				if (col >= 9) {
					out.newLine();
					col = 0;
				}
			}
		}
	}
}
