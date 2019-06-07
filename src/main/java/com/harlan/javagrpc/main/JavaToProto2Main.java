package com.harlan.javagrpc.main;

import com.harlan.javagrpc.converter.JavaToProto2;
import com.harlan.javagrpc.converter.annotation.GrpcEnabled;
import com.harlan.javagrpc.util.ClassGrabberUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class JavaToProto2Main {
	
	public static void main(String[] args) throws IOException {

		// no arguments?
		if (args.length == 0) {
			System.err.println("Usage:");
			System.err.println("\t mvn exec:java -Dexec.mainClass=com.harlan.javagrpc.main.JavaToProto2Main <classname/package> [<output folder name>]");
			System.err.println("");
			return;
		}
		
		generateProtobufs(args);
	}
	
	private static void generateProtobufs(String[] args) throws IOException {
		
		List<Class<?>> classes = getClasses(args[0]);
		String outputProtoDir = parseOutputFolder(args[1]);
		if (outputProtoDir != null && !outputProtoDir.isEmpty()) {
			Files.createDirectories(Paths.get(outputProtoDir));
		}
		
		for (Class<?> clazz : classes) {
			
			JavaToProto2 javaToProto2 = new JavaToProto2();
			String protobuf = javaToProto2.getProtobuf(clazz);
			
			// Write to file in output folder?
			if (args.length == 2){
				BufferedWriter out = null;
				try {
					File f = new File(outputProtoDir + clazz.getSimpleName() + ".proto");
					FileWriter fw = new FileWriter(f);
					out = new BufferedWriter(fw);
					out.write(protobuf);
				} catch (Exception e) {
					System.err.println("Got Exception while writing to File.");
					System.err.println(protobuf);
					e.printStackTrace();
				} finally {
					if (out != null) {
						out.flush();
						out.close();
					}
				}
				
			}
			// Write to Console
			else {
				System.out.println(protobuf);
			}
		}
	}
	
	private static List<Class<?>> getClasses(String classnameOrPackage) {
		List<Class<?>> classes = Collections.emptyList();
		
		try {
			classes = ClassGrabberUtil.getClassesOrSingleClass(classnameOrPackage, GrpcEnabled.class);
		} catch (Exception e) {
			System.err.println("Could not load class. Make sure it is in the classpath!");
			e.printStackTrace();
		}
		
		return classes;
	}

	private static String parseOutputFolder(String outputFolder) {
		if (outputFolder.endsWith("/") || outputFolder.endsWith("\\")) {
			return outputFolder;
		}
		return outputFolder + "/";
	}
	
}
