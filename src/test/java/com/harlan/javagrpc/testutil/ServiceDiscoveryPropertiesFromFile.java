package com.harlan.javagrpc.testutil;

import java.util.Properties;

public class ServiceDiscoveryPropertiesFromFile implements IServiceDiscoveryProperties {

	private final String DEFAULT_GRPC_HOST = "127.0.0.1";
	private final int DEFAULT_GRPC_PORT = 50051;
	private final int DEFAULT_CONSUL_CLIENT_PORT = 8500;
	
	private String grpcHost;
	private int grpcPort;
	private String consulServiceName;
	private String consulId;
	private String consulHost;
	private int consulPort;
	private String consulCheckTcp;
	private String consulCheckInterval;
	private String consulCheckTtl;
	private String consulCheckTimeout;
	
	public ServiceDiscoveryPropertiesFromFile() {
		Properties props = PropertiesFromClassLoader.getProperties("service-discovery-test.properties");
		
		this.grpcHost = props.getProperty("grpc.host", DEFAULT_GRPC_HOST);
		this.grpcPort = Integer.parseInt(props.getProperty("grpc.port", String.valueOf(DEFAULT_GRPC_PORT)));
		this.consulServiceName = "grpc-service-test";
		this.consulId = "server-test-1";
		this.consulHost = props.getProperty("consul.host");
		this.consulPort = Integer.parseInt(props.getProperty("consul.port", String.valueOf(DEFAULT_CONSUL_CLIENT_PORT)));
		this.consulCheckTcp = props.getProperty("consul.check.tcp");
		this.consulCheckInterval = "10s";
		this.consulCheckTtl = "30s"; // Consul internally does a curl every 10 seconds
		this.consulCheckTimeout = "1s";
	}

	@Override
	public String getGrpcHost() {
		return grpcHost;
	}

	@Override
	public int getGrpcPort() {
		return grpcPort;
	}

	@Override
	public String getConsulServiceName() {
		return consulServiceName;
	}

	@Override
	public String getConsulId() {
		return consulId;
	}

	@Override
	public String getConsulHost() {
		return consulHost;
	}

	@Override
	public int getConsulPort() {
		return consulPort;
	}

	@Override
	public String getConsulCheckTcp() {
		return consulCheckTcp;
	}

	@Override
	public String getConsulCheckInterval() {
		return consulCheckInterval;
	}

	@Override
	public String getConsulCheckTtl() {
		return consulCheckTtl;
	}

	@Override
	public String getConsulCheckTimeout() {
		return consulCheckTimeout;
	}
	
}
