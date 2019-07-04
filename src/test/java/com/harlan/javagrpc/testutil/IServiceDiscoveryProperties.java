package com.harlan.javagrpc.testutil;

import java.util.List;

public interface IServiceDiscoveryProperties {
	
	List<String> getGrpcAddressList();
	
	String[] splitAddress(String grpcAddress);

	String getConsulServiceName();

	String getConsulServiceIdPrefix();

	String getConsulHost();

	int getConsulPort();

	String getConsulCheckTcp();

	String getConsulCheckInterval();

	String getConsulCheckTtl();

	String getConsulCheckTimeout();

}
