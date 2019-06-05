package net.badata.protobuf.converter.utils;

import java.util.AbstractList;

public enum ProtobufInnerTypes {

	BooleanArrayList,
	ByteStringListView,
	DoubleArrayList,
	FloatArrayList,
	IntArrayList,
	LongArrayList;
	
	@SuppressWarnings("unchecked")
	public static <T> AbstractList<T> castToAbstractList(Object target) {
		return (AbstractList<T>) target;
	}
	
}
