package com.harlan.javagrpc.main;

import com.harlan.javagrpc.converter.JavaToProto;
import com.harlan.javagrpc.converter.annotation.RemoteAccessEnabled;
import com.harlan.javagrpc.util.ClassGrabberUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Copyright - Lloyd Sparkes 2012.</br>
 * 2019: Updated to reflect proto3 language version.
 * </p>
 * This class simply takes a class/interface, or a set of them given a package, and generate a Protocol Buffers file per class/interface.
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
 * Usage - CLI:
 *   mvn exec:java -Dexec.mainClass=com.harlan.javagrpc.main.JavaToProtoMain <classname/package> [<output folder name>]
 * 
 *   If output file name is not specified the output will be to the console.
 * 
 *   Ensure that the class name or the package is in the class path somewhere.
 * </pre>
 * @author Lloyd Sparkes
 * @author Pablo Fabricio Lettieri <fabri1983@gmail.com>
 */
public class JavaToProtoMain {
	
	/**
	 * Entry Point for the CLI Interface to this Program.
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		// no arguments?
		if (args.length == 0) {
			System.err.println("Usage:");
			System.err.println("\t mvn exec:java -Dexec.mainClass=com.harlan.javagrpc.main.JavaToProtoMain <classname/package> [<output folder name>]");
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
			
			JavaToProto jtp = new JavaToProto(clazz);
			String protobuf = jtp.toString();
			
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
			classes = ClassGrabberUtil.getClassesOrSingleClass(classnameOrPackage, RemoteAccessEnabled.class);
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
