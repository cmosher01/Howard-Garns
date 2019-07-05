/*
 * Created on Oct 23, 2005
 */
package nu.mine.mosher.sudoku.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Improved version of <code>java.util.Date</code>. Objects of this class are
 * immutable. This class actually represents a bridge to
 * <code>java.util.Date</code>.
 * 
 * @author Chris Mosher
 */
public class Time implements Comparable<Time>, Serializable {
	private static final String ISO8601_RFC3339_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	private static final SimpleDateFormat fmtDateTime = new SimpleDateFormat(ISO8601_RFC3339_DATE_TIME_FORMAT);

	private final long ms;

	private transient String asString;

	/**
	 * @param date
	 *          the <code>java.util.Date</code> this object will wrap
	 */
	public Time(final Date date) {
		this(date.getTime());
	}

	private Time(final long ms) {
		this.ms = ms;
		init();
	}

	/**
	 * Returns this time as a (new) <code>java.util.Date</code>.
	 * 
	 * @return new <code>java.util.Date</code>
	 */
	public Date asDate() {
		return new Date(this.ms);
	}

	@Override
	public boolean equals(final Object object) {
		if (!(object instanceof Time)) {
			return false;
		}
		final Time that = (Time) object;
		return this.ms == that.ms;
	}

	@Override
	public int hashCode() {
		return (int) (this.ms ^ (this.ms >>> 32));
	}

	/**
	 * This time, as a string in the format:
	 * <code>yyyy-MM-dd'T'HH:mm:ss.SSSZ</code> (as in
	 * <code>SimpleDateFormat</code>), or an empty string if this time is zero.
	 * 
	 * @return time as a string
	 */
	@Override
	public String toString() {
		return this.asString;
	}

	@Override
	public int compareTo(final Time that) {
		if (this.ms < that.ms) {
			return -1;
		}
		if (that.ms < this.ms) {
			return +1;
		}
		return 0;
	}

	private void writeObject(final ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
	}

	private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();

		init();
	}

	private void init() {
		if (this.ms != 0) {
			this.asString = fmtDateTime.format(new Date(this.ms));
		} else {
			this.asString = "";
		}
	}

	public static Time readFromString(String sTime) throws ParseException {
		long ms = 0;
		if (sTime.length() > 0) {
			if (sTime.endsWith("Z")) {
				sTime = sTime.substring(0, sTime.length() - 1) + "+0000";
			}
			ms = fmtDateTime.parse(sTime).getTime();
		}
		return new Time(ms);
	}
}