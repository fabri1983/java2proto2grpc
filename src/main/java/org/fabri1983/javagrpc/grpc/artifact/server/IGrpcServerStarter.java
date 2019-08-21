package org.fabri1983.javagrpc.grpc.artifact.server;

import io.grpc.Server;

import java.io.IOException;
import java.util.List;

public interface IGrpcServerStarter {

	GrpcServerStarter registerBeforeStart(GrpcServiceMarker grpcService);

	GrpcServerStarter registerBeforeStart(List<GrpcServiceMarker> grpcServices);
	
	GrpcServerStarter register(GrpcServiceMarker grpcService);
	
	void start() throws IOException;

	void stop();
	
	void forceStop();

	void blockUntilShutdown(boolean blockInOtherThread);

	Server getServer();

}
