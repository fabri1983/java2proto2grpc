package org.fabri1983.javagrpc.grpc.artifact.interceptor;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.grpc.ForwardingServerCallListener.SimpleForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

public class BulkheadGrpcServerInterceptor implements ServerInterceptor {

	private Bulkhead bulkhead;

	public BulkheadGrpcServerInterceptor(Bulkhead bulkhead) {
		this.bulkhead = bulkhead;
	}

	public Bulkhead getBulkhead() {
		return bulkhead;
	}
	
	@Override
	public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
			ServerCallHandler<ReqT, RespT> next) {

		if (bulkhead.tryAcquirePermission()) {
			Listener<ReqT> listener = next.startCall(call, headers);
			SimpleForwardingServerCallListener<ReqT> serverCallListener = createServerCallListener(listener);
			return serverCallListener;
		} else {
			call.close(
					Status.UNAVAILABLE.withDescription("Bulkhead " + bulkhead.getName() + " is full."),
					new Metadata());
			SimpleForwardingServerCallListener<ReqT> dummyServerCallListener = createDummyServerCallListener();
			return dummyServerCallListener;
		}
	}

	private <ReqT> SimpleForwardingServerCallListener<ReqT> createServerCallListener(Listener<ReqT> listener) {
		return new SimpleForwardingServerCallListener<ReqT>(listener) {
			@Override
			public void onCancel() {
				bulkhead.onComplete();
				super.onCancel();
			}

			@Override
			public void onComplete() {
				bulkhead.onComplete();
				super.onComplete();
			}
		};
	}

	private <ReqT> SimpleForwardingServerCallListener<ReqT> createDummyServerCallListener() {
		return new SimpleForwardingServerCallListener<ReqT>(null) {
			@Override
			public void onCancel() {
			}
	
			@Override
			public void onComplete() {
			}
		};
	}

}
