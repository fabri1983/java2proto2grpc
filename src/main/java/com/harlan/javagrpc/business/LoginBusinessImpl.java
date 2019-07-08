package com.harlan.javagrpc.business;

import com.harlan.javagrpc.business.contract.LoginBusiness;
import com.harlan.javagrpc.model.Request;
import com.harlan.javagrpc.model.Request2;
import com.harlan.javagrpc.model.Response;

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
