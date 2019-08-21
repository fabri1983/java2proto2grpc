package org.fabri1983.javagrpc.model;

import java.util.List;
import java.util.Map;

import org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass;
import org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoField;
import org.fabri1983.javagrpc.service.contract.protobuf.Request2InnerProto;

@ProtoClass(Request2InnerProto.class)
public class Request2Inner {

	@ProtoField
	private int req2InnerId;
	@ProtoField
	private String req2InnerName;
	@ProtoField
	private List<String> req2InnerString;
	@ProtoField
	private List<Req2InnerList> req2InnerList;
	@ProtoField
	private Map<String, Request2InnerMap> request2InnerMap;

	public int getReq2InnerId() {
		return req2InnerId;
	}

	public void setReq2InnerId(int req2InnerId) {
		this.req2InnerId = req2InnerId;
	}

	public String getReq2InnerName() {
		return req2InnerName;
	}

	public void setReq2InnerName(String req2InnerName) {
		this.req2InnerName = req2InnerName;
	}

	public List<String> getReq2InnerString() {
		return req2InnerString;
	}

	public void setReq2InnerString(List<String> req2InnerString) {
		this.req2InnerString = req2InnerString;
	}

	public List<Req2InnerList> getReq2InnerList() {
		return req2InnerList;
	}

	public void setReq2InnerList(List<Req2InnerList> req2InnerList) {
		this.req2InnerList = req2InnerList;
	}

	public Map<String, Request2InnerMap> getRequest2InnerMap() {
		return request2InnerMap;
	}

	public void setRequest2InnerMap(Map<String, Request2InnerMap> request2InnerMap) {
		this.request2InnerMap = request2InnerMap;
	}

}
