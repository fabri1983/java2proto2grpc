package org.fabri1983.javagrpc.protobuf.converter.type;


import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Converts domain {@link java.time.LocalDateTime LocalDateTime} field value to 
 * protobuf {@link com.google.protobuf.Timestamp Timestamp} field value.
 *
 * @author jsjem
 * @author Roman Gushel
 * @author Pablo Fabricio Lettieri &lt;fabri1983@gmail.com&gt;
 */
public class LocalDateTimeTimestampConverterImpl implements TypeConverter<LocalDateTime, Timestamp> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocalDateTime toDomainValue(final Object instance) {
		Timestamp timestamp = (Timestamp) instance;
		LocalDateTime localDateTime = Instant
				.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos())
				.atZone(ZoneOffset.UTC)
				.toLocalDateTime();
		return localDateTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp toProtobufValue(final Object instance) {
		LocalDateTime localDateTime = (LocalDateTime) instance;
		Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
		Timestamp timestamp = TimeUtil.normalizedTimestamp(instant.getEpochSecond(), instant.getNano());
		return timestamp;
	}
}
