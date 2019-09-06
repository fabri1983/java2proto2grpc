package org.fabri1983.javagrpc.grpc.artifact.server;

import io.grpc.stub.StreamObserver;

/**
 * Used to let Spring collects beans implementing this interface to register them in a gRPC Server instance.
 * Those beans must extend from XxxImplBase which is the grpc generated class for server implementation. 
 */
public interface GrpcServiceMarker {

	default void grcpTryCatch(StreamObserver<?> responseObserver, Runnable process) {
		try {
			process.run();
		} catch (Exception e) {
			responseObserver.onError(e);
		}
	}
	
}
