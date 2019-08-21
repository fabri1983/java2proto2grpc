package org.fabri1983.javagrpc.protobuf.converter.type;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Converts domain {@link java.time.LocalDateTime LocalDateTime} field value to 
 * protobuf {@link java.lang.Long Long} field value.
 *
 * @author jsjem
 * @author Roman Gushel
 * @author Pablo Fabricio Lettieri &lt;fabri1983@gmail.com&gt;
 */
public class LocalDateTimeLongConverterImpl implements TypeConverter<LocalDateTime, Long> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocalDateTime toDomainValue(final Object instance) {
		Long millis = (Long) instance;
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long toProtobufValue(final Object instance) {
		LocalDateTime localDateTime = (LocalDateTime) instance;
		return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
	}
}
