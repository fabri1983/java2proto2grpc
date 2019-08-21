package org.fabri1983.javagrpc.testutil.rules;

import org.fabri1983.javagrpc.grpc.artifact.GrpcConfiguration;
import org.fabri1983.javagrpc.grpc.artifact.client.managedchannel.IGrpcManagedChannel;
import org.fabri1983.javagrpc.grpc.artifact.client.managedchannel.IGrpcManagedChannelFactory;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class GrpcManagedChannelRule extends GrpcCleanupRule {

	private IGrpcManagedChannel managedChannel;
	private IGrpcManagedChannelFactory managedChannelFactory;
	private GrpcConfiguration config;
	
	public GrpcManagedChannelRule(IGrpcManagedChannelFactory managedChannelFactory, 
			GrpcConfiguration config) {
		super();
		this.managedChannelFactory = managedChannelFactory;
		this.config = config;
	}

	@Override
	public Statement apply(final Statement base, Description description) {
		managedChannel = managedChannelFactory.from(config);
		register(managedChannel.getChannel());
		return super.apply(base, description);
	}

	public IGrpcManagedChannel getManagedChannel() {
		return managedChannel;
	}
	
}
