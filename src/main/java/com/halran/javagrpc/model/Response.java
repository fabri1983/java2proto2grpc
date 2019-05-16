package com.halran.javagrpc.model;

import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

@ProtoClass(com.harlan.javagrpc.service.contract.protobuf.Response.class)
public class Response {

	@ProtoField
	private int id;
	@ProtoField
	private String name;
	
	public static Response from (int id, String name) {
		Response newObj = new Response();
		newObj.id = id;
		newObj.name = name;
		return newObj;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
