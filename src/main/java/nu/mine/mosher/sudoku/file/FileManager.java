/*
 * Created on Oct 18, 2005
 */
package nu.mine.mosher.sudoku.file;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import nu.mine.mosher.sudoku.gui.FrameManager;
import nu.mine.mosher.sudoku.gui.exception.UserCancelled;
import nu.mine.mosher.sudoku.state.GameManager;
import nu.mine.mosher.sudoku.state.InitialState;
import nu.mine.mosher.sudoku.state.MoveAutomationType;
import nu.mine.mosher.sudoku.util.BruteForce;

public class FileManager {
	private JMenuItem itemFileNew;
	private JMenuItem itemFileOpen;
	private JMenuItem itemFileSave;
	private JMenuItem itemFileSaveAs;

	private final GameManager game;
	private final FrameManager framer;

	private File file;
	private GameManager gameLastSaved;

	public FileManager(final GameManager game, final FrameManager framer) {
		this.game = game;
		this.framer = framer;
		this.gameLastSaved = (GameManager) this.game.clone();
	}

	public void appendMenuItems(final JMenu appendTo) {
		this.itemFileNew = new JMenuItem("New");
		this.itemFileNew.setMnemonic(KeyEvent.VK_N);
		this.itemFileNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		this.itemFileNew.addActionListener(new ActionListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
				try {
					fileGenNew();
				} catch (final Throwable error) {
					error.printStackTrace();
				}
			}
		});
		appendTo.add(this.itemFileNew);

		this.itemFileNew = new JMenuItem("Paste as New");
		this.itemFileNew.addActionListener(new ActionListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
				try {
					fileNew();
				} catch (final Throwable error) {
					error.printStackTrace();
				}
			}
		});
		appendTo.add(this.itemFileNew);

		this.itemFileOpen = new JMenuItem("Open\u2026");
		this.itemFileOpen.setMnemonic(KeyEvent.VK_O);
		this.itemFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		this.itemFileOpen.addActionListener(new ActionListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
				try {
					fileOpen();
				} catch (final Throwable error) {
					error.printStackTrace();
				}
			}
		});
		appendTo.add(this.itemFileOpen);

		this.itemFileSave = new JMenuItem("Save");
		this.itemFileSave.setMnemonic(KeyEvent.VK_S);
		this.itemFileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		this.itemFileSave.addActionListener(new ActionListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
				try {
					fileSave();
				} catch (final Throwable error) {
					error.printStackTrace();
				}
			}
		});
		appendTo.add(this.itemFileSave);

		this.itemFileSaveAs = new JMenuItem("Save As\u2026");
		this.itemFileSaveAs.setMnemonic(KeyEvent.VK_A);
		this.itemFileSaveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		this.itemFileSaveAs.addActionListener(new ActionListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
				try {
					fileSaveAs();
				} catch (final Throwable error) {
					error.printStackTrace();
				}
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
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.file), "UTF-8"));
			this.game.write(out);
			this.gameLastSaved = (GameManager) this.game.clone();
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
			this.file = this.framer.getFileToOpen(this.file);
			in = new BufferedReader(new InputStreamReader(new FileInputStream(this.file), "UTF-8"));
			this.game.read(in);
			this.gameLastSaved = (GameManager) this.game.clone();
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
			this.gameLastSaved = (GameManager) this.game.clone();
			verifyUniqueSolution();
		} catch (UserCancelled e) {
			// user pressed the cancel button, so just return
		}
	}

	public void verifyLoseUnsavedChanges() throws UserCancelled {
		if (this.game.equals(this.gameLastSaved)) {
			return;
		}

		if (!this.framer.askOK("Your current game will be DISCARDED. Is this OK?")) {
			throw new UserCancelled();
		}
	}

	private void verifyUniqueSolution() throws UserCancelled {
		final BruteForce brute = new BruteForce(this.game);
		final int cSolution = brute.countSolutions();
		if (cSolution < 1) {
			if (!this.framer.askOK("There is actually no solution to this puzzle. Are you sure you want to play it?")) {
				throw new UserCancelled();
			}
		}
		if (1 < cSolution) {
			if (!this.framer.askOK("This puzzle actually has " + cSolution + " solutions. Are you sure you want to play it?")) {
				throw new UserCancelled();
			}
		}
	}

	private static final String getRandom9() {
		final Integer[] r = {1,2,3,4,5,6,7,8,9};
		Collections.shuffle(Arrays.<Integer>asList(r));
		final StringBuilder sb = new StringBuilder(9);
		for (final Integer i : r) {
			sb.append(i.toString());
		}
		return sb.toString();
	}

	private final Random rand = new Random();

	private void fileGenNew() {
		try {
			verifyLoseUnsavedChanges();
			this.file = null;


			boolean hasUniqueSolution = false;
			while (!hasUniqueSolution) {
				// TODO randomize this:
				this.game.read(getRandom9()+"000000000000000000000000000000000000000000000000000000000000000000000000");
				final BruteForce brute = new BruteForce(this.game);
				final String sBoard = brute.getFirstSolution();
				this.game.read(sBoard);

				// TODO change for different difficulty levels:
				final int openFields = 54;

				for (int j = 0; j < openFields;) {
					final int sbox = this.rand.nextInt(9);
					final int square = this.rand.nextInt(9);
					if (this.game.hasAnswer(sbox, square)) {
						this.game.erase(sbox, square, MoveAutomationType.AUTOMATIC);
						j++;
					}
				}

				final BruteForce brute2 = new BruteForce(this.game);
				hasUniqueSolution = brute2.hasUniqueSolution();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this.gameLastSaved = (GameManager) this.game.clone();
		} catch (UserCancelled e) {
			// user pressed the cancel button, so just return
		}
	}
}
