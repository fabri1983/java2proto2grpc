package org.fabri1983.javagrpc.testutil.rules;

import java.io.IOException;

import org.fabri1983.javagrpc.grpc.artifact.server.GrpcServerStarter;
import org.fabri1983.javagrpc.grpc.artifact.server.GrpcServiceMarker;
import org.fabri1983.javagrpc.grpc.artifact.server.IGrpcServerStarter;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class GrpcServerStarterRule extends GrpcCleanupRule {

	private IGrpcServerStarter[] serverStarters;
	private int[] ports;
	
	public GrpcServerStarterRule(int ... ports) {
		super();
		this.ports = ports;
	}
	
	@Override
	public Statement apply(final Statement base, Description description) {
		boolean withShutdownHook = false;
		serverStarters = new IGrpcServerStarter[ports.length];
		for (int i=0; i < serverStarters.length; ++i) {
			serverStarters[i] = new GrpcServerStarter(ports[i], withShutdownHook);
			startServerOrThrow(i);
			register(serverStarters[i].getServer());
		}
		return super.apply(base, description);
	}

	public void registerService(GrpcServiceMarker grpcService) {
		for (IGrpcServerStarter serverStarter : serverStarters) {
			serverStarter.register(grpcService);
		}
	}
	
	private void startServerOrThrow(int i) {
		try {
			serverStarters[i].start();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
	
}
