package org.fabri1983.javagrpc.protobuf.converter.utils;

import com.google.protobuf.MessageLite;

import org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass;
import org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClasses;
import org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoField;
import org.fabri1983.javagrpc.protobuf.converter.exception.MappingException;
import org.fabri1983.javagrpc.protobuf.converter.exception.WriteException;
import org.fabri1983.javagrpc.protobuf.converter.inspection.DefaultValue;
import org.fabri1983.javagrpc.protobuf.converter.inspection.NullValueInspector;
import org.fabri1983.javagrpc.protobuf.converter.mapping.Mapper;
import org.fabri1983.javagrpc.protobuf.converter.resolver.FieldResolverFactory;
import org.fabri1983.javagrpc.protobuf.converter.type.TypeConverter;

/**
 * Utilities for extract data stored in the annotations.
 *
 * @author jsjem
 * @author Roman Gushel
 */
public class AnnotationUtils {

	/**
	 * Find {@link org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass ProtoClass} related to {@code protobufClass}.
	 *
	 * @param domainClass   Domain class annotated by {@link org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass
	 *                      ProtoClass}.
	 * @param protobufClass Related Protobuf message class.
	 * @return Instance of {@link org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass ProtoClass} or null if there is
	 * no relation between {@code domainClass} and {@code protobufClass}.
	 */
	public static ProtoClass findProtoClass(final Class<?> domainClass, final Class<? extends MessageLite> protobufClass) {
		if (domainClass.isAnnotationPresent(ProtoClass.class)) {
			return domainClass.getAnnotation(ProtoClass.class);
		} else if (domainClass.isAnnotationPresent(ProtoClasses.class)) {
			ProtoClasses protoClasses = domainClass.getAnnotation(ProtoClasses.class);
			for (ProtoClass protoClass : protoClasses.value()) {
				if (protobufClass.isAssignableFrom(protoClass.value())) {
					return protoClass;
				}
			}
		}
		return null;
	}


	/**
	 * Create {@link org.fabri1983.javagrpc.protobuf.converter.mapping.Mapper Mapper} implementation from class specified in the
	 * {@link org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass ProtoClass} mapper field.
	 *
	 * @param annotation Instance of {@link org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass ProtoClass} annotation.
	 * @return Instance of the {@link org.fabri1983.javagrpc.protobuf.converter.mapping.Mapper Mapper} interface.
	 * @throws MappingException If mapper instance does not contain default constructor or default constructor not
	 *                          public.
	 */
	public static Mapper createMapper(final ProtoClass annotation) throws MappingException {
		try {
			return annotation.mapper().newInstance();
		} catch (InstantiationException e) {
			throw new MappingException("Default constructor not found.");
		} catch (IllegalAccessException e) {
			throw new MappingException("Make default constructor public for "
					+ annotation.mapper().getSimpleName(), e);
		}
	}

	/**
	 * Create {@link org.fabri1983.javagrpc.protobuf.converter.resolver.FieldResolverFactory FieldResolverFactory} implementation
	 * from class specified in the
	 * {@link org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass ProtoClass} fieldFactory field.
	 *
	 * @param annotation Instance of {@link org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass ProtoClass} annotation.
	 * @return Instance of the {@link org.fabri1983.javagrpc.protobuf.converter.resolver.FieldResolverFactory
	 * FieldResolverFactory} interface.
	 * @throws MappingException If field resolver factory implementation does not contain default constructor or
	 *                          default constructor not public.
	 */
	public static FieldResolverFactory createFieldFactory(final ProtoClass annotation) throws MappingException {
		try {
			return annotation.fieldFactory().newInstance();
		} catch (InstantiationException e) {
			throw new MappingException("Default constructor not found.");
		} catch (IllegalAccessException e) {
			throw new MappingException("Make default constructor public for "
					+ annotation.fieldFactory().getSimpleName(), e);
		}
	}

	/**
	 * Create {@link org.fabri1983.javagrpc.protobuf.converter.type.TypeConverter TypeConverter} implementation from class
	 * specified in the {@link org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass ProtoClass} converter field.
	 *
	 * @param annotation Instance of {@link org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass ProtoClass} annotation.
	 * @return Instance of the {@link org.fabri1983.javagrpc.protobuf.converter.type.TypeConverter TypeConverter} interface.
	 * @throws WriteException If converter class does not contain default constructor or default constructor not
	 *                        public.
	 */
	public static TypeConverter<?, ?> createTypeConverter(final ProtoField annotation) throws WriteException {
		try {
			return annotation.converter().newInstance();
		} catch (InstantiationException e) {
			throw new WriteException("Default constructor not found.");
		} catch (IllegalAccessException e) {
			throw new WriteException("Make default constructor public for "
					+ annotation.converter().getSimpleName(), e);
		}
	}

	/**
	 * Create {@link org.fabri1983.javagrpc.protobuf.converter.inspection.NullValueInspector NullValueInspector} implementation
	 * from class specified in the {@link org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass ProtoClass} nullValue
	 * field.
	 *
	 * @param annotation Instance of {@link org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass ProtoClass} annotation.
	 * @return Instance of the {@link org.fabri1983.javagrpc.protobuf.converter.inspection.NullValueInspector NullValueInspector}
	 * interface.
	 * @throws WriteException If null value inspector class does not contain default constructor or default
	 *                        constructor not public.
	 */
	public static NullValueInspector createNullValueInspector(final ProtoField annotation) throws WriteException {
		try {
			return annotation.nullValue().newInstance();
		} catch (InstantiationException e) {
			throw new WriteException("Default constructor not found.");
		} catch (IllegalAccessException e) {
			throw new WriteException("Make default constructor public for "
					+ annotation.nullValue().getSimpleName(), e);
		}
	}

	/**
	 * Create {@link org.fabri1983.javagrpc.protobuf.converter.inspection.DefaultValue DefaultValue} implementation
	 * from class specified in the {@link org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass ProtoClass} nullValue
	 * field.
	 *
	 * @param annotation Instance of {@link org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoClass ProtoClass} annotation.
	 * @return Instance of the {@link org.fabri1983.javagrpc.protobuf.converter.inspection.DefaultValue DefaultValue} interface.
	 * @throws WriteException If default value creator class does not contain default constructor or default
	 *                        constructor is not public.
	 */
	public static DefaultValue createDefaultValue(final ProtoField annotation) throws WriteException {
		try {
			return annotation.defaultValue().newInstance();
		} catch (InstantiationException e) {
			throw new WriteException("Default constructor not found.");
		} catch (IllegalAccessException e) {
			throw new WriteException("Make default constructor public for "
					+ annotation.defaultValue().getSimpleName(), e);
		}
	}
}
