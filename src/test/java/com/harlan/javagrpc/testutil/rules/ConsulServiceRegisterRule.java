package com.harlan.javagrpc.testutil.rules;

import com.harlan.javagrpc.testutil.ConsulProperties;

import io.shunters.grpc.component.consul.ConsulServiceDiscovery;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsulServiceRegisterRule extends ExternalResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private boolean registered;

	@Override
	protected void before() throws Throwable {
		try {
			ConsulServiceDiscovery.singleton(ConsulProperties.consulHost, ConsulProperties.consulPort)
				.createService(
						ConsulProperties.consulServiceName, 
						ConsulProperties.consulId, 
						null, 
						ConsulProperties.consulHost, 
						ConsulProperties.consulPort, 
						null, 
						null, 
						null, // ConsulProperties.consulCheckInterval 
						ConsulProperties.consulCheckTimeout,
						ConsulProperties.consulCheckTtl);
			registered = true;
			log.info("Consul: " + ConsulProperties.consulServiceName + " registered.");
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
				ConsulServiceDiscovery.singleton(ConsulProperties.consulHost, ConsulProperties.consulPort)
					.deregisterService(ConsulProperties.consulId);
				log.info("Consul: " + ConsulProperties.consulServiceName + " deregistered.");
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
