package com.harlan.javagrpc.testutil;

public interface IServiceDiscoveryProperties {

	String getGrpcHost();

	int getGrpcPort();

	String getConsulServiceName();

	String getConsulId();

	String getConsulHost();

	int getConsulPort();

	String getConsulCheckTcp();

	String getConsulCheckInterval();

	String getConsulCheckTtl();

	String getConsulCheckTimeout();

}
