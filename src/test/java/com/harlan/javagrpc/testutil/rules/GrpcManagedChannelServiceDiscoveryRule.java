package com.harlan.javagrpc.testutil.rules;

import com.harlan.javagrpc.grpc.artifact.GrpcConfiguration;
import com.harlan.javagrpc.grpc.artifact.client.managedchannel.GrpcManagedChannelServiceDiscovery;
import com.harlan.javagrpc.grpc.artifact.client.managedchannel.IGrpcManagedChannel;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class GrpcManagedChannelServiceDiscoveryRule extends GrpcCleanupRule {

	private IGrpcManagedChannel managedChannel;
	private GrpcConfiguration config;
	
	public GrpcManagedChannelServiceDiscoveryRule(GrpcConfiguration config) {
		super();
		this.config = config;
	}

	@Override
	public Statement apply(final Statement base, Description description) {
		managedChannel = new GrpcManagedChannelServiceDiscovery(config);
		register(managedChannel.getChannel());
		return super.apply(base, description);
	}

	public IGrpcManagedChannel getManagedChannel() {
		return managedChannel;
	}
	
}
