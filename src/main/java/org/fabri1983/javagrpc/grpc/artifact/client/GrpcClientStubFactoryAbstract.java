package org.fabri1983.javagrpc.grpc.artifact.client;

import com.codahale.metrics.MetricRegistry;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.metrics.BulkheadMetrics;
import io.github.resilience4j.metrics.CircuitBreakerMetrics;
import io.grpc.ClientInterceptor;
import io.grpc.stub.AbstractStub;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.fabri1983.javagrpc.grpc.artifact.client.managedchannel.IGrpcManagedChannel;
import org.fabri1983.javagrpc.grpc.artifact.interceptor.BulkheadGrpcClientInterceptor;
import org.fabri1983.javagrpc.grpc.artifact.interceptor.CircuitBreakerGrpcClientInterceptor;

public abstract class GrpcClientStubFactoryAbstract<B, A, F> implements IGrpcClientStubFactory<B, A, F> {

	protected IGrpcManagedChannel managedChannel;
	private ClientInterceptor[] interceptors;
	private boolean createInterceptors;
	private boolean addMetrics;
	
	public GrpcClientStubFactoryAbstract<B, A, F> withManagedChannel(IGrpcManagedChannel managedChannel) {
		this.managedChannel = managedChannel;
		return this;
	}
	
	public GrpcClientStubFactoryAbstract<B, A, F> withInterceptors() {
		createInterceptors = true;
		return this;
	}
	
	public GrpcClientStubFactoryAbstract<B, A, F> withInterceptors(ClientInterceptor[] interceptors) {
		this.interceptors = interceptors;
		return this;
	}
	
	public GrpcClientStubFactoryAbstract<B, A, F> withMetrics() {
		addMetrics = true;
		return this;
	}
	
	protected abstract String getGrpcClientName();
	
	protected abstract <T extends GrpcClientStub<?,?,?>> T innerBuild();
	
	public <T extends GrpcClientStub<?,?,?>> T build() {
		
		// if interceptors weren't externally created then create each of them
		if (createInterceptors && (interceptors == null || interceptors.length == 0)) {
			List<ClientInterceptor> listInterceptors = new ArrayList<>(5);
			addBulkhead(listInterceptors);
			addRetry(listInterceptors);
			addCircuitBreaker(listInterceptors);
			addRateLimiter(listInterceptors);
			interceptors = listInterceptors.toArray(new ClientInterceptor[0]);
		}
		
		// register metrics?
		if (addMetrics) {
			registerMetrics();
		}
		
		return innerBuild();
	}

	private void addBulkhead(List<ClientInterceptor> listInterceptors) {
		// create Bulkhead to limit number of client calls
		BulkheadConfig config = BulkheadConfig.custom()
		    .maxConcurrentCalls(100)
		    .maxWaitDuration(Duration.ofMillis(1000))
		    .build();
		Bulkhead bulkheadService = Bulkhead.of(getGrpcClientName(), config);
		
		// wrap the Bulkhead into a interceptor
		ClientInterceptor bulkheadInterceptor = new BulkheadGrpcClientInterceptor(bulkheadService);
		
		// add it to the list
		listInterceptors.add(bulkheadInterceptor);
		
		// register the Bulkhead for collections of metrics
		
			
	}

	private void addRetry(List<ClientInterceptor> listInterceptors) {
		// TODO Add Retry interceptor
	}

	private void addCircuitBreaker(List<ClientInterceptor> listInterceptors) {
		// TODO Add Circuit Breaker interceptor
	}

	private void addRateLimiter(List<ClientInterceptor> listInterceptors) {
		// TODO Add Rate Limiter interceptor
	}
	
	private void registerMetrics() {
		if (interceptors == null || interceptors.length == 0) {
			return;
		}
		
		MetricRegistry collectorRegistry = new MetricRegistry();
		
		for (ClientInterceptor cli : interceptors) {
		
			// register Bulkhead metrics?
			if (cli instanceof BulkheadGrpcClientInterceptor) {
				Bulkhead bulkhead = ((BulkheadGrpcClientInterceptor) cli).getBulkhead();
				collectorRegistry.registerAll(BulkheadMetrics.ofBulkhead(bulkhead));
			}
			
			// register Retry metrics?
			// TODO
			
			// register Circuit Braker metrics?
			if (cli instanceof CircuitBreakerGrpcClientInterceptor) {
				CircuitBreaker circuitBreaker = ((CircuitBreakerGrpcClientInterceptor) cli).getCircuitBreaker();
				collectorRegistry.registerAll(CircuitBreakerMetrics.ofCircuitBreaker(circuitBreaker));
			}
			
			// register Rate limiter metrics?
			// TODO
		}
	}

	protected <T extends AbstractStub<T>> T ifInterceptors(T stub) {
		if (interceptors == null || interceptors.length == 0) {
			return stub;
		}
		return stub.withInterceptors(interceptors);
	}
	
}
