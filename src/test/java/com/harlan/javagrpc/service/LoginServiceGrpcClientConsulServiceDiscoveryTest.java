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
import com.harlan.javagrpc.testutil.ConsulProperties;
import com.harlan.javagrpc.testutil.rules.ConsulServiceRegisterRule;
import com.harlan.javagrpc.testutil.rules.GrpcManagedChannelWithConsulRule;
import com.harlan.javagrpc.testutil.rules.GrpcServerStarterRule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@ClassRule
	public static ConsulServiceRegisterRule consulServiceRegisterRule = new ConsulServiceRegisterRule();
	
	@Rule
	public GrpcServerStarterRule serverStarterRule = new GrpcServerStarterRule(50051);
	
	@Rule
	public GrpcManagedChannelWithConsulRule mangedChannelRule = new GrpcManagedChannelWithConsulRule(
			GrpcConfiguration.fromConsulServiceDiscovery(
					ConsulProperties.consulServiceName,
					ConsulProperties.consulHost,
					ConsulProperties.consulPort,
					0) /* 0 secs = disable health check */);

	@Test
	public void testMultiClientWithServiceDiscovery() throws InterruptedException {
		if (!consulServiceRegisterRule.isRegistered()) {
			log.warn("Consul Service wasn't registered. Test won't run and is resolved as correct.");
			Assert.assertTrue(true);
			return;
		}
		
		// register login service
		registerLoginServiceGrpc();
		
		// number of concurrent client stubs calls
		int repeatNumStubs = 10;
		
		// create login service proxy (stub)
		List<LoginService> loginServices = repeatLoginServiceClientStub(repeatNumStubs);
		
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
        List<Future<Void>> futures = executorService.invokeAll(tasks, 5, TimeUnit.SECONDS);
        
        // block until all tasks are done
        futures.forEach( f -> {
				try {
					f.get();
				} catch (InterruptedException | ExecutionException ex) {
					throw new RuntimeException(ex);
				}
			});
	}

	private void registerLoginServiceGrpc() {
		LoginBusiness loginBusiness = new LoginBusinessImpl();
		LoginServiceGrpcImpl loginServiceGrpc = new LoginServiceGrpcImpl(loginBusiness);
		serverStarterRule.getServerStarter().register(loginServiceGrpc);
	}
	
	private List<LoginService> repeatLoginServiceClientStub(int repeatNum) {
		LoginService loginServiceStub = createLoginServiceClientStub();
		List<LoginService> list = new ArrayList<>(repeatNum);
		for (int i = 0; i < repeatNum; ++i) {
			list.add(loginServiceStub);
		}
		return list;
	}
	
	private LoginService createLoginServiceClientStub() {
		LoginServiceFutureStub futureStub = LoginServiceGrpc.newFutureStub(mangedChannelRule.getChannel());
		LoginService loginService = new LoginServiceGrpcClientProxy(futureStub);
		return loginService;
	}

	private void callAndAssert(LoginService loginService, User user) {
		try {
			Request request = Request.from(user.getId(), user.getName(), user.getCorpus());
			Request2 request2 = Request2.from(user.getId(), user.getName());
			
			Response response = loginService.getRes(request, request2);
	
			Assert.assertEquals(user.getId(), response.getId());
			Assert.assertEquals(user.getName(), response.getName());
			Assert.assertEquals(user.getCorpus(), response.getCorpus());
		}
		catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
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
