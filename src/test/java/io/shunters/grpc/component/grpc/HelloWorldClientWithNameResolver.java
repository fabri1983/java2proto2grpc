package io.shunters.grpc.component.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
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
public class HelloWorldClientWithNameResolver {
    private static Logger log = LoggerFactory.getLogger(HelloWorldClientWithNameResolver.class);

    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    /**
     * Consul NameResolver Usage.
     *
     *
     * @param serviceName consul service name.
     * @param consulHost consul agent host.
     * @param consulPort consul agent port.
     * @param ignoreConsul if true, consul is not used. instead, the static node list will be used.
     * @param hostPorts the static node list, for instance, Arrays.asList("host1:port1", "host2:port2")
     */
    public HelloWorldClientWithNameResolver(String serviceName,
                                            String consulHost,
                                            int consulPort,
                                            boolean ignoreConsul,
                                            List<String> hostPorts) {

        String consulAddr = "consul://" + consulHost + ":" + consulPort;

        int pauseInSeconds = 5;

        channel = ManagedChannelBuilder
                .forTarget(consulAddr)
                .nameResolverFactory(new ConsulNameResolver.ConsulNameResolverProvider(serviceName, pauseInSeconds, ignoreConsul, hostPorts))
                .usePlaintext()
                .build();

        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }


    public void sayHello() {
        try {
        	SearchRequest request = SearchRequest.newBuilder()
    				.addHelloRequest(SearchRequest.HelloRequest.newBuilder()
    				.setName("grpc load balancer"))
    				.build();

        	SearchResponse response = blockingStub.sayHello(request);
        	HelloReply helloReply = response.getHelloReply(0);
            String message = helloReply.getMessage();
            log.info("message: [{}]", message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
