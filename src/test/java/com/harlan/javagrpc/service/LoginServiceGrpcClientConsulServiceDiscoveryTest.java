package com.harlan.javagrpc.service;

import com.halran.javagrpc.grpc.artifact.GrpcConfiguration;
import com.halran.javagrpc.model.Corpus;
import com.halran.javagrpc.model.Request;
import com.halran.javagrpc.model.Request2;
import com.halran.javagrpc.model.Response;
import com.harlan.javagrpc.business.LoginBusinessImpl;
import com.harlan.javagrpc.business.contract.LoginBusiness;
import com.harlan.javagrpc.service.contract.LoginService;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc;
import com.harlan.javagrpc.service.contract.protobuf.LoginServiceGrpc.LoginServiceFutureStub;
import com.harlan.javagrpc.testutil.PropertiesLoader;
import com.harlan.javagrpc.testutil.rules.ConsulServiceRegisterRule;
import com.harlan.javagrpc.testutil.rules.GrpcManagedChannelWithConsulRule;
import com.harlan.javagrpc.testutil.rules.GrpcServerStarterRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Grpc Server and Client (non secured connection) using Consul as Service Discovery.
 */
public class LoginServiceGrpcClientConsulServiceDiscoveryTest {
	
	private static final Logger log = LoggerFactory.getLogger(LoginServiceGrpcClientConsulServiceDiscoveryTest.class);
	
	private static final Properties consulProperties = PropertiesLoader.getProperties("consul-test.properties");
	
	private static final String consulServiceName = "grpcServiceDiscovery-test";
	private static final String consulId = "server1";
	private static final String consulHost = getConsulIp();
	private static final int consulPort = getConsulPort();
	private static final String consulCheckInterval = "10s";
	private static final String consulCheckTimeout = "1s";
	
	@ClassRule
	public static ConsulServiceRegisterRule consulServiceRegisterRule = new ConsulServiceRegisterRule(
			consulServiceName, consulId, consulHost, consulPort, consulCheckInterval, consulCheckTimeout);
	
	@Rule
	public GrpcServerStarterRule serverStarterRule = new GrpcServerStarterRule(50051);
	
	@Rule
	public GrpcManagedChannelWithConsulRule mangedChannelRule = new GrpcManagedChannelWithConsulRule(
			GrpcConfiguration.fromConsulServiceDiscovery(consulServiceName, consulHost, consulPort, 0));

	private static String getConsulIp() {
		return consulProperties.getProperty("consul.ip");
	}
	
	private static int getConsulPort() {
		return Integer.parseInt(consulProperties.getProperty("consul.port"));
	}
	
	@Test
	public void testMultiClientWithServiceDiscovery() throws InterruptedException {
		if (!consulServiceRegisterRule.isRegistered()) {
			log.warn("Consul Service wasn't registered. Test won't run and is resolved as correct.");
			Assert.assertTrue(true);
			return;
		}
		
		// number of client tubs to create
		int numClientStubs = 10;
		
		// register login service
		registerLoginService();
		
		// create login service proxy (stub)
		List<LoginService> loginServices = createLoginServiceClientStub(numClientStubs);
		
		// create some testing data
		User[] users = createUsers();
		
        // wraps as Callable tasks
     	List<Callable<Void>> tasks = loginServices.stream()
     			.map( loginService -> new Callable<Void>() {
     				@Override
     	            public Void call() {
     					int randomIndex = (int) (Math.random() * users.length);
     					User user = users[randomIndex];
     					callAndAssert(loginService, user);
     	                return null;
     	            }
     			})
     			.collect( Collectors.toList() );
     	
		// call grpc stubs in a parallel fashion
		ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Future<Void>> futures = executorService.invokeAll(tasks);
        
        // block until all tasks are done
        futures.forEach( f -> {
				try {
					f.get();
				} catch (InterruptedException | ExecutionException ex) {
					throw new RuntimeException(ex);
				}
			});
	}

	private void registerLoginService() {
		LoginBusiness loginBusiness = new LoginBusinessImpl();
		LoginServiceGrpcImpl loginServiceGrpc = new LoginServiceGrpcImpl(loginBusiness);
		serverStarterRule.getServerStarter().register(loginServiceGrpc);
	}
	
	private LoginService createLoginServiceClientStub() {
		LoginServiceFutureStub futureStub = LoginServiceGrpc.newFutureStub(mangedChannelRule.getChannel());
		LoginService loginService = new LoginServiceGrpcClientProxy(futureStub);
		return loginService;
	}

	private List<LoginService> createLoginServiceClientStub(int numClientStubs) {
		List<LoginService> list = new ArrayList<>(numClientStubs);
		for (int i = 0; i < numClientStubs; ++i) {
			list.add(createLoginServiceClientStub());
		}
		return list;
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
