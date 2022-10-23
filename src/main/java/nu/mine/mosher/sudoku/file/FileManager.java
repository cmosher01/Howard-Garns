/*
 * Created on Oct 18, 2005
 */
package nu.mine.mosher.sudoku.file;

import nu.mine.mosher.sudoku.generate.Generator;
import nu.mine.mosher.sudoku.gui.FrameManager;
import nu.mine.mosher.sudoku.gui.exception.UserCancelled;
import nu.mine.mosher.sudoku.state.GameManager;
import nu.mine.mosher.sudoku.util.BruteForce;

import javax.swing.*;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileManager {
    public FileManager(final GameManager game, final FrameManager framer) {
        this.game = game;
        this.framer = framer;
        this.gameLastSaved = (GameManager) this.game.clone();
    }

    public void appendMenuItems(final JMenu appendTo) {
        this.itemFileNew = new JMenuItem("New");
        this.itemFileNew.setMnemonic(KeyEvent.VK_N);
        this.itemFileNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        this.itemFileNew.addActionListener(e -> {
            try {
                fileGenNew();
            } catch (final Throwable error) {
                error.printStackTrace();
            }
        });
        appendTo.add(this.itemFileNew);

        this.itemFileNew = new JMenuItem("Paste as New");
        this.itemFileNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        this.itemFileNew.addActionListener(e -> {
            try {
                fileNew();
            } catch (final Throwable error) {
                error.printStackTrace();
            }
        });
        appendTo.add(this.itemFileNew);

        this.itemFileOpen = new JMenuItem("Open\u2026");
        this.itemFileOpen.setMnemonic(KeyEvent.VK_O);
        this.itemFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        this.itemFileOpen.addActionListener(e -> {
            try {
                fileOpen();
            } catch (final Throwable error) {
                error.printStackTrace();
            }
        });
        appendTo.add(this.itemFileOpen);

        this.itemFileSave = new JMenuItem("Save");
        this.itemFileSave.setMnemonic(KeyEvent.VK_S);
        this.itemFileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        this.itemFileSave.addActionListener(e -> {
            try {
                fileSave();
            } catch (final Throwable error) {
                error.printStackTrace();
            }
        });
        appendTo.add(this.itemFileSave);

        this.itemFileSaveAs = new JMenuItem("Save As\u2026");
        this.itemFileSaveAs.setMnemonic(KeyEvent.VK_A);
        this.itemFileSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        this.itemFileSaveAs.addActionListener(e -> {
            try {
                fileSaveAs();
            } catch (final Throwable error) {
                error.printStackTrace();
            }
        });
        appendTo.add(this.itemFileSaveAs);
    }

    public void updateMenu() {
        this.itemFileNew.setEnabled(true);
        this.itemFileOpen.setEnabled(true);
        this.itemFileSave.setEnabled(this.file != null);
        this.itemFileSaveAs.setEnabled(true);
    }

    public void verifyLoseUnsavedChanges() throws UserCancelled {
        if (this.game.equals(this.gameLastSaved)) {
            return;
        }
        if (!this.framer.askOK("Your current game will be DISCARDED. Is this OK?")) {
            throw new UserCancelled();
        }
    }



    private final FrameManager framer;
    private final GameManager game;
    private File file;
    private GameManager gameLastSaved;
    private JMenuItem itemFileNew;
    private JMenuItem itemFileOpen;
    private JMenuItem itemFileSave;
    private JMenuItem itemFileSaveAs;

    private void fileSaveAs() {
        try {
            this.file = this.framer.getFileToSave(this.file);
            fileSave();
        } catch (final UserCancelled cancelled) {
            // user pressed the cancel button, so just return
        }
    }

    private void fileSave() {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8));
            this.game.write(out);
            this.gameLastSaved = (GameManager)this.game.clone();
        } catch (final Throwable e) {
            e.printStackTrace();
            this.framer.showMessage(e.getLocalizedMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (final Throwable eClose) {
                    eClose.printStackTrace();
                }
            }
        }
    }

    private void fileOpen() {
        BufferedReader in = null;
        try {
            verifyLoseUnsavedChanges();
            this.file = this.framer.getFileToOpen();
            in = new BufferedReader(new InputStreamReader(new FileInputStream(this.file), StandardCharsets.UTF_8));
            this.game.read(in);
            this.gameLastSaved = (GameManager)this.game.clone();
            verifyUniqueSolution();
        } catch (final UserCancelled cancelled) {
            // user pressed the cancel button, so just return
        } catch (final Throwable e) {
            e.printStackTrace();
            this.framer.showMessage(e.getLocalizedMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (final Throwable eClose) {
                    eClose.printStackTrace();
                }
            }
        }
    }

    private void fileNew() {
        try {
            verifyLoseUnsavedChanges();
            final String sBoard = this.framer.getBoardStringFromUser();
            this.file = null;
            this.game.read(sBoard);
            this.gameLastSaved = (GameManager)this.game.clone();
            verifyUniqueSolution();
        } catch (UserCancelled e) {
            // user pressed the cancel button, so just return
        }
    }

    private void verifyUniqueSolution() {
        final BruteForce brute = new BruteForce(this.game);
        final int cSolution = brute.countSolutions();
        if (cSolution < 1) {
            this.framer.tell("This is not a valid Sudoku, because it does not have a solution.");
        }
        if (1 < cSolution) {
            this.framer.tell("This is not a valid Sudoku, because it has " + cSolution + " solutions.");
        }
    }

    private void fileGenNew() {
        try {
            verifyLoseUnsavedChanges();
            this.file = null;
            this.game.read(Generator.generate());
            this.gameLastSaved = (GameManager)this.game.clone();
            verifyUniqueSolution();
        } catch (UserCancelled e) {
            // user pressed the cancel button, so just return
        }
    }
}
