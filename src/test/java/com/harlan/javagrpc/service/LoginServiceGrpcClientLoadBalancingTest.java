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
import com.harlan.javagrpc.testutil.GrpcManagedChannelRule;
import com.harlan.javagrpc.testutil.GrpcServerStarterRule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test Grpc Server and Client (non secured connection). 
 */
public class LoginServiceGrpcClientLoadBalancingTest {
	
	@Rule
	public GrpcServerStarterRule serverStarterRule = new GrpcServerStarterRule(50051);
	
	@Rule
	public GrpcManagedChannelRule mangedChannelRule = new GrpcManagedChannelRule("127.0.0.1", 50051);

	@Test
	public void testMultiClientNonSecured() throws InterruptedException {
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
