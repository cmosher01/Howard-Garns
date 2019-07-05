/*
 * Created on Oct 14, 2005
 */
package nu.mine.mosher.sudoku.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import nu.mine.mosher.sudoku.gui.exception.UserCancelled;

class NewBoardEntry extends JDialog {
	private static final boolean MODAL = true;

	private String sEntry = "";
	private JTextArea textEntry;

	public NewBoardEntry(final Frame owner) throws HeadlessException {
		super(owner, "Enter New Puzzle", MODAL);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

		addInstructions();
		addEntryArea();
		addButtons();

		pack();
		setLocationRelativeTo(null);
	}

	public String ask() throws UserCancelled {
		setVisible(true);
		if (this.sEntry.length() == 0) {
			throw new UserCancelled();
		}

		return this.sEntry;
	}

	private void addInstructions() {
		final JPanel panelLabel = new JPanel(new GridLayout(3, 1));

		final JLabel labelLine1 = new JLabel("Please enter all the numbers in the puzzle. Enter them row by row,");
		panelLabel.add(labelLine1);
		final JLabel labelLine2 = new JLabel("left to right, and top to bottom. Enter a zero (0) to indicate an empty");
		panelLabel.add(labelLine2);
		final JLabel labelLine3 = new JLabel("square. Enter a total of 81 numbers. Note that you can use Ctrl-V to paste.");
		panelLabel.add(labelLine3);

		panelLabel.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));

		getContentPane().add(panelLabel, BorderLayout.NORTH);
	}

	private void addEntryArea() {
		this.textEntry = new JTextArea();

		this.textEntry.setPreferredSize(new Dimension(400, 300));

		this.textEntry.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED),
				BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

		getContentPane().add(this.textEntry, BorderLayout.CENTER);
	}

	private void addButtons() {
		final JPanel panelButtons = new JPanel();

		final JButton buttonOK = new JButton("OK");
		buttonOK.addActionListener(new ActionListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
				ok();
			}
		});
		panelButtons.add(buttonOK);

		final JButton buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(new ActionListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
				cancel();
			}
		});
		panelButtons.add(buttonCancel);

		panelButtons.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));

		getContentPane().add(panelButtons, BorderLayout.SOUTH);
	}

	private void ok() {
		this.sEntry = this.textEntry.getText();
		setVisible(false);
	}

	private void cancel() {
		this.sEntry = "";
		setVisible(false);
	}
}
