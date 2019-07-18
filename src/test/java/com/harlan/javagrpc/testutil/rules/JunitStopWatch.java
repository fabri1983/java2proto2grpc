package com.harlan.javagrpc.testutil.rules;

import java.util.concurrent.TimeUnit;

import org.junit.AssumptionViolatedException;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
import org.slf4j.Logger;

public class JunitStopWatch extends Stopwatch {

	private final Logger log;
	
	public JunitStopWatch(Logger log) {
		super();
		this.log = log;
	}

	@Override
	protected void succeeded(long nanos, Description description) {
//		logInfo(description, "succeeded", nanos);
	}

	@Override
	protected void failed(long nanos, Throwable e, Description description) {
//		logInfo(description, "failed", nanos);
	}

	@Override
	protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
//		logInfo(description, "skipped", nanos);
	}

	@Override
	protected void finished(long nanos, Description description) {
		logInfo(description, "finished", nanos);
	}

	private void logInfo(Description description, String status, long nanos) {
		String testName = description.getMethodName();
		long micros = TimeUnit.NANOSECONDS.toMicros(nanos);
		long millis = TimeUnit.NANOSECONDS.toMillis(nanos);
		if (micros < 1000) {
			log.info("{}() {}, spent {} micros", testName, status, micros);
		} else {
			log.info("{}() {}, spent {} millis", testName, status, millis);
		}
	}

}
