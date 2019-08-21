package org.fabri1983.javagrpc.protobuf.converter.domain;

import java.util.List;

import org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoField;

/**
 * @author jsjem
 * @author Roman Gushel
 */
public class IgnoreDomain {


	public static class IgnoreDataTest {

		@ProtoField
		public Object fieldName;
		@ProtoField(name = "protofield")
		public Object protoFieldName;
		@ProtoField(name = "notIgnored")
		public Object notIgnored;
		public Object notProtoField;

	}

	public static class NoIgnoreDataTest {

		@ProtoField(name = "protofield")
		public Object fieldName;
		@ProtoField
		public IgnoreDataTest ignoreClass;
		@ProtoField
		public List<IgnoreDataTest> ignoredCollection;
		public Object notProtoField;

	}
}
