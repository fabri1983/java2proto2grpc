package com.halran.javagrpc.grpc.artifact;

import io.grpc.BindableService;
import io.grpc.ServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;

import java.io.InputStream;

import javax.net.ssl.SSLException;

/**
 * Register gRPC's {@link BindableService} objects implementing {@link GrpcServiceMarker} to be exposed by a gRPC Server.
 */
public class GrpcServerStarterSecured extends GrpcServerStarter {
	
	public GrpcServerStarterSecured(int port, boolean withShutdownHook) {
		super(port, withShutdownHook);
	}

	@Override
	protected ServerBuilder<?> createBuilder(int port) {
		try {
			boolean mutualAuth = false;
			SslContext sslContext = getSslContextBuilder(mutualAuth);
			return NettyServerBuilder
					.forPort(port)
					.sslContext(sslContext);
		} catch (SSLException ex) {
			System.err.println(ex);
			throw new RuntimeException(ex);
		}
	}
	
	private SslContext getSslContextBuilder(boolean mutualAuth) throws SSLException {
		// No Mutual authentication: provide only server key and certificate
		// Mutual authentication (client side authentication): provide all files
		InputStream certChainFile = getInputStreamFromResource("certs/server1.pem");
	    InputStream privateKeyFile = getInputStreamFromResource("certs/server1.key");
	    
		SslContextBuilder sslClientContextBuilder = SslContextBuilder.forServer(certChainFile, privateKeyFile);
		
		// authenticate client?
		if (mutualAuth) {
			InputStream trustCertCollectionFile = getInputStreamFromResource("certs/ca.pem");
			if (trustCertCollectionFile != null) {
				sslClientContextBuilder.trustManager(trustCertCollectionFile);
				sslClientContextBuilder.clientAuth(ClientAuth.REQUIRE);
			}
		}
		
		return GrpcSslContexts.configure(sslClientContextBuilder).build();
	}

	private InputStream getInputStreamFromResource(final String filePath) {
		return GrpcServerStarterSecured.class.getClassLoader().getResourceAsStream(filePath);
	}
	
}
