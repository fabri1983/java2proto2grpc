package org.fabri1983.javagrpc.protobuf.converter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.fabri1983.javagrpc.protobuf.converter.inspection.DefaultNullValueInspectorImpl;
import org.fabri1983.javagrpc.protobuf.converter.inspection.DefaultValue;
import org.fabri1983.javagrpc.protobuf.converter.inspection.NullValueInspector;
import org.fabri1983.javagrpc.protobuf.converter.inspection.SimpleDefaultValueImpl;
import org.fabri1983.javagrpc.protobuf.converter.type.DefaultConverterImpl;
import org.fabri1983.javagrpc.protobuf.converter.type.TypeConverter;

/**
 * Annotation that marks domain class fields that may be converted to Protobuf messages fields.
 *
 * @author jsjem
 * @author Roman Gushel
 */
@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ProtoField {

	/**
	 * Retrieve field name in the protobuf message declaration.
	 *
	 * @return Name of the field in the protobuf dto class or empty string if name matches the name of annotated field.
	 */
	String name() default "";

	/**
	 * Retrieve class that perform field value conversion.
	 *
	 * @return Class for converting field value or DefaultConverterImpl.
	 */
	Class<? extends TypeConverter<?, ?>> converter() default DefaultConverterImpl.class;

	/**
	 * Retrieve class for generating field default value for domain instance if protobuf field is null.
	 *
	 * @return Class for generating default field value.
	 */
	Class<? extends DefaultValue> defaultValue() default SimpleDefaultValueImpl.class;

	/**
	 * Retrieve class that perform protobuf field value check for null.
	 *
	 * @return Class for null check.
	 */
	Class<? extends NullValueInspector> nullValue() default DefaultNullValueInspectorImpl.class;
}
