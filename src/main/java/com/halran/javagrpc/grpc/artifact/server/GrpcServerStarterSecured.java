package com.halran.javagrpc.grpc.artifact.server;

import io.grpc.BindableService;
import io.grpc.ServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;

import java.io.InputStream;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Register gRPC's {@link BindableService} objects implementing {@link GrpcServiceMarker} to be exposed by a gRPC Server.
 */
public class GrpcServerStarterSecured extends GrpcServerStarter {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	public GrpcServerStarterSecured(int port, boolean withShutdownHook) {
		super(port, withShutdownHook);
	}

	@Override
	protected ServerBuilder<?> createBuilder() {
		try {
			boolean mutualAuth = false;
			SslContext sslContext = getSslContextBuilder(mutualAuth);
			return NettyServerBuilder
					.forPort(getPort())
					.sslContext(sslContext);
		} catch (SSLException ex) {
			log.error("", ex);
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
