package com.harlan.javagrpc.testutil;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ServiceDiscoveryPropertiesFromFile implements IServiceDiscoveryProperties {

	private List<String> grpcAddressList;
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
		
		this.grpcAddressList = Arrays.asList(props.getProperty("grpc.address.list").split(","))
				.stream().map( s -> s.trim() ).collect( Collectors.toList() );
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
	public List<String> getGrpcAddressList() {
		return grpcAddressList;
	}

	@Override
	public String[] splitAddress(String grpcAddress) {
		String[] split = grpcAddress.split(":");
		for (int i=0; i < split.length; ++i) {
			split[i] = split[i].trim();
		}
		return split;
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
