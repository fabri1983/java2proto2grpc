package com.harlan.javagrpc.main.login;

import com.halran.javagrpc.grpc.artifact.server.GrpcServerStarterSecured;
import com.halran.javagrpc.grpc.artifact.server.IGrpcServerStarter;
import com.harlan.javagrpc.business.LoginBusinessImpl;
import com.harlan.javagrpc.business.contract.LoginBusiness;
import com.harlan.javagrpc.service.LoginServiceGrpcImpl;

import java.io.IOException;

public class LoginServerMain {
	
	public static void main(String[] args) throws IOException {
		int port = 50051;
		boolean withShutdownHook = true;
		IGrpcServerStarter serverStarter = new GrpcServerStarterSecured(port, withShutdownHook);
		
		// register login service
		LoginBusiness loginBusiness = new LoginBusinessImpl();
		LoginServiceGrpcImpl loginServiceGrpc = new LoginServiceGrpcImpl(loginBusiness);
		serverStarter.registerBeforeStart(loginServiceGrpc);
		
		serverStarter.start();
		
		// you can register additional bindable services using serverStarter.register()
		
		boolean blockInOtherThread = false;
		serverStarter.blockUntilShutdown(blockInOtherThread);
	}
	
}
