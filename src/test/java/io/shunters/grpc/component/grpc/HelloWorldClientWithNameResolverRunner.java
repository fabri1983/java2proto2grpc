package io.shunters.grpc.component.grpc;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Created by mykidong on 2018-01-10.
 */
public class HelloWorldClientWithNameResolverRunner {

    @Test
    public void searchKeyword() throws Exception {
    	String serviceName = System.getProperty("serviceName", "service-name");
        String consulHost = System.getProperty("consulHost", "localhost");
        int consulPort = Integer.valueOf(System.getProperty("consulPort", "8500"));
        boolean ignoreConsul = Boolean.valueOf(System.getProperty("ignoreConsul", "true"));
        String hostPorts = System.getProperty("hostPorts", "localhost:50051");

        List<String> hostPortsList = null;
        if(ignoreConsul)
        {
            String[] tokens = hostPorts.split(",");
            hostPortsList = Arrays.asList(tokens);
        }
        
    	HelloWorldClientWithNameResolver client = new HelloWorldClientWithNameResolver(serviceName, consulHost, consulPort, ignoreConsul, hostPortsList);
        client.sayHello();
    }

}
