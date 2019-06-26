package io.shunters.grpc.component.grpc;

import java.util.Arrays;

import org.junit.Test;

/**
 * Created by mykidong on 2018-01-10.
 */
public class HelloWorldClientRunner {

    @Test
    public void searchKeyword() throws Exception {
    	HelloWorldClient client = new HelloWorldClient(Arrays.asList("localhost:50051"));
        client.sayHello();
    }

}
