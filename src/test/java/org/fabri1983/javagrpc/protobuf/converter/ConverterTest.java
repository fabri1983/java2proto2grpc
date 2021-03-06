package org.fabri1983.javagrpc.protobuf.converter;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.fabri1983.javagrpc.protobuf.converter.domain.ConverterDomain;
import org.fabri1983.javagrpc.protobuf.converter.domain.ConverterDomain.TestEnumConverter.TestEnum;
import org.fabri1983.javagrpc.protobuf.converter.proto.ConverterProto;
import org.fabri1983.javagrpc.protobuf.converter.type.DateTimestampConverterImpl;
import org.fabri1983.javagrpc.protobuf.converter.type.LocalDateLongConverterImpl;
import org.fabri1983.javagrpc.protobuf.converter.type.LocalDateTimeLongConverterImpl;
import org.fabri1983.javagrpc.protobuf.converter.type.LocalDateTimeTimestampConverterImpl;
import org.fabri1983.javagrpc.protobuf.converter.type.LocalDateTimestampConverterImpl;
import org.fabri1983.javagrpc.protobuf.converter.type.LocalTimeLongConverterImpl;
import org.fabri1983.javagrpc.protobuf.converter.type.LocalTimeTimestampConverterImpl;
import org.fabri1983.javagrpc.protobuf.converter.type.TimeUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author jsjem
 * @author Roman Gushel
 */
public class ConverterTest {

	private ConverterDomain.Test testDomain;
	private ConverterProto.ConverterTest testProtobuf;

	private FieldsIgnore fieldsIgnore;

	@Before
	public void setUp() throws Exception {
		createTestProtobuf();
		createTestDomain();
		createIgnoredFieldsMap();
	}

	private void createTestProtobuf() {
		testProtobuf = ConverterProto.ConverterTest.newBuilder()
				.setBooleanValue(false)
				.setFloatValue(0.1f)
				.setDoubleValue(0.5)
				.setIntValue(1)
				.setLongValue(2L)
				.setStringValue("3")
				.setPrimitiveValue(ConverterProto.PrimitiveTest.newBuilder()
						.setBooleanValue(true)
						.setFloatValue(-0.1f)
						.setDoubleValue(-0.5)
						.setIntValue(-1)
						.setLongValue(-2L))
				.setFieldConversionValue(ConverterProto.FieldConverterTest.newBuilder()
						.setEnumString("THREE")
						.setDateToLong(System.currentTimeMillis())
						.setLocalDateTimeToLong(System.currentTimeMillis())
						.setLocalDateToLong(nowLocalDateInMillis())
						.setLocalTimeToLong(nowLocalTimeInSeconds())
						.setDateToTimestamp(toTimestampFromMillis(System.currentTimeMillis()))
						.setLocalDateTimeToTimestamp(toTimestampFromMillis(System.currentTimeMillis()))
						.setLocalDateToTimestamp(toTimestampFromMillis(nowLocalDateInMillis()))
						.setLocalTimeToTimestamp(toTimestampFromSeconds(nowLocalTimeInSeconds()))
						.addStringSetValue("11"))
				.setNullDefaultValue(ConverterProto.NullDefaultTest.newBuilder()
								.setCustomInspectionString("Assumed as null value")
								.setDefaultPrimitives(ConverterProto.PrimitiveTest.newBuilder())
				)
				.addStringListValue("10")
				.addComplexListValue(ConverterProto.PrimitiveTest.newBuilder().setIntValue(1001))
				.addComplexSetValue(ConverterProto.PrimitiveTest.newBuilder().setIntValue(1002))
				.setBytesValue(ByteString.copyFrom(new byte[]{ 0, 1, 3, 7 }))
				.setRecursiveValue(ConverterProto.ConverterTest.newBuilder().setIntValue(1))
				.putSimpleMapValue("key", "value")
				.putComplexMapValue("key", ConverterProto.PrimitiveTest.newBuilder().setIntValue(1001).build())
				.build();
	}

	private long nowLocalDateInMillis() {
		return LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
	}

	private long nowLocalTimeInSeconds() {
		return LocalTime.now().truncatedTo(ChronoUnit.SECONDS).toSecondOfDay();
	}
	
	private Timestamp toTimestampFromMillis(long millis) {
		Timestamp timestamp = TimeUtil.normalizedTimestamp(
				millis / TimeUtil.MILLIS_PER_SECOND,
				(int) ((millis % TimeUtil.MILLIS_PER_SECOND) * TimeUtil.NANOS_PER_MILLISECOND));
		return timestamp;
	}

