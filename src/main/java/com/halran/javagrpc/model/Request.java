package com.halran.javagrpc.model;

import com.halran.javagrpc.model.converter.CorpusEnumStringConverter;

import java.util.List;
import java.util.Map;

import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

@ProtoClass(com.harlan.javagrpc.service.contract.protobuf.Request.class)
public class Request {
	
	@ProtoField(converter = CorpusEnumStringConverter.class)
	private Corpus corpus;
	@ProtoField
	private int i1;
	@ProtoField
	private Integer i2;
	@ProtoField
	private long l1;
	@ProtoField
	private Long l2;
	@ProtoField
	private String s;
	@ProtoField
	private double d1;
	@ProtoField
	private Double d2;
	@ProtoField
	private float f1;
	@ProtoField
	private Float f2;
	@ProtoField
	private boolean b1;
	@ProtoField
	private Boolean b2;
//	@ProtoField
//	private Request2 req2;
	@ProtoField
	private List<String> list1;
	@ProtoField
	private List<TestList> list2;
	@ProtoField
	private Map<String, TestMap> map;
	
	public static Request from(int id, String name, Corpus corpus) {
		Request newObj = new Request();
		newObj.i1 = id;
		newObj.s = name;
		newObj.corpus = corpus;
		return newObj;
	}

	public Corpus getCorpus() {
		return corpus;
	}

	public void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}

	public int getI1() {
		return i1;
	}

	public void setI1(int i1) {
		this.i1 = i1;
	}

	public Integer getI2() {
		return i2;
	}

	public void setI2(Integer i2) {
		this.i2 = i2;
	}

	public long getL1() {
		return l1;
	}

	public void setL1(long l1) {
		this.l1 = l1;
	}

	public Long getL2() {
		return l2;
	}

	public void setL2(Long l2) {
		this.l2 = l2;
	}

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	public double getD1() {
		return d1;
	}

	public void setD1(double d1) {
		this.d1 = d1;
	}

	public Double getD2() {
		return d2;
	}

	public void setD2(Double d2) {
		this.d2 = d2;
	}

	public float getF1() {
		return f1;
	}

	public void setF1(float f1) {
		this.f1 = f1;
	}

	public Float getF2() {
		return f2;
	}

	public void setF2(Float f2) {
		this.f2 = f2;
	}

	public boolean isB1() {
		return b1;
	}

	public void setB1(boolean b1) {
		this.b1 = b1;
	}

	public Boolean getB2() {
		return b2;
	}

	public void setB2(Boolean b2) {
		this.b2 = b2;
	}

//	public Request2 getReq2() {
//		return req2;
//	}
//
//	public void setReq2(Request2 req2) {
//		this.req2 = req2;
//	}

	public List<String> getList1() {
		return list1;
	}

	public void setList1(List<String> list1) {
		this.list1 = list1;
	}

	public List<TestList> getList2() {
		return list2;
	}

	public void setList2(List<TestList> list2) {
		this.list2 = list2;
	}

	public Map<String, TestMap> getMap() {
		return map;
	}

	public void setMap(Map<String, TestMap> map) {
		this.map = map;
	}

}
