/*
 * Created on Oct 8, 2005
 */
package nu.mine.mosher.sudoku.solve;

import nu.mine.mosher.sudoku.state.*;
import nu.mine.mosher.sudoku.util.Converter;

class SolverSingleInRow implements Solver {
    public SolverSingleInRow(final GameManager gameToSolve) {
        this.game = gameToSolve;
    }

    @Override
    public boolean solve() {
        boolean changed = false;

        for (int row = 0; row < 9; ++row) {
            if (affirm(row)) {
                changed = true;
            }
        }

        return changed;
    }
    private final GameManager game;

    private boolean affirm(int row) {
        boolean changed = false;

        for (int poss = 0; poss < 9; ++poss) {
            int cPoss = 0;
            int iColPoss = -1;
            for (int iCol = 0; iCol < 9; ++iCol) {
                final int isbox = Converter.sboxOf(row, iCol);
                final int isquare = Converter.squareOf(row, iCol);
                if (!this.game.isEliminated(isbox, isquare, poss)) {
                    ++cPoss;
                    iColPoss = iCol;
                }
            }
            if (cPoss == 1) {
                final int isbox = Converter.sboxOf(row, iColPoss);
                final int isquare = Converter.squareOf(row, iColPoss);
                if (!this.game.hasAnswer(isbox, isquare)) {
                    this.game.keep(isbox, isquare, poss, MoveAutomationType.AUTOMATIC);
                    changed = true;
                }
            }
        }

        return changed;
    }
}
