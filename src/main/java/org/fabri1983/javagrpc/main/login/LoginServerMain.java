package org.fabri1983.javagrpc.main.login;

import java.io.IOException;

import org.fabri1983.javagrpc.business.LoginBusinessImpl;
import org.fabri1983.javagrpc.business.contract.LoginBusiness;
import org.fabri1983.javagrpc.grpc.artifact.server.GrpcServerStarterSecured;
import org.fabri1983.javagrpc.grpc.artifact.server.IGrpcServerStarter;
import org.fabri1983.javagrpc.service.grpc.server.LoginServiceGrpcServer;

public class LoginServerMain {
	
	public static void main(String[] args) throws IOException {
		int port = 50051;
		boolean withShutdownHook = true;
		IGrpcServerStarter serverStarter = new GrpcServerStarterSecured(port, withShutdownHook);
		
		// register login service
		LoginBusiness loginBusiness = new LoginBusinessImpl();
		LoginServiceGrpcServer loginServiceGrpc = new LoginServiceGrpcServer(loginBusiness);
		serverStarter.registerBeforeStart(loginServiceGrpc);
		
		serverStarter.start();
		
		// you can register additional bindable services using serverStarter.register()
		
		boolean blockInOtherThread = false;
		serverStarter.blockUntilShutdown(blockInOtherThread);
	}
	
}
