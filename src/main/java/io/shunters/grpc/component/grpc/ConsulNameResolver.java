package io.shunters.grpc.component.grpc;

import io.grpc.Attributes;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;
import io.shunters.grpc.api.component.ServiceDiscovery;
import io.shunters.grpc.component.consul.ConsulServiceDiscovery;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsulNameResolver extends NameResolver {

    private static Logger log = LoggerFactory.getLogger(ConsulNameResolver.class);

    private URI uri;
    private String serviceName;
    private int pauseInSeconds;
    private boolean ignoreConsul;
    private List<String> hostPorts;

    private Listener listener;

    private List<ServiceDiscovery.ServiceNode> nodes;

    private ConnectionCheckTimer connectionCheckTimer;

    private ConsulNameResolver() {
    }

	public static ConsulNameResolver fromWithHealthCheck(URI uri, String serviceName, int pauseInSeconds, boolean ignoreConsul,
			List<String> hostPorts) {
		ConsulNameResolver newObj = fromNoHealthCheck(uri, serviceName, pauseInSeconds, ignoreConsul, hostPorts);
		// run connection check timer.
		newObj.connectionCheckTimer = new ConnectionCheckTimer(newObj, newObj.pauseInSeconds);
		newObj.connectionCheckTimer.runTimer();
		return newObj;
	}
    
	public static ConsulNameResolver fromNoHealthCheck(URI uri, String serviceName, int pauseInSeconds, boolean ignoreConsul,
			List<String> hostPorts) {
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
        List<EquivalentAddressGroup> addrs = new ArrayList<>();

        if(!this.ignoreConsul) {
            String consulHost = uri.getHost();
            int consulPort = uri.getPort();

            nodes = getServiceNodes(serviceName, consulHost, consulPort);
            if (nodes == null || nodes.size() == 0) {
                log.warn("There is no node info for serviceName: [{}]...", serviceName);
                return;
            }

            for (ServiceDiscovery.ServiceNode node : nodes) {
                String host = node.getHost();
                int port = node.getPort();
                log.info("Found serviceName: [" + serviceName + "], host: [" + host + "], port: [" + port + "]");

                List<SocketAddress> sockaddrsList = new ArrayList<SocketAddress>();
                sockaddrsList.add(new InetSocketAddress(host, port));
                addrs.add(new EquivalentAddressGroup(sockaddrsList));
            }
        }
        else
        {
            nodes = new ArrayList<>();
            for(String hostPort : this.hostPorts)
            {
                String[] tokens = hostPort.split(":");

                String host = tokens[0];
                int port = Integer.valueOf(tokens[1]);
                log.info("static host: [" + host + "], port: [" + port + "]");

                nodes.add(new ServiceDiscovery.ServiceNode("", host, port));

                List<SocketAddress> sockaddrsList = new ArrayList<SocketAddress>();
                sockaddrsList.add(new InetSocketAddress(host, port));
                addrs.add(new EquivalentAddressGroup(sockaddrsList));
            }
        }

        if(addrs.size() > 0) {
            this.listener.onAddresses(addrs, Attributes.EMPTY);
        }
    }

    public List<ServiceDiscovery.ServiceNode> getNodes() {
        return this.nodes;
    }

    private List<ServiceDiscovery.ServiceNode> getServiceNodes(String serviceName, String consulHost, int consulPort) {
        ServiceDiscovery serviceDiscovery = ConsulServiceDiscovery.singleton(consulHost, consulPort);

        return serviceDiscovery.getHealthServices(serviceName);
    }

    @Override
    public void shutdown() {

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
            this.timer.scheduleAtFixedRate(this.timerTask, delay, this.pauseInSeconds * 1000);
        }

        public void reset() {
            this.timerTask.cancel();
            this.timer.purge();
            this.timerTask = new ConnectionCheckTimerTask(consulNameResolver);
        }
    }

    private static class ConnectionCheckTimerTask extends TimerTask {
        private ConsulNameResolver consulNameResolver;

        public ConnectionCheckTimerTask(ConsulNameResolver consulNameResolver) {
            this.consulNameResolver = consulNameResolver;
        }

        @Override
        public void run() {
            List<ServiceDiscovery.ServiceNode> nodes = consulNameResolver.getNodes();
            if(nodes != null) {
                for (ServiceDiscovery.ServiceNode node : nodes) {
                    String host = node.getHost();
                    int port = node.getPort();
                    try {
                    	// creating a socket stream also connects to it
                        Socket socketClient = new Socket(host, port);
                        socketClient.close();
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        log.info("service nodes being reloaded...");

                        this.consulNameResolver.loadServiceNodes();
                        break;
                    }
                }
            }
            else
            {
                log.info("no service nodes...");
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
         * @param serviceName consul service name.
         * @param pauseInSeconds timer check period in seconds. If >=1 then health check is used. Otherwise isn't.
         * @param ignoreConsul if true, consul is not used. instead, the static node list will be used.
         * @param hostPorts the static node list, for instance, Arrays.asList("host1:port1", "host2:port2")
         */
        public ConsulNameResolverProvider(String serviceName, int pauseInSeconds, boolean ignoreConsul, List<String> hostPorts)
        {
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
        public NameResolver newNameResolver(URI uri, Attributes attributes) {
        	if (withHealthCheck) {
        		return ConsulNameResolver.fromWithHealthCheck(uri, serviceName, pauseInSeconds, this.ignoreConsul, this.hostPorts);
        	}
        	return ConsulNameResolver.fromNoHealthCheck(uri, serviceName, pauseInSeconds, this.ignoreConsul, this.hostPorts);
        }

        @Override
        public String getDefaultScheme() {
            return "consul";
        }
    }

}
