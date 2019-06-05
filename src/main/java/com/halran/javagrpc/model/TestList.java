package com.halran.javagrpc.model;

import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

@ProtoClass(com.harlan.javagrpc.service.contract.protobuf.TestList.class)
public class TestList {
	
	@ProtoField
	private int listId;
	@ProtoField
	private String listName;

	public static TestList from(int listId, String listName) {
		TestList newObj = new TestList();
		newObj.listId = listId;
		newObj.listName = listName;
		return newObj;
	}
	
	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}
	
}
