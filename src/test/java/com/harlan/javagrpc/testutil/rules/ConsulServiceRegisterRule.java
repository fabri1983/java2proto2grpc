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
			// attempt to deregister service
			deregisterService();
			// register service
			registerService();
			
			// TODO wait until health check is ok. Currently health check is omitted in ConsulServiceDiscovery.java line 109
			
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
				deregisterService();
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

	private void registerService() {
		ConsulServiceDiscovery.singleton(ConsulProperties.consulHost, ConsulProperties.consulPort)
			.createService(
					ConsulProperties.consulServiceName, 
					ConsulProperties.consulId, 
					null, 
					ConsulProperties.consulHost, 
					ConsulProperties.consulPort, 
					null, // ConsulProperties.consulCheckScript,
					null, // ConsulProperties.consulCheckHttp,
					ConsulProperties.consulCheckTcp, 
					ConsulProperties.consulCheckInterval, 
					ConsulProperties.consulCheckTimeout,
					null); // ConsulProperties.consulCheckTtl
	}

	private void deregisterService() {
		ConsulServiceDiscovery.singleton(ConsulProperties.consulHost, ConsulProperties.consulPort)
			.deregisterService(ConsulProperties.consulId);
	}

}
