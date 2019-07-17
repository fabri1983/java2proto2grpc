package com.harlan.javagrpc.service;

import com.harlan.javagrpc.business.GreeterBusinessImpl;
import com.harlan.javagrpc.business.contract.GreeterBusiness;
import com.harlan.javagrpc.grpc.artifact.client.GrpcClientWithLoadBalancer;
import com.harlan.javagrpc.testutil.IServiceDiscoveryProperties;
import com.harlan.javagrpc.testutil.ServiceDiscoveryPropertiesFromFile;
import com.harlan.javagrpc.testutil.rules.ConsulServiceRegisterRule;
import com.harlan.javagrpc.testutil.rules.GrpcServerStarterRule;
import com.harlan.javagrpc.testutil.rules.JunitPrintTestName;
import com.harlan.javagrpc.testutil.rules.JunitStopWatch;

import io.grpc.examples.helloworld.protobuf.GreeterGrpc;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterBlockingStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterFutureStub;
import io.grpc.examples.helloworld.protobuf.GreeterGrpc.GreeterStub;
import io.grpc.examples.helloworld.protobuf.SearchRequest;
import io.grpc.examples.helloworld.protobuf.SearchResponse;
import io.grpc.examples.helloworld.protobuf.SearchResponse.HelloReply;

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

	private static final IServiceDiscoveryProperties serviceDiscoveryProps = 
			new ServiceDiscoveryPropertiesFromFile();
	
	@Rule( order = 1)
	public final GrpcServerStarterRule serverStarterRule = new GrpcServerStarterRule(50051, 50052);
	
	@Rule( order = 2)
	public final ConsulServiceRegisterRule consulServiceRegisterRule = 
			new ConsulServiceRegisterRule(serviceDiscoveryProps, Arrays.asList("localhost:50051", "localhost:50052"));
	
	private final List<String> staticGrpcHostPortList = Arrays.asList("localhost:50051", "localhost:50052");
	
	@Rule
	public JunitStopWatch stopwatch = new JunitStopWatch(log);
	
	@Rule
	public JunitPrintTestName testName = new JunitPrintTestName(log);
	
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

		String message = "grpc load balancer";

		registerGreeterServiceGrpc();

		// number of concurrent client stubs calls
		int repeatNumStubs = 1000;

		// create grpc client using Consul as load balancer
		GrpcClientWithLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub> clientLbs = 
				createClientLoadBalancerWithConsul(GreeterGrpc.class);
		// repeat it N times
		List<GrpcClientWithLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub>> clientLoadBalancers = 
				repeatClient(repeatNumStubs, clientLbs);

		// wraps as Callable tasks
     	List<Callable<Void>> tasks = clientLoadBalancers.stream()
     			.map( clientLb -> new Callable<Void>() {
     				@Override
     	            public Void call() {
     					String messageResult = callGreeterService(clientLb, message);
     					Assert.assertEquals(message, messageResult);
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
						log.error("{}. {}", ex.getClass().getSimpleName(), ex.getMessage());
						throw new RuntimeException(ex);
					}
				})
	        	.count();

		Assert.assertEquals(repeatNumStubs, finishedCount);

		// shutdown clients before the serverStarterRule calls its shutdown method
		shutdownClients(clientLoadBalancers);
	}

	/**
	 * Using Load Balancer with static gRPC nodes.
	 */
	@Test
	public void greeterServiceWithLoadBalancerWithStaticGrpcHostsTest() throws Exception {

		String message = "grpc load balancer";

		registerGreeterServiceGrpc();

		// number of concurrent client stubs calls
		int repeatNumStubs = 1000;

		// create grpc client with load balancer and static grpc nodes
		GrpcClientWithLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub> clientLbs = 
				createClientLoadBalancerStaticGrpcNodes(GreeterGrpc.class);
		// repeat it N times
		List<GrpcClientWithLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub>> clientLoadBalancers = 
				repeatClient(repeatNumStubs, clientLbs);

		// wraps as Callable tasks
		List<Callable<Void>> tasks = clientLoadBalancers.stream()
     			.map( clientLb -> new Callable<Void>() {
     				@Override
     	            public Void call() {
     					String messageResult = callGreeterService(clientLb, message);
     					Assert.assertEquals(message, messageResult);
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
						log.error("{}. {}", ex.getClass().getSimpleName(), ex.getMessage());
						throw new RuntimeException(ex);
					}
				})
            	.count();

		Assert.assertEquals(repeatNumStubs, finishedCount);

		// shutdown clients before the serverStarterRule calls its shutdown method
		shutdownClients(clientLoadBalancers);
	}

	private void registerGreeterServiceGrpc() {
		GreeterBusiness greeterBusiness = new GreeterBusinessImpl();
		GreeterServiceGrpcImpl greeterServiceGrpc = new GreeterServiceGrpcImpl(greeterBusiness);
		serverStarterRule.registerService(greeterServiceGrpc);
	}

	private <G, B, A, F> GrpcClientWithLoadBalancer<G, B, A, F> createClientLoadBalancerWithConsul(Class<G> grpcClass) {
		GrpcClientWithLoadBalancer<G, B, A, F> lb = 
				new GrpcClientWithLoadBalancer<>(
						serviceDiscoveryProps.getConsulServiceName(),
						serviceDiscoveryProps.getConsulHost(),
						serviceDiscoveryProps.getConsulPort(),
						grpcClass);
		return lb;
	}

	private <G, B, A, F> GrpcClientWithLoadBalancer<G, B, A, F> createClientLoadBalancerStaticGrpcNodes(Class<G> grpcClass) {
		GrpcClientWithLoadBalancer<G, B, A, F> clientLb = 
				new GrpcClientWithLoadBalancer<>(staticGrpcHostPortList, grpcClass);
		return clientLb;
	}

	private <G, B, A, F> List<GrpcClientWithLoadBalancer<G, B, A, F>> repeatClient(int repeatNum, 
			GrpcClientWithLoadBalancer<G, B, A, F> clientLb) {
		List<GrpcClientWithLoadBalancer<G, B, A, F>> list = new ArrayList<>(repeatNum);
		for (int i = 0; i < repeatNum; ++i) {
			list.add(clientLb);
		}
		return list;
	}

	private String callGreeterService(GrpcClientWithLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub> clientLb,
			String message) {
		try {
			SearchRequest request = SearchRequest.newBuilder()
					.addHelloRequest(
							SearchRequest.HelloRequest.newBuilder().setName(message))
					.build();
	
			SearchResponse response = clientLb.getBlockingStub().sayHello(request);
			HelloReply helloReply = response.getHelloReply(0);
			String messageResult = helloReply.getMessage();
			return messageResult;
		}
		catch (Exception e) {
			log.error("{}. {}", e.getClass().getSimpleName(), e.getMessage());
			return null;
		}
	}

	private void shutdownClients(
			List<GrpcClientWithLoadBalancer<GreeterGrpc, GreeterBlockingStub, GreeterStub, GreeterFutureStub>> clientLoadBalancers) {
		clientLoadBalancers.forEach(client -> client.shutdown());
	}
}
