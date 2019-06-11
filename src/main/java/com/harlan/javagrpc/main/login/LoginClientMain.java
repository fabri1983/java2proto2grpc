package com.harlan.javagrpc.main.login;

import com.halran.javagrpc.model.Corpus;
import com.halran.javagrpc.model.Request;
import com.halran.javagrpc.model.Request2;
import com.halran.javagrpc.model.Response;
import com.harlan.javagrpc.service.LoginServiceGrpcProxy;
import com.harlan.javagrpc.service.contract.LoginService;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceBlockingStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

public class LoginClientMain {
	
	private final ManagedChannel channel;
	private final LoginService loginService;
	
	public LoginClientMain(String host, int port) {
		channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
		LoginServiceBlockingStub blockingStub = LoginServiceGrpc.newBlockingStub(channel);
		loginService = new LoginServiceGrpcProxy(blockingStub);
	}

	public void shutdown() throws InterruptedException {
		System.out.println("Client shutdown.");
		channel.shutdown().awaitTermination(2, TimeUnit.SECONDS);
	}

	public void login(User user, Corpus corpus) {
		Request request = Request.from(user.getId(), user.getName(), corpus);
		Request2 request2 = Request2.from(user.getId(), user.getName());
		int loginId = loginService.login(request);
		Response response = loginService.getRes(request, request2);
		System.out.println("login id: " + loginId + ". Corpus: " + response.getCorpus().toString());
	}

	public static void main(String[] args) throws InterruptedException {
		LoginClientMain client = new LoginClientMain("127.0.0.1", 50051);
		User[] users = new User[] { 
				User.from(11, "pepito"),
				User.from(22, "martita"),
				User.from(33, "robertito")};
		Corpus[] corpusValues = Corpus.values();
		for (int i = 0; i < users.length; i++) {
			client.login(users[i], corpusValues[i % corpusValues.length]);
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
