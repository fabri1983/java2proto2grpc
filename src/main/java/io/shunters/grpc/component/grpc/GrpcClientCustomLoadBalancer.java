package io.shunters.grpc.component.grpc;

import io.shunters.grpc.api.component.ServiceDiscovery;
import io.shunters.grpc.component.consul.ConsulServiceDiscovery;
import io.shunters.grpc.util.RoundRobin;
import io.shunters.grpc.util.RoundRobin.Robin;
import io.shunters.grpc.util.TimeUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a custom Load Balancer strategy using a Round Robin policy.
 */
public class GrpcClientCustomLoadBalancer<G, B, A, F> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private RoundRobin<GrpcClient<G, B, A, F>> roundRobin;
    private List<RoundRobin.Robin<GrpcClient<G, B, A, F>>> robinList;

    private static final int DEFAULT_PAUSE_IN_SECONDS = 5;

    ReentrantLock lock = new ReentrantLock();

    private String serviceName;
    private String consulHost;
    private int consulPort;
    private boolean ignoreConsul;
    private Class<G> grpcClass;
    private List<String> hostPorts;
    private int pauseInSeconds;

    private ConnectionCheckTimer<G, B, A, F> connectionCheckTimer;

    /**
     * using consul service discovery.
     *
     * @param serviceName
     * @param consulHost
     * @param consulPort
     * @param grpcClass
     */
    public GrpcClientCustomLoadBalancer(String serviceName, String consulHost, 
    		int consulPort, Class<G> grpcClass) {
        this(serviceName, consulHost, consulPort, false, grpcClass, DEFAULT_PAUSE_IN_SECONDS, null);
    }

    public GrpcClientCustomLoadBalancer(String serviceName, String consulHost, 
    		int consulPort, Class<G> grpcClass, int pauseInSeconds) {
        this(serviceName, consulHost, consulPort, false, grpcClass, pauseInSeconds, null);
    }

    /**
     * using static node list.
     *
     * @param hostPorts
     * @param rpcClass
     */
    public GrpcClientCustomLoadBalancer(List<String> hostPorts, Class<G> rpcClass) {
        this(null, null, -1, true, rpcClass, DEFAULT_PAUSE_IN_SECONDS, hostPorts);
    }

    public GrpcClientCustomLoadBalancer(List<String> hostPorts, Class<G> rpcClass, int pauseInSeconds) {
        this(null, null, -1, true, rpcClass, pauseInSeconds, hostPorts);
    }


    public GrpcClientCustomLoadBalancer(String serviceName, String consulHost, int consulPort, boolean ignoreConsul, Class<G> rpcClass, List<String> hostPorts) {
        this(serviceName, consulHost, consulPort, ignoreConsul, rpcClass, DEFAULT_PAUSE_IN_SECONDS, hostPorts);
    }

    public GrpcClientCustomLoadBalancer(String serviceName, String consulHost, int consulPort, boolean ignoreConsul, Class<G> rpcClass, int pauseInSeconds, List<String> hostPorts) {
        this.serviceName = serviceName;
        this.consulHost = consulHost;
        this.consulPort = consulPort;
        this.ignoreConsul = ignoreConsul;
        this.grpcClass = rpcClass;
        this.hostPorts = hostPorts;
        this.pauseInSeconds = pauseInSeconds;

        loadServiceNodes();

        // run connection check timer.
        this.connectionCheckTimer = new ConnectionCheckTimer<G, B, A, F>(this, this.pauseInSeconds);
        this.connectionCheckTimer.runTimer();
    }

    public void loadServiceNodes() {
        lock.lock();
        try {
            robinList = new ArrayList<>();
            if (!ignoreConsul) {

                List<ServiceDiscovery.ServiceNode> nodes = null;
                while (true) {
                    nodes = getServiceNodes(serviceName, consulHost, consulPort);

                    if (nodes == null || nodes.size() == 0) {
                        log.warn("There is no node info for serviceName: [{}]...", serviceName);

                        TimeUtils.sleep(pauseInSeconds * 1000);
                    } else {
                        break;
                    }
                }

                for (ServiceDiscovery.ServiceNode node : nodes) {
                    String host = node.getHost();
                    int port = node.getPort();

                    log.info("ServiceName: [" + serviceName + "], host: [" + host + "], port: [" + port + "]");

                    GrpcClient<G, B, A, F> client = new GrpcClient<>(host, port, grpcClass);

                    robinList.add(new Robin<GrpcClient<G, B, A, F>>(client));
                }
            } else {
                for (String hostPort : hostPorts) {
                    String[] tokens = hostPort.split(":");

                    GrpcClient<G, B, A, F> client = new GrpcClient<>(tokens[0], Integer.valueOf(tokens[1]), grpcClass);
                    robinList.add(new Robin<GrpcClient<G, B, A, F>>(client));
                }
            }

            roundRobin = new RoundRobin<GrpcClient<G, B, A, F>>(robinList);
        } finally {
            lock.unlock();
        }
    }

    private List<ServiceDiscovery.ServiceNode> getServiceNodes(String serviceName, String consulHost, int consulPort) {
        ServiceDiscovery serviceDiscovery = ConsulServiceDiscovery.singleton(consulHost, consulPort);
        return serviceDiscovery.getHealthServices(serviceName);
    }

    public B getBlockingStub() {
        lock.lock();
        try {
            return this.roundRobin.next().getBlockingStub();
        } finally {
            lock.unlock();
        }
    }

    public A getAsyncStub() {
        lock.lock();
        try {
            return this.roundRobin.next().getAsyncStub();
        } finally {
            lock.unlock();
        }
    }

    public List<RoundRobin.Robin<GrpcClient<G, B, A, F>>> getRobinList() {
        return this.robinList;
    }

    public void shutdown() {
        for (RoundRobin.Robin<GrpcClient<G, B, A, F>> robin : robinList) {
            try {
                robin.call().shutdown();
            } catch (InterruptedException e) {
                log.error(e.getClass().getSimpleName() + ". " + e.getMessage());
            }
        }
    }

    private static class ConnectionCheckTimer<R, B, A, F> {
    	
        private ConnectionCheckTimerTask<R, B, A, F> timerTask;
        private int delay = 1000;
        private int pauseInSeconds;
        private Timer timer;
        private GrpcClientCustomLoadBalancer<R, B, A, F> lb;

        public ConnectionCheckTimer(GrpcClientCustomLoadBalancer<R, B, A, F> lb, int pauseInSeconds) {
            this.lb = lb;
            this.pauseInSeconds = pauseInSeconds;

            this.timerTask = new ConnectionCheckTimerTask<R, B, A, F>(this.lb);
            this.timer = new Timer();
        }

        public void runTimer() {
            this.timer.scheduleAtFixedRate(this.timerTask, delay, this.pauseInSeconds * 1000);
        }

        public void reset() {
            this.timerTask.cancel();
            this.timer.purge();
            this.timerTask = new ConnectionCheckTimerTask<R, B, A, F>(this.lb);
        }
    }

    private static class ConnectionCheckTimerTask<R, B, A, F> extends TimerTask {
    	
    	private final Logger log = LoggerFactory.getLogger(this.getClass());
    	
        private GrpcClientCustomLoadBalancer<R, B, A, F> lb;

        public ConnectionCheckTimerTask(GrpcClientCustomLoadBalancer<R, B, A, F> lb) {
            this.lb = lb;
        }

        @Override
        public void run() {

            for (RoundRobin.Robin<GrpcClient<R, B, A, F>> robin : lb.getRobinList()) {
                String host = robin.call().getHost();
                int port = robin.call().getPort();
                try {
                	// creating a socket stream also connects to it
                    Socket socketClient = new Socket(host, port);
                    socketClient.close();
                } catch (IOException e) {
                    log.error(e.getClass().getSimpleName() + ". " + e.getMessage());
                    log.warn("Service nodes being reloaded...");
                    this.lb.loadServiceNodes();
                    break;
                }
            }
        }
    }
}
