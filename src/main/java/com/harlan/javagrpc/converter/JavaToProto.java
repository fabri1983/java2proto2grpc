package com.harlan.javagrpc.converter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Copyright - Lloyd Sparkes 2012.</br>
 * 2019: Updated to reflect proto3 language version.
 * </p>
 * LICENSE: Public Domain - do as you wish, just retain this message.</br>
 * I just ask that if you find bugs or improve the code, you raise a push request or an issue, so i can update the code for everyone.</br>
 * DISCLAIMER: I am not responsible for your usage of this code, or for any bugs or issues with this code or any resulting side effects.
 * </p>
 * <pre>
 * Supports:
 *   Nested POJO's
 *   Enums
 *   Arrays/Collections/Lists/Sets (BUT only if they have a type specifier!! (so List<Byte> is supported, List is not supported)
 *   Maps/KeyValuePairs (BUT they need a type specifier!! (so Map<String,Integer> is supported, Map is not supported)
 *   Primitives
 *   Boxed Primitives 
 *   
 * Does Not Support:
 *   Nested Collections e.g. Map<List<String>,String>
 *   Arrays with more than one Dimension
 * 
 * Usage - Code example:
 *   Class<?> clazz = ...;
 *   JavaToProto jpt = new JavaToProto(clazz);
 *   String protoFile = jpt.toString();	
 * </pre>
 * @author Lloyd Sparkes
 * @author Pablo Fabricio Lettieri <fabri1983@gmail.com>
 */

public class JavaToProto {
	
	private static final String NAME = "JavaToProto Generator";
	private static final String VERSION = "v0.2";
	
	// rpc related
	private static final String SERVICE = "service";
	private static final String RPC = "rpc";
	
	// protocol bufffers related
	private static final String OPEN_BLOCK = "{";
	private static final String CLOSE_BLOCK = "}";
	private static final String MESSAGE = "message";
	private static final String ENUM = "enum";
	private static final String NEWLINE = "\n";
	private static final String TAB = "\t";	
	private static final String COMMENT = "//";
	private static final String SPACE = " ";
	private static final String PATH_SEPERATOR = ".";
	private static final String OPTIONAL = "optional";
	private static final String REQUIRED = "required";
	private static final String REPEATED = "repeated";
	private static final String LINE_END = ";";
	
	private StringBuilder builder;
	private Map<Class<?>, String> typesMap = getPrimitivesMap();
	private Stack<Class<?>> classStack = new Stack<Class<?>>();
	private int tabDepth = 0;
	
	/**
	 * Creates a new Instance of JavaToProto to process the given class
	 * @param classToProcess - The Class to be Processed - MUST BE NOT NULL!
	 */
	public JavaToProto(Class<?> classToProcess){
		if(classToProcess == null){
			throw new RuntimeException("You gave me a null class to process. This cannot be done, please pass in an instance of Class.");
		}
		classStack.push(classToProcess);
	}
	
	private Map<Class<?>,String> getPrimitivesMap(){
		Map<Class<?>, String> results = new HashMap<Class<?>, String>(20); // this is just an initial size, will grow during protobuf generation
		
		results.put(double.class, "double");
		results.put(float.class, "float");
		results.put(int.class, "sint32");
		results.put(long.class, "sint64");
		results.put(boolean.class, "bool");
		results.put(Double.class, "double");
		results.put(Float.class, "float");
		results.put(Integer.class, "sint32");
		results.put(Long.class, "sint64");
		results.put(Boolean.class, "bool");
		results.put(String.class, "string");
		results.put(LocalDateTime.class, "Timestamp");
		results.put(LocalDate.class, "Timestamp");
		results.put(LocalTime.class, "Timestamp");
		
		return results;
	}

	public String getTabs(){
		String res = "";
		for(int i = 0; i < tabDepth; i++){
			res = res + TAB;
		}
		return res;
	}
	
	public String getPath(){
		String path = "";
		
		Stack<Class<?>> tStack = new Stack<Class<?>>();
		
		while(!classStack.isEmpty()) {
			Class<?> t = classStack.pop();
			if(path.length() == 0){
				path = t.getSimpleName();
			} else {
				path = t.getSimpleName() + PATH_SEPERATOR + path;
			}
			tStack.push(t);
		}
		
		while(!tStack.isEmpty()){
			classStack.push(tStack.pop());
		}
		
		return path;
	}
	
	public Class<?> currentClass(){
		return classStack.peek();
	}
	
	private void processField(String repeated, String type, String name, int index){
		builder.append(getTabs())
			.append(repeated).append(SPACE).append(type).append(SPACE).append(name).append(SPACE)
			.append("=").append(SPACE).append(index).append(LINE_END).append(NEWLINE);
	}
	
	private void generateProtoFile(){
		builder = new StringBuilder();
		
		// syntax, options, and package
		appendHeader();
		
		generateServiceWithRpcs();
		
		generateMessagesFromMethods();
	}
	
	private void appendHeader() {
		String packageName = currentClass().getPackage().getName();
		String simpleName = currentClass().getSimpleName();
		
		builder.append("syntax = \"proto3\"").append(LINE_END).append(NEWLINE);
		builder.append(NEWLINE);
		// for empty return type and/or parameters
		builder.append("import \"google/protobuf/empty.proto\"").append(LINE_END).append(NEWLINE);
		// for time representation from LocalDateTime, LocalDate and LocalTime classes
		builder.append("import \"google/protobuf/timestamp.proto\"").append(LINE_END).append(NEWLINE);
		builder.append(NEWLINE);
		
		// File Header
		builder.append(COMMENT).append(SPACE).append("Generated by ").append(NAME).append(SPACE)
			.append(VERSION).append(" @ ").append(new Date());
		builder.append(NEWLINE)
			.append(NEWLINE);
		
		builder.append("option java_multiple_files = true").append(LINE_END).append(NEWLINE);
		builder.append("option java_package = \"").append(packageName).append(".protobuf\"").append(LINE_END).append(NEWLINE);
		builder.append("option java_outer_classname = \"").append(simpleName).append("Proto\"").append(LINE_END).append(NEWLINE);
		builder.append(NEWLINE);
		builder.append("package ").append(simpleName).append(LINE_END).append(NEWLINE);
		builder.append(NEWLINE);
	}

	private void generateServiceWithRpcs() {
		String simpleName = currentClass().getSimpleName();
		
		// define the service as the class name
		builder.append(SERVICE).append(SPACE).append(simpleName).append(SPACE).append(OPEN_BLOCK).append(NEWLINE);
		
		tabDepth++;
		
		// define rpc methods
		Method[] methods = currentClass().getDeclaredMethods(); // excludes inherited methods
		for (Method method : methods) {
			if (Modifier.isPrivate(method.getModifiers())) {
				continue;
			}
			builder.append(getTabs());
			
			String methodNameCapitalized = capitalizeFirstChar(method.getName());
			
			String messageIn = "google.protobuf.Empty";
			if (method.getParameterCount() > 0) {
				messageIn = methodNameCapitalized + "MessageIn";
			}
			
			String messageOut = "google.protobuf.Empty";
			if (!method.getReturnType().equals(Void.TYPE)) {
				messageOut = methodNameCapitalized + "MessageOut";
			}
			
			builder.append(RPC).append(SPACE).append(methodNameCapitalized).append(SPACE);
			// parameter type and return type both are wrappers which their message definition is generated afterwards
			builder.append("(").append(messageIn).append(")").append(SPACE).append("returns").append(SPACE)
				.append("(").append(messageOut).append(")").append(SPACE).append("{}").append(LINE_END).append(NEWLINE);
		}
		
		tabDepth--;
		
		builder.append(CLOSE_BLOCK).append(NEWLINE);
		builder.append(NEWLINE);
	}

	private void generateMessagesFromMethods() {
		
		Method[] methods = currentClass().getDeclaredMethods(); // excludes inherited methods
		for (Method method : methods) {
			if (Modifier.isPrivate(method.getModifiers())) {
				continue;
			}
			
			// has any parameter?
			if (method.getParameterCount() > 0) {
			
				String methodNameCapitalized = capitalizeFirstChar(method.getName());
				String messageIn = methodNameCapitalized + "MessageIn";
				
				// open wrapper message for method parameters 
				builder.append(MESSAGE).append(SPACE).append(messageIn).append(SPACE).append(OPEN_BLOCK).append(NEWLINE);
				
				// process all parameter Classes
				tabDepth++;
				for(Class<?> type : method.getParameterTypes()) {
					classStack.push(type);
					buildMessagesFromCurrentClass();
					classStack.pop();
				}
				tabDepth--;
				
				// close wrapper message for method parameters
				builder.append(CLOSE_BLOCK).append(NEWLINE);
			}
			
			// has return type?
			if (!method.getReturnType().equals(Void.TYPE)) {

				String methodNameCapitalized = capitalizeFirstChar(method.getName());
				String messageOut = methodNameCapitalized + "MessageOut";
				
				// open wrapper message for return type
				builder.append(MESSAGE).append(SPACE).append(messageOut).append(SPACE).append(OPEN_BLOCK).append(NEWLINE);
				
				// process return Class
				tabDepth++;
				classStack.push(method.getReturnType());
				buildMessagesFromCurrentClass();
				classStack.pop();
				tabDepth--;
				
				// close wrapper message for return type
				builder.append(CLOSE_BLOCK).append(NEWLINE);
			}
		}
	}
	
	private String buildMessagesFromCurrentClass(){
		
		// TODO elaborate further on next condition, I think is only useful to only process fields
//		if(currentClass().isInterface() || currentClass().isEnum() || Modifier.isAbstract(currentClass().getModifiers())){
//			throw new RuntimeException("A Message cannot be an Interface, Abstract OR an Enum");
//		}
		
		String messageName = currentClass().getSimpleName();
		
		typesMap.put(currentClass(), getPath());
		
		builder.append(getTabs()).append(MESSAGE).append(SPACE).append(messageName).append(OPEN_BLOCK).append(NEWLINE);
		
		tabDepth++;
		
		processFields();
		
		tabDepth--;
		
		builder.append(getTabs()).append(CLOSE_BLOCK).append(NEWLINE);
		
		return messageName;		
	}
	
	private void processFields(){
		Field[] fields = currentClass().getDeclaredFields();
		
		int i = 0;
		for(Field f : fields){
			i++;
			
			int mod = f.getModifiers();
			if(Modifier.isAbstract(mod) || Modifier.isTransient(mod)){
				//Skip this field
				continue;
			}
			
			Class<?> fieldType = f.getType();
			
			//Primitives or Types we have come across before
			if(typesMap.containsKey(fieldType)){
				processField(OPTIONAL,typesMap.get(fieldType), f.getName(), i);
				continue;
			}
			
			if(fieldType.isEnum()){
				processEnum(fieldType);
				processField(OPTIONAL,typesMap.get(fieldType), f.getName(), i);
				continue;
			}
			
			if(Map.class.isAssignableFrom(fieldType)){
				Class<?> innerType = null;
				Class<?> innerType2 = null;
				String entryName = "Entry_"+f.getName();
				
				Type t = f.getGenericType();
				
				if(t instanceof ParameterizedType){
					ParameterizedType tt = (ParameterizedType)t;
					innerType = (Class<?>) tt.getActualTypeArguments()[0];
					innerType2 = (Class<?>) tt.getActualTypeArguments()[1];	
					buildEntryType(entryName, innerType, innerType2);
				}
				
				processField(REPEATED,entryName, f.getName(), i);
				continue;
			}
			
			if(fieldType.isArray()){
				Class<?> innerType = fieldType.getComponentType();
				if(!typesMap.containsKey(innerType)){
					buildNestedType(innerType);
				}
				processField(REPEATED,typesMap.get(fieldType), f.getName(), i);
				continue;
			}
			
			if(Collection.class.isAssignableFrom(fieldType)){
				Class<?> innerType = null;
				
				Type t = f.getGenericType();
				
				if(t instanceof ParameterizedType){
					ParameterizedType tt = (ParameterizedType)t;
					innerType = (Class<?>) tt.getActualTypeArguments()[0];
				}
				
				if(!typesMap.containsKey(innerType)){
					buildNestedType(innerType);
				}
				processField(REPEATED,typesMap.get(fieldType), f.getName(), i);
				continue;
			}
			
			//Ok so not a primitive / scalar, not a map or collection, and we haven't already processed it.
			//So it must be another POJO
			buildNestedType(fieldType);
			processField(REPEATED,typesMap.get(fieldType), f.getName(), i);
		}
	}
	
	private void buildNestedType(Class<?> type){
		classStack.push(type);
		buildMessagesFromCurrentClass();
		classStack.pop();
	}
	
	private void buildEntryType(String name, Class<?> innerType, Class<?> innerType2) {
	
		typesMap.put(currentClass(), getPath());
		
		builder.append(getTabs()).append(MESSAGE).append(SPACE).append(name).append(OPEN_BLOCK).append(NEWLINE);
		
		tabDepth++;
		
		if(!typesMap.containsKey(innerType)){
			buildNestedType(innerType);
			typesMap.remove(innerType);
			typesMap.put(innerType, getPath()+PATH_SEPERATOR+name+PATH_SEPERATOR+innerType.getSimpleName());
		}
		processField(REQUIRED,typesMap.get(innerType), "key", 1);
		
		if(!typesMap.containsKey(innerType2)){
			buildNestedType(innerType2);
			typesMap.remove(innerType2);
			typesMap.put(innerType2, getPath()+PATH_SEPERATOR+name+PATH_SEPERATOR+innerType2.getSimpleName());
		}
		processField(REQUIRED,typesMap.get(innerType2), "value", 2);
		
		tabDepth--;
		
		builder.append(getTabs()).append(CLOSE_BLOCK).append(NEWLINE);
	}

	private void processEnum(Class<?> enumType){
		
		classStack.push(enumType);
		typesMap.put(enumType, getPath());
		classStack.pop();
		
		builder.append(getTabs()).append(ENUM).append(SPACE).append(enumType.getSimpleName()).append(OPEN_BLOCK).append(NEWLINE);
		
		tabDepth++;
		
		int i = 0;
		for(Object e : enumType.getEnumConstants()){
			builder.append(getTabs()).append(e.toString()).append(" = ").append(i).append(LINE_END).append(NEWLINE);
		}
		
		tabDepth--;
		
		builder.append(getTabs()).append(CLOSE_BLOCK).append(NEWLINE);
	}
	
	private String capitalizeFirstChar(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
	
	/**
	 * If the Proto file has not been generated, generate it. Then return it in string format.
	 * @return String - a String representing the proto file representing this class.
	 */
	@Override
	public String toString()
	{
		if(builder == null){
			generateProtoFile();
		}
		return builder.toString();
	}

}
