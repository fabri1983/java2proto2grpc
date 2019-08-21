package org.fabri1983.javagrpc.business.contract;

import org.fabri1983.javagrpc.model.Request;
import org.fabri1983.javagrpc.model.Request2;
import org.fabri1983.javagrpc.model.Response;

public interface LoginBusiness {
	
	public void loginVoid();
	
	public int login(Request req);

	public Response getRes(Request req, Request2 req2);

}
