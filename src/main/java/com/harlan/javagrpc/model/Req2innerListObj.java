package com.harlan.javagrpc.model;

import com.harlan.javagrpc.service.contract.protobuf.Req2innerListObjProto;

import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

@ProtoClass(Req2innerListObjProto.class)
public class Req2innerListObj {
	
	@ProtoField
	private int req2innerListObjId;
	@ProtoField
	private String req2innerListObjName;

	public int getReq2innerListObjId() {
		return req2innerListObjId;
	}

	public void setReq2innerListObjId(int req2innerListObjId) {
		this.req2innerListObjId = req2innerListObjId;
	}

	public String getReq2innerListObjName() {
		return req2innerListObjName;
	}

	public void setReq2innerListObjName(String req2innerListObjName) {
		this.req2innerListObjName = req2innerListObjName;
	}

}
