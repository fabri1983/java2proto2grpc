package com.harlan.javagrpc.converter;

import com.halran.javagrpc.model.Corpus;
import com.halran.javagrpc.model.Request;
import com.halran.javagrpc.model.Request2;
import com.halran.javagrpc.model.TestList;
import com.halran.javagrpc.model.TestMap;
import com.harlan.javagrpc.service.contract.protobuf.RequestProto;

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
		RequestProto requestProto = Converter.create().toProtobuf(RequestProto.class, requestDomain);
		
		// convert proto to domain model
		Request requestDomainConverted = Converter.create()
				.toDomain(Request.class, requestProto);
		
		// both objects must be equals
		boolean comparisonResult = compareRequest(requestDomain, requestDomainConverted);
		Assert.assertTrue("Both request must contain same data.", comparisonResult);
	}
	
	private boolean compareRequest(Request req1, Request req2) {
		if (!req1.getCorpus().equals(req2.getCorpus())) {
			return false;
		}
		if (req1.getI1() != req2.getI1()) {
			return false;
		}
		if (!req1.getI2().equals(req2.getI2())) {
			return false;
		}
		if (req1.getL1() != req2.getL1()) {
			return false;
		}
		if (!req1.getL2().equals(req2.getL2())) {
			return false;
		}
		if (!req1.getS().equals(req2.getS())) {
			return false;
		}
		if (req1.getD1() != req2.getD1()) {
			return false;
		}
		if (!req1.getD2().equals(req2.getD2())) {
			return false;
		}
		if (req1.getF1() != req2.getF1()) {
			return false;
		}
		if (!req1.getF2().equals(req2.getF2())) {
			return false;
		}
		if (req1.isB1() != req2.isB1()) {
			return false;
		}
		if (!req1.getB2().equals(req2.getB2())) {
			return false;
		}
		if (!compareRequest2(req1.getReq2(), req2.getReq2())) {
			return false;
		}
		if (!compareList(req1.getList1(), req2.getList1())) {
			return false;
		}
		if (!compareList(req1.getList2(), req2.getList2())) {
			return false;
		}
		if (!compareMap(req1.getMap(), req2.getMap())) {
			return false;
		}
		if (!compareIntArray(req1.getIntArray(), req2.getIntArray())) {
			return false;
		}
		return true;
	}

	private boolean compareRequest2(Request2 req1, Request2 req2) {
		if (req1.getId() != req2.getId()) {
			return false;
		}
		if (!req1.getName().equals(req2.getName())) {
			return false;
		}
		return true;
	}

	private <T> boolean compareList(List<T> list1, List<T> list2) {
		for (int i=0, c=list1.size(); i < c; ++i) {
			T value1 = list1.get(i);
			T value2 = list2.get(i);
			if (!value1.equals(value2)) {
				return false;
			}
		}
		return true;
	}

	private <K, V> boolean compareMap(Map<K, V> map1, Map<K, V> map2) {
		for (K key : map1.keySet()) {
			V value1 = map1.get(key);
			V value2 = map2.get(key);
			if (!value1.equals(value2)) {
				return false;
			}
		}
		return true;
	}

	private boolean compareIntArray(int[] intArray1, int[] intArray2) {
		for (int i=0, c=intArray1.length; i < c; ++i) {
			if (intArray1[i] != intArray2[i]) {
				return false;
			}
		}
		return true;
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
