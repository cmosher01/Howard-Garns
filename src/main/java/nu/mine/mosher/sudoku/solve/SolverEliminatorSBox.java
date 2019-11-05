/*
 * Created on Oct 8, 2005
 */
package nu.mine.mosher.sudoku.solve;

import nu.mine.mosher.sudoku.state.*;

class SolverEliminatorSBox implements Solver {
    public SolverEliminatorSBox(final GameManager gameToSolve) {
        this.game = gameToSolve;
    }

    @Override
    public boolean solve() {
        boolean changed = false;

        for (int sbox = 0; sbox < 9; ++sbox) {
            for (int square = 0; square < 9; ++square) {
                if (eliminate(sbox, square)) {
                    changed = true;
                }
            }
        }

        return changed;
    }
    private final GameManager game;

    private boolean eliminate(int sbox, int square) {
        boolean changed = false;

        if (!this.game.hasAnswer(sbox, square)) {
            return changed;
        }

        final int imposs = this.game.getAnswer(sbox, square);

        for (int iSquare = 0; iSquare < 9; ++iSquare) {
            if (iSquare != square) {
                if (!this.game.isEliminated(sbox, iSquare, imposs)) {
                    this.game.toggle(sbox, iSquare, imposs, MoveAutomationType.AUTOMATIC);
                    changed = true;
                }
            }
        }

        return changed;
    }
}
