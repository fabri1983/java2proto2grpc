package org.fabri1983.javagrpc.grpc.artifact.discovery;

import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsulNameResolver extends NameResolver {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private URI uri;
	private String serviceName;
	private int pauseInSeconds;
	private boolean ignoreConsul;
	private List<String> hostPorts;

	private Listener listener;

	private List<IServiceDiscovery.ServiceNode> nodes;

	private ConnectionCheckTimer connectionCheckTimer;

	private ConsulNameResolver() {
	}

	public static ConsulNameResolver withHealthCheck(URI uri, String serviceName, int pauseInSeconds,
			boolean ignoreConsul, List<String> hostPorts) {
		ConsulNameResolver newObj = noHealthCheck(uri, serviceName, pauseInSeconds, ignoreConsul, hostPorts);
		newObj.connectionCheckTimer = new ConnectionCheckTimer(newObj, newObj.pauseInSeconds);
		newObj.connectionCheckTimer.runTimer();
		return newObj;
	}

	public static ConsulNameResolver noHealthCheck(URI uri, String serviceName, int pauseInSeconds,
			boolean ignoreConsul, List<String> hostPorts) {
		ConsulNameResolver newObj = new ConsulNameResolver();
		newObj.uri = uri;
		newObj.serviceName = serviceName;
		newObj.pauseInSeconds = pauseInSeconds;
		newObj.ignoreConsul = ignoreConsul;
		newObj.hostPorts = hostPorts;
		return newObj;
	}

	@Override
	public String getServiceAuthority() {
		return this.uri.getAuthority();
	}

	@Override
	public void start(Listener listener) {
		this.listener = listener;
		loadServiceNodes();
	}

	private void loadServiceNodes() {
		List<EquivalentAddressGroup> addrs = new ArrayList<>(5);

		if (!this.ignoreConsul) {
			String consulHost = uri.getHost();
			int consulPort = uri.getPort();

			nodes = getServiceNodes(serviceName, consulHost, consulPort);

			if (nodes == null || nodes.size() == 0) {
				log.warn("There is no node info for serviceName: [{}]...", serviceName);
				return;
			}

			for (IServiceDiscovery.ServiceNode node : nodes) {
				String host = node.getHost();
				int port = node.getPort();

				log.info("Found Service. name: [{}], host: [{}], port: [{}]", serviceName, host, port);

				List<SocketAddress> sockaddrsList = new ArrayList<SocketAddress>(2);
				sockaddrsList.add(new InetSocketAddress(host, port));
				addrs.add(new EquivalentAddressGroup(sockaddrsList));
			}
		} else {
			nodes = new ArrayList<>(5);
			for (String hostPort : this.hostPorts) {
				String[] tokens = hostPort.split(":");
				String host = tokens[0];
				int port = Integer.valueOf(tokens[1]);
				
				log.info("Static Node. host: [{}], port: [{}]", host, port);

				nodes.add(new IServiceDiscovery.ServiceNode("", host, port));

				List<SocketAddress> sockaddrsList = new ArrayList<SocketAddress>(2);
				sockaddrsList.add(new InetSocketAddress(host, port));
				addrs.add(new EquivalentAddressGroup(sockaddrsList));
			}
		}

		if (addrs.size() > 0) {
			this.listener.onAddresses(addrs, Attributes.EMPTY);
		}
	}

	private List<IServiceDiscovery.ServiceNode> getServiceNodes(String serviceName, String consulHost, int consulPort) {
		IServiceDiscovery serviceDiscovery = ConsulServiceDiscovery.singleton(consulHost, consulPort);
		return serviceDiscovery.getHealthServices(serviceName);
	}

	public List<IServiceDiscovery.ServiceNode> getNodes() {
		return this.nodes;
	}

	@Override
	public void shutdown() {
		if (connectionCheckTimer != null) {
			connectionCheckTimer.shutdown();
		}
	}

	private static class ConnectionCheckTimer {
		private ConnectionCheckTimerTask timerTask;
		private int delay = 1000;
		private int pauseInSeconds;
		private Timer timer;
		private ConsulNameResolver consulNameResolver;

		public ConnectionCheckTimer(ConsulNameResolver consulNameResolver, int pauseInSeconds) {
			this.consulNameResolver = consulNameResolver;
			this.pauseInSeconds = pauseInSeconds;

			this.timerTask = new ConnectionCheckTimerTask(this.consulNameResolver);
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

	private static class ConnectionCheckTimerTask extends TimerTask {

		private final Logger log = LoggerFactory.getLogger(this.getClass());

		private ConsulNameResolver consulNameResolver;

		public ConnectionCheckTimerTask(ConsulNameResolver consulNameResolver) {
			this.consulNameResolver = consulNameResolver;
		}

		@Override
		public void run() {
			List<IServiceDiscovery.ServiceNode> nodes = consulNameResolver.getNodes();
			if (nodes != null) {
				for (IServiceDiscovery.ServiceNode node : nodes) {
					String host = node.getHost();
					int port = node.getPort();
					try {
						// creating a socket stream also connects to it
						Socket socketClient = new Socket(host, port);
						socketClient.close();
					} catch (IOException e) {
						log.error(e.getClass().getSimpleName() + ". " + e.getMessage());
						log.warn("Service nodes being reloaded...");
						this.consulNameResolver.loadServiceNodes();
						break;
					}
				}
			} else {
				log.info("No service nodes...");
			}
		}
	}

	public static class ConsulNameResolverProvider extends NameResolverProvider {

		private String serviceName;
		private int pauseInSeconds;
		private boolean ignoreConsul;
		private List<String> hostPorts;
		private boolean withHealthCheck;

		/**
		 * @param serviceName    consul service name.
		 * @param pauseInSeconds timer check period in seconds. If >=1 then health check
		 *                       is used. Otherwise isn't.
		 * @param ignoreConsul   if true, consul is not used. instead, the static node
		 *                       list will be used.
		 * @param hostPorts      the static node list, for instance,
		 *                       Arrays.asList("host1:port1", "host2:port2")
		 */
		public ConsulNameResolverProvider(String serviceName, int pauseInSeconds, boolean ignoreConsul,
				List<String> hostPorts) {
			this.serviceName = serviceName;
			this.pauseInSeconds = pauseInSeconds;
			this.ignoreConsul = ignoreConsul;
			this.hostPorts = hostPorts;
			this.withHealthCheck = pauseInSeconds >= 1;
		}

		@Override
		protected boolean isAvailable() {
			return true;
		}

		@Override
		protected int priority() {
			return 5;
		}

		@Nullable
		@Override
		public NameResolver newNameResolver(URI uri, NameResolver.Args args) {
			if (withHealthCheck) {
				return ConsulNameResolver.withHealthCheck(uri, serviceName, pauseInSeconds, this.ignoreConsul, this.hostPorts);
			}
			return ConsulNameResolver.noHealthCheck(uri, serviceName, pauseInSeconds, this.ignoreConsul, this.hostPorts);
		}

		@Override
		public String getDefaultScheme() {
			return "consul";
		}
	}

}
