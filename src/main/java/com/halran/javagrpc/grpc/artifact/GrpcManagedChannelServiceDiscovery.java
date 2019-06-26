package com.halran.javagrpc.grpc.artifact;

import io.grpc.ManagedChannelBuilder;
import io.shunters.grpc.component.grpc.ConsulNameResolver;

import java.util.List;

public class GrpcManagedChannelServiceDiscovery extends GrpcManagedChannel {

	public GrpcManagedChannelServiceDiscovery(GrpcConfiguration config) {
		super(config);
	}
	
	@Override
	protected ManagedChannelBuilder<?> createManagedChannelBuilder(GrpcConfiguration config) {
		
		String consulAddr = "consul://" + config.getConsulHost() + ":" + config.getConsulPort();
		String consulServiceName = config.getConsulServiceName();
		boolean ignoreConsul = config.isIgnoreConsul();
		List<String> staticHostPorts = config.getStaticHostPorts();
        int timerCheckPeriodInSeconds = config.getTimerCheckPeriodInSeconds();
        
        ConsulNameResolver.ConsulNameResolverProvider consulNameResolverProvider = 
        		new ConsulNameResolver.ConsulNameResolverProvider(consulServiceName, timerCheckPeriodInSeconds, 
        				ignoreConsul, staticHostPorts);
        
        return ManagedChannelBuilder
        		.forTarget(consulAddr)
                .nameResolverFactory(consulNameResolverProvider)
                // use plain text if your entire microservice ecosystem is inside a controlled network, 
                // otherwise setup your security artifacts such as key/trust stores
                .usePlaintext();
	}
	
}
