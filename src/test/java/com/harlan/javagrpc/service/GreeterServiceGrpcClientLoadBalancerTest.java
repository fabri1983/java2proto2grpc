package com.harlan.javagrpc.service;

import com.harlan.javagrpc.testutil.IServiceDiscoveryProperties;
import com.harlan.javagrpc.testutil.ServiceDiscoveryPropertiesFromFile;
import com.harlan.javagrpc.testutil.rules.ConsulServiceRegisterRule;
import com.harlan.javagrpc.testutil.rules.GrpcServerStarterRule;

import io.grpc.examples.helloworld.protobuf.GreeterGrpc;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterBlockingStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterFutureStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterStub;
import io.grpc.examples.helloworld.protobuf.SearchRequest;
import io.grpc.examples.helloworld.protobuf.SearchResponse;
import io.grpc.examples.helloworld.protobuf.SearchResponse.HelloReply;
import io.shunters.grpc.component.grpc.GrpcClientCustomLoadBalancer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Grpc Server and Client (non secured connection) using Client Load Balancing.
 */
public class GreeterServiceGrpcClientLoadBalancerTest {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final String MESSAGE = "grpc load balancer";

	private static final IServiceDiscoveryProperties serviceDiscoveryProps = 
			new ServiceDiscoveryPropertiesFromFile();
	
	@Rule( order = 1)
	public final GrpcServerStarterRule serverStarterRule = new GrpcServerStarterRule(50051, 50052);
	
	@Rule( order = 2)
	public final ConsulServiceRegisterRule consulServiceRegisterRule = new ConsulServiceRegisterRule(serviceDiscoveryProps);
	
	private final List<String> staticGrpcHostPortList = Arrays.asList("localhost:50051", "localhost:50052");
	
	/**
	 * Using Load Balancer with Consul Service Discovery.
	 */ 
	@Test
	public void greeterServiceWithLoadBalancerWithConsulServiceDiscoveryTest() throws Exception {
		if (!consulServiceRegisterRule.isRegistered()) {
			log.warn("Consul Service wasn't registered. Test won't run and is resolved as correct.");
			Assert.assertTrue(true);
			return;
		}
		
		registerGreeterServiceGrpc();
		
		// number of concurrent client stubs calls
		int repeatNumStubs = 1000;
		
		// create grpc client using Consul as load balancer 
		GrpcClientCustomLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub> clientLb = 
				createClientLoadBalancerWithConsul();
		// repeat it N times
		List<GrpcClientCustomLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub>> clientLoadBalancers = 
				repeatClient(repeatNumStubs, clientLb);
		
		// wraps as Callable tasks
     	List<Callable<Void>> tasks = clientLoadBalancers.stream()
     			.map( lb -> new Callable<Void>() {
     				@Override
     	            public Void call() {
     					String message = callGreeterService(lb);
     					Assert.assertEquals(MESSAGE, message);
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

	/**
	 * Using Load Balancer with static gRPC nodes.
	 */
	@Test
	public void greeterServiceWithLoadBalancerWithStaticGrpcHostsTest() throws Exception {
		
		registerGreeterServiceGrpc();
		
		// number of concurrent client stubs calls
		int repeatNumStubs = 1000;
		
		// create grpc client with load balancer and static grpc nodes
		GrpcClientCustomLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub> clientLb = 
				createClientLoadBalancerStaticGrpcNodes();
		// repeat it N times
		List<GrpcClientCustomLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub>> clientLoadBalancers = 
				repeatClient(repeatNumStubs, clientLb);
		
		// wraps as Callable tasks
     	List<Callable<Void>> tasks = clientLoadBalancers.stream()
     			.map( lb -> new Callable<Void>() {
     				@Override
     	            public Void call() {
     					String message = callGreeterService(lb);
     					Assert.assertEquals(MESSAGE, message);
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
		GreeterServiceGrpcImpl greeterServiceGrpc = new GreeterServiceGrpcImpl();
		serverStarterRule.registerService(greeterServiceGrpc);
	}

	private GrpcClientCustomLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub> createClientLoadBalancerWithConsul() {
		GrpcClientCustomLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub> lb = 
				new GrpcClientCustomLoadBalancer<>(
						serviceDiscoveryProps.getConsulServiceName(),
						serviceDiscoveryProps.getConsulHost(),
						serviceDiscoveryProps.getConsulPort(),
						GreeterGrpc.class);
		return lb;
	}

	private GrpcClientCustomLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub> createClientLoadBalancerStaticGrpcNodes() {
		GrpcClientCustomLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub> clientLb = 
				new GrpcClientCustomLoadBalancer<>(staticGrpcHostPortList, GreeterGrpc.class);
		return clientLb;
	}

	private List<GrpcClientCustomLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub>> repeatClient(int repeatNum, 
			GrpcClientCustomLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub> clientLb) {
		List<GrpcClientCustomLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub>> list = new ArrayList<>(repeatNum);
		for (int i = 0; i < repeatNum; ++i) {
			list.add(clientLb);
		}
		return list;
	}

	private String callGreeterService(GrpcClientCustomLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub> lb) {
		try {
			SearchRequest request = SearchRequest.newBuilder()
					.addHelloRequest(SearchRequest.HelloRequest.newBuilder().setName(MESSAGE))
					.build();
	
			SearchResponse response = lb.getBlockingStub().sayHello(request);
			HelloReply helloReply = response.getHelloReply(0);
			String message = helloReply.getMessage();
			return message;
		}
		catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}
}
