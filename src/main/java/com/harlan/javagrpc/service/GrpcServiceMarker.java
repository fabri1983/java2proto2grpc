package com.harlan.javagrpc.service;

/**
 * Used to let Spring collects beans implementing this interface to register them in a gRPC Server instance.
 * So this interface must be implemented by classes that extends from XxxImplBase. 
 */
public interface GrpcServiceMarker {

}
