package com.harlan.javagrpc.testutil.rules;

import com.halran.javagrpc.grpc.artifact.GrpcServerStarter;
import com.halran.javagrpc.grpc.artifact.IGrpcServerStarter;

import java.io.IOException;

import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class GrpcServerStarterRule extends GrpcCleanupRule {

	private IGrpcServerStarter serverStarter;
	private int port;
	
	public GrpcServerStarterRule(int port) {
		super();
		this.port = port;
	}
	
	@Override
	public Statement apply(final Statement base, Description description) {
		boolean withShutdownHook = false;
		serverStarter = new GrpcServerStarter(port, withShutdownHook);
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
