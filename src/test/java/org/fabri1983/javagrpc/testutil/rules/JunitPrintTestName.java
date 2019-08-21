package org.fabri1983.javagrpc.testutil.rules;

import org.junit.rules.TestName;
import org.junit.runner.Description;
import org.slf4j.Logger;

public class JunitPrintTestName extends TestName {

	private final Logger log;
	
	public JunitPrintTestName(Logger log) {
		super();
		this.log = log;
	}

	@Override
    protected void starting(Description d) {
        super.starting(d);
        log.info("{}()", getMethodName());
    }
	
}
