package net.badata.protobuf.converter.writer;

import com.google.protobuf.MessageLite;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.badata.protobuf.converter.exception.WriteException;
import net.badata.protobuf.converter.resolver.FieldResolver;
import net.badata.protobuf.converter.type.TypeConverter;
import net.badata.protobuf.converter.utils.FieldUtils;
import net.badata.protobuf.converter.utils.Primitives;

/**
 * Writes data to the protobuf dto.
 *
 * @author jsjem
 * @author Roman Gushel
 */
public class ProtobufWriter extends AbstractWriter {

	private final Class<? extends MessageLite.Builder> destinationClass;

	/**
	 * Constructor.
	 *
	 * @param destination Protobuf dto builder instance.
	 */
	public ProtobufWriter(final MessageLite.Builder destination) {
		super(destination);
		destinationClass = destination.getClass();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void write(final Object destination, final FieldResolver fieldResolver, final Object value) throws
			WriteException {
		if (value != null) {
			TypeConverter<?, ?> typeConverter = fieldResolver.getTypeConverter();
			writeValue(destination, fieldResolver, typeConverter.toProtobufValue(value));
		}
	}


	private void writeValue(final Object destination, final FieldResolver fieldResolver, final Object value) throws WriteException {
		Class<?> valueClass = extractValueClass(value);
		while (valueClass != null) {
			String setterName = FieldUtils.createProtobufSetterName(fieldResolver);
			try {
				Object finalValue = value;
				// does destination setter method expects an array?
				if (FieldUtils.doesSetterExpectsArray(fieldResolver)) {
					finalValue = convertArrayToIterable(value);
				}
				destinationClass.getMethod(setterName, valueClass).invoke(destination, finalValue);
				break;
			} catch (IllegalAccessException e) {
				throw new WriteException(
						String.format("Access denied. '%s.%s(%s)'", destinationClass.getName(), setterName, valueClass));
			} catch (InvocationTargetException e) {
				throw new WriteException(
						String.format("Can't set field value through '%s.%s(%s)'", destinationClass.getName(), setterName, valueClass));
			} catch (NoSuchMethodException e) {
				if (valueClass.getSuperclass() != null) {
					valueClass = valueClass.getSuperclass();
				} else {
					throw new WriteException(
							String.format("Setter not found: '%s.%s(%s)'", destinationClass.getName(), setterName, valueClass));
				}
			}
		}
	}

	private Class<?> extractValueClass(final Object value) {
		Class<?> valueClass = value.getClass();
		if (Primitives.isWrapperType(valueClass)) {
			return Primitives.unwrap(valueClass);
		}
		if (Primitives.isArrayOfWrapperType(valueClass)) {
			return Iterable.class;
		}
		if (Collection.class.isAssignableFrom(valueClass)) {
			return Iterable.class;
		}
		if (Map.class.isAssignableFrom(valueClass)) {
			return Map.class;
		}
		return valueClass;
	}

	private Iterable<?> convertArrayToIterable(final Object value) {
		Class<?> valueClass = value.getClass().getComponentType();
		if (boolean.class.isAssignableFrom(valueClass)) {
			boolean[] array = (boolean[]) value;
			List<Boolean> list = new ArrayList<>(array.length);
			for (int i=0, c=array.length; i < c; ++i) {
				list.add(i, array[i]);
			}
			return list;
		}
		else if (byte.class.isAssignableFrom(valueClass)) {
			byte[] array = (byte[]) value;
			List<Byte> list = new ArrayList<>(array.length);
			for (int i=0, c=array.length; i < c; ++i) {
				list.add(i, array[i]);
			}
			return list;
		}
		else if (char.class.isAssignableFrom(valueClass)) {
			char[] array = (char[]) value;
			List<Character> list = new ArrayList<>(array.length);
			for (int i=0, c=array.length; i < c; ++i) {
				list.add(i, array[i]);
			}
			return list;
		}
		else if (double.class.isAssignableFrom(valueClass)) {
			double[] array = (double[]) value;
			List<Double> list = new ArrayList<>(array.length);
			for (int i=0, c=array.length; i < c; ++i) {
				list.add(i, array[i]);
			}
			return list;
		}
		else if (float.class.isAssignableFrom(valueClass)) {
			float[] array = (float[]) value;
			List<Float> list = new ArrayList<>(array.length);
			for (int i=0, c=array.length; i < c; ++i) {
				list.add(i, array[i]);
			}
			return list;
		}
		else if (int.class.isAssignableFrom(valueClass)) {
			int[] array = (int[]) value;
			List<Integer> list = new ArrayList<>(array.length);
			for (int i=0, c=array.length; i < c; ++i) {
				list.add(i, array[i]);
			}
			return list;
		}
		else if (long.class.isAssignableFrom(valueClass)) {
			long[] array = (long[]) value;
			List<Long> list = new ArrayList<>(array.length);
			for (int i=0, c=array.length; i < c; ++i) {
				list.add(i, array[i]);
			}
			return list;
		}
		else if (short.class.isAssignableFrom(valueClass)) {
			short[] array = (short[]) value;
			List<Short> list = new ArrayList<>(array.length);
			for (int i=0, c=array.length; i < c; ++i) {
				list.add(i, array[i]);
			}
			return list;
		}
		else {
			Object[] array = (Object[]) value;
			List<Object> list = new ArrayList<>(array.length);
			for (int i=0, c=array.length; i < c; ++i) {
				list.add(i, array[i]);
			}
			return list;
		}
	}
}
