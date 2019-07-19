package com.harlan.javagrpc.grpc.artifact;

import java.util.List;

public class GrpcConfiguration {

	private String host;
	private int port;
	private String consulServiceName;
	private String consulHost;
	private int consulPort;
	private boolean ignoreConsul;
	private List<String> staticGrpcHostPorts;
	private int timerCheckPeriodInSeconds;
	private boolean useInternalGrpcLoadBalancing;
	
	public static GrpcConfiguration from(String host, int port) {
		GrpcConfiguration newObj = new GrpcConfiguration();
		newObj.host = host;
		newObj.port = port;
		return newObj;
	}
	
	public static GrpcConfiguration fromConsulServiceDiscovery(String consulServiceName, 
			String consulHost, int consulPort, int timerCheckPeriodInSeconds, boolean useInternalGrpcLoadBalancing) {
		GrpcConfiguration newObj = new GrpcConfiguration();
		newObj.consulServiceName = consulServiceName;
		newObj.consulHost = consulHost;
		newObj.consulPort = consulPort;
		newObj.timerCheckPeriodInSeconds = timerCheckPeriodInSeconds;
		newObj.ignoreConsul = false;
		newObj.useInternalGrpcLoadBalancing = useInternalGrpcLoadBalancing;
		return newObj;
	}

	public GrpcConfiguration fromConsulStaticHosts(List<String> staticHostPorts, 
			int timerCheckPeriodInSeconds, boolean useInternalGrpcLoadBalancing) {
		GrpcConfiguration newObj = new GrpcConfiguration();
		newObj.staticGrpcHostPorts = staticHostPorts;
		newObj.timerCheckPeriodInSeconds = timerCheckPeriodInSeconds;
		newObj.ignoreConsul = true;
		newObj.useInternalGrpcLoadBalancing = useInternalGrpcLoadBalancing;
		return newObj;
	}
	
	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getConsulServiceName() {
		return consulServiceName;
	}

	public String getConsulHost() {
		return consulHost;
	}

	public int getConsulPort() {
		return consulPort;
	}

	public boolean isIgnoreConsul() {
		return ignoreConsul;
	}

	public List<String> getStaticGrpcHostPorts() {
		return staticGrpcHostPorts;
	}

	public int getTimerCheckPeriodInSeconds() {
		return timerCheckPeriodInSeconds;
	}

	public boolean isUseInternalGrpcLoadBalancing() {
		return useInternalGrpcLoadBalancing;
	}

}
