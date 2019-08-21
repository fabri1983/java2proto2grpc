package org.fabri1983.javagrpc.testutil.rules;

import java.io.IOException;

import org.fabri1983.javagrpc.grpc.artifact.server.GrpcServerStarterSecured;
import org.fabri1983.javagrpc.grpc.artifact.server.IGrpcServerStarter;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class GrpcServerStarterSecuredRule extends GrpcCleanupRule {

	private IGrpcServerStarter serverStarter;
	private int port;
	
	public GrpcServerStarterSecuredRule(int port) {
		super();
		this.port = port;
	}
	
	@Override
	public Statement apply(final Statement base, Description description) {
		boolean withShutdownHook = false;
		serverStarter = new GrpcServerStarterSecured(port, withShutdownHook);
		startServerOrThrow();
		register(serverStarter.getServer());
		return super.apply(base, description);
	}

	public IGrpcServerStarter getServerStarter() {
		return serverStarter;
	}
	
	private void startServerOrThrow() {
		try {
			serverStarter.start();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
}
