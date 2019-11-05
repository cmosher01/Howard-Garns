/*
 * Created on Oct 12, 2005
 */
package nu.mine.mosher.sudoku.solve;

import nu.mine.mosher.sudoku.HowardGarns;
import nu.mine.mosher.sudoku.state.GameManager;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class SolverManager {
    public SolverManager(final GameManager gameToSolve) {
        this.gamer = gameToSolve;
        this.solverEliminatorRow = new SolverEliminatorRow(gameToSolve);
        this.solverEliminatorColumn = new SolverEliminatorColumn(gameToSolve);
        this.solverEliminatorSBox = new SolverEliminatorSBox(gameToSolve);
        this.solverSingleInRow = new SolverSingleInRow(gameToSolve);
        this.solverSingleInColumn = new SolverSingleInColumn(gameToSolve);
        this.solverSingleInSBox = new SolverSingleInSBox(gameToSolve);

        this.observer = new Observer() {
            @Override
            public void update(final Observable observableThatChagned, final Object typeOfChange) {
                if (this.recursing) {
                    return;
                }
                this.recursing = true;

                autoSolveIfSelected();

                this.recursing = false;
            }
            private boolean recursing;
        };
        gameToSolve.addObserver(this.observer);
    }

    public void appendMenuItems(final JMenu appendTo) {
        this.itemSolve = new JMenuItem("Solve");
        this.itemSolve.setMnemonic(KeyEvent.VK_S);
        this.itemSolve.addActionListener(e -> solve());
        appendTo.add(this.itemSolve);

        this.itemAutomatic = new JCheckBoxMenuItem("Automatically");
        this.itemAutomatic.setMnemonic(KeyEvent.VK_A);
        this.itemAutomatic.setSelected(getPref("autoSolve"));
        this.itemAutomatic.addActionListener(e -> {
            setPref("autoSolve", e);
            autoSolveIfSelected();
            updateMenu();
        });
        appendTo.add(this.itemAutomatic);

        appendTo.addSeparator();

        this.itemEliminateFromSameRow = new JCheckBoxMenuItem("Eliminate From Same Row");
        this.itemEliminateFromSameRow.addActionListener(e -> setPref("elimRow", e));
        this.itemEliminateFromSameRow.setSelected(getPref("elimRow"));
        appendTo.add(this.itemEliminateFromSameRow);
        this.itemEliminateFromSameColumn = new JCheckBoxMenuItem("Eliminate From Same Column");
        this.itemEliminateFromSameColumn.addActionListener(e -> setPref("elimCol", e));
        this.itemEliminateFromSameColumn.setSelected(getPref("elimCol"));
        appendTo.add(this.itemEliminateFromSameColumn);
        this.itemEliminateFromSameSBox = new JCheckBoxMenuItem("Eliminate From Same Box");
        this.itemEliminateFromSameSBox.addActionListener(e -> setPref("elimBox", e));
        this.itemEliminateFromSameSBox.setSelected(getPref("elimBox"));
        appendTo.add(this.itemEliminateFromSameSBox);
        this.itemAffirmSingletonsInRow = new JCheckBoxMenuItem("Affirm Singles in Same Row");
        this.itemAffirmSingletonsInRow.addActionListener(e -> setPref("afirmRow", e));
        this.itemAffirmSingletonsInRow.setSelected(getPref("afirmRow"));
        appendTo.add(this.itemAffirmSingletonsInRow);
        this.itemAffirmSingletonsInColumn = new JCheckBoxMenuItem("Affirm Singles in Same Column");
        this.itemAffirmSingletonsInColumn.addActionListener(e -> setPref("afirmCol", e));
        this.itemAffirmSingletonsInColumn.setSelected(getPref("afirmCol"));
        appendTo.add(this.itemAffirmSingletonsInColumn);
        this.itemAffirmSingletonsInSBox = new JCheckBoxMenuItem("Affirm Singles in Same Box");
        this.itemAffirmSingletonsInSBox.addActionListener(e -> setPref("afirmBox", e));
        this.itemAffirmSingletonsInSBox.setSelected(getPref("afirmBox"));
        appendTo.add(this.itemAffirmSingletonsInSBox);
    }

    private void setPref(final String key, final ActionEvent e) {
        final JCheckBoxMenuItem cb = (JCheckBoxMenuItem) e.getSource();
        HowardGarns.prefs().putBoolean(key, cb.getModel().isSelected());
    }

    private boolean getPref(final String key) {
        return HowardGarns.prefs().getBoolean(key, false);
    }

    private void autoSolveIfSelected() {
        if (this.itemAutomatic.isSelected()) {
            solve();
        }
    }

    public void close() {
        this.gamer.deleteObserver(this.observer);
    }

    public void solve() {
        boolean changed;
        do {
            changed = solveOnce();
        } while (changed);
    }

    public void updateMenu() {
        this.itemSolve.setEnabled(!this.itemAutomatic.isSelected());
    }



    private final GameManager gamer;
    private final Observer observer;
    private final Solver solverEliminatorColumn;
    private final Solver solverEliminatorRow;
    private final Solver solverEliminatorSBox;
    private final Solver solverSingleInColumn;
    private final Solver solverSingleInRow;
    private final Solver solverSingleInSBox;
    private JCheckBoxMenuItem itemAffirmSingletonsInColumn;
    private JCheckBoxMenuItem itemAffirmSingletonsInRow;
    private JCheckBoxMenuItem itemAffirmSingletonsInSBox;
    private JCheckBoxMenuItem itemAutomatic;
    private JCheckBoxMenuItem itemEliminateFromSameColumn;
    private JCheckBoxMenuItem itemEliminateFromSameRow;
    private JCheckBoxMenuItem itemEliminateFromSameSBox;
    private JMenuItem itemSolve;

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
}
