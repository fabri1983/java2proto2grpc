package com.harlan.javagrpc.main.login;

import com.halran.javagrpc.model.Request;
import com.harlan.javagrpc.service.LoginServiceRemoteProxy;
import com.harlan.javagrpc.service.contract.LoginService;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceBlockingStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

public class LoginClient {
	
	private final ManagedChannel channel;
	private final LoginServiceBlockingStub blockingStub;
	private final LoginService loginService;
	
	public LoginClient(String host, int port) {
		channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
		blockingStub = LoginServiceGrpc.newBlockingStub(channel);
		loginService = new LoginServiceRemoteProxy(blockingStub);
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(2, TimeUnit.SECONDS);
	}

	public void login(User user) {
		Request request = Request.from(user.getId(), user.getName());
		int loginId = loginService.login(request);
		System.out.println("login id: " + loginId);
	}

	public static void main(String[] args) throws InterruptedException {
		LoginClient client = new LoginClient("127.0.0.1", 50051);
		User[] users = new User[] { 
				User.from(1, "pepito"),
				User.from(2, "martita"),
				User.from(3, "robertito")};
		for (int i = 0; i < users.length; i++) {
			client.login(users[i]);
		}
		client.shutdown();
	}
	
	private static class User {

		private int id;
		private String name;

		public static User from(int id, String name) {
			User newObj = new User();
			newObj.id = id;
			newObj.name = name;
			return newObj;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

	}
	
}
