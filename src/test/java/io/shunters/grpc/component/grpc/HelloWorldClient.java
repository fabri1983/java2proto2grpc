package io.shunters.grpc.component.grpc;

import io.grpc.examples.helloworld.protobuf.GreeterGrpc;
import io.grpc.examples.helloworld.protobuf.SearchRequest;
import io.grpc.examples.helloworld.protobuf.SearchResponse;
import io.grpc.examples.helloworld.protobuf.SearchResponse.HelloReply;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mykidong on 2018-01-10.
 */
public class HelloWorldClient {
	
    private static Logger log = LoggerFactory.getLogger(HelloWorldClient.class);

    private GrpcLoadBalancer<GreeterGrpc, GreeterGrpc.GreeterBlockingStub, GreeterGrpc.GreeterStub> lb;

    /**
     * using consul service discovery.
     *
     * @param serviceName
     * @param consulHost
     * @param consulPort
     */
    public HelloWorldClient(String serviceName, String consulHost, int consulPort) {
        lb = new GrpcLoadBalancer<>(serviceName, consulHost, consulPort, GreeterGrpc.class);
    }

    /**
     * using static node list.
     *
     * @param hostPorts, for instance, Arrays.asList("host1:port1", "host2:port2")
     */
    public HelloWorldClient(List<String> hostPorts) {
        lb = new GrpcLoadBalancer<>(hostPorts, GreeterGrpc.class);
    }

    public void sayHello() {
        try {
        	SearchRequest request = SearchRequest.newBuilder()
    				.addHelloRequest(SearchRequest.HelloRequest.newBuilder()
    				.setName("grpc load balancer"))
    				.build();

        	SearchResponse response = lb.getBlockingStub().sayHello(request);
        	HelloReply helloReply = response.getHelloReply(0);
            String message = helloReply.getMessage();
            log.info("message: [{}]", message);
        } catch (Exception e) {
        	log.error("", e);;
        }
    }
}
