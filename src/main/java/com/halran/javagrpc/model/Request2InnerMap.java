package com.halran.javagrpc.model;

import com.harlan.javagrpc.service.contract.protobuf.Request2InnerMapProto;

import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

@ProtoClass(Request2InnerMapProto.class)
public class Request2InnerMap {

	@ProtoField
	private int request2InnerMapId;
	@ProtoField
	private String request2InnerMapName;
	@ProtoField
	private Request2InnerMapObj request2InnerMapObj;

	public int getRequest2InnerMapId() {
		return request2InnerMapId;
	}

	public void setRequest2InnerMapId(int request2InnerMapId) {
		this.request2InnerMapId = request2InnerMapId;
	}

	public String getRequest2InnerMapName() {
		return request2InnerMapName;
	}

	public void setRequest2InnerMapName(String request2InnerMapName) {
		this.request2InnerMapName = request2InnerMapName;
	}

	public Request2InnerMapObj getRequest2InnerMapObj() {
		return request2InnerMapObj;
	}

	public void setRequest2InnerMapObj(Request2InnerMapObj request2InnerMapObj) {
		this.request2InnerMapObj = request2InnerMapObj;
	}

}
