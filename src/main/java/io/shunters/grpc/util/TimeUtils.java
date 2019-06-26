package io.shunters.grpc.util;

public class TimeUtils {

	public static void pauseSpinLock(long delay) {
		long start = System.nanoTime();
		while (start + delay >= System.nanoTime())
			;
	}

	public static void sleep(long sleepInMillis) {
		try {
			Thread.sleep(sleepInMillis);
		} catch (Exception e) {
			// nothing to report
		}
	}
}
