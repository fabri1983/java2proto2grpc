package com.harlan.javagrpc.testutil;

import java.util.Properties;

public class ConsulProperties {

	private static final Properties consulProperties = PropertiesLoader.getProperties("consul-test.properties");
	
	public static final String consulServiceName = "grpcServiceDiscovery-test";
	public static final String consulId = "server1";
	public static final String consulHost = consulProperties.getProperty("consul.ip");
	public static final int consulPort = Integer.parseInt(consulProperties.getProperty("consul.port"));
	public static final String consulCheckInterval = "10s";
	public static final String consulCheckTimeout = "1s";
	
}
