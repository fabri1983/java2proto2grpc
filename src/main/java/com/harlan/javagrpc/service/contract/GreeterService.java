package com.harlan.javagrpc.service.contract;

/**
 * Service Wrapper for io.grpc.examples.helloworld.protobuf.GreeterGrpc.
 * 
 * This interface hasn't been decorated with @GrpcEnabled since the protobuf "Greeter" is the one from grpc examples.
 */
public interface GreeterService {

	String sayHello(String message);
	
}
