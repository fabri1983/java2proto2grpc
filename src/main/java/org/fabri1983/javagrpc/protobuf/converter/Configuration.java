package org.fabri1983.javagrpc.protobuf.converter;

/**
 * Contains configuration parameters that will be used by {@link org.fabri1983.javagrpc.protobuf.converter.Converter
 * Converter} during performing of object conversion.
 *
 * @author jsjem
 * @author Roman Gushel
 */
public final class Configuration {

	private final FieldsIgnore ignoredFields;
	private final boolean includeInheritedFields;

	/**
	 * Create builder for {@link org.fabri1983.javagrpc.protobuf.converter.Configuration Configuration}.
	 *
	 * @return new  {@link org.fabri1983.javagrpc.protobuf.converter.Configuration.Builder Builder} instance.
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Constructor.
	 *
	 * @param ignoredFields          Ignored fields map.
	 * @param includeInheritedFields Flags that allows to convert domain fields that is inherited from super class.
	 */
	private Configuration(final FieldsIgnore ignoredFields, final boolean includeInheritedFields) {
		this.ignoredFields = ignoredFields;
		this.includeInheritedFields = includeInheritedFields;
	}

	/**
	 * Getter for ignored fields map.
	 *
	 * @return Map with ignored fields.
	 */
	public FieldsIgnore getIgnoredFields() {
		return ignoredFields;
	}

	/**
	 * Check whether converter has to process fields inherited from domain super class.
	 *
	 * @return true when inherited fields included to conversion.
	 */
	public boolean withInheritedFields() {
		return includeInheritedFields;
	}

	/**
	 * Builder for {@link org.fabri1983.javagrpc.protobuf.converter.Configuration Configuration}.
	 */
	public static final class Builder {
		private FieldsIgnore ignoredFields;
		private boolean includeInheritedFields;

		/**
		 * Set mapping for ignore fields.
		 *
		 * @param ignoredFields Ignore fields mapping.
		 * @return {@link org.fabri1983.javagrpc.protobuf.converter.Configuration.Builder Builder} instance.
		 */
		public Builder setIgnoredFields(final FieldsIgnore ignoredFields) {
			checkIgnoredFields(ignoredFields);
			this.ignoredFields = ignoredFields;
			return this;
		}

		private void checkIgnoredFields(final FieldsIgnore ignoredFields) {
			if (ignoredFields == null) {
				throw new IllegalArgumentException("Argument ignoredFields can't be null");
			}
		}

		/**
		 * Add ignored fields mappings from existing {@link org.fabri1983.javagrpc.protobuf.converter.FieldsIgnore FieldsIgnore}.
		 *
		 * @param ignoredFields Instance with ignored fields mappings.
		 * @return {@link org.fabri1983.javagrpc.protobuf.converter.Configuration.Builder Builder} instance.
		 */
		public Builder addIgnoredFields(final FieldsIgnore ignoredFields) {
			checkIgnoredFields(ignoredFields);
			this.ignoredFields.addAll(ignoredFields);
			return this;
		}

		/**
		 * Set {@code includeInheritedFields} to true.
		 *
		 * @return {@link org.fabri1983.javagrpc.protobuf.converter.Configuration.Builder Builder} instance.
		 */
		public Builder withInheritedFields() {
			includeInheritedFields = true;
			return this;
		}

		/**
		 * Create {@link org.fabri1983.javagrpc.protobuf.converter.Configuration Configuration}.
		 *
		 * @return new Configuration instance.
		 */
		public Configuration build() {
			return new Configuration(ignoredFields.copy(), includeInheritedFields);
		}

		private Builder() {
			ignoredFields = new FieldsIgnore();
		}
	}
}
