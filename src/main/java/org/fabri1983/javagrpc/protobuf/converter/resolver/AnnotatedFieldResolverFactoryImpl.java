package org.fabri1983.javagrpc.protobuf.converter.resolver;

import java.lang.reflect.Field;

import org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoField;
import org.fabri1983.javagrpc.protobuf.converter.exception.ConverterException;
import org.fabri1983.javagrpc.protobuf.converter.exception.WriteException;
import org.fabri1983.javagrpc.protobuf.converter.utils.AnnotationUtils;
import org.fabri1983.javagrpc.protobuf.converter.utils.FieldUtils;

/**
 * Implementation of {@link org.fabri1983.javagrpc.protobuf.converter.resolver.FieldResolverFactory FieldResolverFactory} that
 * creates FieldResolver according to data stored in the {@link org.fabri1983.javagrpc.protobuf.converter.annotation.ProtoField
 * ProtoField} annotation.
 *
 * @author jsjem
 * @author Roman Gushel
 */
public class AnnotatedFieldResolverFactoryImpl implements FieldResolverFactory {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FieldResolver createResolver(final Field field) {
		DefaultFieldResolverImpl fieldResolver = new DefaultFieldResolverImpl(field);
		if (field.isAnnotationPresent(ProtoField.class)) {
			try {
				initializeFieldResolver(fieldResolver, field.getAnnotation(ProtoField.class));
			} catch (WriteException e) {
				throw new ConverterException("Can't initialize field resolver", e);
			}
		}
		return fieldResolver;
	}

	private void initializeFieldResolver(final DefaultFieldResolverImpl resolver, final ProtoField annotation) throws
			WriteException {
		if (!"".equals(annotation.name())) {
			resolver.setProtobufName(annotation.name());
		}
		Class<?> protobufType = FieldUtils.extractProtobufFieldType(annotation.converter(), resolver.getProtobufType());
		resolver.setProtobufType(protobufType);
		resolver.setConverter(AnnotationUtils.createTypeConverter(annotation));
		resolver.setNullValueInspector(AnnotationUtils.createNullValueInspector(annotation));
		resolver.setDefaultValue(AnnotationUtils.createDefaultValue(annotation));
	}

}
