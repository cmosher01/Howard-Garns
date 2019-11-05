/*
 * Created on Oct 20, 2005
 */
package nu.mine.mosher.sudoku.check;

import nu.mine.mosher.sudoku.gui.FrameManager;
import nu.mine.mosher.sudoku.state.GameManager;

import javax.swing.*;
import java.awt.event.*;

public class CheckerManager {
    public CheckerManager(final GameManager gameToCheck, final FrameManager framer) {
        this.framer = framer;
        this.checkerAnswer = new AnswerChecker(gameToCheck);
        this.checkerValidity = new ValidityChecker(gameToCheck);
    }

    public void appendMenuItems(final JMenu appendTo) {
        final JMenuItem itemCheckAnswer = new JMenuItem("Answer");
        itemCheckAnswer.setMnemonic(KeyEvent.VK_A);
        itemCheckAnswer.addActionListener(e -> checkAnswer());
        appendTo.add(itemCheckAnswer);
    }

    public boolean isCorrect() {
        return this.checkerAnswer.check();
    }

    public boolean isValid() {
        return this.checkerValidity.check();
    }

    public void updateMenu() {
        // nothing to update for our menu
    }



    private final AnswerChecker checkerAnswer;
    private final ValidityChecker checkerValidity;
    private final FrameManager framer;

    private void checkAnswer() {
        // TODO maybe provide better messages (with more info) after checking puzzle solutions?
        this.framer.showMessage(isCorrect() ? "The puzzle has been solved correctly." : "Incorrect solution for the puzzle.");
    }
}
