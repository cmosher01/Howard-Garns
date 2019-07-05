/*
 * Created on Oct 13, 2005
 */
package nu.mine.mosher.sudoku.state;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import nu.mine.mosher.sudoku.state.exception.IllegalGameFormat;
import nu.mine.mosher.sudoku.util.Script;
import nu.mine.mosher.sudoku.util.Time;

public class GameManager /*extends Observable*/ implements Cloneable {
	private LinkedList<GameState> rUndoState = new LinkedList<GameState>();
	private GameState state;

	private LinkedList<GameMove> rMove = new LinkedList<GameMove>();
	private LinkedList<GameMove> rMoveRedo = new LinkedList<GameMove>();

	private JMenuItem itemUndo;
	private JMenuItem itemRedo;

	private Observable observable = new Observable() {
		@Override
		public void notifyObservers() {
			setChanged();
			super.notifyObservers();
		}
	};

	public GameManager() {
		read("");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone() {
		try {
			final GameManager that = (GameManager) super.clone();
			that.rUndoState = (LinkedList<GameState>) this.rUndoState.clone();
			that.rMove = (LinkedList<GameMove>) this.rMove.clone();
			that.rMoveRedo = (LinkedList<GameMove>) this.rMoveRedo.clone();
			// TODO properly clone observable
			return that;
		} catch (final CloneNotSupportedException cantHappen) {
			throw new IllegalStateException(cantHappen);
		}
	}

	public void toggle(final int sbox, final int square, final int poss, final MoveAutomationType auto) {
		final boolean currentlyEliminated = this.state.isEliminated(sbox, square, poss);

		GameMoveType moveType;
		if (currentlyEliminated) {
			moveType = GameMoveType.POSSIBLE;
		} else {
			moveType = GameMoveType.ELIMINATED;
		}

		final GameMove move = new GameMove(new Time(new Date()), sbox, square, poss, moveType, auto);

		if (auto.equals(MoveAutomationType.MANUAL)) {
			this.rMoveRedo.clear();
		}
		move(move);
	}

	public void keep(final int sbox, final int square, final int poss, final MoveAutomationType auto) {
		final GameMove move = new GameMove(new Time(new Date()), sbox, square, poss, GameMoveType.AFFIRMED, auto);

		if (auto.equals(MoveAutomationType.MANUAL)) {
			this.rMoveRedo.clear();
		}
		move(move);
	}

	public void erase(final int sbox, final int square, final MoveAutomationType auto) {
		final GameMove move = new GameMove(new Time(new Date()), sbox, square, 0/*ignored*/, GameMoveType.RESET, auto);

		move(move);
	}

	private void move(final GameMove move) {
		this.rUndoState.addLast(this.state);
		this.rMove.addLast(move);

		this.state = GameState.move(this.state, move);

		// TODO maybe add "type of change" argument to notifyObservers, maybe
		// something like: (START, MOVE, SOLVE, UNDO_REDO)
		notifyObservers();
	}

	private void undo() {
		// undo all previous auto-moves back to, and including, the last manual move
		GameMove move = this.rMove.removeLast();
		GameState st = this.rUndoState.removeLast();
		while (move.getAutomationType().equals(MoveAutomationType.AUTOMATIC) && !this.rMove.isEmpty()) {
			move = this.rMove.removeLast();
			st = this.rUndoState.removeLast();
		}

		this.state = st;
		if (move.getAutomationType().equals(MoveAutomationType.MANUAL)) {
			this.rMoveRedo.addFirst(move);
		}

		notifyObservers();
	}

	private void redo() {
		final GameMove move = this.rMoveRedo.removeFirst();
		move(move);
	}

	public void read(final BufferedReader reader) throws IOException, IllegalGameFormat {
		final Script in = new Script(reader, '#');
		final List<String> rLine = new ArrayList<String>(512);
		in.appendLines(rLine);

		if (rLine.isEmpty()) {
			throw new IllegalGameFormat("empty");
		}
		// TODO need to turn off autosolving while loading, only do it once at the
		// end
		if (rLine.get(0).length() != 9 * 9) {
			// treat it as an initial state
			final StringBuilder sb = new StringBuilder();
			for (final String sLine : rLine) {
				sb.append(sLine);
			}
			read(sb.toString());
			return;
		}

		// chances are pretty good this is one of our files

		final String sInitialState = rLine.remove(0);
		read(sInitialState);

		for (final String sMove : rLine) {
			final GameMove move;
			try {
				move = GameMove.readFromString(sMove);
			} catch (final ParseException e) {
				throw new IllegalGameFormat(e);
			}
			move(move);
		}
		notifyObservers(); // ??? notify observers, once, here at the end
	}

	public void read(final String sInitialState) {
		final InitialState stateInitial = InitialState.createFromString(sInitialState);
		final GameState stateGame = GameState.createFromInitial(stateInitial);

		this.state = stateGame;

		this.rUndoState.clear();
		this.rMove.clear();
		this.rMoveRedo.clear();

		notifyObservers();
	}

	public void notifyObservers() {
		this.observable.notifyObservers();
	}

	public void write(final BufferedWriter out) throws IOException {
		out.write("# initial puzzle:");
		out.newLine();

		final GameState stateInitial = getInitialState();
		out.write(InitialState.createFromGameState(stateInitial).toString());
		out.newLine();

		out.write("# moves:");
		out.newLine();

		for (final GameMove move : this.rMove) {
			out.write(move.toString());
			out.newLine();
		}
	}

	private GameState getInitialState() {
		if (this.rUndoState.isEmpty()) {
			return this.state;
		}

		return this.rUndoState.getFirst();
	}

	public boolean isEliminated(final int sbox, final int square, final int possibility) {
		return this.state.isEliminated(sbox, square, possibility);
	}

	public boolean hasAnswer(final int sbox, final int square) {
		return this.state.hasAnswer(sbox, square);
	}

	public int getAnswer(final int sbox, final int square) {
		return this.state.getAnswer(sbox, square);
	}

	public void appendMenuItems(final JMenu appendTo) {
		this.itemUndo = new JMenuItem("Undo");
		this.itemUndo.setMnemonic(KeyEvent.VK_U);
		this.itemUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		this.itemUndo.addActionListener(new ActionListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
				undo();
			}
		});
		appendTo.add(this.itemUndo);

		this.itemRedo = new JMenuItem("Redo");
		this.itemRedo.setMnemonic(KeyEvent.VK_R);
		this.itemRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		this.itemRedo.addActionListener(new ActionListener() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void actionPerformed(@SuppressWarnings("unused") final ActionEvent e) {
				redo();
			}
		});
		appendTo.add(this.itemRedo);
	}

	public void updateMenu() {
		this.itemUndo.setEnabled(!this.rUndoState.isEmpty());
		this.itemRedo.setEnabled(!this.rMoveRedo.isEmpty());
	}

	@Override
	public boolean equals(final Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof GameManager)) {
			return false;
		}
		final GameManager that = (GameManager) object;
		return this.getInitialState().equals(that.getInitialState()) && this.rMove.equals(that.rMove);
	}

	@Override
	public int hashCode() {
		int h = 17;
		h *= 37;
		h += this.getInitialState().hashCode();
		h *= 37;
		h += this.rMove.hashCode();
		return h;
	}

	public GameState getState() {
		return this.state;
	}

	public void addObserver(final Observer observer) {
		this.observable.addObserver(observer);
	}

	public void deleteObserver(final Observer observer) {
		this.observable.deleteObserver(observer);
	}

	public void deleteObservers() {
		this.observable.deleteObservers();
	}
}
