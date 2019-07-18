package com.harlan.javagrpc.grpc.artifact.client;

import com.harlan.javagrpc.grpc.artifact.GrpcConfiguration;
import com.harlan.javagrpc.grpc.artifact.client.RoundRobin.Robin;
import com.harlan.javagrpc.grpc.artifact.client.managedchannel.GrpcManagedChannel;
import com.harlan.javagrpc.grpc.artifact.client.managedchannel.IGrpcManagedChannel;
import com.harlan.javagrpc.grpc.artifact.discovery.ConsulServiceDiscovery;
import com.harlan.javagrpc.grpc.artifact.discovery.IServiceDiscovery;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a custom Load Balancer strategy using a Round Robin policy for the different 
 * types of gRPC stubs: B (blocking), A (async), F (future).
 */
public class GrpcClientWithLoadBalancer<B, A, F> implements IGrpcClient<B, A, F> {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static final int DEFAULT_PAUSE_IN_SECONDS = 5;

	private RoundRobin<GrpcClientStub<B, A, F>> roundRobin;
	private List<RoundRobin.Robin<GrpcClientStub<B, A, F>>> robinList;
	private ConnectionCheckTimer<B, A, F> connectionCheckTimer;
	private ReentrantLock lock = new ReentrantLock();

	private String serviceName;
	private String consulHost;
	private int consulPort;
	private boolean ignoreConsul;
	private List<String> hostPorts;
	private int pauseInSeconds;
	private IGrpcClientStubFactory<B, A, F> stubFactory;
	
	/**
	 * using consul service discovery.
	 *
	 * @param serviceName
	 * @param consulHost
	 * @param consulPort
	 * @param stubFactory
	 */
	public GrpcClientWithLoadBalancer(String serviceName, String consulHost, int consulPort, IGrpcClientStubFactory<B, A, F> stubFactory) {
		this(serviceName, consulHost, consulPort, false, stubFactory, DEFAULT_PAUSE_IN_SECONDS, null);
	}

	public GrpcClientWithLoadBalancer(String serviceName, String consulHost, int consulPort, IGrpcClientStubFactory<B, A, F> stubFactory,
			int pauseInSeconds) {
		this(serviceName, consulHost, consulPort, false, stubFactory, pauseInSeconds, null);
	}

	/**
	 * using static node list.
	 *
	 * @param hostPorts
	 * @param stubFactory
	 */
	public GrpcClientWithLoadBalancer(List<String> hostPorts, IGrpcClientStubFactory<B, A, F> stubFactory) {
		this(null, null, -1, true, stubFactory, DEFAULT_PAUSE_IN_SECONDS, hostPorts);
	}

	public GrpcClientWithLoadBalancer(List<String> hostPorts, IGrpcClientStubFactory<B, A, F> stubFactory, int pauseInSeconds) {
		this(null, null, -1, true, stubFactory, pauseInSeconds, hostPorts);
	}

	public GrpcClientWithLoadBalancer(String serviceName, String consulHost, int consulPort, boolean ignoreConsul,
			IGrpcClientStubFactory<B, A, F> stubFactory, List<String> hostPorts) {
		this(serviceName, consulHost, consulPort, ignoreConsul, stubFactory, DEFAULT_PAUSE_IN_SECONDS, hostPorts);
	}

	public GrpcClientWithLoadBalancer(String serviceName, String consulHost, int consulPort, boolean ignoreConsul,
			IGrpcClientStubFactory<B, A, F> stubFactory, int pauseInSeconds, List<String> hostPorts) {
		this.serviceName = serviceName;
		this.consulHost = consulHost;
		this.consulPort = consulPort;
		this.ignoreConsul = ignoreConsul;
		this.hostPorts = hostPorts;
		this.pauseInSeconds = pauseInSeconds;
		this.stubFactory = stubFactory;
		
		loadServiceNodes();

		// run connection check timer
		this.connectionCheckTimer = new ConnectionCheckTimer<B, A, F>(this, this.pauseInSeconds);
		this.connectionCheckTimer.runTimer();
	}

