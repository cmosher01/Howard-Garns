/*
 * Created on Dec 28, 2005
 */
package nu.mine.mosher.sudoku.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JMenu;

import nu.mine.mosher.sudoku.check.CheckerManager;
import nu.mine.mosher.sudoku.solve.SolverManager;
import nu.mine.mosher.sudoku.state.GameManager;
import nu.mine.mosher.sudoku.state.InitialState;
import nu.mine.mosher.sudoku.state.MoveAutomationType;

/**
 * Not thread-safe.
 * 
 * @author Chris Mosher
 */
public class BruteForce {
	private final GameManager game;

	private int cSolution;

	public BruteForce(final GameManager game) {
		this.game = (GameManager) game.clone();
		// TODO add this when GameManager.clone properly clones observable: this.game.deleteObservers();
	}

	public int countSolutions() {
		this.cSolution = 0;

		brute(this.game, true, false);

		return this.cSolution;
	}

	public String getFirstSolution() {
		this.cSolution = 0;

		return brute(this.game, false, false);
	}

	public boolean hasUniqueSolution() {
		this.cSolution = 0;

		brute(this.game, true, true);

		return this.cSolution == 1;
	}

	private static final List<Integer> getRandom9() {
		final Integer[] r = {0,1,2,3,4,5,6,7,8};
		final List<Integer> al = Arrays.<Integer>asList(r);
		Collections.shuffle(al);
		return al;
	}

	private String brute(final GameManager gameSoFar, final boolean all, final boolean quitAfterTwoFound) {
		final SolverManager solver = new SolverManager(gameSoFar);
		solver.appendMenuItems(new JMenu());
		solver.solve();
		solver.close();

		final CheckerManager checker = new CheckerManager(gameSoFar, null);

		if (!checker.isValid()) {
			return "";
		}

		if (checker.isCorrect()) {
			if (all) {
				++this.cSolution;
				return "keep going";
			}
			return InitialState.createFromGameState(gameSoFar.getState()).asString('0', false);
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
				final String result = brute(trial, all, quitAfterTwoFound);
				if (!all && !result.isEmpty()) {
					return result;
				}
				if (quitAfterTwoFound && this.cSolution >= 2) {
					return result;
				}
			}
		}

		return "";
	}
}
