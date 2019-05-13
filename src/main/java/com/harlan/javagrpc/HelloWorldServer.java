package com.harlan.javagrpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.SearchRequest;
import io.grpc.examples.helloworld.SearchResponse;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class HelloWorldServer {


    private int port = 50051;
    private Server server;

    private void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new GreeterImpl())
                .build()
                .start();

        System.out.println("service start...");

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {

                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                HelloWorldServer.this.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    // block 一直到退出程序 
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {

        final HelloWorldServer server = new HelloWorldServer();
        server.start();
        server.blockUntilShutdown();
    }


    // 实现 定义一个实现服务接口的类 
    private class GreeterImpl extends GreeterGrpc.GreeterImplBase {
@Override
public void sayHello(SearchRequest request, StreamObserver<SearchResponse> responseObserver) {
	 System.out.println("sayHello service:"+request.getHelloRequest(0).getName());
	 String message = request.getHelloRequest(0).getName();
     SearchResponse reply = SearchResponse.newBuilder().addHelloReply(SearchResponse.HelloReply.newBuilder().setMessage("hello"+message)).build();
     responseObserver.onNext(reply);
     responseObserver.onCompleted();
}

      /*  public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
            System.out.println("sayHello service:"+req.getName());
            HelloReply reply = HelloReply.newBuilder().setMessage(("Hello: " + req.getName())).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
        
        public void sayWorld(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        	System.out.println("sayWorld service:"+request.getName());
            HelloReply reply = HelloReply.newBuilder().setMessage(("Hello: " + request.getName())).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }*/
    }
} 