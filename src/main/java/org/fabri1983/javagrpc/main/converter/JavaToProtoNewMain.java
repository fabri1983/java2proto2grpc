package org.fabri1983.javagrpc.main.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.fabri1983.javagrpc.converter.JavaToProtoNew;
import org.fabri1983.javagrpc.converter.annotation.GrpcEnabled;
import org.fabri1983.javagrpc.util.ClassGrabberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaToProtoNewMain {
	
	private static final Logger log = LoggerFactory.getLogger(JavaToProtoNewMain.class);
	
	public static void main(String[] args) throws IOException {
		
		// no arguments?
		if (args.length == 0) {
			log.error("Usage:");
			log.error("mvn exec:java -Dexec.mainClass=" + JavaToProtoNewMain.class.getName() + " <classname/package> [<output folder name>]");
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
			
			JavaToProtoNew javaToProto2 = new JavaToProtoNew();
			String protobuf = javaToProto2.getProtobuf(clazz);
			
			if (protobuf == null) {
				log.info(clazz.getSimpleName() + " has no declared methods.");
				continue;
			}
			
			// Write to file in output folder?
			if (args.length == 2){
				BufferedWriter out = null;
				try {
					File f = new File(outputProtoDir + clazz.getSimpleName() + ".proto");
					FileWriter fw = new FileWriter(f);
					out = new BufferedWriter(fw);
					out.write(protobuf);
				} catch (Exception e) {
					log.error("Got Exception while writing to File.", e);
					log.error(protobuf);
				} finally {
					if (out != null) {
						out.flush();
						out.close();
					}
				}
				
			}
			// Write to logger
			else {
				log.info(protobuf);
			}
		}
	}
	
	private static List<Class<?>> getClasses(String classnameOrPackage) {
		List<Class<?>> classes = Collections.emptyList();
		
		try {
			classes = ClassGrabberUtil.getClassesOrSingleClass(classnameOrPackage, GrpcEnabled.class);
		} catch (Exception e) {
			log.error("Could not load class. Make sure it is in the classpath!", e);
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
