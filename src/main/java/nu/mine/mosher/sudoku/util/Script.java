/*
 * Created on Oct 17, 2005
 */
package nu.mine.mosher.sudoku.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;

/**
 * Reads all lines from a script type file. Each line will be stripped of
 * EOL-style comments (beginning with the given comment character), then
 * trimmed. Blank lines (after the above cleaning) are ignored.
 * 
 * @author Chris Mosher
 */
public class Script {
	private final BufferedReader in;
	private final char charEOLComment;

	/**
	 * . Initializes this Script to read line from the given BufferedReader, and
	 * use the given character as an EOL comment character.
	 * 
	 * @param in
	 * @param charEOLComment
	 */
	public Script(final BufferedReader in, final char charEOLComment) {
		this.in = in;
		this.charEOLComment = charEOLComment;
	}

	/**
	 * Reads all lines, cleans them, and appends them to the given Collection.
	 * 
	 * @param appendTo
	 * @throws IOException
	 */
	public void appendLines(final Collection<String> appendTo) throws IOException {
		for (String sLine = this.in.readLine(); sLine != null; sLine = this.in.readLine()) {
			final String sCleanedLine = cleanLine(sLine);
			if (sCleanedLine.length() > 0) {
				appendTo.add(sCleanedLine);
			}
		}
	}

	private String cleanLine(final String sLine) {
		String ret = sLine;

		final int iComment = ret.indexOf(this.charEOLComment);
		if (iComment >= 0) {
			ret = ret.substring(0, iComment);
		}

		ret = ret.trim();

		return ret;
	}
}
