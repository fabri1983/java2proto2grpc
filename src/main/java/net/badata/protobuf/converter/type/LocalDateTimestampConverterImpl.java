package net.badata.protobuf.converter.type;


import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Converts domain {@link java.time.LocalDate LocalDate} field value to 
 * protobuf {@link com.google.protobuf.Timestamp Timestamp} field value.
 *
 * @author jsjem
 * @author Roman Gushel
 * @author Pablo Fabricio Lettieri &lt;fabri1983@gmail.com&gt;
 */
public class LocalDateTimestampConverterImpl implements TypeConverter<LocalDate, Timestamp> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocalDate toDomainValue(final Object instance) {
		Timestamp timestamp = (Timestamp) instance;
		LocalDate localDate = Instant
				.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos())
				.atZone(ZoneOffset.UTC)
				.toLocalDate();
		return localDate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp toProtobufValue(final Object instance) {
		LocalDate localDate = (LocalDate) instance;
		Instant instant = localDate.atStartOfDay(ZoneOffset.UTC).toInstant();
		Timestamp timestamp = TimeUtil.normalizedTimestamp(instant.getEpochSecond(), instant.getNano());
		return timestamp;
	}
}
