package com.halran.javagrpc.grpc.artifact;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * Register gRPC's {@link BindableService} objects implementing {@link GrpcServiceMarker} to be exposed by a gRPC Server.
 */
public class GrpcServerStarter implements IGrpcServerStarter {

	private int port;
	private Server server;
	private ServerBuilder<?> serverBuilder;
	
	public GrpcServerStarter(int port) {
		this.port = port;
		serverBuilder = createBuilder(port);
	}

	protected ServerBuilder<?> createBuilder(int port) {
		return ServerBuilder.forPort(port);
	}
	
	@Override
	public GrpcServerStarter register(GrpcServiceMarker grpcService) {
		if (!(grpcService instanceof BindableService)) {
            String simpleName = grpcService.getClass().getSimpleName();
			String message = "GrpcServiceMarker should only used for grpc BindableService. "
            		+ "Found wrong usage of GrpcServiceMarker for service: " + simpleName;
			throw new RuntimeException(message);
        }
        serverBuilder.addService((BindableService) grpcService);
		return this;
	}
	
	@Override
	public void start() throws IOException {
		
		try {
			server = serverBuilder
					.build()
					.start();
			System.out.println("Grpc Server started. Listening on port " + port);
		}
		catch (IOException e) {
			System.err.println("*** Could not start gRPC Server. " + e.getMessage());
			throw e;
		}

		// register server shutdown hook in case this server is executed as a standalone process.
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.err.println("*** shutting down gRPC server since JVM is shutting down.");
				GrpcServerStarter.this.stop();
				System.err.println("*** server shut down.");
			}
		});
	}

	@Override
	public void stop() {
		if (server != null) {
			server.shutdown();
		}
	}

	@Override
	public void blockUntilShutdown(boolean blockInOtherThread) {
		if (server != null) {
			if (blockInOtherThread) {
				new Thread() {
					@Override
					public void run() {
						awaitTermination();
					}
				};
			}
			else {
				awaitTermination();
			}
		}
	}

	private void awaitTermination() {
		try {
			server.awaitTermination();
		} catch (InterruptedException ex) {
			System.err.println(ex);
		}
	}
	
}
