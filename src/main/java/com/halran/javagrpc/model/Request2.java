package com.halran.javagrpc.model;

import com.harlan.javagrpc.service.contract.protobuf.Request2Proto;

import java.util.List;

import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

@ProtoClass(Request2Proto.class)
public class Request2 {

	@ProtoField
	private int id;
	@ProtoField
	private String name;
	@ProtoField
	private Request2Inner req2Inner;
	@ProtoField
	private Request req;
	@ProtoField
	private List<Integer> integer;
	@ProtoField
	private List<Response> resps;

	public static Request2 from(int id, String name) {
		Request2 newObj = new Request2();
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

	public Request2Inner getReq2Inner() {
		return req2Inner;
	}

	public void setReq2Inner(Request2Inner req2Inner) {
		this.req2Inner = req2Inner;
	}

	public Request getReq() {
		return req;
	}

	public void setReq(Request req) {
		this.req = req;
	}

	public List<Integer> getInteger() {
		return integer;
	}

	public void setInteger(List<Integer> integer) {
		this.integer = integer;
	}

	public List<Response> getResps() {
		return resps;
	}

	public void setResps(List<Response> resps) {
		this.resps = resps;
	}
	
}
