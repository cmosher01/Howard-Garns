/*
 * Created on Sep 24, 2005
 */
package nu.mine.mosher.sudoku.state;

/**
 * Represents a (possibly partially or fully) solved sudoku puzzle. Contains
 * information about the answers of each square, and also for those squares that
 * do not have answers the possibilities that have been eliminated.
 * 
 * @author Chris Mosher
 */
class GameState {
	private final boolean[][][] eliminated = new boolean[9][9][9];
	private int hash;

	/**
	 * Initializes a game to have no answers and nothing eliminated.
	 */
	private GameState() {
		// instantiated by static factory methods
	}

	private void set(int sbox, int square, int possibility, boolean elim) {
		this.eliminated[sbox][square][possibility] = elim;
	}

	private void keep(int sbox, int square, int answer) {
		for (int poss = 0; poss < 9; ++poss) {
			this.eliminated[sbox][square][poss] = (poss != answer);
		}
	}

	/**
	 * Checks if the given possibility has been eliminated from the given square.
	 * 
	 * @param sbox
	 * @param square
	 * @param possibility
	 *          0-8
	 * @return true is the possibility has been eliminated
	 */
	public boolean isEliminated(int sbox, int square, int possibility) {
		return this.eliminated[sbox][square][possibility];
	}

	/**
	 * Checks if the given square has an answer. This is equivalent to the square
	 * having all but one possibility eliminated.
	 * 
	 * @param sbox
	 * @param square
	 * @return true if the square has an answer
	 */
	public boolean hasAnswer(int sbox, int square) {
		int cPossibility = 0;
		for (int poss = 0; poss < 9; ++poss) {
			if (!isEliminated(sbox, square, poss)) {
				++cPossibility;
			}
		}
		return cPossibility == 1;
	}

	/**
	 * Gets the answer in the given square. Throws an exception if the square does
	 * not have an answer.
	 * 
	 * @param sbox
	 * @param square
	 * @return the answer 0-8
	 */
	public int getAnswer(int sbox, int square) {
		assert hasAnswer(sbox, square);

		for (int poss = 0; poss < 9; ++poss) {
			if (!isEliminated(sbox, square, poss)) {
				return poss;
			}
		}
		assert false;
		return -1;
	}

	/**
	 * Makes a given move from the given state. Returns a new state representing
	 * the puzzle after the move has been made.
	 * 
	 * @param from
	 * @param move
	 * @return new state after move
	 */
	public static GameState move(final GameState from, final GameMove move) {
		final GameState state = new GameState();

		for (int iSBox = 0; iSBox < 9; ++iSBox) {
			for (int iSquare = 0; iSquare < 9; ++iSquare) {
				final int len = from.eliminated[iSBox][iSquare].length;
				System.arraycopy(from.eliminated[iSBox][iSquare], 0, state.eliminated[iSBox][iSquare], 0, len);
			}
		}

		switch (move.getMoveType()) {
		case ELIMINATED: {
			state.set(move.getSbox(), move.getSquare(), move.getPossibility(), true);
		}
			break;
		case POSSIBLE: {
			state.set(move.getSbox(), move.getSquare(), move.getPossibility(), false);
		}
			break;
		case AFFIRMED: {
			for (int iPossibility = 0; iPossibility < 9; ++iPossibility) {
				final boolean eliminated = (iPossibility != move.getPossibility());
				state.set(move.getSbox(), move.getSquare(), iPossibility, eliminated);
			}
		}
			break;
		case RESET: {
			for (int iPossibility = 0; iPossibility < 9; ++iPossibility) {
				state.set(move.getSbox(), move.getSquare(), iPossibility, false);
			}
		}
			break;
		default:
			assert false;
		}

		return state;
	}

	/**
	 * Creates a new game initialized with the given <code>InitialState</code>.
	 * 
	 * @param stateGetFrom
	 * @return the new game
	 */
	public static GameState createFromInitial(final InitialState stateGetFrom) {
		final GameState ret = new GameState();

		for (int iSBox = 0; iSBox < 9; ++iSBox) {
			for (int iSquare = 0; iSquare < 9; ++iSquare) {
				if (stateGetFrom.hasAnswer(iSBox, iSquare)) {
					final int answer = stateGetFrom.getAnswer(iSBox, iSquare);
					ret.keep(iSBox, iSquare, answer);
				}
			}
		}

		return ret;
	}

	@Override
	public boolean equals(final Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof GameState)) {
			return false;
		}
		final GameState that = (GameState) object;

		if (this.hashCode() != that.hashCode()) {
			return false;
		}

		for (int iSBox = 0; iSBox < 9; ++iSBox) {
			for (int iSquare = 0; iSquare < 9; ++iSquare) {
				for (int iPoss = 0; iPoss < 9; ++iPoss) {
					if (this.eliminated[iSBox][iSquare][iPoss] != that.eliminated[iSBox][iSquare][iPoss]) {
						return false;
					}
				}
			}
		}

		return true;
	}

	@Override
	public synchronized int hashCode() {
		if (this.hash == 0) {
			this.hash = getHash();
		}
		return this.hash;
	}

	private int getHash() {
		int h = 17;
		for (int iSBox = 0; iSBox < 9; ++iSBox) {
			for (int iSquare = 0; iSquare < 9; ++iSquare) {
				for (int iPoss = 0; iPoss < 9; ++iPoss) {
					h *= 37;
					h += this.eliminated[iSBox][iSquare][iPoss] ? 0 : 1;
				}
			}
		}
		return h;
	}
}
