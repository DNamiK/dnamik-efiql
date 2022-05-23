/*
 * DNamiK EFIQL
 * Copyright (C) 2022 Kristijan Georgiev
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.dnamik.efiql.core.ast;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import lombok.Value;

/**
 * ComparisonOperator
 *
 * @version 1.0.0
 * @since   May 23, 2022
 * @author  Kristijan Georgiev
 *
 */
@Value
public final class ComparisonOperator {

	private final long valuesSize;

	private final Set<String> operators;

	private final Pattern acceptedValues;

	public ComparisonOperator(String... operators) {
		this(1, operators);
	}

	public ComparisonOperator(long valuesSize, String... operators) {
		this(valuesSize, null, operators);
	}

	public ComparisonOperator(boolean multipleValues, String... operators) {
		this(multipleValues ? -1 : 1, operators);
	}

	public ComparisonOperator(Pattern acceptedValues, String... operators) {
		this(1, acceptedValues, operators);
	}

	public ComparisonOperator(long valuesSize, Pattern acceptedValues, String... operators) {
		this.operators = Set.of(operators);
		this.valuesSize = valuesSize;
		this.acceptedValues = acceptedValues;
	}

	public ComparisonOperator(boolean multipleValues, Pattern acceptedValues, String... operators) {
		this(multipleValues ? -1 : 1, acceptedValues, operators);
	}

	public boolean multipleValues() {
		return valuesSize > 1;
	}

	public boolean matches(String s) {
		return operators.stream().anyMatch(s::equalsIgnoreCase);
	}

	public boolean areValuesValid(String... values) {
		if (values == null || values.length < 1) {
			throw new IllegalArgumentException("No Values provided");
		}

		return acceptedValues == null || Stream.of(values).allMatch(value -> acceptedValues.matcher(value).matches());
	}

}
