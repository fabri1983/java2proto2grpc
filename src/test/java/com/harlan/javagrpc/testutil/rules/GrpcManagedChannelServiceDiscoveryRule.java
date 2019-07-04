package com.harlan.javagrpc.testutil.rules;

import com.halran.javagrpc.grpc.artifact.GrpcConfiguration;
import com.halran.javagrpc.grpc.artifact.GrpcManagedChannelServiceDiscovery;
import com.halran.javagrpc.grpc.artifact.IGrpcManagedChannel;

import io.grpc.ManagedChannel;

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

	public ManagedChannel getChannel() {
		return managedChannel.getChannel();
	}
	
}
