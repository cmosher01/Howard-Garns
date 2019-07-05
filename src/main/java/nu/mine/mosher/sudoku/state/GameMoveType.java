/*
 * Created on Oct 4, 2005
 */
package nu.mine.mosher.sudoku.state;

/**
 * The types of moves that a player can make in the game.
 * 
 * @author Chris Mosher
 */
enum GameMoveType {
	/**
	 * Indicates a move in which the player eliminates a given possible answer
	 * from a square.
	 */
	ELIMINATED,

	/**
	 * Indicates a move in which the player (re-)states that a given answer is
	 * possible for a square.
	 */
	POSSIBLE,

	/**
	 * Indicates a move in which the player affirms that a given square has a
	 * given answer (which therefore eliminates the other 8 possibilities for that
	 * square).
	 */
	AFFIRMED,

	/**
	 * Indicates a move in which the (computer) resets a square so no
	 * possibilities are eliminated.
	 */
	RESET,
}
