/*
 * Created on Oct 8, 2005
 */
package nu.mine.mosher.sudoku.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InitialStateTest {
    /*
     * Test method for 'nu.mine.mosher.sudoku.InitialState.createFromString(String)'
     */
    @Test
    public void testSimpleString() {
        final String stringRep = "001002003004005006007008009010020030040050060070080090100200300400500600700800900";
        final InitialState state = InitialState.createFromString(stringRep);
        assertEquals(stringRep, state.toString());
    }
}
