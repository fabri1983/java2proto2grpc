package io.shunters.grpc.component.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wraps the access to the different types of Grpc Stub: B (blocking), A (async), F (Future).
 * G defines the Grpc class.
 */
public class GrpcClient<G, B, A, F> {

	private static Logger log = LoggerFactory.getLogger(GrpcClientCustomLoadBalancer.class);
	
    private final ManagedChannel channel;
    private B blockingStub;
    private A asyncStub;
    private F futureStub;
    
    private String host;
    private int port;
    private Class<G> grpcClass;

    public GrpcClient(String host, int port, Class<G> grpcClass) {
        this(ManagedChannelBuilder.forAddress(host, port)
        		.usePlaintext().build(), grpcClass);

        this.host = host;
        this.port = port;
        this.grpcClass = grpcClass;
    }

    @SuppressWarnings("unchecked")
	public GrpcClient(ManagedChannel channel, Class<G> grpcClass) {
        this.channel = channel;
        try {
            Method blockingStubMethod = grpcClass.getMethod("newBlockingStub", Channel.class);
            blockingStub = (B) blockingStubMethod.invoke(null, channel);

            Method asyncStubMethod = grpcClass.getMethod("newStub", Channel.class);
            asyncStub = (A) asyncStubMethod.invoke(null, channel);
            
            Method futureStubMethod = grpcClass.getMethod("newFutureStub", Channel.class);
            futureStub = (F) futureStubMethod.invoke(null, channel);
        }
        catch (Exception e) {
        	 log.error(e.getClass().getSimpleName() + ". " + e.getMessage());
        }
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public B getBlockingStub() {
        return this.blockingStub;
    }

    public A getAsyncStub() {
        return this.asyncStub;
    }

    public F getFutureStub() {
        return this.futureStub;
    }
    
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public Class<G> getGrpcClass() {
        return grpcClass;
    }
}
