package org.fabri1983.javagrpc.model;

import org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass;
import org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoField;
import org.fabri1983.javagrpc.service.contract.protobuf.TestMapProto;

@ProtoClass(TestMapProto.class)
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mapId;
		result = prime * result + ((mapName == null) ? 0 : mapName.hashCode());
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
		TestMap other = (TestMap) obj;
		if (mapId != other.mapId)
			return false;
		if (mapName == null) {
			if (other.mapName != null)
				return false;
		} else if (!mapName.equals(other.mapName))
			return false;
		return true;
	}
	
}
