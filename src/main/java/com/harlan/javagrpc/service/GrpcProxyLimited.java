package com.harlan.javagrpc.service;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

public class GrpcProxyLimited {

	// limit rpc calls made to the stub
	protected final Semaphore limiter = new Semaphore(100);
	
	protected <T> T withLimitUse(Supplier<ListenableFuture<T>> process) {
		try {
			limiter.acquire();
			ListenableFuture<T> future = process.get();
			future.addListener(() -> limiter.release(), MoreExecutors.directExecutor());
			Futures.addCallback(future, callback(), MoreExecutors.directExecutor());
			return future.get();
		}
		catch (InterruptedException | ExecutionException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private <T> FutureCallback<T> callback() {
		return new FutureCallback<T>() {
			@Override
			public void onSuccess(T result) {
			}

			@Override
			public void onFailure(Throwable t) {
				System.err.println("ERROR: " + t.getLocalizedMessage());
			}
		};
	}
	
}
