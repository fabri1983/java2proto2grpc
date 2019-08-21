package org.fabri1983.javagrpc.model;

import org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass;
import org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoField;
import org.fabri1983.javagrpc.service.contract.protobuf.Request2InnerMapObjProto;

@ProtoClass(Request2InnerMapObjProto.class)
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
