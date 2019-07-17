package com.harlan.javagrpc.main.greeter;

import com.harlan.javagrpc.business.GreeterBusinessImpl;
import com.harlan.javagrpc.business.contract.GreeterBusiness;
import com.harlan.javagrpc.grpc.artifact.server.GrpcServerStarter;
import com.harlan.javagrpc.grpc.artifact.server.IGrpcServerStarter;
import com.harlan.javagrpc.service.GreeterServiceGrpcImpl;

import java.io.IOException;

public class GreeterServerMain {

	public static void main(String[] args) throws IOException {
		int port = 50051;
		boolean withShutdownHook = true;
		IGrpcServerStarter server = new GrpcServerStarter(port, withShutdownHook);
		
		// register greeter service
		GreeterBusiness greeterBusiness = new GreeterBusinessImpl();
		GreeterServiceGrpcImpl greeterService = new GreeterServiceGrpcImpl(greeterBusiness);
		server.registerBeforeStart(greeterService);
		
		server.start();
		boolean blockInOtherThread = false;
		server.blockUntilShutdown(blockInOtherThread);
	}

}