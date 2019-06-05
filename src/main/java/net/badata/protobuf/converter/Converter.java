package net.badata.protobuf.converter;

import com.google.protobuf.MessageLite;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.exception.ConverterException;
import net.badata.protobuf.converter.exception.MappingException;
import net.badata.protobuf.converter.exception.TypeRelationException;
import net.badata.protobuf.converter.exception.WriteException;
import net.badata.protobuf.converter.mapping.Mapper;
import net.badata.protobuf.converter.mapping.MappingResult;
import net.badata.protobuf.converter.resolver.FieldResolver;
import net.badata.protobuf.converter.resolver.FieldResolverFactory;
import net.badata.protobuf.converter.utils.AnnotationUtils;
import net.badata.protobuf.converter.utils.FieldUtils;
import net.badata.protobuf.converter.utils.MessageUtils;
import net.badata.protobuf.converter.writer.DomainWriter;
import net.badata.protobuf.converter.writer.ProtobufWriter;

/**
 * Converts data from Protobuf messages to domain model objects and vice versa.
 *
 * @author jsjem
 * @author Roman Gushel
 */
public final class Converter {

	private final Configuration configuration;

	/**
	 * Create default converter.
	 *
	 * @return Converter instance.
	 */
	public static Converter create() {
		return create(Configuration.builder().build());
	}

	/**
	 * Create converter with map of ignored fields.
	 *
	 * @param fieldsIgnore Map of fields that has to be ignored by this converter instance.
	 * @return new Converter instance.
	 * @deprecated use {@code create(Configuration)} instead.
	 */
	@Deprecated
	public static Converter create(final FieldsIgnore fieldsIgnore) {
		Configuration.Builder configurationBuilder = Configuration.builder();
		configurationBuilder.setIgnoredFields(fieldsIgnore);
		return new Converter(configurationBuilder.build());
	}

	/**
	 * Create configured converter.
	 *
	 * @param configuration Parameters for conversion.
	 * @return new Converter instance.
	 */
	public static Converter create(final Configuration configuration) {
		return new Converter(configuration);
	}

	/**
	 * Create object that performing conversion from protobuf object to domain model object and vice versa.
	 *
	 * @param configuration Parameters for conversion.
	 */
	private Converter(final Configuration configuration) {
		if (configuration == null) {
			throw new IllegalArgumentException("Argument configuration can't be null");
		}
		this.configuration = configuration;
	}

	/**
	 * Create domain object list from Protobuf dto list.
	 *
	 * @param domainClass        Expected domain object type.
	 * @param protobufCollection Source instance of Protobuf dto collection.
	 * @param <T>                Domain type.
	 * @param <E>                Protobuf dto type.
	 * @return Domain objects list filled with data stored in the Protobuf dto list.
	 */
	@SuppressWarnings("unchecked")
	public <T, E extends MessageLite> List<T> toDomain(final Class<T> domainClass, final Collection<E>
			protobufCollection) {
		return toDomain(List.class, domainClass, protobufCollection);

	}

	@SuppressWarnings("unchecked")
	private <T, E extends MessageLite, K extends Collection<?>> K toDomain(final Class<K> collectionClass,
			final Class<T> domainClass, final Collection<E> protobufCollection) {
		Collection<T> domainList = List.class.isAssignableFrom(collectionClass) ? new ArrayList<T>() : new
				HashSet<T>();
		for (E protobuf : protobufCollection) {
			domainList.add(toDomain(domainClass, protobuf));
		}
		return (K) domainList;
	}

	/**
	 * Create domain object from Protobuf dto.
	 *
	 * @param domainClass Expected domain object type.
	 * @param protobuf    Source instance of Protobuf dto bounded to domain.
	 * @param <T>         Domain type.
	 * @param <E>         Protobuf dto type.
	 * @return Domain instance filled with data stored in the Protobuf dto.
	 */
	public <T, E extends MessageLite> T toDomain(final Class<T> domainClass, final E protobuf) {
		if (protobuf == null) {
			return null;
		}
		T domain = createDomain(domainClass);
		ProtoClass protoClass = testDataBinding(domain.getClass(), protobuf.getClass());
		try {
			fillDomain(domain, protobuf, protoClass);
			return domain;
		} catch (MappingException e) {
			throw new ConverterException("Field mapping error", e);
		} catch (WriteException e) {
			throw new ConverterException("Domain field value setting error", e);
		}
	}

