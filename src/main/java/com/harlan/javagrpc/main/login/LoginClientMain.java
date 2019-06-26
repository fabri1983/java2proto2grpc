package com.harlan.javagrpc.main.login;

import com.halran.javagrpc.grpc.artifact.GrpcConfiguration;
import com.halran.javagrpc.grpc.artifact.GrpcManagedChannelSecured;
import com.halran.javagrpc.grpc.artifact.IGrpcManagedChannel;
import com.halran.javagrpc.model.Corpus;
import com.halran.javagrpc.model.Request;
import com.halran.javagrpc.model.Request2;
import com.halran.javagrpc.model.Response;
import com.harlan.javagrpc.service.LoginServiceGrpcClientProxy;
import com.harlan.javagrpc.service.contract.LoginService;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceFutureStub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginClientMain {
	
	private static final Logger log = LoggerFactory.getLogger(LoginClientMain.class);
	
	public static void main(String[] args) throws InterruptedException {
		
		// create managed channel
		String host = "127.0.0.1";
		int port = 50051;
		IGrpcManagedChannel managedChannel = new GrpcManagedChannelSecured(GrpcConfiguration.from(host, port));
		
		// create login service proxy (stub)
		LoginServiceFutureStub futureStub = LoginServiceGrpc.newFutureStub(managedChannel.getChannel());
		LoginService loginService = new LoginServiceGrpcClientProxy(futureStub);
		
		// create some testing data
		User[] users = new User[] { 
				User.from(11, "pepito", Corpus.IMAGES),
				User.from(22, "martita", Corpus.LOCAL),
				User.from(33, "robertito", Corpus.PRODUCTS)};
		
		// call grpc stub
		for (int i = 0; i < users.length; i++) {
			testCall(loginService, users[i]);
		}
		
		managedChannel.shutdown();
	}
	
	private static void testCall(LoginService loginService, User user) {
		Request request = Request.from(user.getId(), user.getName(), user.getCorpus());
		Request2 request2 = Request2.from(user.getId(), user.getName());
		
		int loginId = loginService.login(request);
		Response response = loginService.getRes(request, request2);
		log.info("login id: " + loginId + ". Corpus: " + response.getCorpus().toString());
	}

	private static class User {

		private int id;
		private String name;
		private Corpus corpus;
		
		public static User from(int id, String name, Corpus corpus) {
			User newObj = new User();
			newObj.id = id;
			newObj.name = name;
			newObj.corpus = corpus;
			return newObj;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public Corpus getCorpus() {
			return corpus;
		}
	}
	
}
