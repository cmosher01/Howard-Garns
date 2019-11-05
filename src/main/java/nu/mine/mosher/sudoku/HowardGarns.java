/*
 * Created on Sep 24, 2005
 */
package nu.mine.mosher.sudoku;

import nu.mine.mosher.sudoku.check.CheckerManager;
import nu.mine.mosher.sudoku.file.FileManager;
import nu.mine.mosher.sudoku.gui.FrameManager;
import nu.mine.mosher.sudoku.gui.exception.UserCancelled;
import nu.mine.mosher.sudoku.solve.SolverManager;
import nu.mine.mosher.sudoku.state.GameManager;

import javax.swing.*;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;

/**
 * The main GUI for the whole program.
 *
 * @author Chris Mosher
 */
public class HowardGarns implements Runnable, Closeable {
    public static Preferences prefs() {
        return Preferences.userNodeForPackage(HowardGarns.class);
    }

    /**
     * Main program entry point. Instantiate a HowardGarns object (on the main
     * thread) and runs it on Swing's event dispatch thread.
     *
     * @param args (none)
     * @throws InterruptedException      for a thread interuption
     * @throws InvocationTargetException for an error in the program
     */
    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> new HowardGarns().run());
    }

    @Override
    public void close() {
        try {
            this.filer.verifyLoseUnsavedChanges();
            this.framer.close(); // this exits the app
        } catch (final UserCancelled e) {
            // there were unsaved changes, and the user
            // decided not to lose them, therefore we
            // don't want to exit the application
        }
    }

    /**
     * Runs the application.
     */
    @Override
    public void run() {
        // create the main frame window for the application
        this.framer.init(this::createAppMenuBar, closer());
        updateGameChange();
        this.game.addObserver((observableThatChanged, typeOfChange) -> updateGameChange());
    }



    private final GameManager game = new GameManager();
    private final FrameManager framer = new FrameManager(this.game);
    private final FileManager filer = new FileManager(this.game, this.framer);
    private final CheckerManager checker = new CheckerManager(this.game, this.framer);
    private final SolverManager solver = new SolverManager(this.game);

    private HowardGarns() {
        // instantiated by main only
    }

    private WindowAdapter closer() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                close();
            }
        };
    }

    private void updateGameChange() {
        this.game.updateMenu();
        this.filer.updateMenu();
        this.solver.updateMenu();
        this.checker.updateMenu();

        this.framer.repaint();
    }

    private JMenuBar createAppMenuBar() {
        final JMenuBar menubar = new JMenuBar();
        appendMenus(menubar);
        return menubar;
    }

    private void appendMenus(final JMenuBar bar) {
        final JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);

        this.filer.appendMenuItems(menuFile);
        menuFile.addSeparator();
        appendMenuItems(menuFile);

        bar.add(menuFile);

        final JMenu menuEdit = new JMenu("Edit");
        menuEdit.setMnemonic(KeyEvent.VK_E);
        this.game.appendMenuItems(menuEdit);
        bar.add(menuEdit);

        final JMenu menuSolve = new JMenu("Solve");
        menuSolve.setMnemonic(KeyEvent.VK_S);
        this.solver.appendMenuItems(menuSolve);
        bar.add(menuSolve);

        final JMenu menuCheck = new JMenu("Check");
        menuCheck.setMnemonic(KeyEvent.VK_C);
        this.checker.appendMenuItems(menuCheck);
        bar.add(menuCheck);
    }

    private void appendMenuItems(final JMenu menu) {
        final JMenuItem itemFileExit = new JMenuItem("Quit");
        itemFileExit.setMnemonic(KeyEvent.VK_Q);
        itemFileExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        itemFileExit.addActionListener(e -> close());
        menu.add(itemFileExit);
    }
}