	private ProtoClass testDataBinding(final Class<?> domainClass, final Class<? extends MessageLite> protobufClass) {
		ProtoClass protoClassAnnotation = AnnotationUtils.findProtoClass(domainClass, protobufClass);
		if (protoClassAnnotation == null) {
			throw new ConverterException(new TypeRelationException(domainClass, protobufClass));
		}
		return protoClassAnnotation;
	}

	private <T> T createDomain(final Class<T> domainClass) {
		try {
			return domainClass.newInstance();
		} catch (InstantiationException e) {
			throw new ConverterException("Default constructor not found for " + domainClass.getSimpleName(), e);
		} catch (IllegalAccessException e) {
			throw new ConverterException("Make default constructor of " + domainClass.getSimpleName() + " public", e);
		}
	}

	private <E extends MessageLite> void fillDomain(final Object domain, final E protobuf,
			final ProtoClass protoClassAnnotation) throws MappingException, WriteException {
		Class<?> domainClass = domain.getClass();
		Mapper fieldMapper = AnnotationUtils.createMapper(protoClassAnnotation);
		FieldResolverFactory fieldFactory = AnnotationUtils.createFieldFactory(protoClassAnnotation);
		for (Field field : getDomainFields(domainClass)) {
			if (configuration.getIgnoredFields().ignored(field)) {
				continue;
			}
			FieldResolver fieldResolver = fieldFactory.createResolver(field);
			fillDomainField(fieldResolver, fieldMapper.mapToDomainField(fieldResolver, protobuf, domain));
		}
	}

	private List<Field> getDomainFields(final Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();

		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

		if (configuration.withInheritedFields()) {
			Class<?> superClazz = clazz.getSuperclass();
			if (superClazz != null) {
				fields.addAll(getDomainFields(superClazz));
			}
		}
		return fields;
	}

	private void fillDomainField(final FieldResolver fieldResolver, final MappingResult mappingResult)
			throws WriteException {
		DomainWriter fieldWriter = new DomainWriter(mappingResult.getDestination());
		Object mappedValue = mappingResult.getValue();
		switch (mappingResult.getCode()) {
			case NESTED_MAPPING:
				fieldWriter.write(fieldResolver, createNestedConverter().toDomain(fieldResolver.getDomainType(),
						(MessageLite) mappedValue));
				break;
			case COLLECTION_MAPPING:
				Class<?> collectionType = FieldUtils.extractCollectionType(fieldResolver.getField());
				if (FieldUtils.isComplexType(collectionType)) {
					mappedValue = createDomainValueList(collectionType, mappedValue);
				}
				fieldWriter.write(fieldResolver, mappedValue);
				break;
			case MAP_MAPPING:
//				Class<?>[] mapTypes = FieldUtils.extractMapTypes(fieldResolver.getField());
//				boolean isKeyComplex = FieldUtils.isComplexType(mapTypes[0]);
//				boolean isValueComplex = FieldUtils.isComplexType(mapTypes[1]);
//				// process 4 types of combination between key and value classes
//				if (isKeyComplex && isValueComplex) {
//					Class<?> keyClass = mapTypes[0];
//					Class<?> valueClass = mapTypes[1];
//					mappedValue = createDomainValueMap1(keyClass, valueClass, mappedValue);
//				}
//				else if (isKeyComplex && !isValueComplex) {
//					Class<?> keyClass = mapTypes[0];
//					mappedValue = createDomainValueMap2(keyClass, mappedValue);
//				}
//				else if (!isKeyComplex && isValueComplex) {
//					Class<?> valueClass = mapTypes[1];
//					mappedValue = createDomainValueMap3(valueClass, mappedValue);
//				}
//				if (!isKeyComplex && !isValueComplex) {
					mappedValue = createDomainValueMap4(mappedValue);
//				}
				fieldWriter.write(fieldResolver, mappedValue);
				break;
			case MAPPED:
			default:
				fieldWriter.write(fieldResolver, mappedValue);
		}
	}

	private Converter createNestedConverter() {
		return create(configuration);
	}

	@SuppressWarnings("unchecked")
	private <T> List<T> createDomainValueList(final Class<T> type, final Object protobufCollection) {
		return createNestedConverter().toDomain(type, (List<? extends MessageLite>) protobufCollection);
	}

