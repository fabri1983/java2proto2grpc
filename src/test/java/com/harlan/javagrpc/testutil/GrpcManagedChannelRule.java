package com.harlan.javagrpc.testutil;

import com.halran.javagrpc.grpc.artifact.GrpcManagedChannel;
import com.halran.javagrpc.grpc.artifact.IGrpcManagedChannel;

import io.grpc.ManagedChannel;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class GrpcManagedChannelRule extends GrpcCleanupRule {

	private IGrpcManagedChannel managedChannel;
	private String host;
	private int port;
	
	public GrpcManagedChannelRule(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	@Override
	public Statement apply(final Statement base, Description description) {
		managedChannel = new GrpcManagedChannel(host, port);
		register(managedChannel.getChannel());
		return super.apply(base, description);
	}

	public ManagedChannel getChannel() {
		return managedChannel.getChannel();
	}
	
}
