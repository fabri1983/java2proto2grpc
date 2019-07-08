package com.harlan.javagrpc.grpc.artifact.server;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.util.MutableHandlerRegistry;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Register gRPC's {@link BindableService} objects implementing {@link GrpcServiceMarker} to be exposed by a gRPC Server.
 */
public class GrpcServerStarter implements IGrpcServerStarter {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private int port;
	private boolean withShutdownHook;
	private Server server;
	private ServerBuilder<?> serverBuilder;
	private MutableHandlerRegistry serviceRegistry;
	
	public GrpcServerStarter(int port, boolean withShutdownHook) {
		this.port = port;
		this.withShutdownHook = withShutdownHook;
		serviceRegistry = new MutableHandlerRegistry();
		serverBuilder = createBuilder()
				// substantial performance improvements. However, it also requires the application to not block under any circumstances.
				.directExecutor()
				// allow to register services once the server has started
				.fallbackHandlerRegistry(serviceRegistry);
	}

	protected ServerBuilder<?> createBuilder() {
		return ServerBuilder
				.forPort(port);
	}
	
	public int getPort() {
		return port;
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
			log.info("Grpc Server started. Listening on port " + port);
		}
		catch (IOException e) {
			log.error("*** Could not start gRPC Server. {}. {}", e.getClass().getSimpleName(), e.getMessage());
			throw e;
		}

		// register server shutdown hook in case this server is executed as a standalone process.
		if (withShutdownHook) {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					log.error("*** shutting down gRPC server since JVM is shutting down.");
					GrpcServerStarter.this.stop();
					log.error("*** server shut down.");
				}
			});
		}
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

	@Override
	public Server getServer() {
		return server;
	}

	private void awaitTermination() {
		try {
			server.awaitTermination();
		} catch (InterruptedException ex) {
			log.error("", ex);
		}
	}

	private void checkIsBindableService(GrpcServiceMarker grpcService) {
		if (!(grpcService instanceof BindableService)) {
	        String simpleName = grpcService.getClass().getSimpleName();
			String message = GrpcServiceMarker.class.getSimpleName() + " should only used for grpc BindableService. "
	        		+ "Found wrong usage of GrpcServiceMarker for service: " + simpleName;
			throw new RuntimeException(message);
	    }
	}
	
}