	@SuppressWarnings("unchecked")
	private <K extends MessageLite, V extends MessageLite> Map<?, ?> createDomainValueMap1(
			final Class<?> keyClass, final Class<?> valueClass, final Object mappedValue) {
		
		final Map<K, V> protobufMap = (Map<K, V>) mappedValue;
		if (protobufMap == null) {
			return new HashMap<>(1);
		}
		
		Map<Object, Object> domainMap = new HashMap<>((int)(protobufMap.entrySet().size() / 0.75) + 1);
		
		for (Iterator<?> it = protobufMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = (Entry<K, V>) it.next();
			Object domainKey = toDomain(keyClass, entry.getKey());
			Object domainValue = toDomain(valueClass, entry.getValue());
			domainMap.put(domainKey, domainValue);
		}
		
		return domainMap;
	}

	@SuppressWarnings("unchecked")
	private <K extends MessageLite, V> Map<?, V> createDomainValueMap2(final Class<?> keyClass, 
			final Object mappedValue) {
		
		final Map<K, V> protobufMap = (Map<K, V>) mappedValue;
		if (protobufMap == null) {
			return new HashMap<>(1);
		}
		
		Map<Object, V> domainMap = new HashMap<>((int)(protobufMap.entrySet().size() / 0.75) + 1);
		
		for (Iterator<?> it = protobufMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = (Entry<K, V>) it.next();
			Object domainKey = toDomain(keyClass, entry.getKey());
			V domainValue = entry.getValue();
			domainMap.put(domainKey, domainValue);
		}
		
		return domainMap;
	}
	
	@SuppressWarnings("unchecked")
	private <K, V extends MessageLite> Map<K, ?> createDomainValueMap3(final Class<?> valueClass, 
			final Object mappedValue) {
		
		final Map<K, V> protobufMap = (Map<K, V>) mappedValue;
		if (protobufMap == null) {
			return new HashMap<>(1);
		}
		
		Map<K, Object> domainMap = new HashMap<>((int)(protobufMap.entrySet().size() / 0.75) + 1);
		
		for (Iterator<?> it = protobufMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = (Entry<K, V>) it.next();
			K domainKey = entry.getKey();
			Object domainValue = toDomain(valueClass, entry.getValue());
			domainMap.put(domainKey, domainValue);
		}
		
		return domainMap;
	}
	
	@SuppressWarnings("unchecked")
	private <K, V> Map<K, V> createDomainValueMap4(final Object mappedValue) {
		
		final Map<K, V> protobufMap = (Map<K, V>) mappedValue;
		if (protobufMap == null) {
			return new HashMap<>(1);
		}
		
		Map<K, V> domainMap = new HashMap<>((int)(protobufMap.entrySet().size() / 0.75) + 1);
		
		for (Iterator<?> it = protobufMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = (Entry<K, V>) it.next();
			K domainKey = entry.getKey();
			V domainValue = entry.getValue();
			domainMap.put(domainKey, domainValue);
		}
		
		return domainMap;
	}
	
	/**
	 * Create Protobuf dto list from domain object list.
	 *
	 * @param protobufClass    Expected Protobuf class.
	 * @param domainCollection Source domain collection.
	 * @param <T>              Domain type.
	 * @param <E>              Protobuf dto type.
	 * @return Protobuf dto list filled with data stored in the domain object list.
	 */
	@SuppressWarnings("unchecked")
	public <T, E extends MessageLite> List<E> toProtobuf(final Class<E> protobufClass, final Collection<T>
			domainCollection) {
		return toProtobuf(List.class, protobufClass, domainCollection);
	}

	@SuppressWarnings("unchecked")
	private <T, E extends MessageLite, K extends Collection<?>> K toProtobuf(final Class<K> collectionClass,
			final Class<E> protobufClass, final Collection<T> domainCollection) {
		Collection<E> protobufCollection = List.class.isAssignableFrom(collectionClass) ? 
				new ArrayList<E>() : new HashSet<E>();
		if (domainCollection != null) {
			for (T domain : domainCollection) {
				protobufCollection.add(toProtobuf(protobufClass, domain));
			}
		}
		return (K) protobufCollection;
	}

