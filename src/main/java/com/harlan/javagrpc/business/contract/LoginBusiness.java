package com.harlan.javagrpc.business.contract;

import com.halran.javagrpc.model.Request;
import com.halran.javagrpc.model.Request2;
import com.halran.javagrpc.model.Response;

public interface LoginBusiness {
	
	public void loginVoid();
	
	public int login(Request req);

	public Response getRes(Request req, Request2 req2);

}
