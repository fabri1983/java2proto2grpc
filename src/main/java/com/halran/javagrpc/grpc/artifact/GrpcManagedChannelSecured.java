package com.halran.javagrpc.grpc.artifact;

import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import java.io.InputStream;

import javax.net.ssl.SSLException;

public class GrpcManagedChannelSecured extends GrpcManagedChannel {

	public GrpcManagedChannelSecured(String host, int port) {
		super(host, port);
	}
	
	@Override
	protected ManagedChannel createManagedChannel(String host, int port) {
		try {
			SslContext sslContext = buildSslContext();
			return NettyChannelBuilder
					.forAddress(host, port)
					// Only for using provided test certs to match the Subject Alternative Names in the test certificates. 
					// You can generate your own self-signed certificates with commands in the certs README.
					.overrideAuthority("foo.test.google.fr")
					.sslContext(sslContext)
					.build();
		} catch (SSLException ex) {
			System.err.println(ex);
			throw new RuntimeException(ex);
		}
	}
	
	private SslContext buildSslContext() throws SSLException {
		// No Mutual authentication: provides only the ca.pem
		// Mutual authentication: provides all files
		InputStream trustCertCollectionFile = getInputStreamFromResource("certs/ca.pem");
		InputStream clientCertChainFile = getInputStreamFromResource("certs/client.pem");
		InputStream clientPrivateKeyFile = getInputStreamFromResource("certs/client.key");
		
		SslContextBuilder builder = GrpcSslContexts.forClient();
		
		if (trustCertCollectionFile != null) {
			builder.trustManager(trustCertCollectionFile);
		}
		
		if (clientCertChainFile != null && clientPrivateKeyFile != null) {
			builder.keyManager(clientCertChainFile, clientPrivateKeyFile);
		}
		
		return builder.build();
	}
	
	private InputStream getInputStreamFromResource(final String filePath) {
		return GrpcManagedChannelSecured.class.getClassLoader().getResourceAsStream(filePath);
	}
	
}
