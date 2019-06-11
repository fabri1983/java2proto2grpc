package com.halran.javagrpc.server;

import com.harlan.javagrpc.service.GrpcServiceMarker;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer {

	private int port;
	private Server server;
	private ServerBuilder<?> serverBuilder;
	
	public GrpcServer(int port) {
		this.port = port;
		serverBuilder = ServerBuilder.forPort(port);
	}
	
	public GrpcServer register(GrpcServiceMarker grpcService) {
		if (!(grpcService instanceof BindableService)) {
            String simpleName = grpcService.getClass().getSimpleName();
			String message = "GrpcBusinessMarker should only used for grpc BindableService. "
            		+ "Found wrong usage of GrpcBusinessMarker for service: " + simpleName;
			throw new RuntimeException(message);
        }
        serverBuilder.addService((BindableService) grpcService);
		return this;
	}
	
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
				GrpcServer.this.stop();
				System.err.println("*** server shut down.");
			}
		});
	}

	public void stop() {
		if (server != null) {
			server.shutdown();
		}
	}

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
