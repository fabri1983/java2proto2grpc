package com.harlan.javagrpc.business.contract;

import com.harlan.javagrpc.model.Request;
import com.harlan.javagrpc.model.Request2;
import com.harlan.javagrpc.model.Response;

public interface LoginBusiness {
	
	public void loginVoid();
	
	public int login(Request req);

	public Response getRes(Request req, Request2 req2);

}
