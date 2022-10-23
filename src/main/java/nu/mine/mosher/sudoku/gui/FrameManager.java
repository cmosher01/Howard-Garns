/*
 * Created on Oct 19, 2005
 */
package nu.mine.mosher.sudoku.gui;

import nu.mine.mosher.sudoku.HowardGarns;
import nu.mine.mosher.sudoku.gui.exception.UserCancelled;
import nu.mine.mosher.sudoku.state.GameManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowListener;
import java.awt.image.MemoryImageSource;
import java.io.*;
import java.util.Optional;

public class FrameManager implements Closeable {
    public FrameManager(final GameManager game) {
        this.game = game;
    }

    @Override
    public void close() {
        this.frame.dispose();
    }

    public boolean askOK(final String message) {
        final int choice = JOptionPane.showConfirmDialog(this.frame, message, "Confirm", JOptionPane.OK_CANCEL_OPTION);
        return choice == JOptionPane.OK_OPTION;
    }

    public void tell(final String message) {
        JOptionPane.showMessageDialog(this.frame, message);
    }

    public String getBoardStringFromUser() throws UserCancelled {
        NewBoardEntry entry = null;
        try {
            entry = new NewBoardEntry(this.frame);
            return entry.ask();
        } finally {
            if (entry != null) {
                entry.dispose();
            }
        }
    }

    private static File directory() {
        return new File(HowardGarns.prefs().get("directory", "./"));
    }

    private static void directory(final File dir) {
        HowardGarns.prefs().put("directory", dir.getAbsolutePath());
    }

    public File getFileToOpen() throws UserCancelled {
        final JFileChooser chooser = new JFileChooser(directory());
        final int actionType = chooser.showOpenDialog(this.frame);
        if (actionType != JFileChooser.APPROVE_OPTION) {
            throw new UserCancelled();
        }

        directory(chooser.getCurrentDirectory());

        return chooser.getSelectedFile();
    }

    public File getFileToSave(final File initial) throws UserCancelled {
        final JFileChooser chooser = new JFileChooser(Optional.ofNullable(initial).orElse(directory()));
        final int actionType = chooser.showSaveDialog(this.frame);
        if (actionType != JFileChooser.APPROVE_OPTION) {
            throw new UserCancelled();
        }

        directory(chooser.getCurrentDirectory());

        return chooser.getSelectedFile();
    }

    public void init(final MenuBarFactory factoryMenuBar, final WindowListener listenerWindow) {
        setLookAndFeel();

        setDecorated();

        // Create the window.
        this.frame = new JFrame();

        // If the user clicks the close box, we call the WindowListener
        // that's passed in by the caller (who is responsible for calling
        // our close method if he determines it is OK to terminate the app)
        this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.frame.addWindowListener(listenerWindow);

        this.frame.setIconImage(getFrameIcon());

        this.frame.setTitle("Howard Garns\u2019s Number Place (Sudoku)");

        this.frame.setJMenuBar(factoryMenuBar.createMenuBar());

        // Create and set up the content pane.
        this.frame.setContentPane(new Board(this.game));

        this.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        this.frame.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        this.frame.getLayeredPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        // Set the window's size and position.
        this.frame.pack();
        this.frame.setLocationRelativeTo(null);

        // Display the window.
        this.frame.setVisible(true);
    }

    public void repaint() {
        this.frame.repaint();
    }

    public void showMessage(final String message) {
        JOptionPane.showMessageDialog(this.frame, message);
    }



    private final GameManager game;
    private JFrame frame;

    private Image getFrameIcon() {
        final int w = 100;
        final int h = 100;
        final int[] pix = new int[w * h];

        final int colorLine = Color.ORANGE.getRGB();
        final int colorBack = Color.WHITE.getRGB();
        int index = 0;
        for (int y = 0; y < h; y++) {
            final boolean yLine = (29 < y && y < 37) || (62 < y && y < 70);
            for (int x = 0; x < w; x++) {
                final boolean xLine = (29 < x && x < 37) || (62 < x && x < 70);
                int color;
                if (xLine || yLine) {
                    color = colorLine;
                } else {
                    color = colorBack;
                }
                pix[index++] = color;
            }
        }
        return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(w, h, pix, 0, w));
    }

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (final Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    private static void setDecorated() {
        // Use look and feel's (not OS's) decorations.
        // Must be done before creating any JFrame or JDialog
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
    }
}
