package org.fabri1983.javagrpc.grpc.artifact.client.managedchannel;

import io.grpc.LoadBalancerRegistry;
import io.grpc.ManagedChannelBuilder;

import java.util.List;

import org.fabri1983.javagrpc.grpc.artifact.GrpcConfiguration;
import org.fabri1983.javagrpc.grpc.artifact.discovery.ConsulNameResolver;

public class GrpcManagedChannelServiceDiscovery extends GrpcManagedChannel {

	private String targetAddress;
	
	public GrpcManagedChannelServiceDiscovery(GrpcConfiguration config) {
		super(config);
	}
	
	@Override
	protected ManagedChannelBuilder<?> createManagedChannelBuilder(GrpcConfiguration config) {
		
		targetAddress = "consul://" + config.getConsulHost() + ":" + config.getConsulPort();
		String consulServiceName = config.getConsulServiceName();
		boolean ignoreConsul = config.isIgnoreConsul();
		List<String> staticGrpcHostPorts = config.getStaticGrpcHostPorts();
        int timerCheckPeriodInSeconds = config.getTimerCheckPeriodInSeconds();
        
        // create dynamic or static service discovery resolver
        ConsulNameResolver.ConsulNameResolverProvider consulNameResolverProvider = 
        		new ConsulNameResolver.ConsulNameResolverProvider(consulServiceName, timerCheckPeriodInSeconds, 
        				ignoreConsul, staticGrpcHostPorts);

        ManagedChannelBuilder<?> builder = ManagedChannelBuilder
        		.forTarget(targetAddress)
                .nameResolverFactory(consulNameResolverProvider)
                // use plain text if your entire microservice ecosystem is inside a closed network or behind a firewall, 
                // otherwise setup your security artifacts such as key/trust stores
                .usePlaintext();
        
        // use grpc internal load balancing?
        if (config.isUseInternalGrpcLoadBalancing()) {
        	// use round robin policy
            String loadBalancerPolicyName = LoadBalancerRegistry.getDefaultRegistry()
            		.getProvider("round_robin")
            		.getPolicyName();
            builder.defaultLoadBalancingPolicy(loadBalancerPolicyName);
        }
        
		return builder;
	}
	
	@Override
	public String getTargetAddress() {
		return targetAddress;
	}
	
}
