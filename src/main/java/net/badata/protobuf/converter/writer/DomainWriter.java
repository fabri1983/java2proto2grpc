package net.badata.protobuf.converter.writer;

import com.google.protobuf.ByteString;

import java.lang.reflect.InvocationTargetException;
import java.util.AbstractList;

import net.badata.protobuf.converter.exception.WriteException;
import net.badata.protobuf.converter.inspection.DefaultValue;
import net.badata.protobuf.converter.inspection.NullValueInspector;
import net.badata.protobuf.converter.resolver.FieldResolver;
import net.badata.protobuf.converter.type.TypeConverter;
import net.badata.protobuf.converter.utils.FieldUtils;
import net.badata.protobuf.converter.utils.ProtobufInnerTypes;

/**
 * Writes data to the domain instance.
 *
 * @author jsjem
 * @author Roman Gushel
 */
public class DomainWriter extends AbstractWriter {

	private final Class<?> destinationClass;

	/**
	 * Constructor.
	 *
	 * @param destination Domain object.
	 */
	public DomainWriter(final Object destination) {
		super(destination);
		destinationClass = destination.getClass();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void write(final Object destination, final FieldResolver fieldResolver, final Object value)
			throws WriteException {
		NullValueInspector nullInspector = fieldResolver.getNullValueInspector();
		DefaultValue defaultValueCreator = fieldResolver.getDefaultValue();
		TypeConverter<?, ?> typeConverter = fieldResolver.getTypeConverter();
		if (nullInspector.isNull(value)) {
			writeValue(destination, fieldResolver, defaultValueCreator.generateValue(fieldResolver.getDomainType()));
		} else {
			writeValue(destination, fieldResolver, typeConverter.toDomainValue(value));
		}
	}

	private void writeValue(final Object destination,  final FieldResolver fieldResolver, final Object value) throws WriteException {
		String setterName = FieldUtils.createDomainSetterName(fieldResolver);
		try {
			Object finalValue = value;
			// does destination setter method expects an array?
			if (FieldUtils.doesSetterExpectsArray(fieldResolver)) {
				finalValue = convertToArray(value);
			}
			destinationClass.getMethod(setterName, fieldResolver.getDomainType()).invoke(destination, finalValue);
		} catch (IllegalAccessException e) {
			throw new WriteException(
					String.format("Access denied. '%s.%s()'", destinationClass.getName(), setterName));
		} catch (InvocationTargetException e) {
			throw new WriteException(
					String.format("Can't set field value through '%s.%s()'", destinationClass.getName(), setterName));
		} catch (NoSuchMethodException e) {
			throw new WriteException(
					String.format("Setter not found: '%s.%s()'", destinationClass.getName(), setterName));
		}
	}

	private Object convertToArray(Object value) {
		String simpleName = value.getClass().getSimpleName();
		if (ProtobufInnerTypes.BooleanArrayList.name().equals(simpleName)) {
			AbstractList<Boolean> list = ProtobufInnerTypes.castToAbstractList(value);
			boolean[] array = new boolean[list.size()];
			for (int i=0, c=list.size(); i < c; ++i) {
				array[i] = list.get(i);
			}
			return array;
		}
		if (ProtobufInnerTypes.ByteStringListView.name().equals(simpleName)) {
			AbstractList<ByteString> list = ProtobufInnerTypes.castToAbstractList(value);
			byte[] array = new byte[list.size()];
			for (int i=0, c=list.size(); i < c; ++i) {
				ByteString byteString = list.get(i);
				for (int j=0, c2=byteString.size(); j < c2; ++j) {
					array[(i*c2) + j] = byteString.byteAt(j);
				}
			}
			return array;
		}
		if (ProtobufInnerTypes.DoubleArrayList.name().equals(simpleName)) {
			AbstractList<Double> list = ProtobufInnerTypes.castToAbstractList(value);
			double[] array = new double[list.size()];
			for (int i=0, c=list.size(); i < c; ++i) {
				array[i] = list.get(i);
			}
			return array;
		}
		if (ProtobufInnerTypes.FloatArrayList.name().equals(simpleName)) {
			AbstractList<Float> list = ProtobufInnerTypes.castToAbstractList(value);
			float[] array = new float[list.size()];
			for (int i=0, c=list.size(); i < c; ++i) {
				array[i] = list.get(i);
			}
			return array;
		}
		if (ProtobufInnerTypes.IntArrayList.name().equals(simpleName)) {
			AbstractList<Integer> list = ProtobufInnerTypes.castToAbstractList(value);
			int[] array = new int[list.size()];
			for (int i=0, c=list.size(); i < c; ++i) {
				array[i] = list.get(i);
			}
			return array;
		}
		if (ProtobufInnerTypes.LongArrayList.name().equals(simpleName)) {
			AbstractList<Long> list = ProtobufInnerTypes.castToAbstractList(value);
			long[] array = new long[list.size()];
			for (int i=0, c=list.size(); i < c; ++i) {
				array[i] = list.get(i);
			}
			return array;
		}
		else {
			AbstractList<?> list = (AbstractList<?>) value;
			return list.toArray();
		}
	}
}
