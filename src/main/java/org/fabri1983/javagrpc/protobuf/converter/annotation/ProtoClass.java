package org.fabri1983.javagrpc.protobuf.converter.annotation;

import com.google.protobuf.MessageLite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.fabri1983.javagrpc.protobuf.converter.mapping.DefaultMapperImpl;
import org.fabri1983.javagrpc.protobuf.converter.mapping.Mapper;
import org.fabri1983.javagrpc.protobuf.converter.resolver.AnnotatedFieldResolverFactoryImpl;
import org.fabri1983.javagrpc.protobuf.converter.resolver.FieldResolverFactory;

/**
 * Annotation that marks domain class that may be converted to Protobuf messages.
 *
 * @author jsjem
 * @author Roman Gushel
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ProtoClass {

	/**
	 * Retrieve related protobuf class.
	 *
	 * @return Class that represents protobuf dto.
	 */
	Class<? extends MessageLite> value();

	/**
	 * Retrieve class that perform data mapping between domain and protobuf instances.
	 *
	 * @return Class for mapping data.
	 */
	Class<? extends Mapper> mapper() default DefaultMapperImpl.class;

	/**
	 * Retrieve factory class that creates resolvers for domain class fields.
	 *
	 * @return implementation of {@link org.fabri1983.javagrpc.protobuf.converter.resolver.FieldResolverFactory
	 * FieldResolverFactory}
	 */
	Class<? extends FieldResolverFactory> fieldFactory() default AnnotatedFieldResolverFactoryImpl.class;
}
