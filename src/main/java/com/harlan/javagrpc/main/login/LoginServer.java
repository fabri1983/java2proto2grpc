package com.harlan.javagrpc.main.login;

import com.harlan.javagrpc.business.LoginBusinessImpl;
import com.harlan.javagrpc.business.contract.LoginBusiness;
import com.harlan.javagrpc.service.LoginServiceGrpcImpl;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class LoginServer {

	private int port = 50051;
	private Server server;

	private void start() throws IOException {
		LoginBusiness loginBusiness = new LoginBusinessImpl();
		LoginServiceGrpcImpl loginServiceGrpc = new LoginServiceGrpcImpl(loginBusiness);
		
		server = ServerBuilder.forPort(port)
				.addService(loginServiceGrpc)
				.build()
				.start();

		System.out.println("Login Server started. Listening on port " + port);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.err.println("*** shutting down gRPC server since JVM is shutting down.");
				LoginServer.this.stop();
				System.err.println("*** server shut down.");
			}
		});
	}

	private void stop() {
		if (server != null) {
			server.shutdown();
		}
	}

	private void blockUntilShutdown() throws InterruptedException {
		if (server != null) {
			server.awaitTermination();
		}
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		final LoginServer server = new LoginServer();
		server.start();
		server.blockUntilShutdown();
	}
	
}
