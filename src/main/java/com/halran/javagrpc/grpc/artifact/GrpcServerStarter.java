package com.halran.javagrpc.grpc.artifact;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.util.MutableHandlerRegistry;

import java.io.IOException;
import java.util.List;

/**
 * Register gRPC's {@link BindableService} objects implementing {@link GrpcServiceMarker} to be exposed by a gRPC Server.
 */
public class GrpcServerStarter implements IGrpcServerStarter {

	private int port;
	private Server server;
	private ServerBuilder<?> serverBuilder;
	private MutableHandlerRegistry serviceRegistry;
	
	public GrpcServerStarter(int port) {
		this.port = port;
		serviceRegistry = new MutableHandlerRegistry();
		serverBuilder = createBuilder(port)
				// substantialperformance improvements. However, it also requires the application to not block under any circumstances.
				.directExecutor()
				// allow to register services once the server has started
				.fallbackHandlerRegistry(serviceRegistry);
	}

	protected ServerBuilder<?> createBuilder(int port) {
		return ServerBuilder
				.forPort(port)
				// substantialperformance improvements. However, it also requires the application to not block under any circumstances.
				.directExecutor();
	}
	
	@Override
	public GrpcServerStarter registerBeforeStart(List<GrpcServiceMarker> grpcServices) {
		for (GrpcServiceMarker grpcService : grpcServices) {
			registerBeforeStart(grpcService);
		}
		return this;
	}
	
	@Override
	public GrpcServerStarter registerBeforeStart(GrpcServiceMarker grpcService) {
		checkIsBindableService(grpcService);
        serverBuilder.addService((BindableService) grpcService);
		return this;
	}

	@Override
	public GrpcServerStarter register(GrpcServiceMarker grpcService) {
		checkIsBindableService(grpcService);
		serviceRegistry.addService((BindableService) grpcService);
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
	public void forceStop() {
		if (server != null) {
			server.shutdownNow();
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

	private void checkIsBindableService(GrpcServiceMarker grpcService) {
		if (!(grpcService instanceof BindableService)) {
	        String simpleName = grpcService.getClass().getSimpleName();
			String message = "GrpcServiceMarker should only used for grpc BindableService. "
	        		+ "Found wrong usage of GrpcServiceMarker for service: " + simpleName;
			throw new RuntimeException(message);
	    }
	}
	
}
