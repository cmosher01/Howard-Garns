/*
 * Created on Sep 24, 2005
 */
package nu.mine.mosher.sudoku.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import nu.mine.mosher.sudoku.state.GameManager;
import nu.mine.mosher.sudoku.state.MoveAutomationType;

class PossibililtyPanel extends JPanel {
	private static final Color COLOR_POSSIBILITY = new Color(96, 128, 0);

	private final GameManager game;
	private final int iSbox;
	private final int iSquare;
	private final int iPoss;

	public PossibililtyPanel(final GameManager game, final int iSbox, final int iSquare, final int i) {
		this.game = game;
		this.iSbox = iSbox;
		this.iSquare = iSquare;
		this.iPoss = i;

		setOpaque(false);

		setPreferredSize(new Dimension(30, 30));

		setForeground(COLOR_POSSIBILITY);

		addMouseListener(new MouseAdapter() {
			@SuppressWarnings("synthetic-access")
			@Override
			public void mouseClicked(final MouseEvent e) {
				mouseCicked(e);
			}
		});
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);

		if (this.game.hasAnswer(this.iSbox, this.iSquare)) {
			return;
		}

		if (this.game.isEliminated(this.iSbox, this.iSquare, this.iPoss)) {
			return;
		}

		final String sPoss = Integer.toString(this.iPoss + 1);
		drawCentered(sPoss, g);
	}

	private void drawCentered(final String sPoss, final Graphics g) {
		final FontMetrics fontMetrics = g.getFontMetrics();

		final Rectangle2D bounds = fontMetrics.getStringBounds(sPoss, g);
		final int textHeight = (int) (bounds.getHeight());
		final int textWidth = (int) (bounds.getWidth());

		final int x = (getWidth() - textWidth) / 2;
		final int y = (getHeight() - textHeight) / 2 + fontMetrics.getAscent();

		g.drawString(sPoss, x, y);
	}

	private void mouseCicked(final MouseEvent e) {
		final boolean hasAnswer = this.game.hasAnswer(this.iSbox, this.iSquare);
		final boolean isAnswer = !this.game.isEliminated(this.iSbox, this.iSquare, this.iPoss);

		if (e.getButton() == MouseEvent.BUTTON1) {
			if (!(hasAnswer && isAnswer)) {
				this.game.toggle(this.iSbox, this.iSquare, this.iPoss, MoveAutomationType.MANUAL);
			}
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			if (!hasAnswer) {
				this.game.keep(this.iSbox, this.iSquare, this.iPoss, MoveAutomationType.MANUAL);
			}
		}
	}
}
