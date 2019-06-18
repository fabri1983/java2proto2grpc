package com.harlan.javagrpc.service;

import com.halran.javagrpc.model.Corpus;
import com.halran.javagrpc.model.Request;
import com.halran.javagrpc.model.Request2;
import com.halran.javagrpc.model.Response;
import com.harlan.javagrpc.business.LoginBusinessImpl;
import com.harlan.javagrpc.business.contract.LoginBusiness;
import com.harlan.javagrpc.service.contract.LoginService;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceFutureStub;
import com.harlan.javagrpc.testutil.GrpcManagedChannelSecuredRule;
import com.harlan.javagrpc.testutil.GrpcServerStarterSecuredRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test Secured Grpc Server and Client. 
 */
public class LoginServiceSecuredGrpcTest {
	
	@Rule
	public GrpcServerStarterSecuredRule serverStarterRule = new GrpcServerStarterSecuredRule(50051);
	
	@Rule
	public GrpcManagedChannelSecuredRule mangedChannelRule = new GrpcManagedChannelSecuredRule("127.0.0.1", 50051);
	
	@Test
	public void testSecured() {
		
		// register login service
		LoginBusiness loginBusiness = new LoginBusinessImpl();
		LoginServiceGrpcImpl loginServiceGrpc = new LoginServiceGrpcImpl(loginBusiness);
		serverStarterRule.getServerStarter().register(loginServiceGrpc);
		
		// create login service proxy (stub)
		LoginServiceFutureStub futureStub = LoginServiceGrpc.newFutureStub(mangedChannelRule.getChannel());
		LoginService loginService = new LoginServiceGrpcClientProxy(futureStub);
		
		// create some testing data
		User[] users = new User[] { 
				User.from(11, "pepito", Corpus.IMAGES),
				User.from(22, "martita", Corpus.LOCAL),
				User.from(33, "robertito", Corpus.PRODUCTS)};
		
		// call grpc stub
		for (int i = 0; i < users.length; i++) {
			callAndAssert(loginService, users[i]);
		}
	}
	
	private void callAndAssert(LoginService loginService, User user) {
		Request request = Request.from(user.getId(), user.getName(), user.getCorpus());
		Request2 request2 = Request2.from(user.getId(), user.getName());
		
		Response response = loginService.getRes(request, request2);

		Assert.assertEquals(user.getId(), response.getId());
		Assert.assertEquals(user.getName(), response.getName());
		Assert.assertEquals(user.getCorpus(), response.getCorpus());
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
