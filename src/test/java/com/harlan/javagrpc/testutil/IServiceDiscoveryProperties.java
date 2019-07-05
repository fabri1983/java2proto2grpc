package com.harlan.javagrpc.testutil;

public interface IServiceDiscoveryProperties {
	
	String getConsulServiceName();

	String getConsulServiceIdPrefix();

	String getConsulHost();

	int getConsulPort();

	String getConsulCheckTcp();

	String getConsulCheckInterval();

	String getConsulCheckTtl();

	String getConsulCheckTimeout();

}
