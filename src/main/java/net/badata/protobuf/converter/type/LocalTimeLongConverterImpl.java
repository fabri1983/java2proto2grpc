package net.badata.protobuf.converter.type;


import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Converts domain {@link java.time.LocalTime LocalTime} field value to 
 * protobuf {@link java.lang.Long Long} field value.
 *
 * @author jsjem
 * @author Roman Gushel
 * @author Pablo Fabricio Lettieri &lt;fabri1983@gmail.com&gt;
 */
public class LocalTimeLongConverterImpl implements TypeConverter<LocalTime, Long> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LocalTime toDomainValue(final Object instance) {
		Long secondOfDay = (Long) instance;
		return LocalTime.ofSecondOfDay(secondOfDay).truncatedTo(ChronoUnit.SECONDS);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long toProtobufValue(final Object instance) {
		LocalTime localTime = (LocalTime) instance;
		return Long.valueOf(localTime.truncatedTo(ChronoUnit.SECONDS).toSecondOfDay());
	}
}
