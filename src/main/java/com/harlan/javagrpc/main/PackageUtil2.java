package com.harlan.javagrpc.main;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PackageUtil2 {
	
	private static Map<String,TreeMap<Integer,String>> map = new HashMap<>();

	public static void main(String[] args) {

		final String packageName = "com.harlan.javagrpc.service.contract";
		final String protoDir = "src/main/proto/";

		List<Class<?>> classes = ClassUtil.getClasses(packageName);
		for (Class<?> clazz : classes) {

			try {
				String name = clazz.getSimpleName();
				StringBuffer sb = new StringBuffer(2048);
				sb.append("syntax = \"proto3\";\r\n");
				sb.append("\r\n");
				sb.append("option java_multiple_files = true;\r\n");
				sb.append("option java_package = \"" + clazz.getPackage().getName() + ".protobuf\";\r\n");
				sb.append("option java_outer_classname = \"" + name + "Proto\";\r\n");
				sb.append("\r\n");
				sb.append("package " + name + ";\r\n");
				sb.append("\r\n");
				sb.append("service " + name + " {\r\n");
				
				//服务
				Method[] methods = clazz.getMethods();
				for (Method method : methods) {
					sb.append("\t" + "rpc ");
					sb.append(method.getName() + " ");
					sb.append("(" + capitalizeFirstChar(method.getName()) + "MessageIn) returns ");
					sb.append("(" + capitalizeFirstChar(method.getName()) + "MessageOut) {};\r\n");
				}
				sb.append("}\r\n");
				
				//messages
				for (Method method : methods) {
					sb.append("message " + capitalizeFirstChar(method.getName()) + "MessageIn {\r\n");
					Class<?>[] reqParam = method.getParameterTypes();
					if(reqParam.length == 1) {
						for (Class<?> cl : reqParam) {
							sb.append("\t" + cl.getSimpleName() + " " + cl.getSimpleName() + " = 1;\r\n");
							TreeMap<Integer,String> tm = new TreeMap<Integer,String>();
							map.put(cl.getName(), tm);
							// process fields
							Field[] fields = cl.getDeclaredFields();
							int i = 1;
							for (Field field : fields) {
								handleField(sb, field, i, tm);
								i++;
							}
						}
					} else if (reqParam.length > 1) {
//						List<String> nameList = new ArrayList<>();
						int count = 0;
						for (Class<?> cl : reqParam) {
//							nameList.add(cl.getSimpleName());
							sb.append("\t" + cl.getSimpleName() + " " + cl.getSimpleName() + " = " + (++count) + ";\r\n");
							TreeMap<Integer, String> tm = new TreeMap<Integer, String>();
							map.put(cl.getName(), tm);
							// process fields
							Field[] fields = cl.getDeclaredFields();
							int i = 1;
							for (Field field : fields) {
								handleField(sb, field, i, tm);
								i++;
							}
//							sb.append("\t}\r\n");
						}
//						for (int i = 0; i <= nameList.size() - 1; i++) {
//							sb.append("\t repeated " + nameList.get(i) + " " + nameList.get(i) + " = " + (i + 1) + ";\r\n");
//						}
					}
					sb.append("}\r\n");
					sb.append("message " + capitalizeFirstChar(method.getName()) + "MessageOut {\r\n");
					Class<?> resClazz = method.getReturnType();
//					sb.append("\t" + resClazz.getSimpleName() + " " + resClazz.getSimpleName() + " = 1;\r\n");
					if(isJavaClass(resClazz)) {
						sb.append("\t" + handleFieldType(resClazz.getSimpleName()) + " " + resClazz.getSimpleName() + " = 1 ;\r\n");
					}else {
						sb.append("\t" + resClazz.getSimpleName() + " " + resClazz.getSimpleName() + " = 1;\r\n");
						TreeMap<Integer,String> tm = new TreeMap<Integer,String>();
						map.put(resClazz.getName(), tm);
						Field[] fields = resClazz.getDeclaredFields();
						int i = 1;
						for (Field field : fields) {
							handleField(sb, field,i,tm);
							i++;
						}
					}
					sb.append("}\r\n");
				}
				sb.append(Map2StringBuffer(map));
				FileWriter writer = new FileWriter(protoDir + name + ".proto");
				writer.write(sb.toString());
				writer.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	private static String capitalizeFirstChar(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	private static StringBuffer Map2StringBuffer(Map<String, TreeMap<Integer, String>> map) {
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, TreeMap<Integer, String>> entry : map.entrySet()) {
			String className = entry.getKey();
			TreeMap<Integer, String> fieldMap = entry.getValue();
			try {
				Class<?> clazz = Class.forName(className);
				sb.append("message " + clazz.getSimpleName() + " {\r\n");
				for (Integer key : fieldMap.keySet()) {
					String field = fieldMap.get(key);
					sb.append(field);
				}
				sb.append("}\r\n");
			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
			}
		}
		return sb;
	}
	
	/**
	 * 判断是否为自定义对象类型
	 * @param clz
	 * @return
	 */
	private static boolean isJavaClass(Class<?> clz) {
		return clz != null && clz.getClassLoader() == null;
	}
	
	private static void handleField(StringBuffer sb, Field field, Integer i, TreeMap<Integer,String> tm) {
		if (field.getType() == Map.class) {
			ParameterizedType pt = (ParameterizedType) field.getGenericType();
			handleGeneric(sb, field, pt.getActualTypeArguments()[0].getTypeName(), i, tm);
			handleGeneric(sb, field, pt.getActualTypeArguments()[1].getTypeName(), i, tm);
//			sb.append("\tmap<" + getGenericByTypeName(pt.getActualTypeArguments()[0].getTypeName())+ ", " + 
//						getGenericByTypeName(pt.getActualTypeArguments()[1].getTypeName()) + "> " + field.getName() + " = "+ i + ";\r\n");
			tm.put(i, "\tmap<" + getGenericByTypeName(pt.getActualTypeArguments()[0].getTypeName())+ ", " + 
						getGenericByTypeName(pt.getActualTypeArguments()[1].getTypeName()) + "> " + field.getName() + " = "+ i + ";\r\n");
		}
		else if (field.getType() == List.class) {
			ParameterizedType pt = (ParameterizedType) field.getGenericType();
			try {
				Class<?> clazz = Class.forName(pt.getActualTypeArguments()[0].getTypeName());
				if(isJavaClass(clazz)) {
					sb.append("\trepeated " + handleFieldType(clazz.getName()) + " " +  field.getName() + " = " + i + ";\r\n");
					return;
				}
//				sb.append("\tmessage " + clazz.getSimpleName() + " { \r\n");
				if (!map.containsKey(clazz.getName())) {
					TreeMap<Integer, String> listTm = new TreeMap<Integer, String>();
					map.put(clazz.getName(), listTm);
					Field[] fields = clazz.getDeclaredFields();
					int j = 1;
					for (Field f : fields) {
						handleField(sb, f, j, listTm);
						j++;
					}
				}
//				sb.append("\t}\r\n");
//				sb.append("\trepeated " + clazz.getSimpleName() + " " + field.getName() + " = " + i + ";\r\n");
				tm.put(i, "\trepeated " + clazz.getSimpleName() + " " + field.getName() + " = " + i + ";\r\n");
			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
			}
		}
		else if (isJavaClass(field.getType())) {
			//sb.append("\t" + handleFieldType(field.getType().getName()) + " " + field.getName() + " = " + i + ";\r\n");
			tm.put(i, "\t" + handleFieldType(field.getType().getName()) + " " + field.getName() + " = " + i + ";\r\n");
			return;
		}
		else {
//			sb.append("\tmessage " + field.getType().getSimpleName() + " { \r\n");
			int j = 1;
			try {
				Class<?> cl = Class.forName(field.getType().getName());
				if(!map.containsKey(cl.getName())) {
					TreeMap<Integer, String> ObjTm = new TreeMap<Integer, String>();
					map.put(cl.getName(), ObjTm);
					Field[] fields = cl.getDeclaredFields();
					for(Field f : fields) {
						handleField(sb, f, j,ObjTm);
						j++;
					}
//					sb.append("\t}\r\n");
				}
//				sb.append("\t" + field.getType().getSimpleName() + " " +field.getName() + " = " + i +";\r\n");
				tm.put(i, "\t" + field.getType().getSimpleName() + " " +field.getName() + " = " + i +";\r\n");
//				System.out.println(field.getType().getName());
//				System.out.println("自定义对象");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static String getGenericByTypeName(String typeName) {
		try {
			Class<?> clazz = Class.forName(typeName);
			if (isJavaClass(clazz)) {
				return handleFieldType(typeName);
			} else {
				return clazz.getSimpleName();
			}
		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
		}
		return null;
	}
	
	private static void handleGeneric(StringBuffer sb, Field field ,String typeName, int i, TreeMap<Integer,String> tm) {
		try {
			Class<?> clazz = Class.forName(typeName);
			if(isJavaClass(clazz)) {
				//sb.append("\trepeated " + clazz.getSimpleName() + " " +  field.getName() + " = " + i + ";\r\n");
				return;
			}
			if(!map.containsKey(clazz.getName())) {
				TreeMap<Integer, String> ObjTm = new TreeMap<Integer, String>();
				map.put(clazz.getName(), ObjTm);
				Field[] fields = clazz.getDeclaredFields();
				int j = 1;
				for(Field f : fields) {
					handleField(sb, f, j,ObjTm);
					j++;
				}
			}
//			sb.append("\tmessage " + clazz.getSimpleName() + " { \r\n");
			
//			sb.append("\t}\r\n");
		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	private static String handleFieldType(String typeName) {
		String returnName = "";
		switch (typeName) {
		case "int":
		case "java.lang.Integer":
			returnName = "int32";
			break;
		case "long":
		case "java.lang.Long":
			returnName = "int64";
			break;
		case "java.lang.String":
			returnName = "string";
			break;
		case "double":
		case "java.lang.Double":
			returnName = "double";
			break;
		case "float":
		case "java.lang.Float":
			returnName = "float";
			break;
		case "boolean":
		case "java.lang.Boolean":
			returnName = "bool";
			break;
		case "byte":
		case "java.lang.Byte":
			returnName = "byte";
			break;
		default:
			break;
		}
		return returnName;
	}

}
