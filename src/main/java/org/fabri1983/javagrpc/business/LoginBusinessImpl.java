package org.fabri1983.javagrpc.business;

import org.fabri1983.javagrpc.business.contract.LoginBusiness;
import org.fabri1983.javagrpc.model.Request;
import org.fabri1983.javagrpc.model.Request2;
import org.fabri1983.javagrpc.model.Response;

public class LoginBusinessImpl implements LoginBusiness {
	
	@Override
	public void loginVoid() {
	}
	
	@Override
	public int login(Request req) {
		return req.getI1();
	}
	
	@Override
	public Response getRes(Request req, Request2 req2) {
		return Response.from(req.getI1(), req.getS(), req.getCorpus());
	}

}
