package org.fabri1983.javagrpc.protobuf.converter.type;


import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 * Converts domain {@link java.time.LocalTime LocalTime} field value to 
 * protobuf {@link com.google.protobuf.Timestamp Timestamp} field value.
 *
 * @author jsjem
 * @author Roman Gushel
 * @author Pablo Fabricio Lettieri &lt;fabri1983@gmail.com&gt;
 */
public class LocalTimeTimestampConverterImpl implements TypeConverter<LocalTime, Timestamp> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocalTime toDomainValue(final Object instance) {
		Timestamp timestamp = (Timestamp) instance;
		LocalTime localTime = Instant
				.ofEpochSecond(timestamp.getSeconds())
				.atZone(ZoneOffset.UTC)
				.toLocalTime().truncatedTo(ChronoUnit.SECONDS);
		return localTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp toProtobufValue(final Object instance) {
		LocalTime localTime = (LocalTime) instance;
		Timestamp timestamp = TimeUtil.normalizedTimestamp(localTime.toSecondOfDay(), 0);
		return timestamp;
	}
}