	/**
	 * Create Protobuf dto from domain object.
	 *
	 * @param protobufClass Expected Protobuf class.
	 * @param domain        Source domain instance to which protobufClass is bounded.
	 * @param <T>           Domain type.
	 * @param <E>           Protobuf dto type.
	 * @return Protobuf dto filled with data stored in the domain object.
	 */
	@SuppressWarnings("unchecked")
	public <T, E extends MessageLite> E toProtobuf(final Class<E> protobufClass, final T domain) {
		if (domain == null) {
			return null;
		}
		E.Builder protobuf = createProtobuf(protobufClass);
		ProtoClass protoClass = testDataBinding(domain.getClass(), protobufClass);
		try {
			fillProtobuf(protobuf, domain, protoClass);
			return (E) protobuf.build();
		} catch (MappingException e) {
			throw new ConverterException("Field mapping error", e);
		} catch (WriteException e) {
			throw new ConverterException("Protobuf field value setting error", e);
		}
	}

	private <E extends MessageLite> E.Builder createProtobuf(final Class<E> protobufClass) {
		try {
			return (E.Builder) protobufClass.getDeclaredMethod("newBuilder").invoke(null);
		} catch (IllegalAccessException e) {
			throw new ConverterException("Can't access 'newBuilder()' method for " + protobufClass.getName(), e);
		} catch (InvocationTargetException e) {
			throw new ConverterException("Can't instantiate protobuf builder for " + protobufClass.getName(), e);
		} catch (NoSuchMethodException e) {
			throw new ConverterException("Method 'newBuilder()' not found in " + protobufClass.getName(), e);
		}
	}

	private <E extends MessageLite.Builder> void fillProtobuf(final E protobuf, final Object domain,
			final ProtoClass protoClassAnnotation) throws MappingException, WriteException {
		Class<?> domainClass = domain.getClass();
		Mapper fieldMapper = AnnotationUtils.createMapper(protoClassAnnotation);
		FieldResolverFactory fieldFactory = AnnotationUtils.createFieldFactory(protoClassAnnotation);
		for (Field field : getDomainFields(domainClass)) {
			if (configuration.getIgnoredFields().ignored(field)) {
				continue;
			}
			FieldResolver fieldResolver = fieldFactory.createResolver(field);
			fillProtobufField(fieldResolver, fieldMapper.mapToProtobufField(fieldResolver, domain, protobuf));
		}
	}

	@SuppressWarnings("unchecked")
	private void fillProtobufField(final FieldResolver fieldResolver, final MappingResult mappingResult)
			throws WriteException {
		ProtobufWriter fieldWriter = new ProtobufWriter((MessageLite.Builder) mappingResult.getDestination());
		Object mappedValue = mappingResult.getValue();
		switch (mappingResult.getCode()) {
			case NESTED_MAPPING:
				Class<? extends MessageLite> protobufClass = MessageUtils.getMessageType(mappingResult.getDestination(),
						FieldUtils.createProtobufGetterName(fieldResolver));
				fieldWriter.write(fieldResolver, createNestedConverter().toProtobuf(protobufClass, mappedValue));
				break;
			case COLLECTION_MAPPING:
				Class<?> collectionType = FieldUtils.extractCollectionType(fieldResolver.getField());
				if (FieldUtils.isComplexType(collectionType)) {
					Class<? extends MessageLite> protobufCollectionClass = MessageUtils.getMessageCollectionType(
							mappingResult.getDestination(), FieldUtils.createProtobufGetterName(fieldResolver));
					mappedValue = createProtobufValueList(protobufCollectionClass, fieldResolver.getDomainType(),
							(Collection<?>) mappedValue);
				}
				fieldWriter.write(fieldResolver, mappedValue);
				break;
			case MAP_MAPPING:
				Class<?>[] mapTypes = MessageUtils.getMessageMapTypes(mappingResult.getDestination(), 
						FieldUtils.createProtobufGetterName(fieldResolver));
				boolean isKeyComplex = FieldUtils.isComplexType(mapTypes[0]);
				boolean isValueComplex = FieldUtils.isComplexType(mapTypes[1]);
				// process 4 types of combination between key and value classes
				if (isKeyComplex && isValueComplex) {
					Class<? extends MessageLite> keyClass = (Class<? extends MessageLite>) mapTypes[0];
					Class<? extends MessageLite> valueClass = (Class<? extends MessageLite>) mapTypes[1];
					mappedValue = createProtobufValueMap1(keyClass, valueClass, (Map<?, ?>) mappedValue);
				}
				else if (isKeyComplex && !isValueComplex) {
					Class<? extends MessageLite> keyClass = (Class<? extends MessageLite>) mapTypes[0];
					mappedValue = createProtobufValueMap2(keyClass, (Map<?, ?>) mappedValue);
				}
				else if (!isKeyComplex && isValueComplex) {
					Class<? extends MessageLite> valueClass = (Class<? extends MessageLite>) mapTypes[1];
					mappedValue = createProtobufValueMap3(valueClass, (Map<?, ?>) mappedValue);
				}
				else if (!isKeyComplex && !isValueComplex) {
					mappedValue = createProtobufValueMap4((Map<?, ?>) mappedValue);
				}
				fieldWriter.write(fieldResolver, mappedValue);
				break;
			case MAPPED:
			default:
				fieldWriter.write(fieldResolver, mappedValue);
		}
	}

