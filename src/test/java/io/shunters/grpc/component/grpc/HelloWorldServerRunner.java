package io.shunters.grpc.component.grpc;

import org.junit.Test;

/**
 * Created by mykidong on 2018-01-10.
 */
public class HelloWorldServerRunner {

    @Test
    public void runServer() throws Exception {
        final HelloWorldServer server = new HelloWorldServer();
        server.start();
        server.blockUntilShutdown();
    }

}
