/*
 * Created on Oct 4, 2005
 */
package nu.mine.mosher.sudoku.state;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.StringTokenizer;

/**
 * Represents one move in the game. This will usually be the elimination of one
 * possible number from a square. Other options include affirming an answer in a
 * square, or re-enabling a possibility that had previously been eliminated.
 *
 * @author Chris Mosher
 */
class GameMove {
    /**
     * Factory method, reading a move from a given string, in the format returned
     * by toString.
     *
     * @param stringToReadFrom
     * @return new GameMove
     * @throws ParseException
     */
    public static GameMove readFromString(final String stringToReadFrom) throws ParseException {
        final StringTokenizer st = new StringTokenizer(stringToReadFrom, ",");

        final ZonedDateTime time = ZonedDateTime.parse(backCompatTime(st.nextToken()));
        final int sbox = Integer.parseInt(st.nextToken());
        final int square = Integer.parseInt(st.nextToken());
        final int possibility = Integer.parseInt(st.nextToken());
        final GameMoveType move = GameMoveType.valueOf(st.nextToken());
        final MoveAutomationType auto = MoveAutomationType.valueOf(st.nextToken());

        return new GameMove(time, sbox, square, possibility, move, auto);
    }

    private static String backCompatTime(String possiblyOldFormatTime) {
        return possiblyOldFormatTime.replaceFirst("^(.*[+-]\\d\\d)(\\d\\d)$", "$1:$2");
    }

    /**
     * Initializes this move.
     *
     * @param time
     * @param sbox
     * @param square
     * @param possibility
     * @param move
     * @param auto
     */
    public GameMove(final ZonedDateTime time, final int sbox, final int square, final int possibility, final GameMoveType move,
                    final MoveAutomationType auto) {
        this.time = time;
        this.sbox = sbox;
        this.square = square;
        this.possibility = possibility;
        this.move = move;
        this.auto = auto;

        verify0to8(this.sbox);
        verify0to8(this.square);
        verify0to8(this.possibility);
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof GameMove)) {
            return false;
        }
        final GameMove that = (GameMove) object;
        return this.time.equals(that.time) && this.sbox == that.sbox && this.square == that.square
            && this.possibility == that.possibility && this.move.equals(that.move) && this.auto.equals(that.auto);
    }

    @Override
    public int hashCode() {
        int h = 17;

        h *= 37;
        h += this.time.hashCode();
        h *= 37;
        h += this.sbox;
        h *= 37;
        h += this.square;
        h *= 37;
        h += this.possibility;
        h *= 37;
        h += this.move.hashCode();
        h *= 37;
        h += this.auto.hashCode();

        return h;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(41);
        appendAsString(sb);
        return sb.toString();
    }

    public void appendAsString(final StringBuffer appendTo) {
        appendTo.append(this.time.toString());
        appendTo.append(',');
        appendTo.append(this.sbox);
        appendTo.append(',');
        appendTo.append(this.square);
        appendTo.append(',');
        appendTo.append(this.possibility);
        appendTo.append(',');
        appendTo.append(this.move.toString());
        appendTo.append(',');
        appendTo.append(this.auto.toString());
    }

    /**
     * @return Returns the automation of move.
     */
    public MoveAutomationType getAutomationType() {
        return this.auto;
    }

    /**
     * @return Returns the type of move.
     */
    public GameMoveType getMoveType() {
        return this.move;
    }

    /**
     * @return Returns the possibility, 0-8
     */
    public int getPossibility() {
        return this.possibility;
    }

    /**
     * @return Returns the box, 0-8
     */
    public int getSbox() {
        return this.sbox;
    }

    /**
     * @return Returns the square, 0-8
     */
    public int getSquare() {
        return this.square;
    }

    /**
     * @return Returns the time.
     */
    public ZonedDateTime getTime() {
        return this.time;
    }



    private final MoveAutomationType auto;
    private final GameMoveType move;
    private final int possibility;
    private final int sbox;
    private final int square;
    private final ZonedDateTime time;

    private void verify0to8(final int i) {
        if (i < 0 || 9 <= i) {
            throw new IllegalArgumentException("must be 0-8, was " + i);
        }
    }
}