	private void loadServiceNodes() {
		lock.lock();
		try {
			robinList = new ArrayList<>(2);

			if (!ignoreConsul) {

				List<IServiceDiscovery.ServiceNode> nodes = Collections.emptyList();
				while (true) {
					nodes = getServiceNodes(serviceName, consulHost, consulPort);

					if (nodes == null || nodes.size() == 0) {
						log.warn("There is no node info for serviceName: [{}]...", serviceName);
						sleep(pauseInSeconds);
					} else {
						break;
					}
				}

				for (IServiceDiscovery.ServiceNode node : nodes) {
					String host = node.getHost();
					int port = node.getPort();

					log.info("Found Service. name: [{}], host: [{}], port: [{}]", serviceName, host, port);

					// TODO [Improvement] use a Factory to decide which managed channel to use
					IGrpcManagedChannel managedChannel = new GrpcManagedChannel(GrpcConfiguration.from(host, port));
					GrpcClientStub<B, A, F> client = new GrpcClientStub<>(managedChannel, stubFactory);

					robinList.add(new Robin<GrpcClientStub<B, A, F>>(client));
				}
			} else {
				for (String hostPort : hostPorts) {
					String[] tokens = hostPort.split(":");
					String host = tokens[0].trim();
					Integer port = Integer.valueOf(tokens[1].trim());

					log.info("Static Node. host: [{}], port: [{}]", host, port);
					
					// TODO [Improvement] use a Factory to decide which managed channel to use
					IGrpcManagedChannel managedChannel = new GrpcManagedChannel(GrpcConfiguration.from(host, port));
					GrpcClientStub<B, A, F> client = new GrpcClientStub<>(managedChannel, stubFactory);

					robinList.add(new Robin<GrpcClientStub<B, A, F>>(client));
				}
			}

			roundRobin = new RoundRobin<GrpcClientStub<B, A, F>>(robinList);
		} finally {
			lock.unlock();
		}
	}

	private List<IServiceDiscovery.ServiceNode> getServiceNodes(String serviceName, String consulHost, int consulPort) {
		IServiceDiscovery serviceDiscovery = ConsulServiceDiscovery.singleton(consulHost, consulPort);
		return serviceDiscovery.getHealthServices(serviceName);
	}

	@Override
	public B getBlockingStub() {
		lock.lock();
		try {
			return this.roundRobin.next().getBlockingStub();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public A getAsyncStub() {
		lock.lock();
		try {
			return this.roundRobin.next().getAsyncStub();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public F getFutureStub() {
		lock.lock();
		try {
			return this.roundRobin.next().getFutureStub();
		} finally {
			lock.unlock();
		}
	}
	
	public List<RoundRobin.Robin<GrpcClientStub<B, A, F>>> getRobinList() {
		return this.robinList;
	}

	@Override
	public void shutdown() {
		if (connectionCheckTimer != null) {
			connectionCheckTimer.shutdown();
		}
		
		for (RoundRobin.Robin<GrpcClientStub<B, A, F>> robin : robinList) {
			robin.get().shutdown();
		}
	}

	private void sleep(long sleepInSeconds) {
		try {
			Thread.sleep(TimeUnit.SECONDS.toMillis(sleepInSeconds));
		} catch (Exception e) {
			// nothing to report
		}
	}
	
	private static class ConnectionCheckTimer<B, A, F> {

		private ConnectionCheckTimerTask<B, A, F> timerTask;
		private int delay = 1000;
		private int pauseInSeconds;
		private Timer timer;
		private GrpcClientWithLoadBalancer<B, A, F> clientWithLb;

		public ConnectionCheckTimer(GrpcClientWithLoadBalancer<B, A, F> clientWithLb, int pauseInSeconds) {
			this.clientWithLb = clientWithLb;
			this.pauseInSeconds = pauseInSeconds;

			this.timerTask = new ConnectionCheckTimerTask<B, A, F>(this.clientWithLb);
			this.timer = new Timer();
		}

		public void runTimer() {
			this.timer.scheduleAtFixedRate(this.timerTask, delay, TimeUnit.SECONDS.toMillis(this.pauseInSeconds));
		}

		public void shutdown() {
			this.timerTask.cancel();
			this.timer.cancel();
			this.timer.purge();
		}
	}

	private static class ConnectionCheckTimerTask<B, A, F> extends TimerTask {

		private final Logger log = LoggerFactory.getLogger(this.getClass());

		private GrpcClientWithLoadBalancer<B, A, F> clientWithLb;

		public ConnectionCheckTimerTask(GrpcClientWithLoadBalancer<B, A, F> clientWithLb) {
			this.clientWithLb = clientWithLb;
		}

		@Override
		public void run() {

			for (RoundRobin.Robin<GrpcClientStub<B, A, F>> robin : clientWithLb.getRobinList()) {
				String host = robin.get().getHost();
				int port = robin.get().getPort();
				try {
					// creating a socket stream also connects to it
					Socket socketClient = new Socket(host, port);
					socketClient.close();
				} catch (IOException e) {
					log.error("{}, {}", e.getClass().getSimpleName(), e.getMessage());
					log.warn("Service nodes being reloaded...");
					this.clientWithLb.loadServiceNodes();
					break;
				}
			}
		}
	}
}
