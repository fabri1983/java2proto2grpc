package com.harlan.javagrpc.testutil.rules;

import com.harlan.javagrpc.testutil.IServiceDiscoveryProperties;

import io.shunters.grpc.api.component.ServiceDiscovery;
import io.shunters.grpc.component.consul.ConsulServiceDiscovery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsulServiceRegisterRule extends ExternalResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private IServiceDiscoveryProperties serviceDiscoveryProps;
	private boolean registered;
	private ServiceDiscovery consulSingleton;
	private List<String> tempServiceIds = new ArrayList<>(2);
	
	public ConsulServiceRegisterRule(IServiceDiscoveryProperties props) {
		super();
		this.serviceDiscoveryProps = props;
		this.consulSingleton = ConsulServiceDiscovery.singleton(
				serviceDiscoveryProps.getConsulHost(), 
				serviceDiscoveryProps.getConsulPort());
	}

	@Override
	protected void before() throws Throwable {
		try {
			// register service
			registerService();
			
			// FIXME [Improvement] Query the health check until is available
			LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
			
			registered = true;
			log.info("Consul: " + serviceDiscoveryProps.getConsulServiceName() + " registered.");
		}
		catch (Exception e) {
			log.warn(e.getClass().getSimpleName() + ". " + e.getMessage());
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
			log.warn(e.getClass().getSimpleName() + ". " + e.getMessage());
		}
		registered = false;
	}
	
	public boolean isRegistered() {
		return registered;
	}

	private void registerService() {
		serviceDiscoveryProps.getGrpcAddressList().forEach( grpcAddress -> {
			
			String[] split = serviceDiscoveryProps.splitAddress(grpcAddress);
			String grpcHost = split[0];
			int grpcPort = Integer.valueOf(split[1]);
			
			String serviceId = serviceDiscoveryProps.getConsulServiceIdPrefix() + grpcAddress;
			tempServiceIds.add(serviceId);
			
			consulSingleton.createService(
					serviceDiscoveryProps.getConsulServiceName(), 
					serviceId, 
					null, // tags
					grpcHost, 
					grpcPort, 
					null, // serviceDiscoveryProps.getConsulCheckScript(),
					null, // serviceDiscoveryProps.getConsulCheckHttp(),
					serviceDiscoveryProps.getConsulCheckTcp(), 
					serviceDiscoveryProps.getConsulCheckInterval(), 
					serviceDiscoveryProps.getConsulCheckTimeout(),
					null); // serviceDiscoveryProps.getConsulCheckTtl()
		});
	}

	private void deregisterService() {
		tempServiceIds.forEach( serviceId -> consulSingleton.deregisterService(serviceId) );
	}

}