	private Timestamp toTimestampFromSeconds(long seconds) {
		Timestamp timestamp = TimeUtil.normalizedTimestamp(seconds, 0);
		return timestamp;
	}
	
	private void createTestDomain() {
		ConverterDomain.PrimitiveTest primitiveTest = new ConverterDomain.PrimitiveTest();
		primitiveTest.setBooleanValue(true);
		primitiveTest.setFloatValue(-0.2f);
		primitiveTest.setDoubleValue(-0.6);
		primitiveTest.setIntValue(-101);
		primitiveTest.setLongValue(-102L);

		ConverterDomain.FieldConverterTest fieldConverterTest = new ConverterDomain.FieldConverterTest();
		fieldConverterTest.setEnumString(TestEnum.TWO);
		fieldConverterTest.setDateToLong(new Date());
		fieldConverterTest.setLocalDateTimeToLong(nowLocalDateTime());
		fieldConverterTest.setLocalDateToLong(nowLocalDate());
		fieldConverterTest.setLocalTimeToLong(nowLocalTime());
		fieldConverterTest.setDateToTimestamp(new Date());
		fieldConverterTest.setLocalDateTimeToTimestamp(nowLocalDateTime());
		fieldConverterTest.setLocalDateToTimestamp(nowLocalDate());
		fieldConverterTest.setLocalTimeToTimestamp(nowLocalTime());
		Set<String> stringSet = new HashSet<String>();
		stringSet.add("111");
		fieldConverterTest.setStringSetValue(stringSet);

		testDomain = new ConverterDomain.Test();
		testDomain.setBoolValue(false);
		testDomain.setFloatValue(0.2f);
		testDomain.setDoubleValue(0.6);
		testDomain.setIntValue(101);
		testDomain.setLongValue(102L);
		testDomain.setStringValue("103");
		testDomain.setPrimitiveValue(primitiveTest);
		testDomain.setFieldConversionValue(fieldConverterTest);
		testDomain.setSimpleListValue(Arrays.asList("110"));

		ConverterDomain.PrimitiveTest primitiveTestItem = new ConverterDomain.PrimitiveTest();
		primitiveTestItem.setIntValue(-1001);
		testDomain.setComplexListValue(Arrays.asList(primitiveTestItem));
		ConverterDomain.PrimitiveTest primitiveTestSItem = new ConverterDomain.PrimitiveTest();
		primitiveTestSItem.setIntValue(-1002);
		testDomain.setComplexSetValue(new HashSet<ConverterDomain.PrimitiveTest>(Arrays.asList(primitiveTestSItem)));
		testDomain.setComplexNullableCollectionValue(null);

		testDomain.setBytesValue(ByteString.copyFrom(new byte[]{ 0, 1, 3, 7 }));

		ConverterDomain.Test nestedValue = new ConverterDomain.Test();
		nestedValue.setIntValue(1);
		testDomain.setRecursiveValue(nestedValue);
		
		testDomain.setSimpleMapValue(Collections.singletonMap("key", "value"));
		testDomain.setComplexMapValue(Collections.singletonMap("key", primitiveTestItem));
	}

	/**
	 * Use System.currentTimeMillis() to avoid the extra microseconds added on Java9+ when using Instant.now().
	 * See https://stackoverflow.com/questions/39586311/java-8-localdatetime-now-only-giving-precision-of-milliseconds
	 * 
	 * @return
	 */
	private LocalDateTime nowLocalDateTime() {
		Instant instantNow = Instant.ofEpochMilli( System.currentTimeMillis() );
		LocalDateTime nowLocalDateTime = instantNow.atOffset(ZoneOffset.UTC).toLocalDateTime();
		return nowLocalDateTime;
	}

	/**
	 * Use System.currentTimeMillis() to avoid the extra microseconds added on Java9+ when using Instant.now().
	 * See https://stackoverflow.com/questions/39586311/java-8-localdatetime-now-only-giving-precision-of-milliseconds
	 * 
	 * @return
	 */
	private LocalDate nowLocalDate() {
		Instant instantNow = Instant.ofEpochMilli( System.currentTimeMillis() );
		LocalDate nowLocalDate = instantNow.atOffset(ZoneOffset.UTC).toLocalDate();
		return nowLocalDate;
	}
	
