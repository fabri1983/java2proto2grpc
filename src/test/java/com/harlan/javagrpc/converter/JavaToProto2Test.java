package com.harlan.javagrpc.converter;

import com.halran.javagrpc.model.Corpus;
import com.halran.javagrpc.model.Request;
import com.halran.javagrpc.model.Request2;
import com.halran.javagrpc.model.TestList;
import com.halran.javagrpc.model.TestMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.badata.protobuf.converter.Converter;

public class JavaToProto2Test {

	@Test
	public void domainModelToProtoAndViceversaTest() {
		
		Corpus corpus = Corpus.IMAGES;
		int i1 = 11;
		Integer i2 = Integer.valueOf(22);
		long l1 = 11L;
		Long l2 = Long.valueOf(22);
		String s = "a string";
		double d1 = 11d;
		Double d2 = Double.valueOf(22);
		float f1 = 11f;
		Float f2 = Float.valueOf(22);
		boolean b1 = true;
		Boolean b2 = Boolean.TRUE;
		Request2 req2 = Request2.from(123, "req2Name");
		List<String> list1 = Arrays.asList("s1", "s2");
		List<TestList> list2 = Arrays.asList( TestList.from(1, "testList1"), TestList.from(2, "testList2") );
		Map<String, TestMap> map = createMapForTestMap();
		int[] intArray = new int[] { 11, 22 };
		
		Request requestDomain = new Request();
		requestDomain.setCorpus(corpus);
		requestDomain.setI1(i1);
		requestDomain.setI2(i2);
		requestDomain.setL1(l1);
		requestDomain.setL2(l2);
		requestDomain.setS(s);
		requestDomain.setD1(d1);
		requestDomain.setD2(d2);
		requestDomain.setF1(f1);
		requestDomain.setF2(f2);
		requestDomain.setB1(b1);
		requestDomain.setB2( b2);
		requestDomain.setReq2(req2);
		requestDomain.setList1(list1);
		requestDomain.setList2(list2);
		requestDomain.setMap(map);
		requestDomain.setIntArray(intArray);
		
		// convert domain model into protobuf object
		com.harlan.javagrpc.service.contract.protobuf.Request requestProto = Converter.create()
				.toProtobuf(com.harlan.javagrpc.service.contract.protobuf.Request.class, requestDomain);
		
		// convert proto to domain model
		Request requestDomainConverted = Converter.create()
				.toDomain(Request.class, requestProto);
		
		// both objects must be equals
		Assert.assertEquals(requestDomain, requestDomainConverted);
	}
	
	private Map<String, TestMap> createMapForTestMap() {
		TestMap testMap1 = TestMap.from(111, "mapName111");
		TestMap testMap2 = TestMap.from(222, "mapName222");
		Map<String, TestMap> map = new HashMap<>();
		map.put("key1", testMap1);
		map.put("key2", testMap2);
		return map;
	}
	
}
