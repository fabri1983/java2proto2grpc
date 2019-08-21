package org.fabri1983.javagrpc.grpc.artifact.client.managedchannel;

import org.fabri1983.javagrpc.grpc.artifact.GrpcConfiguration;

public interface IGrpcManagedChannelFactory {

	IGrpcManagedChannel from(GrpcConfiguration config);
	
	boolean isServiceDiscoveryCapable();
	
	public class GrpcManagedChannelNonSecuredFactory implements IGrpcManagedChannelFactory {
		
		@Override
		public IGrpcManagedChannel from(GrpcConfiguration config) {
			return new GrpcManagedChannel(config);
		}

		@Override
		public boolean isServiceDiscoveryCapable() {
			return false;
		}
	}
	
	public class GrpcManagedChannelSecuredFactory implements IGrpcManagedChannelFactory {
		
		@Override
		public IGrpcManagedChannel from(GrpcConfiguration config) {
			return new GrpcManagedChannelSecured(config);
		}
		
		@Override
		public boolean isServiceDiscoveryCapable() {
			return false;
		}
	}
	
	public class GrpcManagedChannelServiceDiscoveryFactory implements IGrpcManagedChannelFactory {
		
		@Override
		public IGrpcManagedChannel from(GrpcConfiguration config) {
			return new GrpcManagedChannelServiceDiscovery(config);
		}
		
		@Override
		public boolean isServiceDiscoveryCapable() {
			return true;
		}
	}
	
}
