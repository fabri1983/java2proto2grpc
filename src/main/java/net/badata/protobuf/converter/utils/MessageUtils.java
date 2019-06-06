package net.badata.protobuf.converter.utils;

import com.google.protobuf.MessageLite;
import com.google.protobuf.StringValue;

import java.lang.reflect.ParameterizedType;

/**
 * Utilities for extract Protobuf messages .
 *
 * @author jsjem
 * @author Roman Gushel
 */
public class MessageUtils {

	/**
	 * Extract Protobuf message type.
	 *
	 * @param object     Object that contains Protobuf message.
	 * @param methodName getter method name.
	 * @return Class of the Protobuf message.
	 */
	@SuppressWarnings("unchecked")
	public static Class<? extends MessageLite> getMessageType(final Object object, final String methodName) {
		try {
			return (Class<? extends MessageLite>) object.getClass().getMethod(methodName).getReturnType();
		} catch (NoSuchMethodException e) {
			return MessageLite.class;
		}
	}

	/**
	 * Extract Protobuf message type from collection.
	 *
	 * @param object     Object that contains collection of Protobuf messages.
	 * @param methodName getter method name.
	 * @return Class of the Protobuf message.
	 */
	@SuppressWarnings("unchecked")
	public static Class<? extends MessageLite> getMessageCollectionType(final Object object, final String methodName) {
		try {
			ParameterizedType stringListType = (ParameterizedType) object.getClass().getMethod(methodName)
					.getGenericReturnType();
			return (Class<? extends MessageLite>) stringListType.getActualTypeArguments()[0];
		} catch (NoSuchMethodException e) {
			return MessageLite.class;
		}
	}

	/**
	 * Extract Protobuf message types from map. They might not inherit from MessageLite.
	 *
	 * @param object     Object that contains map of Protobuf messages.
	 * @param methodName getter method name.
	 * @return Class of the Protobuf message.
	 */
	public static Class<?>[] getMessageMapTypes(final Object object, final String methodName) {
		try {
			ParameterizedType stringMapType = (ParameterizedType) object.getClass().getMethod(methodName)
					.getGenericReturnType();
			return new Class<?>[] {
				(Class<?>) stringMapType.getActualTypeArguments()[0], 
				(Class<?>) stringMapType.getActualTypeArguments()[1]};
		} catch (NoSuchMethodException e) {
			return new Class<?>[] { StringValue.class, Object.class };
		}
	}
	
}
