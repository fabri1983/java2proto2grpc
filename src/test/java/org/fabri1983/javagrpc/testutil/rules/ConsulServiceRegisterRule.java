package org.fabri1983.javagrpc.testutil.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.fabri1983.javagrpc.grpc.artifact.discovery.ConsulServiceDiscovery;
import org.fabri1983.javagrpc.grpc.artifact.discovery.IServiceDiscovery;
import org.fabri1983.javagrpc.testutil.IServiceDiscoveryProperties;
import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsulServiceRegisterRule extends ExternalResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private IServiceDiscoveryProperties serviceDiscoveryProps;
	private boolean registered;
	private IServiceDiscovery consulClient;
	private List<String> grpcAddressList;
	private List<String> tempServiceIds = new ArrayList<>(2);
	
	public ConsulServiceRegisterRule(IServiceDiscoveryProperties props, List<String> grpcAddressList) {
		super();
		this.serviceDiscoveryProps = props;
		this.grpcAddressList = grpcAddressList;
		this.consulClient = ConsulServiceDiscovery.singleton(
				serviceDiscoveryProps.getConsulHost(), 
				serviceDiscoveryProps.getConsulPort());
	}

	@Override
	protected void before() throws Throwable {
		try {
			// register service
			registerService();
			
			// FIXME [Improvement] Use consul client to query the health check until is available
			log.warn("Waiting 1 sec for new added services start their health check service.");
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
		grpcAddressList.forEach( grpcAddress -> {
			
			String[] split = splitAddress(grpcAddress);
			String grpcHost = split[0];
			int grpcPort = Integer.valueOf(split[1]);
			
			String serviceId = serviceDiscoveryProps.getConsulServiceIdPrefix() + grpcAddress;
			tempServiceIds.add(serviceId);
			
			consulClient.createService(
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
		tempServiceIds.forEach( serviceId -> consulClient.deregisterService(serviceId) );
	}

	private String[] splitAddress(String grpcAddress) {
		String[] split = grpcAddress.split(":");
		for (int i=0; i < split.length; ++i) {
			split[i] = split[i].trim();
		}
		return split;
	}

}
