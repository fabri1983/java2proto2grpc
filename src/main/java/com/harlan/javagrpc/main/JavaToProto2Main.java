package com.harlan.javagrpc.main;

import com.harlan.javagrpc.converter.JavaToProto2;
import com.harlan.javagrpc.converter.annotation.RemoteAccessEnabled;
import com.harlan.javagrpc.util.ClassGrabberUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JavaToProto2Main {
	
	public static void main(String[] args) throws IOException {

		final String packageName = "com.harlan.javagrpc.service.contract";
		final String protoDir = "src/main/proto/";
		Files.createDirectories(Paths.get(protoDir));
		
		JavaToProto2 javaToProto2 = new JavaToProto2();
		
		List<Class<?>> classes = ClassGrabberUtil.getClasses(packageName, RemoteAccessEnabled.class);
		for (Class<?> clazz : classes) {

			String protobuf = javaToProto2.getProtobuf(clazz);
			
			try {
				String name = clazz.getSimpleName();
				FileWriter writer = new FileWriter(protoDir + name + ".proto");
				writer.write(protobuf);
				writer.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
