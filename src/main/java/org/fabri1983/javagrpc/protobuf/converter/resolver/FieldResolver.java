package org.fabri1983.javagrpc.protobuf.converter.resolver;

import java.lang.reflect.Field;

import org.fabri1983.javagrpc.protobuf.converter.inspection.DefaultValue;
import org.fabri1983.javagrpc.protobuf.converter.inspection.NullValueInspector;
import org.fabri1983.javagrpc.protobuf.converter.type.TypeConverter;

/**
 * Resolver for domain field data that necessary for performing conversion.
 *
 * @author jsjem
 * @author Roman Gushel
 */
public interface FieldResolver {

	/**
	 * Getter for {@link java.lang.reflect.Field Field} instance.
	 *
	 * @return domain class field.
	 */
	Field getField();

	/**
	 * Getter for domain class field name.
	 *
	 * @return String with domain field name.
	 */
	String getDomainName();

	/**
	 * Getter for protobuf message field name.
	 *
	 * @return String with protobuf field name.
	 */
	String getProtobufName();

	/**
	 * Getter for domain class field type.
	 *
	 * @return String with domain field type.
	 */
	Class<?> getDomainType();

	/**
	 * Getter for protobuf message field type.
	 *
	 * @return String with protobuf field type.
	 */
	Class<?> getProtobufType();

	/**
	 * Getter for field type converter.
	 *
	 * @return instance of field type converter.
	 */
	TypeConverter<?, ?> getTypeConverter();

	/**
	 * Getter for protobuf field null value inspector.
	 *
	 * @return instance of null value inspector.
	 */
	NullValueInspector getNullValueInspector();

	/**
	 * Getter for domain field default value generator.
	 *
	 * @return instance of default value generator.
	 */
	DefaultValue getDefaultValue();

}
