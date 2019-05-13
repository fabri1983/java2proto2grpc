package com.harlan.javagrpc.service;

import com.halran.javagrpc.mode.Request;
import com.halran.javagrpc.mode.Request2;
import com.halran.javagrpc.mode.Response;

public interface LoginService {
	public int login();

	public Response getRes(Request req,Request2 req2);
	
//	public Response getRes2(Request req,Request2 req2);

}
