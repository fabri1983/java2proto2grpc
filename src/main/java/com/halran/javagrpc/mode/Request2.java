package com.halran.javagrpc.mode;

import java.util.List;

public class Request2 {
//	private int id;
//	private String name;
	private Request2Inner req2Inner;
	private Request req;
//	private List<Integer> integer;
//	private List<Response> resps;
	

	
	public static boolean isJavaClass(Class<?> clz) {    
	    return clz != null && clz.getClassLoader() == null;    
	  }    
	      
	  public static void main(String... args) {    
	    System.out.println(isJavaClass(Integer.class)); // true    
	    System.out.println(isJavaClass(String.class)); // true  
	    System.out.println(isJavaClass(List.class)); // false    
	  }
}
