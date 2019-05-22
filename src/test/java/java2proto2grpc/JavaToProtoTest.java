package java2proto2grpc;

import com.harlan.javagrpc.converter.JavaToProto;
import com.harlan.javagrpc.service.contract.LoginService;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class JavaToProtoTest {

	@Test
	public void javaInterfaceToProtoTest() {
		Class<?> clazz = LoginService.class;
		JavaToProto jtp = new JavaToProto(clazz);
		String protobuf = jtp.toString();
		
		System.out.println(protobuf);
		
		// I just want to ensure no exception were thrown
		assertTrue(true);
	}
	
}
