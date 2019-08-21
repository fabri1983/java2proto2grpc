package org.fabri1983.javagrpc.testutil;

import java.util.Properties;

public class ServiceDiscoveryPropertiesFromFile implements IServiceDiscoveryProperties {

	private String consulServiceName;
	private String consulServiceIdPrefix;
	private String consulHost;
	private int consulPort;
	private String consulCheckTcp;
	private String consulCheckInterval;
	private String consulCheckTtl;
	private String consulCheckTimeout;
	
	public ServiceDiscoveryPropertiesFromFile() {
		
		Properties props = PropertiesFromClassLoader.getProperties("service-discovery-test.properties");
		
		this.consulServiceName = "grpc-service-test";
		this.consulServiceIdPrefix = "id-test_";
		this.consulHost = props.getProperty("consul.host");
		this.consulPort = Integer.parseInt(props.getProperty("consul.port"));
		this.consulCheckTcp = props.getProperty("consul.check.tcp");
		this.consulCheckInterval = "1s"; // if bigger than 1s then you have to wait some seconds before the new registered service gets its check available
		this.consulCheckTtl = "30s";
		this.consulCheckTimeout = "1s";
	}

	@Override
	public String getConsulServiceName() {
		return consulServiceName;
	}

	@Override
	public String getConsulServiceIdPrefix() {
		return consulServiceIdPrefix;
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
