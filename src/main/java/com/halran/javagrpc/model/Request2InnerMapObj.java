package com.halran.javagrpc.model;

import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

@ProtoClass(com.harlan.javagrpc.service.contract.protobuf.Request2InnerMapObj.class)
public class Request2InnerMapObj {

	@ProtoField
	private int request2InnerMapObjId;
	@ProtoField
	private String request2InnerMapObjName;

	public int getRequest2InnerMapObjId() {
		return request2InnerMapObjId;
	}

	public void setRequest2InnerMapObjId(int request2InnerMapObjId) {
		this.request2InnerMapObjId = request2InnerMapObjId;
	}

	public String getRequest2InnerMapObjName() {
		return request2InnerMapObjName;
	}

	public void setRequest2InnerMapObjName(String request2InnerMapObjName) {
		this.request2InnerMapObjName = request2InnerMapObjName;
	}

}
