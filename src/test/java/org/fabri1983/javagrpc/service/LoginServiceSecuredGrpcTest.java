package org.fabri1983.javagrpc.service;

import org.fabri1983.javagrpc.business.LoginBusinessImpl;
import org.fabri1983.javagrpc.business.contract.LoginBusiness;
import org.fabri1983.javagrpc.grpc.artifact.GrpcConfiguration;
import org.fabri1983.javagrpc.grpc.artifact.client.managedchannel.IGrpcManagedChannelFactory.GrpcManagedChannelSecuredFactory;
import org.fabri1983.javagrpc.model.Corpus;
import org.fabri1983.javagrpc.model.Request;
import org.fabri1983.javagrpc.model.Request2;
import org.fabri1983.javagrpc.model.Response;
import org.fabri1983.javagrpc.service.contract.LoginService;
import org.fabri1983.javagrpc.service.grpc.client.LoginServiceGrpcClientStub;
import org.fabri1983.javagrpc.service.grpc.server.LoginServiceGrpcServer;
import org.fabri1983.javagrpc.testutil.rules.GrpcManagedChannelRule;
import org.fabri1983.javagrpc.testutil.rules.GrpcServerStarterSecuredRule;
import org.fabri1983.javagrpc.testutil.rules.JunitPrintTestName;
import org.fabri1983.javagrpc.testutil.rules.JunitStopWatch;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Grpc Server and Client using TLS mutual authentication.  
 */
public class LoginServiceSecuredGrpcTest {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Rule(order = 1)
	public GrpcServerStarterSecuredRule serverStarterRule = new GrpcServerStarterSecuredRule(50051);

	@Rule(order = 2)
	public GrpcManagedChannelRule managedChannelRule = new GrpcManagedChannelRule(
			new GrpcManagedChannelSecuredFactory(), GrpcConfiguration.from("127.0.0.1", 50051));

	@Rule(order = 100)
	public JunitStopWatch stopwatch = new JunitStopWatch(log);
	
	@Rule(order = 101)
	public JunitPrintTestName testName = new JunitPrintTestName(log);
	
	@Test
	public void testSecured() {
		
		registerLoginServiceServerGrpc();
		
		LoginService loginService = createLoginServiceClientStub();
		
		// create some testing data
		User[] users = createUsers();
		
		// call grpc stub
		for (int i = 0; i < users.length; i++) {
			callAndAssert(loginService, users[i]);
		}
	}

	private void registerLoginServiceServerGrpc() {
		LoginBusiness loginBusiness = new LoginBusinessImpl();
		LoginServiceGrpcServer loginServiceServerGrpc = new LoginServiceGrpcServer(loginBusiness);
		serverStarterRule.getServerStarter().register(loginServiceServerGrpc);
	}

	private LoginService createLoginServiceClientStub() {
		LoginService loginService = new LoginServiceGrpcClientStub(managedChannelRule.getManagedChannel());
		return loginService;
	}

	private void callAndAssert(LoginService loginService, User user) {
		Request request = Request.from(user.getId(), user.getName(), user.getCorpus());
		Request2 request2 = Request2.from(user.getId(), user.getName());
		
		Response response = loginService.getRes(request, request2);

		Assert.assertEquals(user.getId(), response.getId());
		Assert.assertEquals(user.getName(), response.getName());
		Assert.assertEquals(user.getCorpus(), response.getCorpus());
	}

	private User[] createUsers() {
		User[] users = new User[] { 
				User.from(11, "pepito", Corpus.IMAGES),
				User.from(22, "martita", Corpus.LOCAL),
				User.from(33, "robertito", Corpus.PRODUCTS)};
		return users;
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
