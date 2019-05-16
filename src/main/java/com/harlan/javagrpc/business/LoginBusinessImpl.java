package com.harlan.javagrpc.business;

import com.halran.javagrpc.model.Request;
import com.halran.javagrpc.model.Request2;
import com.halran.javagrpc.model.Response;
import com.harlan.javagrpc.business.contract.LoginBusiness;

public class LoginBusinessImpl implements LoginBusiness {
	
	@Override
	public int login(Request req) {
		return 1234567890;
	}
	
	@Override
	public Response getRes(Request req, Request2 req2) {
		return Response.from(req.getI1(), req.getS());
	}

}
