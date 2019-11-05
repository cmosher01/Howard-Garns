/*
 * Created on Dec 28, 2005
 */
package nu.mine.mosher.sudoku.util;

import nu.mine.mosher.sudoku.check.CheckerManager;
import nu.mine.mosher.sudoku.solve.SolverManager;
import nu.mine.mosher.sudoku.state.*;

import javax.swing.JMenu;
import java.util.*;

/**
 * Not thread-safe.
 *
 * @author Chris Mosher
 */
public class BruteForce {
    public BruteForce(final GameManager game) {
        this.game = (GameManager) game.clone();
        // TODO add this when GameManager.clone properly clones observable: this.game.deleteObservers();
    }

    public int countSolutions() {
        this.cSolution = 0;
        brute(this.game);
        return this.cSolution;
    }



    private final GameManager game;
    private int cSolution;

    private void brute(final GameManager gameSoFar) {
        final SolverManager solver = new SolverManager(gameSoFar);
        solver.appendMenuItems(new JMenu());
        solver.solve();
        solver.close();

        final CheckerManager checker = new CheckerManager(gameSoFar, null);

        if (!checker.isValid()) {
            return;
        }

        if (checker.isCorrect()) {
            ++this.cSolution;
            return;
        }

        // find first square without answer
        int sboxEmpty = -1;
        int squareEmpty = -1;
        for (int sbox = 0; sbox < 9 && sboxEmpty < 0; ++sbox) {
            for (int square = 0; square < 9 && squareEmpty < 0; ++square) {
                if (!gameSoFar.hasAnswer(sbox, square)) {
                    sboxEmpty = sbox;
                    squareEmpty = square;
                }
            }
        }

        // try each remaining possibility in that square
        final List<Integer> random9 = getRandom9();
        for (final Integer iPoss : random9) {
            if (!gameSoFar.isEliminated(sboxEmpty, squareEmpty, iPoss)) {
                final GameManager trial = (GameManager) gameSoFar.clone();
                trial.keep(sboxEmpty, squareEmpty, iPoss, MoveAutomationType.AUTOMATIC);
            }
        }
    }

    private static List<Integer> getRandom9() {
        final List<Integer> al = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);
        Collections.shuffle(al);
        return al;
    }
}
