/*
 * Created on Oct 12, 2005
 */
package nu.mine.mosher.sudoku.solve;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import nu.mine.mosher.sudoku.state.GameManager;

public class SolverManager {
	private final Observer observer;
	private final GameManager gamer;

	private JMenuItem itemSolve;
	private JCheckBoxMenuItem itemAutomatic;
	private JCheckBoxMenuItem itemEliminateFromSameRow;
	private final Solver solverEliminatorRow;
	private JCheckBoxMenuItem itemEliminateFromSameColumn;
	private final Solver solverEliminatorColumn;
	private JCheckBoxMenuItem itemEliminateFromSameSBox;
	private final Solver solverEliminatorSBox;
	private JCheckBoxMenuItem itemAffirmSingletonsInRow;
	private final Solver solverSingleInRow;
	private JCheckBoxMenuItem itemAffirmSingletonsInColumn;
	private final Solver solverSingleInColumn;
	private JCheckBoxMenuItem itemAffirmSingletonsInSBox;
	private final Solver solverSingleInSBox;

	public SolverManager(final GameManager gameToSolve) {
		this.gamer = gameToSolve;
		this.solverEliminatorRow = new SolverEliminatorRow(gameToSolve);
		this.solverEliminatorColumn = new SolverEliminatorColumn(gameToSolve);
		this.solverEliminatorSBox = new SolverEliminatorSBox(gameToSolve);
		this.solverSingleInRow = new SolverSingleInRow(gameToSolve);
		this.solverSingleInColumn = new SolverSingleInColumn(gameToSolve);
		this.solverSingleInSBox = new SolverSingleInSBox(gameToSolve);

		this.observer = new Observer() {
			private boolean recursing;

			@Override
			public void update(@SuppressWarnings("unused") final Observable observableThatChagned,
					@SuppressWarnings("unused") final Object typeOfChange) {
				if (this.recursing) {
					return;
				}
				this.recursing = true;

				autoSolveIfSelected();

				this.recursing = false;
			}
		};
		gameToSolve.addObserver(this.observer);
	}

	public void appendMenuItems(final JMenu appendTo) {
		this.itemSolve = new JMenuItem("Solve");
		this.itemSolve.setMnemonic(KeyEvent.VK_S);
		this.itemSolve.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
				solve();
			}
		});
		appendTo.add(this.itemSolve);

		this.itemAutomatic = new JCheckBoxMenuItem("Automatically");
		this.itemAutomatic.setMnemonic(KeyEvent.VK_A);
		this.itemAutomatic.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
				autoSolveIfSelected();
				updateMenu();
			}
		});
		appendTo.add(this.itemAutomatic);

		appendTo.addSeparator();

		this.itemEliminateFromSameRow = new JCheckBoxMenuItem("Eliminate From Same Row");
		this.itemEliminateFromSameRow.setSelected(true);
		appendTo.add(this.itemEliminateFromSameRow);
		this.itemEliminateFromSameColumn = new JCheckBoxMenuItem("Eliminate From Same Column");
		this.itemEliminateFromSameColumn.setSelected(true);
		appendTo.add(this.itemEliminateFromSameColumn);
		this.itemEliminateFromSameSBox = new JCheckBoxMenuItem("Eliminate From Same Box");
		this.itemEliminateFromSameSBox.setSelected(true);
		appendTo.add(this.itemEliminateFromSameSBox);
		this.itemAffirmSingletonsInRow = new JCheckBoxMenuItem("Affirm Singles in Same Row");
		this.itemAffirmSingletonsInRow.setSelected(true);
		appendTo.add(this.itemAffirmSingletonsInRow);
		this.itemAffirmSingletonsInColumn = new JCheckBoxMenuItem("Affirm Singles in Same Column");
		this.itemAffirmSingletonsInColumn.setSelected(true);
		appendTo.add(this.itemAffirmSingletonsInColumn);
		this.itemAffirmSingletonsInSBox = new JCheckBoxMenuItem("Affirm Singles in Same Box");
		this.itemAffirmSingletonsInSBox.setSelected(true);
		appendTo.add(this.itemAffirmSingletonsInSBox);
	}

	public void updateMenu() {
		this.itemSolve.setEnabled(!this.itemAutomatic.isSelected());
	}

	public void autoSolveIfSelected() {
		if (this.itemAutomatic.isSelected()) {
			solve();
		}
	}

	public void solve() {
		boolean changed;
		do {
			changed = solveOnce();
		} while (changed);
	}

	private boolean solveOnce() {
		boolean changed = false;
		if (this.itemEliminateFromSameRow.isSelected()) {
			if (this.solverEliminatorRow.solve()) {
				changed = true;
			}
		}
		if (this.itemEliminateFromSameColumn.isSelected()) {
			if (this.solverEliminatorColumn.solve()) {
				changed = true;
			}
		}
		if (this.itemEliminateFromSameSBox.isSelected()) {
			if (this.solverEliminatorSBox.solve()) {
				changed = true;
			}
		}
		if (this.itemAffirmSingletonsInRow.isSelected()) {
			if (this.solverSingleInRow.solve()) {
				changed = true;
			}
		}
		if (this.itemAffirmSingletonsInColumn.isSelected()) {
			if (this.solverSingleInColumn.solve()) {
				changed = true;
			}
		}
		if (this.itemAffirmSingletonsInSBox.isSelected()) {
			if (this.solverSingleInSBox.solve()) {
				changed = true;
			}
		}
		return changed;
	}

	public void close() {
		this.gamer.deleteObserver(this.observer);
	}
}
