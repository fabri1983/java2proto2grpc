package org.fabri1983.javagrpc.model;

import org.fabri1983.javagrpc.model.converter.type.CorpusEnumStringConverter;
import org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass;
import org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoField;
import org.fabri1983.javagrpc.service.contract.protobuf.ResponseProto;

@ProtoClass(ResponseProto.class)
public class Response {

	@ProtoField(converter = CorpusEnumStringConverter.class)
	private Corpus corpus;
	@ProtoField
	private int id;
	@ProtoField
	private String name;
	
	public static Response from (int id, String name, Corpus corpus) {
		Response newObj = new Response();
		newObj.id = id;
		newObj.name = name;
		newObj.corpus = corpus;
		return newObj;
	}

	public Corpus getCorpus() {
		return corpus;
	}

	public void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
