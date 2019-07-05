package com.halran.javagrpc.grpc.artifact.client;

import com.halran.javagrpc.grpc.artifact.GrpcConfiguration;

import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;

import java.io.InputStream;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrpcManagedChannelSecured extends GrpcManagedChannel {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	public GrpcManagedChannelSecured(GrpcConfiguration config) {
		super(config);
	}
	
	@Override
	protected ManagedChannelBuilder<?> createManagedChannelBuilder(GrpcConfiguration config) {
		try {
			boolean mutualAuth = false;
			SslContext sslContext = buildSslContext(mutualAuth);
			return NettyChannelBuilder
					.forAddress(config.getHost(), config.getPort())
					// Only for using provided test certs to match the Subject Alternative Names in the test certificates. 
					// You can generate your own self-signed certificates with commands in the certs README.
					.overrideAuthority("foo.test.google.fr")
					.sslContext(sslContext);
		} catch (SSLException ex) {
			log.error("", ex);
			throw new RuntimeException(ex);
		}
	}
	
	private SslContext buildSslContext(boolean mutualAuth) throws SSLException {
		// No Mutual authentication: provide only the ca.pem
		// Mutual authentication (client side authentication): provide all files
		InputStream trustCertCollectionFile = getInputStreamFromResource("certs/ca.pem");
		
		SslContextBuilder builder = GrpcSslContexts.forClient();
		
		if (trustCertCollectionFile != null) {
			builder.trustManager(trustCertCollectionFile);
		}
		
		// authenticate client?
		if (mutualAuth) {
			InputStream clientCertChainFile = getInputStreamFromResource("certs/client.pem");
			InputStream clientPrivateKeyFile = getInputStreamFromResource("certs/client.key");
			
			if (clientCertChainFile != null && clientPrivateKeyFile != null) {
				builder.keyManager(clientCertChainFile, clientPrivateKeyFile);
			}
		}
		
		return builder.build();
	}
	
	private InputStream getInputStreamFromResource(final String filePath) {
		return GrpcManagedChannelSecured.class.getClassLoader().getResourceAsStream(filePath);
	}
	
}
