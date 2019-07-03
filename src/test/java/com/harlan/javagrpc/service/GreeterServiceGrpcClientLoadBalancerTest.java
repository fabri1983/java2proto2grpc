package com.harlan.javagrpc.service;

import com.harlan.javagrpc.testutil.IServiceDiscoveryProperties;
import com.harlan.javagrpc.testutil.ServiceDiscoveryPropertiesFromFile;
import com.harlan.javagrpc.testutil.rules.ConsulServiceRegisterRule;
import com.harlan.javagrpc.testutil.rules.GrpcServerStarterRule;

import io.grpc.examples.helloworld.protobuf.GreeterGrpc;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterBlockingStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterStub;
import io.grpc.examples.helloworld.protobuf.SearchRequest;
import io.grpc.examples.helloworld.protobuf.SearchResponse;
import io.grpc.examples.helloworld.protobuf.SearchResponse.HelloReply;
import io.shunters.grpc.component.grpc.GrpcClientLoadBalancer;

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
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Grpc Server and Client (non secured connection) using Client Load Balancing and Consul as Service Discovery.
 */
public class GreeterServiceGrpcClientLoadBalancerTest {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final String MESSAGE = "grpc load balancer";

	private static final IServiceDiscoveryProperties serviceDiscoveryProps = 
			new ServiceDiscoveryPropertiesFromFile();
	
	@ClassRule
	public static ConsulServiceRegisterRule consulServiceRegisterRule = 
			new ConsulServiceRegisterRule(serviceDiscoveryProps);
	
	@Rule
	public GrpcServerStarterRule serverStarterRule = new GrpcServerStarterRule(50051);
	
	private List<String> staticGrpcHostPortList = Arrays.asList("localhost:50051");
	
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
		int repeatNumStubs = 10;
		
		// create grpc client using Consul as load balancer 
		GrpcClientLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub> clientLb = 
				createClientLoadBalancerWithConsul();
		// repeat it N times
		List<GrpcClientLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub>> clientLoadBalancers = 
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
        futures.forEach( f -> {
				try {
					f.get();
				} catch (InterruptedException | ExecutionException ex) {
					throw new RuntimeException(ex);
				}
			});
	}

	/**
	 * Using Load Balancer with static gRPC nodes.
	 */
	@Test
	public void greeterServiceWithLoadBalancerWithStaticGrpcHostsTest() throws Exception {
		
		registerGreeterServiceGrpc();
		
		// number of concurrent client stubs calls
		int repeatNumStubs = 10;
		
		// create grpc client with load balancer and static grpc nodes
		GrpcClientLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub> clientLb = 
				createClientLoadBalancerStaticGrpcNodes();
		// repeat it N times
		List<GrpcClientLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub>> clientLoadBalancers = 
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
        futures.forEach( f -> {
				try {
					f.get();
				} catch (InterruptedException | ExecutionException ex) {
					throw new RuntimeException(ex);
				}
			});
	}

	private GrpcClientLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub> createClientLoadBalancerWithConsul() {
		GrpcClientLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub> lb = 
				new GrpcClientLoadBalancer<>(
						serviceDiscoveryProps.getConsulServiceName(),
						serviceDiscoveryProps.getConsulHost(),
						serviceDiscoveryProps.getConsulPort(),
						GreeterGrpc.class);
		return lb;
	}

	private GrpcClientLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub> createClientLoadBalancerStaticGrpcNodes() {
		GrpcClientLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub> clientLb = 
				new GrpcClientLoadBalancer<>(staticGrpcHostPortList, GreeterGrpc.class);
		return clientLb;
	}

	private List<GrpcClientLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub>> repeatClient(int repeatNum, 
			GrpcClientLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub> clientLb) {
		List<GrpcClientLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub>> list = new ArrayList<>(repeatNum);
		for (int i = 0; i < repeatNum; ++i) {
			list.add(clientLb);
		}
		return list;
	}

	private void registerGreeterServiceGrpc() {
		GreeterServiceGrpcImpl greeterServiceGrpc = new GreeterServiceGrpcImpl();
		serverStarterRule.getServerStarter().register(greeterServiceGrpc);
	}
	
	private String callGreeterService(GrpcClientLoadBalancer<GreeterGrpc, GreeterGrpc.GreeterBlockingStub, GreeterGrpc.GreeterStub> lb) {
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
