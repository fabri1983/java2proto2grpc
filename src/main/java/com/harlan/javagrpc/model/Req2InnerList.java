package com.harlan.javagrpc.model;

import com.harlan.javagrpc.service.contract.protobuf.Req2InnerListProto;

import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

@ProtoClass(Req2InnerListProto.class)
public class Req2InnerList {
	
	@ProtoField
	private int req2InnerListId;
	@ProtoField
	private String req2InnerListName;
	@ProtoField
	private Req2innerListObj req2innerListObj;

	public int getReq2InnerListId() {
		return req2InnerListId;
	}

	public void setReq2InnerListId(int req2InnerListId) {
		this.req2InnerListId = req2InnerListId;
	}

	public String getReq2InnerListName() {
		return req2InnerListName;
	}

	public void setReq2InnerListName(String req2InnerListName) {
		this.req2InnerListName = req2InnerListName;
	}

	public Req2innerListObj getReq2innerListObj() {
		return req2innerListObj;
	}

	public void setReq2innerListObj(Req2innerListObj req2innerListObj) {
		this.req2innerListObj = req2innerListObj;
	}
	
}
