package com.halran.javagrpc.grpc.artifact;

import java.io.IOException;

public interface IGrpcServerStarter {

	GrpcServerStarter register(GrpcServiceMarker grpcService);

	void start() throws IOException;

	void stop();

	void blockUntilShutdown(boolean blockInOtherThread);

}
