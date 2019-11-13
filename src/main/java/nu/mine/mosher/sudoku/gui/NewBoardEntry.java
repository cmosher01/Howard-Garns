/*
 * Created on Oct 14, 2005
 */
package nu.mine.mosher.sudoku.gui;

import nu.mine.mosher.sudoku.gui.exception.UserCancelled;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.*;

import static javax.swing.JComponent.WHEN_FOCUSED;



class NewBoardEntry extends JDialog {
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
    private static final boolean MODAL = true;
    private String sEntry = "";
    private JTextArea textEntry;

    private void addInstructions() {
        final JPanel panelLabel = new JPanel(new GridLayout(3, 1));

        final JLabel labelLine1 = new JLabel("Please enter all the numbers in the puzzle. Enter them row by row,");
        panelLabel.add(labelLine1);
        final JLabel labelLine2 = new JLabel("left to right, and top to bottom. Enter a zero (0) to indicate an empty");
        panelLabel.add(labelLine2);
        final JLabel labelLine3 = new JLabel("square. Enter a total of 81 numbers. You can paste from the clipboard.");
        panelLabel.add(labelLine3);

        panelLabel.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));

        getContentPane().add(panelLabel, BorderLayout.NORTH);
    }

    private void addEntryArea() {
        this.textEntry = new JTextArea();

        this.textEntry.setPreferredSize(new Dimension(400, 300));

        this.textEntry.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED),
            BorderFactory.createBevelBorder(BevelBorder.LOWERED)));

        // bind meta-V keystroke to a "Paste" command.
        final InputMap im = new InputMap();
        im.setParent(this.textEntry.getInputMap(WHEN_FOCUSED));
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Paste");
        this.textEntry.setInputMap(WHEN_FOCUSED, im);

        // bind "Paste" command to a pre-defined paste Action
        final ActionMap am = new ActionMap();
        am.setParent(this.textEntry.getActionMap());
        am.put("Paste", TransferHandler.getPasteAction());
        this.textEntry.setActionMap(am);

        getContentPane().add(this.textEntry, BorderLayout.CENTER);
    }

    private void addButtons() {
        final JPanel panelButtons = new JPanel();

        final JButton buttonOK = new JButton("OK");
        buttonOK.addActionListener(e -> ok());
        panelButtons.add(buttonOK);

        final JButton buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(e -> cancel());
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
