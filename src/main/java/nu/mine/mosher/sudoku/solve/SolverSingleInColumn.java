/*
 * Created on Oct 8, 2005
 */
package nu.mine.mosher.sudoku.solve;

import nu.mine.mosher.sudoku.state.*;
import nu.mine.mosher.sudoku.util.Converter;

class SolverSingleInColumn implements Solver {
    public SolverSingleInColumn(final GameManager gameToSolve) {
        this.game = gameToSolve;
    }

    @Override
    public boolean solve() {
        boolean changed = false;

        for (int col = 0; col < 9; ++col) {
            if (affirm(col)) {
                changed = true;
            }
        }

        return changed;
    }
    private final GameManager game;

    private boolean affirm(int col) {
        boolean changed = false;

        for (int poss = 0; poss < 9; ++poss) {
            int cPoss = 0;
            int iRowPoss = -1;
            for (int iRow = 0; iRow < 9; ++iRow) {
                final int isbox = Converter.sboxOf(iRow, col);
                final int isquare = Converter.squareOf(iRow, col);
                if (!this.game.isEliminated(isbox, isquare, poss)) {
                    ++cPoss;
                    iRowPoss = iRow;
                }
            }
            if (cPoss == 1) {
                final int isbox = Converter.sboxOf(iRowPoss, col);
                final int isquare = Converter.squareOf(iRowPoss, col);
                if (!this.game.hasAnswer(isbox, isquare)) {
                    this.game.keep(isbox, isquare, poss, MoveAutomationType.AUTOMATIC);
                    changed = true;
                }
            }
        }

        return changed;
    }
}
