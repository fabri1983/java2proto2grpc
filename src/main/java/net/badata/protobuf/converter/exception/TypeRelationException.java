package net.badata.protobuf.converter.exception;

import com.google.protobuf.MessageLite;

/**
 * Exception notifies that protobuf class specified in the {@link net.badata.protobuf.converter.annotation.ProtoClass
 * ProtoClass} is different form the required protobuf class.
 *
 * @author jsjem
 * @author Roman Gushel
 */
public class TypeRelationException extends Exception {

	private static final long serialVersionUID = 9105154711227348207L;

	/**
	 * Constructs a new TypeRelationExcetion with default message.
	 *
	 * @param domainType   domain instance class.
	 * @param protobufType protobuf dto instance class.
	 */
	public TypeRelationException(final Class<?> domainType, final Class<? extends MessageLite> protobufType) {
		super(domainType.getSimpleName() + " is not bound to " + protobufType.getSimpleName());
	}
}
