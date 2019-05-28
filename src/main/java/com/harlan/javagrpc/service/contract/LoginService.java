package com.harlan.javagrpc.service.contract;

import com.halran.javagrpc.model.Request;
import com.halran.javagrpc.model.Request2;
import com.halran.javagrpc.model.Response;
import com.harlan.javagrpc.converter.RemoteAccessEnabled;

@RemoteAccessEnabled
public interface LoginService {
	
	public void loginVoid();
	
	public int login(Request req);
	
	public Response getRes(Request req, Request2 req2);

}
