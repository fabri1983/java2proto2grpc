package com.harlan.javagrpc.converter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class JavaToProto2 {

	private Map<String,TreeMap<Integer,String>> map;
	
	public String getProtobuf(Class<?> clazz) {

		map = new HashMap<>();
		
		String name = clazz.getSimpleName();
		StringBuffer sb = new StringBuffer(2048);
		
		sb.append("syntax = \"proto3\";\r\n");
		sb.append("\r\n");
		sb.append("import \"google/protobuf/empty.proto\";\r\n"); // for empty return type and/or parameters
		sb.append("import \"google/protobuf/timestamp.proto\";\r\n"); // for time representation from LocalDateTime, LocalDate and LocalTime classes
		sb.append("import \"google/protobuf/duration.proto\";\r\n"); // for duration representation from Duration class
		sb.append("\r\n");
		sb.append("option java_multiple_files = true;\r\n");
		sb.append("option java_package = \"" + clazz.getPackage().getName() + ".protobuf\";\r\n");
		sb.append("option java_outer_classname = \"" + name + "Proto\";\r\n");
		sb.append("\r\n");
		sb.append("package " + name + ";\r\n");
		sb.append("\r\n");
		
		//服务
		Method[] methods = clazz.getDeclaredMethods(); // excludes inherited methods
		
		// sort methods by name and parameters type (in the declared order)
		Arrays.sort(methods, new Comparator<Method>() {
			@Override
			public int compare(Method o1, Method o2) {
				String nameM1 = o1.getName() + appendParameterTypes(o1);
				String nameM2 = o2.getName() + appendParameterTypes(o1);
				return nameM1.compareTo(nameM2);
			}

			private String appendParameterTypes(Method o1) {
				if (o1.getParameterCount() == 0) {
					return "";
				}
				StringBuilder paramStrBuilder = new StringBuilder(64);
				for (Class<?> clazz : o1.getParameterTypes()) {
					paramStrBuilder.append(clazz.getSimpleName());
				}
				return paramStrBuilder.toString();
			}
		});
		
		generateRpcMethods(sb, name, methods);
		
		generateMessages(sb, methods);
		
		// process all messages accumulated in the map
		sb.append(Map2StringBuffer(map));
		
		map.clear();
		
		return sb.toString();
	}

	private void generateRpcMethods(StringBuffer sb, String serviceName, Method[] methods) {
		// Generate map which tells us if a method name is repeated so we can adjust its name when generating rpc methods.
		// The map will contain the method name as key and a counter as value.
		// The values of this map will be modified along the process of rpc methods generation
		Map<String, Integer> methodNameWithCounterMap = generateMethodNameWithCounterMap(methods);
		
		sb.append("service " + serviceName + " {\r\n");
		
		// generate rpc methods
		for (Method method : methods) {
			if (Modifier.isPrivate(method.getModifiers())) {
				continue;
			}
			
			sb.append("\t" + "rpc ");
			String methodName = method.getName();
			String counterSuffix = getCounterAndDecrement(methodName, methodNameWithCounterMap);
			sb.append(methodName + counterSuffix + " ");
			
			String baseMessageName = capitalizeFirstChar(methodName) + counterSuffix;
			
			// parameter type
			String methodParameterType = "google.protobuf.Empty";
			if (method.getParameterCount() > 0) {
				methodParameterType = baseMessageName + "MessageIn";
			}
			
			sb.append("(" + methodParameterType + ") returns ");
			
			// return type
			String methodReturnType = "google.protobuf.Empty";
			if (!method.getReturnType().equals(Void.TYPE)) {
				methodReturnType = baseMessageName + "MessageOut";
			}
			
			sb.append("(" + methodReturnType + ") {};\r\n");
		}
		sb.append("}\r\n");
		sb.append("\r\n");
	}

	private void generateMessages(StringBuffer sb, Method[] methods) {
		// Generate map which tells us if a method name is repeated so we can adjust its name when generating rpc methods.
		// The map will contain the method name as key and a counter as value.
		// The values of this map will be modified along the process of rpc methods generation
		Map<String, Integer> methodNameWithCounterMap = generateMethodNameWithCounterMap(methods);
		
		// process messages for each method's parameters and return types
		for (Method method : methods) {
			if (Modifier.isPrivate(method.getModifiers())) {
				continue;
			}
			
			String methodName = method.getName();
			String counterSuffix = getCounterAndDecrement(methodName, methodNameWithCounterMap);
			String baseMessageName = capitalizeFirstChar(methodName) + counterSuffix;
			
			// has any parameter?
			if (method.getParameterCount() > 0) {
			
				sb.append("message " + baseMessageName + "MessageIn {\r\n");
			
				Parameter[] parameters = method.getParameters();
				if (parameters.length > 0) {
					
					int count = 0;
					for (Parameter parameter : parameters) {
						
						Class<?> cl = parameter.getType();
						String repeatedKeyword = cl.isArray() ? "repeated " : "";
						String classSimpleName = removeArraySymbol(cl.getSimpleName());
						
						// build up param type
						String paramType = getProtobufFieldType(cl);
						if ("".equals(paramType)) {
							paramType = classSimpleName;
						}
						
						// build up a param name because at this level Java does not return the real param name
						String paramName = lowerCaseFirstChar(classSimpleName) + capitalizeFirstChar(parameter.getName());
						sb.append("\t" + repeatedKeyword + paramType + " " + paramName + " = " + (++count) + ";\r\n");
						
						TreeMap<Integer, String> tm = new TreeMap<Integer, String>();
						map.put(removeArraySymbol(cl.getName()), tm);
						
						// process fields
						if (cl.isEnum() || (cl.isArray() && cl.getComponentType().isEnum())) {
							handleEnum(cl);
						} else {
							Field[] fields = cl.getDeclaredFields();
							int i = 1;
							for (Field field : fields) {
								if (isAbstractOrTransient(field)) {
									continue;
								}
								handleField(sb, field, i, tm);
								i++;
							}
						}
					}
				}
				
				sb.append("}\r\n");
			}
			
			// has return type?
			if (!method.getReturnType().equals(Void.TYPE)) {
			
				sb.append("message " + baseMessageName + "MessageOut {\r\n");
				
				Class<?> resClazz = method.getReturnType();
				String repeatedKeyword = resClazz.isArray() ? "repeated " : "";
	
				String classSimpleName = removeArraySymbol(resClazz.getSimpleName());
				String returnType = getProtobufFieldType(resClazz);
				if ("".equals(returnType)) {
					returnType = classSimpleName;
				}
				
				String returnName = lowerCaseFirstChar(removeArraySymbol(classSimpleName));
				sb.append("\t" + repeatedKeyword + returnType + " " + returnName + " = 1;\r\n");
				
				TreeMap<Integer,String> tm = new TreeMap<Integer,String>();
				map.put(removeArraySymbol(resClazz.getName()), tm);
				
				// process fields
				if (resClazz.isEnum() || (resClazz.isArray() && resClazz.getComponentType().isEnum())) {
					handleEnum(resClazz);
				} else {
					Field[] fields = resClazz.getDeclaredFields();
					int i = 1;
					for (Field field : fields) {
						if (isAbstractOrTransient(field)) {
							continue;
						}
						handleField(sb, field, i, tm);
						i++;
					}
				}
				
				sb.append("}\r\n");
			}
		}
	}

	private Map<String, Integer> generateMethodNameWithCounterMap(Method[] methods) {
		Map<String, Integer> methodNameWithCounterMap = new HashMap<>((int)(methods.length / 0.75) + 1);
		Integer zero = Integer.valueOf(0);
		Integer two = Integer.valueOf(2);
		for (Method method : methods) {
			if (Modifier.isPrivate(method.getModifiers())) {
				continue;
			}
			String key = method.getName();
			if (methodNameWithCounterMap.containsKey(key)) {
				// when method name is repeated the counter reflects its occurrences, that's why is changed to 2
				Integer value = methodNameWithCounterMap.get(key);
				if (value.intValue() == 0) { // 0 was the initial value
					methodNameWithCounterMap.put(key, two);
				} else {
					methodNameWithCounterMap.put(key, Integer.valueOf(value.intValue() + 1));
				}
			} else {
				methodNameWithCounterMap.put(key, zero); // zero means not repeated at all
			}
		}
		return methodNameWithCounterMap;
	}
	
	private boolean isAbstractOrTransient(Field field) {
		int mod = field.getModifiers();
		return Modifier.isAbstract(mod) || Modifier.isTransient(mod);
	}

	/**
	 * If method name exists and has counter greater than 0 then returns the actual counter as String.
	 * Otherwise returns empty String.
	 * NOTE: the counter is decremented!
	 * 
	 * @param target
	 * @param methodNameWithCounterMap
	 * @return
	 */
	private String getCounterAndDecrement(String target, Map<String, Integer> methodNameWithCounterMap) {
		Integer currentCounter = methodNameWithCounterMap.get(target);
		// if counter is 0 then return method name
		if (currentCounter.intValue() == 0) {
			return "";
		}
		// return method name + current counter value, then decrement
		else {
			Integer newCounterValue = Integer.valueOf(currentCounter.intValue() - 1);
			methodNameWithCounterMap.put(target, newCounterValue);
			return currentCounter.toString();
		}
	}

	private String capitalizeFirstChar(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	private String lowerCaseFirstChar(String s) {
		return s.substring(0, 1).toLowerCase() + s.substring(1);
	}
	
	private String removeArraySymbol(String s) {
		return s.replace("[]", "");
	}
	
	/**
	 * 判断是否为自定义对象类型
	 * @param clz
	 * @return
	 */
	private boolean isJavaClass(Class<?> clz) {
		return clz != null && !"".equals(getProtobufFieldType(clz));
//		return clz != null && clz.getClassLoader() == null;
	}
	
	private void handleEnum(Class<?> cl) {
		// NOTE: currently Enum types are not being generated since they are defined as string
//		TreeMap<Integer, String> fieldsMap = map.get(cl.getName());
//		// if Enum was already processed then do nothing
//		if (fieldsMap != null && !fieldsMap.isEmpty()) {
//			return;
//		}
//		int i = 0;
//		for(Object e : cl.getEnumConstants()){
//			String enumFieldName = "\t" + e.toString() + " = " + i + ";\r\n";
//			fieldsMap.put(i, enumFieldName);
//			++i;
//		}
	}

	private void handleField(StringBuffer sb, Field field, Integer i, TreeMap<Integer,String> tm) {
		if (Map.class.isAssignableFrom(field.getType())) {
			ParameterizedType pt = (ParameterizedType) field.getGenericType();
			Type[] actualTypeArguments = pt.getActualTypeArguments();
			handleGeneric(sb, field, actualTypeArguments[0].getTypeName(), i, tm);
			handleGeneric(sb, field, actualTypeArguments[1].getTypeName(), i, tm);
			tm.put(i, "\tmap<" + getGenericByTypeName(actualTypeArguments[0].getTypeName())+ ", " + 
						getGenericByTypeName(actualTypeArguments[1].getTypeName()) + "> " + field.getName() + " = "+ i + ";\r\n");
		}
		else if (Collection.class.isAssignableFrom(field.getType())) {
			ParameterizedType pt = (ParameterizedType) field.getGenericType();
			try {
				Type[] actualTypeArguments = pt.getActualTypeArguments();
				Class<?> clazz = Class.forName(actualTypeArguments[0].getTypeName());
				
				if (isJavaClass(clazz)) {
					sb.append("\trepeated " + getProtobufFieldType(clazz) + " " +  field.getName() + " = " + i + ";\r\n");
					return;
				}
				
				if (!map.containsKey(clazz.getName())) {
					TreeMap<Integer, String> listTm = new TreeMap<Integer, String>();
					map.put(removeArraySymbol(clazz.getName()), listTm);
					if (clazz.isEnum() || (clazz.isArray() && clazz.getComponentType().isEnum())) {
						handleEnum(clazz);
					}
					else {
						Field[] fields = clazz.getDeclaredFields();
						int j = 1;
						for (Field f : fields) {
							if (isAbstractOrTransient(field)) {
								continue;
							}
							handleField(sb, f, j, listTm);
							j++;
						}
					}
				}
				
				tm.put(i, "\trepeated " + removeArraySymbol(clazz.getSimpleName()) + " " + field.getName() + " = " + i + ";\r\n");
				
			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
			}
		}
		else if (isJavaClass(field.getType())) {
			String protobufFieldType = getProtobufFieldType(field.getType());
			String repeatedKeyword = field.getType().isArray() ? "repeated " : "";
			tm.put(i, "\t" + repeatedKeyword + protobufFieldType + " " + field.getName() + " = " + i + ";\r\n");
			return;
		}
		// NOTE: currently Enum types are not being generated since they are defined as string
//		else if (field.isEnumConstant()) {
//			handleEnum(field.getType());
//		}
		else {
			int j = 1;
			try {
				Class<?> cl = Class.forName(field.getType().getName());
				String className = removeArraySymbol(cl.getName());
				
				if (!map.containsKey(className)) {
					TreeMap<Integer, String> ObjTm = new TreeMap<Integer, String>();
					map.put(className, ObjTm);
					if (cl.isEnum() || (cl.isArray() && cl.getComponentType().isEnum())) {
						handleEnum(cl);
					} else {
						Field[] fields = cl.getDeclaredFields();
						for(Field f : fields) {
							if (isAbstractOrTransient(field)) {
								continue;
							}
							handleField(sb, f, j,ObjTm);
							j++;
						}
					}
				}
				
				tm.put(i, "\t" + removeArraySymbol(field.getType().getSimpleName()) + " " + field.getName() + " = " + i +";\r\n");
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String getGenericByTypeName(String typeName) {
		try {
			Class<?> clazz = Class.forName(typeName);
			if (isJavaClass(clazz)) {
				return getProtobufFieldType(clazz);
			} else {
				return removeArraySymbol(clazz.getSimpleName());
			}
		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
		}
		return null;
	}
	
	private void handleGeneric(StringBuffer sb, Field field, String typeName, int i, TreeMap<Integer, String> tm) {
		try {
			Class<?> clazz = Class.forName(typeName);
			if(isJavaClass(clazz)) {
				return;
			}
			String className = removeArraySymbol(clazz.getName());
			if(!map.containsKey(className)) {
				TreeMap<Integer, String> ObjTm = new TreeMap<Integer, String>();
				map.put(className, ObjTm);
				if (clazz.isEnum() || (clazz.isArray() && clazz.getComponentType().isEnum())) {
					handleEnum(clazz);
				} else {
					Field[] fields = clazz.getDeclaredFields();
					int j = 1;
					for(Field f : fields) {
						if (isAbstractOrTransient(field)) {
							continue;
						}
						handleField(sb, f, j,ObjTm);
						j++;
					}
				}
			}
		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	private StringBuffer Map2StringBuffer(Map<String, TreeMap<Integer, String>> map) {
			StringBuffer sb = new StringBuffer();
			for (Map.Entry<String, TreeMap<Integer, String>> entry : map.entrySet()) {
				
				String className = entry.getKey();
				
				// for some reason String is saved in the map
				if ("java.lang.String".equals(className) || "String".equals(className)) {
					continue;
				}
				
				TreeMap<Integer, String> fieldMap = entry.getValue();
				try {
					Class<?> clazz = Class.forName(className);
					if (clazz.isEnum()) {
						// NOTE: do not generate enum types since we treat them as string
//						sb.append("enum " + clazz.getSimpleName() + " {\r\n");
//						for (Integer key : fieldMap.keySet()) {
//							String field = fieldMap.get(key);
//							sb.append(field);
//						}
//						sb.append("}\r\n");
					} else {
						sb.append("message " + removeArraySymbol(clazz.getSimpleName()) + " {\r\n");
						for (Integer key : fieldMap.keySet()) {
							String field = fieldMap.get(key);
							sb.append(field);
						}
						sb.append("}\r\n");
					}
				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
				}
			}
			return sb;
		}

	private String getProtobufFieldType(Class<?> clazz) {
		String typeName = clazz.getSimpleName();
		if (clazz.isArray()) {
			typeName = clazz.getComponentType().getSimpleName();
		}
		
		switch (typeName) {
			case "int":
			case "Integer":
			case "java.lang.Integer":
				return "sint32";
			case "long":
			case "Long":
			case "java.lang.Long":
				return "sint64";
			case "String":
			case "java.lang.String":
				return "string";
			case "double":
			case "Double":
			case "java.lang.Double":
				return "double";
			case "float":
			case "Float":
			case "java.lang.Float":
				return "float";
			case "boolean":
			case "Boolean":
			case "java.lang.Boolean":
				return "bool";
			case "byte":
			case "Byte":
			case "java.lang.Byte":
				return "byte";
			case "LocalDateTime":
			case "LocalDate":
			case "LocalTime":
			case "Date":
			case "java.time.LocalDateTime":
			case "java.time.LocalDate":
			case "java.time.LocalTime":
			case "java.util.Date":
				return "google.protobuf.Timestamp";
			case "Duration":
			case "java.time.Duration":
				return "google.protobuf.Duration";
			default:
				break;
		}
		
		// Enum is treated as string
		if (clazz.isEnum() || (clazz.isArray() && clazz.getComponentType().isEnum())) {
			return "string";
		}
		
		return "";
	}
	
}
