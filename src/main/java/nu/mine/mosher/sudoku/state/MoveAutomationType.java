/*
 * Created on Oct 22, 2005
 */
package nu.mine.mosher.sudoku.state;

/**
 * Represents whether the move is manual (made by the user) or automatic (made
 * by the program).
 *
 * @author Chris Mosher
 */
public enum MoveAutomationType {
    /**
     * A move made by the user.
     */
    MANUAL,

    /**
     * A move made automatically by the program.
     */
    AUTOMATIC,
}
