package org.fabri1983.javagrpc.model;

import org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass;
import org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoField;
import org.fabri1983.javagrpc.service.contract.protobuf.Req2innerListObjProto;

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
