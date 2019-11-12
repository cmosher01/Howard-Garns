/*
 * Created on Oct 13, 2005
 */
package nu.mine.mosher.sudoku.state;

import nu.mine.mosher.sudoku.util.*;

import javax.swing.*;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.*;
import java.text.ParseException;
import java.util.*;

import static java.time.ZoneOffset.UTC;
import static java.time.ZonedDateTime.now;

public class GameManager /*extends Observable*/ implements Cloneable {
    public static class IllegalGameFormat extends Exception {
        public IllegalGameFormat(String message) {
            super(message);
        }

        public IllegalGameFormat(Throwable cause) {
            super(cause);
        }
    }

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

    public void addObserver(final Observer observer) {
        this.observable.addObserver(observer);
    }

    public void appendMenuItems(final JMenu appendTo) {
        this.itemUndo = new JMenuItem("Undo");
        this.itemUndo.setMnemonic(KeyEvent.VK_U);
        this.itemUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        this.itemUndo.addActionListener(e -> undo());
        appendTo.add(this.itemUndo);

        this.itemRedo = new JMenuItem("Redo");
        this.itemRedo.setMnemonic(KeyEvent.VK_R);
        this.itemRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        this.itemRedo.addActionListener(e -> redo());
        appendTo.add(this.itemRedo);
    }

    public void deleteObserver(final Observer observer) {
        this.observable.deleteObserver(observer);
    }

    public void deleteObservers() {
        this.observable.deleteObservers();
    }

    public int getAnswer(final int sbox, final int square) {
        return this.state.getAnswer(sbox, square);
    }

    public GameState getState() {
        return this.state;
    }

    public boolean hasAnswer(final int sbox, final int square) {
        return this.state.hasAnswer(sbox, square);
    }

    public boolean isEliminated(final int sbox, final int square, final int possibility) {
        return this.state.isEliminated(sbox, square, possibility);
    }

    public void keep(final int sbox, final int square, final int poss, final MoveAutomationType auto) {
        final GameMove move = new GameMove(now(UTC), sbox, square, poss, GameMoveType.AFFIRMED, auto);

        if (auto.equals(MoveAutomationType.MANUAL)) {
            this.rMoveRedo.clear();
        }
        move(move);
    }

    private void notifyObservers() {
        this.observable.notifyObservers();
    }

    public void read(final BufferedReader reader) throws IOException, IllegalGameFormat {
        final Script in = new Script(reader, '#');
        final List<String> rLine = new ArrayList<>(512);
        in.appendLines(rLine);

        if (rLine.isEmpty()) {
            throw new IllegalGameFormat("empty");
        }
        // TODO need to turn off autosolving while loading, only do it once at the end
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

        this.state = GameState.createFromInitial(stateInitial);

        this.rUndoState.clear();
        this.rMove.clear();
        this.rMoveRedo.clear();

        notifyObservers();
    }

    public void toggle(final int sbox, final int square, final int poss, final MoveAutomationType auto) {
        final boolean currentlyEliminated = this.state.isEliminated(sbox, square, poss);

        GameMoveType moveType;
        if (currentlyEliminated) {
            moveType = GameMoveType.POSSIBLE;
        } else {
            moveType = GameMoveType.ELIMINATED;
        }

        final GameMove move = new GameMove(now(UTC), sbox, square, poss, moveType, auto);

        if (auto.equals(MoveAutomationType.MANUAL)) {
            this.rMoveRedo.clear();
        }
        move(move);
    }

    public void updateMenu() {
        this.itemUndo.setEnabled(!this.rUndoState.isEmpty());
        this.itemRedo.setEnabled(!this.rMoveRedo.isEmpty());
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



    private JMenuItem itemRedo;
    private JMenuItem itemUndo;
    private Observable observable = new Observable() {
        @Override
        public void notifyObservers() {
            setChanged();
            super.notifyObservers();
        }
    };
    private LinkedList<GameMove> rMove = new LinkedList<>();
    private LinkedList<GameMove> rMoveRedo = new LinkedList<>();
    private LinkedList<GameState> rUndoState = new LinkedList<>();
    private GameState state;

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

    private GameState getInitialState() {
        if (this.rUndoState.isEmpty()) {
            return this.state;
        }

        return this.rUndoState.getFirst();
    }
}
