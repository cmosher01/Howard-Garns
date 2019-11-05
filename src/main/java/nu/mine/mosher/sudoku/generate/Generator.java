/* Sudoku puzzle generator
 * Copyright (C) 2011 Daniel Beer <dlbeer@gmail.com>
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 * Ported to Java Nov. 2019, by Christopher Alan Mosher, Shelton, Connecticut, USA, cmosher01@gmail.com, https://mosher.mine.nu
 */

package nu.mine.mosher.sudoku.generate;

import java.util.Random;

public class Generator {
    public static String generate() {
        final StringBuilder s = new StringBuilder();
        final int[] puzzle = actionGenerate();
        for (int row = 0; row < DIM; ++row) {
            for (int col = 0; col < DIM; ++col) {
                s.append(puzzle[row * DIM + col]);
            }
        }
        return s.toString();
    }



    private static final int ORDER = 3;
    private static final int DIM = ORDER * ORDER;
    private static final int ELEMENTS = DIM * DIM;
    private static final int MAX_ITER = 449;
    private static final int ALL_VALUES = (1 << DIM) - 1;
    private static final Random rng = new Random();

    private static class SolveContext {
        int branchScore;
        int count;
        int[] problem = new int[ELEMENTS];
    }

    private static int singleton(int v) {
        return 1 << (v - 1);
    }

    private static int countBits(int m) {
        int c = 0;
        while (m != 0) {
            m &= m - 1;
            ++c;
        }
        return c;
    }

    private static void freedomEliminate(int[] freedom, int x, int y, int v) {
        final int mask = ~singleton(v);
        final int saved = freedom[y * DIM + x];
        int b = x;
        for (int i = 0; i < DIM; ++i) {
            freedom[b] &= mask;
            b += DIM;
        }
        b = y * DIM;
        for (int i = 0; i < DIM; i++) {
            freedom[b + i] &= mask;
        }
        b = (y - (y % ORDER)) * DIM + (x - (x % ORDER));
        for (int i = 0; i < ORDER; i++) {
            for (int j = 0; j < ORDER; j++) {
                freedom[b + j] &= mask;
            }
            b += DIM;
        }
        freedom[y * DIM + x] = saved;
    }

    private static void initFreedom(int[] problem, int[] freedom) {
        for (int x = 0; x < ELEMENTS; ++x) {
            freedom[x] = ALL_VALUES;
        }
        for (int y = 0; y < DIM; ++y) {
            for (int x = 0; x < DIM; ++x) {
                final int v = problem[y * DIM + x];
                if (v != 0) {
                    freedomEliminate(freedom, x, y, v);
                }
            }
        }
    }

    private static int sanityCheck(int[] problem, int[] freedom) {
        for (int i = 0; i < ELEMENTS; ++i) {
            final int v = problem[i];
            if (v != 0) {
                int f = freedom[i];
                if ((f & singleton(v)) == 0) {
                    return -1;
                }
            }
        }
        return 0;
    }

    private static int searchLeastFree(int[] problem, int[] freedom) {
        int best_index = -1;
        int best_score = -1;
        for (int i = 0; i < ELEMENTS; i++) {
            int v = problem[i];
            if (v == 0) {
                int score = countBits(freedom[i]);
                if (best_score < 0 || score < best_score) {
                    best_index = i;
                    best_score = score;
                }
            }
        }
        return best_index;
    }

    private static void solveRcr(SolveContext ctx, int[] freedom, int diff) {
        int[] new_free = new int[ELEMENTS];
        final int r = searchLeastFree(ctx.problem, freedom);
        if (r < 0) {
            if (ctx.count == 0) {
                ctx.branchScore = diff;
            }
            ctx.count++;
            return;
        }
        final int mask = freedom[r];
        final int bf = countBits(mask) - 1;
        diff += bf * bf;
        for (int i = 0; i < DIM; i++) {
            if ((mask & (1 << i)) != 0) {
                copy(freedom, new_free);
                freedomEliminate(new_free, r % DIM, r / DIM, i + 1);
                ctx.problem[r] = i + 1;
                solveRcr(ctx, new_free, diff);
                if (ctx.count >= 2) {
                    return;
                }
            }
        }
        ctx.problem[r] = 0;
    }

    private static void copy(int[] src, int[] new_free) {
        System.arraycopy(src, 0, new_free, 0, ELEMENTS);
    }

    private static int solve(int[] problem, int[] diff) {
        SolveContext ctx = new SolveContext();
        int[] freedom = new int[ELEMENTS];

        copy(problem, ctx.problem);
        ctx.count = 0;
        ctx.branchScore = 0;

        initFreedom(problem, freedom);
        if (sanityCheck(problem, freedom) < 0) {
            return -1;
        }

        solveRcr(ctx, freedom, 0);

        /* Calculate a difficulty score */
        if (diff != null) {
            int empty = 0;
            int mult = 1;

            for (int i = 0; i < ELEMENTS; i++) {
                if (problem[i] == 0) {
                    empty++;
                }
            }

            while (mult <= ELEMENTS) {
                mult *= 10;
            }

            diff[0] = ctx.branchScore * mult + empty;
        }

        return ctx.count - 1;
    }

    /************************************************************************
     * Grid generator
     *
     * We generate grids using a backtracking algorithm similar to the basic
     * solver algorithm. At each step, choose a cell with the smallest number
     * of possible values, and try each value, solving recursively. The key
     * difference is that the values are tested in a random order.
     *
     * An empty grid can be initially populated with a large number of values
     * without backtracking. In the ORDER == 3 case, we can easily fill the
     * whole top band and the first column before resorting to backtracking.
     */

