/*
 * Created on Oct 18, 2005
 */
package nu.mine.mosher.sudoku.state.exception;

public class IllegalGameFormat extends Exception {
	public IllegalGameFormat() {
		super();
	}

	public IllegalGameFormat(String message) {
		super(message);
	}

	public IllegalGameFormat(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalGameFormat(Throwable cause) {
		super(cause);
	}
}
