package com.harlan.javagrpc.testutil;

import io.shunters.grpc.component.consul.ConsulServiceDiscovery;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsulServiceRegisterRule extends ExternalResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private String consulServiceName;
	private String consulId ;
	private String consulHost;
	private int consulPort;
	private String consulCheckInterval;
	private String consulCheckTimeout;
	private boolean registered;
	
	public ConsulServiceRegisterRule(String consulServiceName, String consulId, String consulHost, int consulPort,
			String consulCheckInterval, String consulCheckTimeout) {
		this.consulServiceName = consulServiceName;
		this.consulId = consulId;
		this.consulHost = consulHost;
		this.consulPort = consulPort;
		this.consulCheckInterval = consulCheckInterval;
		this.consulCheckTimeout = consulCheckTimeout;
	}

	@Override
	protected void before() throws Throwable {
		try {
			ConsulServiceDiscovery.singleton(consulHost, consulPort)
				.createService(consulServiceName, consulId, null, consulHost, consulPort, null, null, consulCheckInterval, consulCheckTimeout);
			registered = true;
			log.info("Consul: " + consulServiceName + " registered.");
		}
		catch (Exception e) {
			log.warn(e.getMessage());
			registered = false;
		}
	}

	@Override
	protected void after() {
		try {
			if (registered) {
				ConsulServiceDiscovery.singleton(consulHost, consulPort)
					.deregisterService(consulId);
				log.info("Consul: " + consulServiceName + " deregistered.");
			}
		}
		catch (Exception e) {
			log.warn(e.getMessage());
		}
		registered = false;
	}
	
	public boolean isRegistered() {
		return registered;
	}

}
