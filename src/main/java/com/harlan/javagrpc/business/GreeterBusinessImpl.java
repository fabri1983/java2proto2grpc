package com.harlan.javagrpc.business;

import com.harlan.javagrpc.business.contract.GreeterBusiness;

public class GreeterBusinessImpl implements GreeterBusiness {

	@Override
	public String sayHello(String message) {
		return message;
	}

}
