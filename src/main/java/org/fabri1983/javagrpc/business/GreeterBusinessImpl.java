package org.fabri1983.javagrpc.business;

import org.fabri1983.javagrpc.business.contract.GreeterBusiness;

public class GreeterBusinessImpl implements GreeterBusiness {

	@Override
	public String sayHello(String message) {
		return message;
	}

}
