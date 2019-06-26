package com.harlan.javagrpc.testutil.rules;

import com.halran.javagrpc.grpc.artifact.GrpcConfiguration;
import com.halran.javagrpc.grpc.artifact.GrpcManagedChannelSecured;
import com.halran.javagrpc.grpc.artifact.IGrpcManagedChannel;

import io.grpc.ManagedChannel;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class GrpcManagedChannelSecuredRule extends GrpcCleanupRule {

	private IGrpcManagedChannel managedChannel;
	private GrpcConfiguration config;
	
	public GrpcManagedChannelSecuredRule(GrpcConfiguration config) {
		super();
		this.config = config;
	}

	@Override
	public Statement apply(final Statement base, Description description) {
		managedChannel = new GrpcManagedChannelSecured(config);
		register(managedChannel.getChannel());
		return super.apply(base, description);
	}

	public ManagedChannel getChannel() {
		return managedChannel.getChannel();
	}
	
}
