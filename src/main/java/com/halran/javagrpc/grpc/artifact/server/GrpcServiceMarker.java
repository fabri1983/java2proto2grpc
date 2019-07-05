package com.halran.javagrpc.grpc.artifact.server;

import io.grpc.stub.StreamObserver;

/**
 * Used to let Spring collects beans implementing this interface to register them in a gRPC Server instance.
 * So this interface must be implemented by classes that extends from XxxImplBase. 
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
