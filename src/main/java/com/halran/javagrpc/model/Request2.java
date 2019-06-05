package com.halran.javagrpc.model;

import java.util.List;

import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

@ProtoClass(com.harlan.javagrpc.service.contract.protobuf.Request2.class)
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((integer == null) ? 0 : integer.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((req == null) ? 0 : req.hashCode());
		result = prime * result + ((req2Inner == null) ? 0 : req2Inner.hashCode());
		result = prime * result + ((resps == null) ? 0 : resps.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Request2 other = (Request2) obj;
		if (id != other.id)
			return false;
		if (integer == null) {
			if (other.integer != null)
				return false;
		} else if (!integer.equals(other.integer))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (req == null) {
			if (other.req != null)
				return false;
		} else if (!req.equals(other.req))
			return false;
		if (req2Inner == null) {
			if (other.req2Inner != null)
				return false;
		} else if (!req2Inner.equals(other.req2Inner))
			return false;
		if (resps == null) {
			if (other.resps != null)
				return false;
		} else if (!resps.equals(other.resps))
			return false;
		return true;
	}
	
}
