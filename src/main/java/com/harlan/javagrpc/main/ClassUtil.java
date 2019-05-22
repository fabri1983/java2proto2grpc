package com.harlan.javagrpc.main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class ClassUtil {

	public static List<Class<?>> getClassesOrSingleClass(String s) {
		// treat it as a class
		try {
			Class<?> singleClass = Class.forName(s);
			return Arrays.asList(singleClass);
		} catch (ClassNotFoundException e) {
			// not a class, a missing class, or maybe a package
		}
		// treat it as a package
		return getClasses(s);
	}

	/**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 */
	public static List<Class<?>> getClasses(String packageName) {
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    assert classLoader != null;
	    String path = packageName.replace('.', '/');
	    Enumeration<URL> resources;
		try {
			resources = classLoader.getResources(path);
		} catch (IOException ex) {
			ex.printStackTrace();
			return Collections.emptyList();
		}
	    List<File> dirs = new ArrayList<File>();
	    while (resources.hasMoreElements()) {
	        URL resource = resources.nextElement();
	        dirs.add(new File(resource.getFile()));
	    }
	    List<Class<?>> classes = new ArrayList<Class<?>>();
	    for (File directory : dirs) {
	        classes.addAll(findClasses(directory, packageName));
	    }
	    return classes;
	}

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 */
	public static List<Class<?>> findClasses(File directory, String packageName) {
	    if (!directory.exists()) {
	        return Collections.emptyList();
	    }
	    List<Class<?>> classes = new ArrayList<Class<?>>();
	    File[] files = directory.listFiles();
	    for (File file : files) {
	        if (file.isDirectory()) {
	            assert !file.getName().contains(".");
	            classes.addAll(findClasses(file, packageName + "." + file.getName()));
	        } else if (file.getName().endsWith(".class")) {
	            try {
					classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
				} catch (ClassNotFoundException ex) {
					ex.printStackTrace();
				}
	        }
	    }
	    return classes;
	}
	
}
