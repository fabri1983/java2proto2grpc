package com.harlan.javagrpc.testutil.rules;

import com.harlan.javagrpc.testutil.IServiceDiscoveryProperties;

import io.shunters.grpc.component.consul.ConsulServiceDiscovery;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsulServiceRegisterRule extends ExternalResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private IServiceDiscoveryProperties serviceDiscoveryProps;
	private boolean registered;

	
	public ConsulServiceRegisterRule(IServiceDiscoveryProperties props) {
		super();
		this.serviceDiscoveryProps = props;
	}

	@Override
	protected void before() throws Throwable {
		try {
			// attempt to deregister service
			deregisterService();
			// register service
			registerService();
			
			// TODO wait until health check is ok. Currently health check is omitted in ConsulServiceDiscovery.java line 109
			
			registered = true;
			log.info("Consul: " + serviceDiscoveryProps.getConsulServiceName() + " registered.");
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
				log.info("Consul: " + serviceDiscoveryProps.getConsulServiceName() + " deregistered.");
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
		ConsulServiceDiscovery
			.singleton(serviceDiscoveryProps.getConsulHost(), serviceDiscoveryProps.getConsulPort())
			.createService(
					serviceDiscoveryProps.getConsulServiceName(), 
					serviceDiscoveryProps.getConsulId(), 
					null, // tags
					serviceDiscoveryProps.getGrpcHost(), 
					serviceDiscoveryProps.getGrpcPort(), 
					null, // serviceDiscoveryProps.getConsulCheckScript(),
					null, // serviceDiscoveryProps.getConsulCheckHttp(),
					serviceDiscoveryProps.getConsulCheckTcp(), 
					serviceDiscoveryProps.getConsulCheckInterval(), 
					serviceDiscoveryProps.getConsulCheckTimeout(),
					null); // serviceDiscoveryProps.getConsulCheckTtl()
	}

	private void deregisterService() {
		ConsulServiceDiscovery
			.singleton(serviceDiscoveryProps.getConsulHost(), serviceDiscoveryProps.getConsulPort())
			.deregisterService(serviceDiscoveryProps.getConsulId());
	}

}
