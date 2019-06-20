package net.badata.protobuf.converter.type;

import com.google.protobuf.Timestamp;

public class TimeUtil {

	// Timestamp for "0001-01-01T00:00:00Z"
	private static final long TIMESTAMP_SECONDS_MIN = -62135596800L;

	// Timestamp for "9999-12-31T23:59:59Z"
	private static final long TIMESTAMP_SECONDS_MAX = 253402300799L;

	public static final long NANOS_PER_SECOND = 1000000000;
	public static final long NANOS_PER_MILLISECOND = 1000000;
	public static final long MILLIS_PER_SECOND = 1000;
	  
	public static Timestamp normalizedTimestamp(long seconds, int nanos) {
		if (nanos <= -NANOS_PER_SECOND || nanos >= NANOS_PER_SECOND) {
			seconds += nanos / NANOS_PER_SECOND;
			nanos %= NANOS_PER_SECOND;
		}
		if (nanos < 0) {
			nanos += NANOS_PER_SECOND;
			seconds -= 1;
		}
		if (seconds < TIMESTAMP_SECONDS_MIN || seconds > TIMESTAMP_SECONDS_MAX) {
			throw new IllegalArgumentException("Timestamp is out of valid range.");
		}
		return Timestamp.newBuilder().setSeconds(seconds).setNanos(nanos).build();
	}
	
}
