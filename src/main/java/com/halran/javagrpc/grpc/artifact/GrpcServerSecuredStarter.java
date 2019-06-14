package com.halran.javagrpc.grpc.artifact;

import io.grpc.BindableService;
import io.grpc.ServerBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import java.io.InputStream;

import javax.net.ssl.SSLException;

/**
 * Register gRPC's {@link BindableService} objects implementing {@link GrpcServiceMarker} to be exposed by a gRPC Server.
 */
public class GrpcServerSecuredStarter extends GrpcServerStarter {
	
	public GrpcServerSecuredStarter(int port) {
		super(port);
	}

	@Override
	protected ServerBuilder<?> createBuilder(int port) {
		SslContext sslContext;
		try {
			sslContext = getSslContextBuilder().build();
			return NettyServerBuilder
					.forPort(port)
					.sslContext(sslContext);
		} catch (SSLException ex) {
			System.err.println(ex);
			throw new RuntimeException(ex);
		}
	}
	
	private SslContextBuilder getSslContextBuilder() {
		// No Mutual authentication: provides only server key and certificate
		// Mutual authentication: provides all files
		InputStream certChainFile = getInputStreamFromResource("certs/server1.pem");
	    InputStream privateKeyFile = getInputStreamFromResource("certs/server1.key");
	    InputStream trustCertCollectionFile = getInputStreamFromResource("certs/ca.pem");
	    
		SslContextBuilder sslClientContextBuilder = SslContextBuilder.forServer(certChainFile, privateKeyFile);
		
		// authenticate client?
		if (trustCertCollectionFile != null) {
			sslClientContextBuilder.trustManager(trustCertCollectionFile);
			sslClientContextBuilder.clientAuth(ClientAuth.REQUIRE);
		}
		
		return GrpcSslContexts.configure(sslClientContextBuilder);
	}

	private InputStream getInputStreamFromResource(final String filePath) {
		return GrpcServerSecuredStarter.class.getClassLoader().getResourceAsStream(filePath);
	}
	
}
