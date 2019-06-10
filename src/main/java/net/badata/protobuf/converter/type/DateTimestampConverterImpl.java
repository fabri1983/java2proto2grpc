package net.badata.protobuf.converter.type;


import com.google.protobuf.Timestamp;

import java.util.Date;

/**
 * Converts domain {@link java.util.Date Date} field value to protobuf {@link com.google.protobuf.Timestamp Timestamp} field value.
 *
 * @author jsjem
 * @author Roman Gushel
 * @author Pablo Fabricio Lettieri &lt;fabri1983@gmail.com&gt;
 */
public class DateTimestampConverterImpl implements TypeConverter<Date, Timestamp> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date toDomainValue(final Object instance) {
		Timestamp timestamp = (Timestamp) instance;
		long millis = (timestamp.getSeconds() * 1000) + (timestamp.getNanos() / 1000000);
		return new Date(millis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp toProtobufValue(final Object instance) {
		Date date = (Date) instance;
		long millis = date.getTime();
		Timestamp timestamp = Timestamp.newBuilder()
				.setSeconds(millis / 1000)
				.setNanos((int) ((millis % 1000) * 1000000))
				.build();
		return timestamp;
	}
}
