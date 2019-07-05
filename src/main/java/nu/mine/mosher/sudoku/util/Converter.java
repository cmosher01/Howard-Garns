/*
 * Created on Oct 12, 2005
 */
package nu.mine.mosher.sudoku.util;

/**
 * Contains static methods to convert box/square to and from row/column. The
 * number scheme for boxes (within the board) and squares (within a box) is a
 * follows, visually:
 * 
 * <pre>
 * 0 1 2
 * 3 4 5
 * 6 7 8
 * </pre>
 * 
 * @author Chris Mosher
 */
public final class Converter {
	private Converter() {
		assert false;
	}

//@formatter:off
	private static final int[] SBOX = {
		0, 0, 0, 1, 1, 1, 2, 2, 2,
		0, 0, 0, 1, 1, 1, 2, 2, 2,
		0, 0, 0, 1, 1, 1, 2, 2, 2,
		3, 3, 3, 4, 4, 4, 5, 5, 5,
		3, 3, 3, 4, 4, 4, 5, 5, 5,
		3, 3, 3, 4, 4, 4, 5, 5, 5,
		6, 6, 6, 7, 7, 7, 8, 8, 8,
		6, 6, 6, 7, 7, 7, 8, 8, 8,
		6, 6, 6, 7, 7, 7, 8, 8, 8,
	};
	private static final int[] SQUARE = {
		0, 1, 2, 0, 1, 2, 0, 1, 2,
		3, 4, 5, 3, 4, 5, 3, 4, 5,
		6, 7, 8, 6, 7, 8, 6, 7, 8,
		0, 1, 2, 0, 1, 2, 0, 1, 2,
		3, 4, 5, 3, 4, 5, 3, 4, 5,
		6, 7, 8, 6, 7, 8, 6, 7, 8,
		0, 1, 2, 0, 1, 2, 0, 1, 2,
		3, 4, 5, 3, 4, 5, 3, 4, 5,
		6, 7, 8, 6, 7, 8, 6, 7, 8,
	};
//@formatter:on

	/**
	 * Gets the square number (0-8) of the given row and column.
	 * 
	 * @param row
	 *          0-8
	 * @param col
	 *          0-8
	 * @return square 0-8
	 */
	public static int squareOf(int row, int col) {
		return Converter.SQUARE[row * 9 + col];
	}

	/**
	 * Gets the box number (0-8) of the given row and column.
	 * 
	 * @param row
	 *          0-8
	 * @param col
	 *          0-8
	 * @return box 0-8
	 */
	public static int sboxOf(int row, int col) {
		return Converter.SBOX[row * 9 + col];
	}

	/**
	 * Gets the row number (0-8) of the given box and square.
	 * 
	 * @param sbox
	 *          0-8
	 * @param square
	 *          0-8
	 * @return row 0-8
	 */
	public static int rowOf(int sbox, int square) {
		return Converter.SBOX[sbox * 9 + square];
	}

	/**
	 * Gets the column number (0-8) of the given box and square.
	 * 
	 * @param sbox
	 *          0-8
	 * @param square
	 *          0-8
	 * @return column 0-8
	 */
	public static int colOf(int sbox, int square) {
		return Converter.SQUARE[sbox * 9 + square];
	}

	/**
	 * Gets the box number (0-8) of the given index (0-80)
	 * 
	 * @param i
	 *          0-80
	 * @return box 0-8
	 */
	public static int sbox(final int i) {
		return Converter.SBOX[i];
	}

	/**
	 * Gets the square number (0-8) of the given index (0-80)
	 * 
	 * @param i
	 *          0-80
	 * @return square 0-8
	 */
	public static int square(final int i) {
		return Converter.SQUARE[i];
	}
}
