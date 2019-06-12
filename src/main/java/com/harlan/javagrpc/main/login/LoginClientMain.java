package com.harlan.javagrpc.main.login;

import com.halran.javagrpc.grpc.artifact.GrpcManagedChannel;
import com.halran.javagrpc.model.Corpus;
import com.halran.javagrpc.model.Request;
import com.halran.javagrpc.model.Request2;
import com.halran.javagrpc.model.Response;
import com.harlan.javagrpc.service.LoginServiceGrpcProxy;
import com.harlan.javagrpc.service.contract.LoginService;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceFutureStub;

public class LoginClientMain {
	
	public static void main(String[] args) throws InterruptedException {
		
		// create managed channel
		GrpcManagedChannel managedChannel = new GrpcManagedChannel("127.0.0.1", 50051);
		
		// create login service proxy (stub)
		LoginServiceFutureStub futureStub = LoginServiceGrpc.newFutureStub(managedChannel.getChannel());
		LoginService loginService = new LoginServiceGrpcProxy(futureStub);
		
		// create some testing data
		User[] users = new User[] { 
				User.from(11, "pepito"),
				User.from(22, "martita"),
				User.from(33, "robertito")};
		Corpus[] corpusValues = Corpus.values();
		
		// call grpc stub
		for (int i = 0; i < users.length; i++) {
			testCall(loginService, users[i], corpusValues[i % corpusValues.length]);
		}
		
		managedChannel.shutdown();
	}
	
	private static void testCall(LoginService loginService, User user, Corpus corpus) {
		Request request = Request.from(user.getId(), user.getName(), corpus);
		Request2 request2 = Request2.from(user.getId(), user.getName());
		
		int loginId = loginService.login(request);
		Response response = loginService.getRes(request, request2);
		System.out.println("login id: " + loginId + ". Corpus: " + response.getCorpus().toString());
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
