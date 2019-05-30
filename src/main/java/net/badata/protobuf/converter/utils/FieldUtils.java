package net.badata.protobuf.converter.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoClasses;
import net.badata.protobuf.converter.resolver.FieldResolver;
import net.badata.protobuf.converter.type.TypeConverter;

/**
 * @author jsjem
 * @author Roman Gushel
 */
public final class FieldUtils {

	private static final String HASSER_PREFIX = "has";
	private static final String GETTER_PREFIX = "get";
	private static final String SETTER_PREFIX = "set";
	private static final String BOOLEAN_GETTER_PREFIX = "is";
	private static final String PROTOBUF_LIST_GETTER_POSTFIX = "List";
	private static final String PROTOBUF_LIST_SETTER_PREFIX = "addAll";

	/**
	 * Check whether field has own mapper.
	 *
	 * @param field Testing field.
	 * @return true if field type has {@link net.badata.protobuf.converter.annotation.ProtoClass}  or
	 * {@link net.badata.protobuf.converter.annotation.ProtoClasses} annotation.
	 */
	public static boolean isComplexType(final Field field) {
		return isComplexType(field.getType());
	}

	/**
	 * Check whether type has own mapper.
	 *
	 * @param type Testing type.
	 * @return true if field type has {@link net.badata.protobuf.converter.annotation.ProtoClass} or
	 * {@link net.badata.protobuf.converter.annotation.ProtoClasses} annotation.
	 */
	public static boolean isComplexType(final Class<?> type) {
		return type.isAnnotationPresent(ProtoClass.class) || type.isAnnotationPresent(ProtoClasses.class);
	}

	/**
	 * Check whether field type implements Collection interface.
	 *
	 * @param field Testing field.
	 * @return true if field type implements {@link java.util.Collection}, otherwise false.
	 */
	public static boolean isCollectionType(final Field field) {
		return isCollectionType(field.getType());
	}

	/**
	 * Check whether class implements Collection interface.
	 *
	 * @param type Testing class.
	 * @return true if class implements {@link java.util.Collection}, otherwise false.
	 */
	public static boolean isCollectionType(final Class<?> type) {
		return Collection.class.isAssignableFrom(type) || type.isArray();
	}

	/**
	 * Create protobuf getter name for domain field.
	 *
	 * @param fieldResolver Domain object field resolver.
	 * @return Protobuf field getter name.
	 */
	public static String createProtobufHasserName(final FieldResolver fieldResolver) {
		if (isCollectionType(fieldResolver.getProtobufType())) {
			return null;
		}
		return StringUtils.createMethodName(HASSER_PREFIX, fieldResolver.getProtobufName());
	}

	/**
	 * Create protobuf getter name for domain field.
	 *
	 * @param fieldResolver Domain object field resolver.
	 * @return Protobuf field getter name.
	 */
	public static String createProtobufGetterName(final FieldResolver fieldResolver) {
		String getterName = StringUtils.createMethodName(GETTER_PREFIX, fieldResolver.getProtobufName());
		if (isCollectionType(fieldResolver.getProtobufType())) {
			return getterName + PROTOBUF_LIST_GETTER_POSTFIX;
		}
		return getterName;
	}

	/**
	 * Create protobuf setter name for domain field.
	 *
	 * @param fieldResolver Domain object field resolver.
	 * @return Protobuf field setter name.
	 */
	public static String createProtobufSetterName(final FieldResolver fieldResolver) {
		if (isCollectionType(fieldResolver.getProtobufType())) {
			return StringUtils.createMethodName(PROTOBUF_LIST_SETTER_PREFIX, fieldResolver.getProtobufName());
		}
		return StringUtils.createMethodName(SETTER_PREFIX, fieldResolver.getProtobufName());
	}

	/**
	 * Create domain field getter name.
	 *
	 * @param fieldResolver Domain object field resolver.
	 * @return Domain field getter name.
	 */
	public static String createDomainGetterName(final FieldResolver fieldResolver) {
		if (fieldResolver.getDomainType() == boolean.class) {
			return StringUtils.createMethodName(BOOLEAN_GETTER_PREFIX, fieldResolver.getDomainName());
		}
		return StringUtils.createMethodName(GETTER_PREFIX, fieldResolver.getDomainName());
	}

	/**
	 * Create domain field setter name.
	 *
	 * @param fieldResolver Domain object field resolver.
	 * @return Domain field setter name.
	 */
	public static String createDomainSetterName(final FieldResolver fieldResolver) {
		return StringUtils.createMethodName(SETTER_PREFIX, fieldResolver.getDomainName());
	}

	/**
	 * Extract parameter type of the collection.
	 *
	 * @param field Field with type derived from {@link java.util.Collection}.
	 * @return Collection generic type.
	 */
	public static Class<?> extractCollectionType(final Field field) {
		if (field.getType().isArray()) {
			return field.getType().getComponentType();
		}
		else {
			ParameterizedType genericType = (ParameterizedType) field.getGenericType();
			return (Class<?>) genericType.getActualTypeArguments()[0];
		}
	}

	/**
	 * Extract protobuf field type from type converter.
	 *
	 * @param typeConverterClass field converter type.
	 * @param defaultType        Default protobuf field type.
	 * @return Protobuf field type declared in the type converter class or default type when it is unable to extract
	 * field type from converter.
	 */
	public static Class<?> extractProtobufFieldType(final Class<? extends TypeConverter<?,?>> typeConverterClass,
			final Class<?> defaultType) {
		Type[] interfaceTypes = typeConverterClass.getGenericInterfaces();
		for (Type interfaceType : interfaceTypes) {
			ParameterizedType parameterizedType = (ParameterizedType) interfaceType;
			if (parameterizedType.getRawType().equals(TypeConverter.class)) {
				Type extractedType = parameterizedType.getActualTypeArguments()[1];
				if (extractedType instanceof ParameterizedType) {
					return (Class<?>) ((ParameterizedType) extractedType).getRawType();
				}
				return Object.class.equals(extractedType) ? defaultType : (Class<?>) extractedType;
			}
		}
		return defaultType;
	}

	private FieldUtils() {
		// empty
	}

	public static boolean doesSetterExpectsArray(FieldResolver fieldResolver) {
		if (fieldResolver.getDomainType().isArray()) {
			return true;
		}
		return false;
	}
}
