package org.fabri1983.javagrpc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.fabri1983.javagrpc.business.GreeterBusinessImpl;
import org.fabri1983.javagrpc.business.contract.GreeterBusiness;
import org.fabri1983.javagrpc.grpc.artifact.GrpcConfiguration;
import org.fabri1983.javagrpc.grpc.artifact.client.managedchannel.IGrpcManagedChannelFactory.GrpcManagedChannelNonSecuredFactory;
import org.fabri1983.javagrpc.model.Corpus;
import org.fabri1983.javagrpc.service.contract.GreeterService;
import org.fabri1983.javagrpc.service.grpc.client.GreeterServiceGrpcClientStub;
import org.fabri1983.javagrpc.service.grpc.server.GreeterServiceGrpcImpl;
import org.fabri1983.javagrpc.testutil.rules.GrpcManagedChannelRule;
import org.fabri1983.javagrpc.testutil.rules.GrpcServerStarterRule;
import org.fabri1983.javagrpc.testutil.rules.JunitPrintTestName;
import org.fabri1983.javagrpc.testutil.rules.JunitStopWatch;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Grpc Server and Client (non secured connection). 
 */
public class GreeterServiceGrpcTest {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Rule(order = 1)
	public GrpcServerStarterRule serverStarterRule = new GrpcServerStarterRule(50051);

	@Rule(order = 2)
	public GrpcManagedChannelRule managedChannelRule = new GrpcManagedChannelRule(
			new GrpcManagedChannelNonSecuredFactory(), GrpcConfiguration.from("127.0.0.1", 50051));

	@Rule(order = 100)
	public JunitStopWatch stopwatch = new JunitStopWatch(log);
	
	@Rule(order = 101)
	public JunitPrintTestName testName = new JunitPrintTestName(log);
	
	@Test
	public void testNonSecured() {
		
		registerGreeterServiceGrpc();
		
		GreeterService greeterService = createGreeterServiceClientStub();
		
		// create some testing data
		String[] messages = createMessages();
		
		// call grpc stub with different data
		for (int i = 0; i < messages.length; i++) {
			callAndAssert(greeterService, messages[i]);
		}
	}

	@Test
	public void testNonSecuredMultiClientCallsWithClientBulkheadLimiter() {
		// TODO
	}
	
	@Test
	public void testNonSecuredMultiClientCalls() throws InterruptedException {
		
		registerGreeterServiceGrpc();
		
		// number of concurrent client stubs calls
		int repeatNumStubs = 1000;
		
		// create login service stub
		List<GreeterService> greeterServices = repeatGreeterServiceClientStub(repeatNumStubs);
		
		// create some testing data
		String[] messages = createMessages();
		
        // wraps as Callable tasks
     	List<Callable<Void>> tasks = greeterServices.stream()
     			.map( greeterService -> new Callable<Void>() {
     				@Override
     	            public Void call() {
     					int randomIndex = (int) (Math.random() * messages.length);
     					String message = messages[randomIndex];
     					callAndAssert(greeterService, message);
     	                return null;
     	            }
     			})
     			.collect( Collectors.toList() );
     	
		// call grpc stubs in a parallel fashion
		ExecutorService executorService = Executors.newFixedThreadPool(4);
        List<Future<Void>> futures = executorService.invokeAll(tasks, 5, TimeUnit.SECONDS);
        
        // block until all tasks are done
        long finishedCount = futures.stream()
	        	.map( f -> {
					try {
						return f.get();
					} catch (InterruptedException | ExecutionException ex) {
						throw new RuntimeException(ex);
					}
				})
	        	.count();
        
        Assert.assertEquals(repeatNumStubs, finishedCount);
	}
	
	private void registerGreeterServiceGrpc() {
		GreeterBusiness greeterBusiness = new GreeterBusinessImpl();
		GreeterServiceGrpcImpl greeterServiceGrpc = new GreeterServiceGrpcImpl(greeterBusiness);
		serverStarterRule.registerService(greeterServiceGrpc);
	}
	
	private GreeterService createGreeterServiceClientStub() {
		GreeterService greeterService = new GreeterServiceGrpcClientStub(managedChannelRule.getManagedChannel());
		return greeterService;
	}

	private List<GreeterService> repeatGreeterServiceClientStub(int repeatNum) {
		GreeterService greeterServiceStub = createGreeterServiceClientStub();
		List<GreeterService> list = new ArrayList<>(repeatNum);
		for (int i = 0; i < repeatNum; ++i) {
			list.add(greeterServiceStub);
		}
		return list;
	}
	
	private void callAndAssert(GreeterService greeterService, String message) {
		String response = greeterService.sayHello(message);
		Assert.assertEquals(message, response);
	}

	private String[] createMessages() {
		String[] messages = new String[] { 
				Corpus.IMAGES.name(),
				Corpus.NEWS.name(),
				Corpus.LOCAL.name(),
				Corpus.PRODUCTS.name(),
				Corpus.VIDEO.name()};
		return messages;
	}
	
}
