package com.harlan.javagrpc.service.contract;

/**
 * Service Wrapper for io.grpc.examples.helloworld.protobuf.GreeterGrpc
 */
public interface GreeterService {

	String sayHello(String message);
	
}
