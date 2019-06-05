package com.halran.javagrpc.model;

import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

@ProtoClass(com.harlan.javagrpc.service.contract.protobuf.TestMap.class)
public class TestMap {

	@ProtoField
	private int mapId;
	@ProtoField
	private String mapName;

	public static TestMap from(int mapId, String mapName) {
		TestMap newObj = new TestMap();
		newObj.mapId = mapId;
		newObj.mapName = mapName;
		return newObj;
	}
	
	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public String getMapName() {
		return mapName;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}
	
}
