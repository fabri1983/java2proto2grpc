package net.badata.protobuf.converter.type;


import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Converts domain {@link java.time.LocalDate LocalDate} field value to 
 * protobuf {@link java.lang.Long Long} field value.
 *
 * @author jsjem
 * @author Roman Gushel
 * @author Pablo Fabricio Lettieri &lt;fabri1983@gmail.com&gt;
 */
public class LocalDateLongConverterImpl implements TypeConverter<LocalDate, Long> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocalDate toDomainValue(final Object instance) {
		Long millis = (Long) instance;
		return Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long toProtobufValue(final Object instance) {
		LocalDate localDate = (LocalDate) instance;
		return localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
	}
}
