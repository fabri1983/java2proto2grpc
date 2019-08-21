package org.fabri1983.javagrpc.testutil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesFromClassLoader {

	private static final Logger log = LoggerFactory.getLogger(PropertiesFromClassLoader.class);
	
	public static Properties getProperties(String propFileName) {
		InputStream inputStream = null;
		Properties props = new Properties();
		
		try {
			inputStream = PropertiesFromClassLoader.class.getClassLoader().getResourceAsStream(propFileName);
			if (inputStream != null) {
				props.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath.");
			}
		} catch (Exception e) {
			log.error("", e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("", e);
				}
			}
		}
		
		return props;
	}
	
}
