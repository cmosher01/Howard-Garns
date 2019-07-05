/*
 * Created on Oct 20, 2005
 */
package nu.mine.mosher.sudoku.check;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import nu.mine.mosher.sudoku.gui.FrameManager;
import nu.mine.mosher.sudoku.state.GameManager;

public class CheckerManager {
	private final GameManager game;
	private final FrameManager framer;
	private final AnswerChecker checkerAnswer;
	private final ValidityChecker checkerValidity;

	public CheckerManager(final GameManager gameToCheck, final FrameManager framer) {
		this.game = gameToCheck;
		this.framer = framer;
		this.checkerAnswer = new AnswerChecker(this.game);
		this.checkerValidity = new ValidityChecker(this.game);
	}

	public void appendMenuItems(final JMenu appendTo) {
		final JMenuItem itemCheckAnswer = new JMenuItem("Answer");
		itemCheckAnswer.setMnemonic(KeyEvent.VK_A);
		itemCheckAnswer.addActionListener(new ActionListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
				checkAnswer();
			}
		});
		appendTo.add(itemCheckAnswer);
	}

	public void updateMenu() {
		// nothing to update for our menu
	}

	private void checkAnswer() {
		// TODO maybe provide better messages (with more info) after checking puzzle
		// solutions?
		this.framer.showMessage(isCorrect() ? "The puzzle has been solved correctly." : "Incorrect solution for the puzzle.");
	}

	public boolean isCorrect() {
		return this.checkerAnswer.check();
	}

	public boolean isValid() {
		return this.checkerValidity.check();
	}
}
