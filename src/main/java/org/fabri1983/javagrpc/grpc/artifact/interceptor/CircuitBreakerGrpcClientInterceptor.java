package org.fabri1983.javagrpc.grpc.artifact.interceptor;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors.CheckedForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

public class CircuitBreakerGrpcClientInterceptor implements ClientInterceptor {

	private CircuitBreaker circuitBreaker;

	public CircuitBreakerGrpcClientInterceptor(CircuitBreaker circuitBreaker) {
		this.circuitBreaker = circuitBreaker;
	}

	public CircuitBreaker getCircuitBreaker() {
		return circuitBreaker;
	}

	@Override
	public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
			CallOptions callOptions, Channel next) {

		ClientCall<ReqT, RespT> delegate = next.newCall(method, callOptions);

		CheckedForwardingClientCall<ReqT, RespT> clientCall = new CheckedForwardingClientCall<ReqT, RespT>(delegate) {
			@Override
			protected void checkedStart(ClientCall.Listener<RespT> responseListener, Metadata headers) throws Exception {
				circuitBreaker.acquirePermission();
				InnerListener<RespT> newResponseListener = new InnerListener<>(responseListener, System.nanoTime());
				delegate().start(newResponseListener, headers);
			}
		};

		return clientCall;
	}

	private class InnerListener<RespT> extends SimpleForwardingClientCallListener<RespT> {

		private long startedAt;

		public InnerListener(ClientCall.Listener<RespT> delegate, long startedAt) {
			super(delegate);
			this.startedAt = startedAt;
		}

		@Override
		public void onClose(Status status, Metadata trailers) {
			long elapsed = System.nanoTime() - startedAt;
			if (status.isOk()) {
				circuitBreaker.onSuccess(elapsed, TimeUnit.NANOSECONDS);
			} else {
				circuitBreaker.onError(elapsed, TimeUnit.NANOSECONDS, new StatusRuntimeException(status, trailers));
			}
			super.onClose(status, trailers);
		}
	}

}
