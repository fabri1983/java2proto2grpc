package org.fabri1983.javagrpc.grpc.artifact.interceptor;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors.CheckedForwardingClientCall;
import io.grpc.ForwardingClientCallListener.SimpleForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;

public class BulkheadGrpcClientInterceptor implements ClientInterceptor {

	private Bulkhead bulkhead;
	
	public BulkheadGrpcClientInterceptor(Bulkhead bulkhead) {
		this.bulkhead = bulkhead;
	}

	@Override
	public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
			CallOptions callOptions, Channel next) {

		ClientCall<ReqT, RespT> delegate = next.newCall(method, callOptions);
		
		CheckedForwardingClientCall<ReqT, RespT> clientCall = new CheckedForwardingClientCall<ReqT, RespT>(delegate) {
			@Override
			protected void checkedStart(ClientCall.Listener<RespT> responseListener, Metadata headers) throws Exception {
				bulkhead.acquirePermission();
				InnerListener<RespT> newResponseListener = new InnerListener<>(responseListener);
				delegate().start(newResponseListener, headers);
			}
		};
		
		return clientCall;
	}

	private class InnerListener<RespT> extends SimpleForwardingClientCallListener<RespT> {
		
		public InnerListener(ClientCall.Listener<RespT> delegate) {
			super(delegate);
		}

		@Override
		public void onClose(Status status, Metadata trailers) {
			bulkhead.onComplete();
			delegate().onClose(status, trailers);
		}
	}
	
}