	private LocalTime nowLocalTime() {
		return LocalTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);
	}
	
	private void createIgnoredFieldsMap() {
		fieldsIgnore = new FieldsIgnore();
		fieldsIgnore.add(ConverterDomain.PrimitiveTest.class);
		fieldsIgnore.add(ConverterDomain.FieldConverterTest.class, "enumString");
		fieldsIgnore.add(ConverterDomain.Test.class, "boolValue");
	}


	@Test
	public void testProtobufToDomain() {
		ConverterDomain.Test result = Converter.create()
				.toDomain(ConverterDomain.Test.class, testProtobuf);

		Assert.assertNotNull(result);

		Assert.assertEquals(testProtobuf.getBooleanValue(), result.getBoolValue());
		Assert.assertEquals((Object) testProtobuf.getFloatValue(), result.getFloatValue());
		Assert.assertEquals((Object) testProtobuf.getDoubleValue(), result.getDoubleValue());
		Assert.assertEquals((Object) testProtobuf.getIntValue(), result.getIntValue());
		Assert.assertEquals((Object) testProtobuf.getLongValue(), result.getLongValue());
		Assert.assertEquals(testProtobuf.getStringValue(), result.getStringValue());

		ConverterProto.PrimitiveTest primitiveProto = testProtobuf.getPrimitiveValue();
		ConverterDomain.PrimitiveTest primitiveDomain = result.getPrimitiveValue();

		Assert.assertEquals(primitiveProto.getLongValue(), primitiveDomain.getLongValue());
		Assert.assertEquals(primitiveProto.getIntValue(), primitiveDomain.getIntValue());
		Assert.assertEquals(primitiveProto.getFloatValue(), primitiveDomain.getFloatValue(), 0f);
		Assert.assertEquals(primitiveProto.getDoubleValue(), primitiveDomain.getDoubleValue(), 0);
		Assert.assertEquals(primitiveProto.getBooleanValue(), primitiveDomain.isBooleanValue());

		ConverterProto.FieldConverterTest conversionProto = testProtobuf.getFieldConversionValue();
		ConverterDomain.FieldConverterTest conversionDomain = result.getFieldConversionValue();

		Assert.assertEquals(conversionProto.getDateToLong(), conversionDomain.getDateToLong().getTime());
		Assert.assertEquals(conversionProto.getLocalDateTimeToLong(), toLong(conversionDomain.getLocalDateTimeToLong()));
		Assert.assertEquals(conversionProto.getLocalDateToLong(), toLong(conversionDomain.getLocalDateToLong()));
		Assert.assertEquals(conversionProto.getLocalTimeToLong(), toLong(conversionDomain.getLocalTimeToLong()));
		Assert.assertEquals(conversionProto.getDateToTimestamp(), toTimestamp(conversionDomain.getDateToTimestamp()));
		Assert.assertEquals(conversionProto.getLocalDateTimeToTimestamp(), toTimestamp(conversionDomain.getLocalDateTimeToTimestamp()));
		Assert.assertEquals(conversionProto.getLocalDateToTimestamp(), toTimestamp(conversionDomain.getLocalDateToTimestamp()));
		Assert.assertEquals(conversionProto.getLocalTimeToTimestamp(), toTimestamp(conversionDomain.getLocalTimeToTimestamp()));
		Assert.assertEquals(conversionProto.getEnumString(), conversionDomain.getEnumString().name());
		Assert.assertTrue(conversionDomain.getStringSetValue().remove(conversionProto.getStringSetValue(0)));

		ConverterDomain.NullDefaultTest nullDefaultDomain = result.getNullDefaultValue();

		Assert.assertEquals(nullDefaultDomain.getCustomInspectionString(), new ConverterDomain.StringDefaultValue()
				.generateValue(null));
		Assert.assertNull(nullDefaultDomain.getDefaultPrimitives().getLongValue());
		Assert.assertNull(nullDefaultDomain.getDefaultPrimitives().getIntValue());
		Assert.assertNull(nullDefaultDomain.getDefaultPrimitives().getFloatValue());
		Assert.assertNull(nullDefaultDomain.getDefaultPrimitives().getDoubleValue());

		Assert.assertEquals(testProtobuf.getStringListValue(0), result.getSimpleListValue().get(0));
		Assert.assertEquals(testProtobuf.getComplexListValue(0).getIntValue(),
				result.getComplexListValue().get(0).getIntValue());
		Assert.assertEquals(testProtobuf.getComplexSetValue(0).getIntValue(),
				result.getComplexSetValue().iterator().next().getIntValue());

		Assert.assertTrue(result.getComplexNullableCollectionValue().isEmpty());

		Assert.assertEquals(testProtobuf.getBytesValue(), result.getBytesValue());
		Assert.assertEquals((Object) testProtobuf.getRecursiveValue().getIntValue(), 
				result.getRecursiveValue().getIntValue());
		
		Assert.assertEquals(testProtobuf.getSimpleMapValueMap(), result.getSimpleMapValue());
		Assert.assertEquals(testProtobuf.getComplexMapValueMap().get("key").getIntValue(),
				result.getComplexMapValue().get("key").getIntValue());
	}

	@Test
	public void testFieldIgnoreProtobufToDomain() {
		Configuration configuration = Configuration.builder().addIgnoredFields(fieldsIgnore).build();
		ConverterDomain.Test result = Converter.create(configuration)
				.toDomain(ConverterDomain.Test.class, testProtobuf);

		Assert.assertNotNull(result);

		Assert.assertNull(result.getBoolValue());
		Assert.assertNull(result.getPrimitiveValue());
		Assert.assertNull(result.getFieldConversionValue().getEnumString());
		Assert.assertNull(result.getComplexListValue());
	}


	@Test
	public void testDomainToProtobuf() {
		ConverterProto.ConverterTest result = Converter.create()
				.toProtobuf(ConverterProto.ConverterTest.class, testDomain);

		Assert.assertNotNull(result);

		Assert.assertEquals(testDomain.getBoolValue(), result.getBooleanValue());
		Assert.assertEquals(testDomain.getFloatValue(), (Object) result.getFloatValue());
		Assert.assertEquals(testDomain.getDoubleValue(), (Object) result.getDoubleValue());
		Assert.assertEquals(testDomain.getIntValue(), (Object) result.getIntValue());
		Assert.assertEquals(testDomain.getLongValue(), (Object) result.getLongValue());
		Assert.assertEquals(testDomain.getStringValue(), result.getStringValue());

		ConverterProto.PrimitiveTest primitiveProto = result.getPrimitiveValue();
		ConverterDomain.PrimitiveTest primitiveDomain = testDomain.getPrimitiveValue();

		Assert.assertEquals(primitiveDomain.getLongValue(), primitiveProto.getLongValue());
		Assert.assertEquals(primitiveDomain.getIntValue(), primitiveProto.getIntValue());
		Assert.assertEquals(primitiveDomain.getFloatValue(), primitiveProto.getFloatValue(), 0f);
		Assert.assertEquals(primitiveDomain.getDoubleValue(), primitiveProto.getDoubleValue(), 0);
		Assert.assertEquals(primitiveDomain.isBooleanValue(), primitiveProto.getBooleanValue());

		ConverterProto.FieldConverterTest conversionProto = result.getFieldConversionValue();
		ConverterDomain.FieldConverterTest conversionDomain = testDomain.getFieldConversionValue();

		Assert.assertEquals(conversionDomain.getDateToLong().getTime(), conversionProto.getDateToLong());
		Assert.assertEquals(conversionDomain.getLocalDateTimeToLong(), toLocalDateTime(conversionProto.getLocalDateTimeToLong()));
		Assert.assertEquals(conversionDomain.getLocalDateToLong(), toLocalDate(conversionProto.getLocalDateToLong()));
		Assert.assertEquals(conversionDomain.getLocalTimeToLong(), toLocalTime(conversionProto.getLocalTimeToLong()));
		Assert.assertEquals(conversionDomain.getDateToTimestamp(), toDate(conversionProto.getDateToTimestamp()));
		Assert.assertEquals(conversionDomain.getLocalDateTimeToTimestamp(), toLocalDateTime(conversionProto.getLocalDateTimeToTimestamp()));
		Assert.assertEquals(conversionDomain.getLocalDateToTimestamp(), toLocalDate(conversionProto.getLocalDateToTimestamp()));
		Assert.assertEquals(conversionDomain.getLocalTimeToTimestamp(), toLocalTime(conversionProto.getLocalTimeToTimestamp()));
		Assert.assertEquals(conversionDomain.getEnumString().name(), conversionProto.getEnumString());
		Assert.assertTrue(conversionDomain.getStringSetValue().remove(conversionProto.getStringSetValue(0)));

		Assert.assertFalse(result.hasNullDefaultValue());

		Assert.assertEquals(testDomain.getSimpleListValue().get(0), result.getStringListValue(0));
		Assert.assertEquals(testDomain.getComplexListValue().get(0).getIntValue(),
				result.getComplexListValue(0).getIntValue());
		Assert.assertEquals(testDomain.getComplexSetValue().iterator().next().getIntValue(),
				result.getComplexSetValue(0).getIntValue());

		Assert.assertTrue(result.getComplexNullableCollectionValueList().isEmpty());
		Assert.assertEquals((Object) testDomain.getRecursiveValue().getIntValue(), result.getRecursiveValue().getIntValue());
		
		Assert.assertEquals(testDomain.getSimpleMapValue(), result.getSimpleMapValueMap());
		Assert.assertEquals(testDomain.getComplexMapValue().get("key").getIntValue(), 
				result.getComplexMapValueMap().get("key").getIntValue());
	}

	@Test
	public void testFieldIgnoreDomainToProtobuf() {
		Configuration configuration = Configuration.builder().addIgnoredFields(fieldsIgnore).build();
		ConverterProto.ConverterTest result = Converter.create(configuration)
				.toProtobuf(ConverterProto.ConverterTest.class, testDomain);

		Assert.assertNotNull(result);

		Assert.assertFalse(result.getBooleanValue());
		Assert.assertFalse(result.hasPrimitiveValue());
		Assert.assertEquals("", result.getFieldConversionValue().getEnumString());
		Assert.assertTrue(result.getComplexListValueList().isEmpty());
	}

	private long toLong(LocalDateTime localDateTime) {
		LocalDateTimeLongConverterImpl converter = new LocalDateTimeLongConverterImpl();
		Long millis = converter.toProtobufValue(localDateTime);
		return millis.longValue();
	}
	
	private long toLong(LocalDate localDate) {
		LocalDateLongConverterImpl converter = new LocalDateLongConverterImpl();
		Long millis = converter.toProtobufValue(localDate);
		return millis.longValue();
	}
	
	private long toLong(LocalTime localTime) {
		LocalTimeLongConverterImpl converter = new LocalTimeLongConverterImpl();
		Long seconds = converter.toProtobufValue(localTime);
		return seconds.longValue();
	}
	
	private LocalDateTime toLocalDateTime(Long millis) {
		LocalDateTimeLongConverterImpl converter = new LocalDateTimeLongConverterImpl();
		return converter.toDomainValue(millis);
	}
	
	private LocalDate toLocalDate(Long millis) {
		LocalDateLongConverterImpl converter = new LocalDateLongConverterImpl();
		return converter.toDomainValue(millis);
	}
	
	private LocalTime toLocalTime(Long seconds) {
		LocalTimeLongConverterImpl converter = new LocalTimeLongConverterImpl();
		return converter.toDomainValue(seconds);
	}
	
	private Timestamp toTimestamp(Date date) {
		DateTimestampConverterImpl converter = new DateTimestampConverterImpl();
		return converter.toProtobufValue(date);
	}
	
	private Date toDate(Timestamp timestamp) {
		DateTimestampConverterImpl converter = new DateTimestampConverterImpl();
		return converter.toDomainValue(timestamp);
	}
	
	private Timestamp toTimestamp(LocalDateTime localDateTime) {
		LocalDateTimeTimestampConverterImpl converter = new LocalDateTimeTimestampConverterImpl();
		return converter.toProtobufValue(localDateTime);
	}

	private LocalDateTime toLocalDateTime(Timestamp timestamp) {
		LocalDateTimeTimestampConverterImpl converter = new LocalDateTimeTimestampConverterImpl();
		return converter.toDomainValue(timestamp);
	}

	private Timestamp toTimestamp(LocalDate localDate) {
		LocalDateTimestampConverterImpl converter = new LocalDateTimestampConverterImpl();
		return converter.toProtobufValue(localDate);
	}
	
	private Timestamp toTimestamp(LocalTime localTime) {
		LocalTimeTimestampConverterImpl converter = new LocalTimeTimestampConverterImpl();
		return converter.toProtobufValue(localTime);
	}
	
	private LocalDate toLocalDate(Timestamp timestamp) {
		LocalDateTimestampConverterImpl converter = new LocalDateTimestampConverterImpl();
		return converter.toDomainValue(timestamp);
	}
	
	private LocalTime toLocalTime(Timestamp timestamp) {
		LocalTimeTimestampConverterImpl converter = new LocalTimeTimestampConverterImpl();
		return converter.toDomainValue(timestamp);
	}
	
}