    private static int pick(int set) {
        int x = rng.nextInt(countBits(set));
        for (int i = 0; i < DIM; i++) {
            if ((set & (1 << i)) != 0) {
                if (x == 0) {
                    return i + 1;
                }
                x--;
            }
        }
        return 0;
    }

    private static void chooseB1(int[] problem) {
        int set = ALL_VALUES;
        for (int i = 0; i < ORDER; i++) {
            for (int j = 0; j < ORDER; j++) {
                final int v = pick(set);
                problem[i * DIM + j] = v;
                set &= ~singleton(v);
            }
        }
    }

    private static void chooseB2(int[] problem) {
        int[] used = new int[ORDER];
        int[] chosen = new int[ORDER];

        /* Gather used values from B1 by box-row */
        for (int i = 0; i < ORDER; i++) {
            for (int j = 0; j < ORDER; j++) {
                used[i] |= singleton(problem[i * DIM + j]);
            }
        }

        /* Choose the top box-row for B2 */
        int set_x = used[1] | used[2];
        for (int i = 0; i < ORDER; i++) {
            final int v = pick(set_x);
            final int mask = singleton(v);

            chosen[0] |= mask;
            set_x &= ~mask;
        }

        /* Choose values for the middle box-row, as long as we can */
        set_x = (used[0] | used[2]) & ~chosen[0];
        int set_y = (used[0] | used[1]) & ~chosen[0];

        while (countBits(set_y) > 3) {
            final int v = pick(set_x);
            final int mask = singleton(v);

            chosen[1] |= mask;
            set_x &= ~mask;
            set_y &= ~mask;
        }

        /* We have no choice for the remainder */
        chosen[1] |= set_x & ~set_y;
        chosen[2] |= set_y;

        /* Permute the triplets in each box-row */
        for (int i = 0; i < ORDER; i++) {
            int set = chosen[i];

            for (int j = 0; j < ORDER; j++) {
                int v = pick(set);

                problem[i * DIM + j + ORDER] = v;
                set &= ~singleton(v);
            }
        }
    }

    private static void chooseB3(int[] problem) {
        for (int i = 0; i < ORDER; i++) {
            int set = ALL_VALUES;
            /* Eliminate already-used values in this row */
            for (int j = 0; j + ORDER < DIM; j++)
                set &= ~singleton(problem[i * DIM + j]);

            /* Permute the remaining values in the last box-row */
            for (int j = 0; j < ORDER; j++) {
                int v = pick(set);

                problem[i * DIM + DIM - ORDER + j] = v;
                set &= ~singleton(v);
            }
        }
    }

    private static void chooseCol1(int[] problem) {
        int set = ALL_VALUES;
        for (int i = 0; i < ORDER; i++) {
            set &= ~singleton(problem[i * DIM]);
        }
        for (int i = ORDER; i < DIM; i++) {
            final int v = pick(set);
            problem[i * DIM] = v;
            set &= ~singleton(v);
        }
    }

    private static int chooseRest(int[] grid, int[] freedom) {
        final int i = searchLeastFree(grid, freedom);
        if (i < 0) {
            return 0;
        }

        int set = freedom[i];
        while (set != 0) {
            int[] new_free = new int[ELEMENTS];
            final int v = pick(set);

            set &= ~singleton(v);
            grid[i] = v;

            copy(freedom, new_free);
            freedomEliminate(new_free, i % DIM, i / DIM, v);

            if (chooseRest(grid, new_free) == 0) {
                return 0;
            }
        }
        grid[i] = 0;
        return -1;
    }

    private static int[] chooseGrid() {
        int[] freedom = new int[ELEMENTS];
        int[] grid = new int[ELEMENTS];

        chooseB1(grid);
        chooseB2(grid);
        chooseB3(grid);
        chooseCol1(grid);

        initFreedom(grid, freedom);
        chooseRest(grid, freedom);

        return grid;
    }

    /************************************************************************
     * Puzzle generator
     *
     * To generate a puzzle, we start with a solution grid, and an initial
     * puzzle (which may be the same as the solution). We try altering the
     * puzzle by either randomly adding a pair of clues from the solution, or
     * randomly removing a pair of clues. After each alteration, we check to
     * see if we have a valid puzzle. If it is, and it's more difficult than
     * anything we've encountered so far, save it as the best puzzle.
     *
     * To avoid getting stuck in local minima in the space of puzzles, we allow
     * the algorithm to wander for a few steps before starting again from the
     * best-so-far puzzle.
     */

    private static int harden(int[] solution, int[] puzzle, int max_iter) {
        int[] best = new int[1];

        solve(puzzle, best);

        for (int i = 0; i < max_iter; i++) {
            int[] next = new int[ELEMENTS];
            copy(puzzle, next);

            for (int j = 0; j < DIM * 2; j++) {
                int c = rng.nextInt(ELEMENTS);
                int[] s = new int[1];
                if (rng.nextBoolean()) {
                    next[c] = solution[c];
                    next[ELEMENTS - c - 1] = solution[ELEMENTS - c - 1];
                } else {
                    next[c] = 0;
                    next[ELEMENTS - c - 1] = 0;
                }

                if (solve(next, s) == 0 && s[0] > best[0]) {
                    copy(next, puzzle);
                    best[0] = s[0];
                }
            }
        }
        return best[0];
    }

    private static int[] actionGenerate() {
        int[] puzzle = new int[ELEMENTS];
        int[] grid = chooseGrid();
        copy(grid, puzzle);

        int diff = harden(grid, puzzle, MAX_ITER);
//        System.out.println("difficulty: "+diff);

        return puzzle;
    }
}