	@SuppressWarnings("unchecked")
	private <E extends MessageLite> Collection<?> createProtobufValueList(final Class<E> type, final Class<?>
			domainCollectionClass, final Collection<?> domainCollection) {
		return createNestedConverter()
				.toProtobuf((Class<? extends Collection<?>>) domainCollectionClass, type, domainCollection);
	}

	private <K extends MessageLite, V extends MessageLite> Map<K, V> createProtobufValueMap1(
			final Class<K> keyClass, final Class<V> valueClass, final Map<?, ?> domainMappedValue) {
		
		if (domainMappedValue == null) {
			return new HashMap<>(1);
		}
		
		Map<K, V> protobufMap = new HashMap<>((int)(domainMappedValue.entrySet().size() / 0.75) + 1);
		
		for (Iterator<?> it = domainMappedValue.entrySet().iterator(); it.hasNext();) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) it.next();
			K keyAsProto = toProtobuf(keyClass, entry.getKey());
			V valueAsProto = toProtobuf(valueClass, entry.getValue());
			protobufMap.put(keyAsProto, valueAsProto);
		}
		
		return protobufMap;
	}

	@SuppressWarnings("unchecked")
	private <K extends MessageLite, V> Map<K, V> createProtobufValueMap2(final Class<K> keyClass, 
			final Map<?, ?> domainMappedValue) {
		
		if (domainMappedValue == null) {
			return new HashMap<>(1);
		}
		
		Map<K, V> protobufMap = new HashMap<>((int)(domainMappedValue.entrySet().size() / 0.75) + 1);
		
		for (Iterator<?> it = domainMappedValue.entrySet().iterator(); it.hasNext();) {
			Map.Entry<?, V> entry = (Map.Entry<?, V>) it.next();
			K keyAsProto = toProtobuf(keyClass, entry.getKey());
			V value = entry.getValue();
			protobufMap.put(keyAsProto, value);
		}
		
		return protobufMap;
	}
	
	@SuppressWarnings("unchecked")
	private <K, V extends MessageLite> Map<K, V> createProtobufValueMap3(final Class<V> valueClass, 
			final Map<?, ?> domainMappedValue) {
		
		if (domainMappedValue == null) {
			return new HashMap<>(1);
		}
		
		Map<K, V> protobufMap = new HashMap<>((int)(domainMappedValue.entrySet().size() / 0.75) + 1);
		
		for (Iterator<?> it = domainMappedValue.entrySet().iterator(); it.hasNext();) {
			Map.Entry<K, ?> entry = (Map.Entry<K, ?>) it.next();
			K key = entry.getKey();
			V valueAsProto = toProtobuf(valueClass, entry.getValue());
			protobufMap.put(key, valueAsProto);
		}
		
		return protobufMap;
	}
	
	@SuppressWarnings("unchecked")
	private <K, V> Map<K, V> createProtobufValueMap4(final Map<?, ?> domainMappedValue) {
		
		if (domainMappedValue == null) {
			return new HashMap<>(1);
		}
		
		Map<K, V> protobufMap = new HashMap<>((int)(domainMappedValue.entrySet().size() / 0.75) + 1);
		
		for (Iterator<?> it = domainMappedValue.entrySet().iterator(); it.hasNext();) {
			Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
			K key = entry.getKey();
			V value = entry.getValue();
			protobufMap.put(key, value);
		}
		
		return protobufMap;
	}
}
