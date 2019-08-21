package org.fabri1983.javagrpc.protobuf.converter.type;

/**
 * Implementation of {@link org.fabri1983.javagrpc.protobuf.converter.type.TypeConverter TypeConverter} that is
 * applied by default.
 *
 * This implementation does not perform field data conversion.
 *
 * @author jsjem
 * @author Roman Gushel
 */
public class DefaultConverterImpl implements TypeConverter<Object, Object> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object toDomainValue(final Object instance) {
		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object toProtobufValue(final Object instance) {
		return instance;
	}
}
