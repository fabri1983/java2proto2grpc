package org.fabri1983.javagrpc.service.contract;

import org.fabri1983.javagrpc.converter.annotation.GrpcEnabled;
import org.fabri1983.javagrpc.model.Request;
import org.fabri1983.javagrpc.model.Request2;
import org.fabri1983.javagrpc.model.Response;

@GrpcEnabled
public interface LoginService {
	
	public void loginVoid();
	
	public int login(Request req);
	
	public Response getRes(Request req, Request2 req2);

}
