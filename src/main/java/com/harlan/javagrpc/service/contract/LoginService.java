package com.harlan.javagrpc.service.contract;

import com.halran.javagrpc.model.Request;
import com.halran.javagrpc.model.Request2;
import com.halran.javagrpc.model.Response;

public interface LoginService {
	
	public int login(Request req);

	public Response getRes(Request req, Request2 req2);

}