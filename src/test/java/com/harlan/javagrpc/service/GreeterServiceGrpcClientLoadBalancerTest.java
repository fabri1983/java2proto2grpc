package com.harlan.javagrpc.service;

import io.grpc.examples.helloworld.protobuf.GreeterGrpc;
import io.grpc.examples.helloworld.protobuf.SearchRequest;
import io.grpc.examples.helloworld.protobuf.SearchResponse;
import io.grpc.examples.helloworld.protobuf.SearchResponse.HelloReply;
import io.shunters.grpc.component.grpc.GrpcLoadBalancer;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreeterServiceGrpcClientLoadBalancerTest {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final String MESSAGE = "grpc load balancer";

	private GrpcLoadBalancer<GreeterGrpc, GreeterGrpc.GreeterBlockingStub, GreeterGrpc.GreeterStub> lb;

	/**
	 * Using consul service discovery.
	 *
	 * @param serviceName
	 * @param consulHost
	 * @param consulPort
	 */
	public GreeterServiceGrpcClientLoadBalancerTest(String serviceName, String consulHost, int consulPort) {
		lb = new GrpcLoadBalancer<>(serviceName, consulHost, consulPort, GreeterGrpc.class);
	}

	/**
	 * using static node list.
	 *
	 * @param hostPorts, for instance, Arrays.asList("host1:port1", "host2:port2")
	 */
	public GreeterServiceGrpcClientLoadBalancerTest(List<String> hostPorts) {
		lb = new GrpcLoadBalancer<>(hostPorts, GreeterGrpc.class);
	}

	private String sayHello() {
		try {
			SearchRequest request = SearchRequest.newBuilder()
					.addHelloRequest(SearchRequest.HelloRequest.newBuilder().setName(MESSAGE))
					.build();

			SearchResponse response = lb.getBlockingStub().sayHello(request);
			HelloReply helloReply = response.getHelloReply(0);
			String message = helloReply.getMessage();
			return message;
		} catch (Exception e) {
			log.error("", e);
			return null;
		}
	}

	//@Test
	public void greeterServiceWithLoadBalancerTest() throws Exception {
		List<String> hostPortList = Arrays.asList("localhost:50051");
		GreeterServiceGrpcClientLoadBalancerTest client = new GreeterServiceGrpcClientLoadBalancerTest(hostPortList);
		String message = client.sayHello();
		Assert.assertEquals(MESSAGE, message);
	}
}
