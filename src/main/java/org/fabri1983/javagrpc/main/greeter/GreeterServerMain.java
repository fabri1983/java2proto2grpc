package org.fabri1983.javagrpc.main.greeter;

import java.io.IOException;

import org.fabri1983.javagrpc.business.GreeterBusinessImpl;
import org.fabri1983.javagrpc.business.contract.GreeterBusiness;
import org.fabri1983.javagrpc.grpc.artifact.server.GrpcServerStarter;
import org.fabri1983.javagrpc.grpc.artifact.server.IGrpcServerStarter;
import org.fabri1983.javagrpc.service.grpc.server.GreeterServiceGrpcServer;

public class GreeterServerMain {

	public static void main(String[] args) throws IOException {
		int port = 50051;
		boolean withShutdownHook = true;
		IGrpcServerStarter server = new GrpcServerStarter(port, withShutdownHook);
		
		// register greeter service
		GreeterBusiness greeterBusiness = new GreeterBusinessImpl();
		GreeterServiceGrpcServer greeterService = new GreeterServiceGrpcServer(greeterBusiness);
		server.registerBeforeStart(greeterService);
		
		server.start();
		boolean blockInOtherThread = false;
		server.blockUntilShutdown(blockInOtherThread);
	}

}