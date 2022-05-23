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
package io.github.dnamik.efiql.core;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

import java.util.Set;
import java.util.regex.Pattern;

import io.github.dnamik.efiql.core.ast.ComparisonOperator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * EFIQLCoreOperators
 *
 * @version 1.0.0
 * @since   May 23, 2022
 * @author  Kristijan Georgiev
 *
 */
@Value
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EFIQLCoreOperators {

	private static final Pattern BOOLEAN_PATTERN = compile("true|false", CASE_INSENSITIVE);

	private static EFIQLCoreOperators instance;

	public static EFIQLCoreOperators getInstance() {
		if (instance == null) {
			instance = new EFIQLCoreOperators();
		}

		return instance;
	}

	private final ComparisonOperator nullOp = new ComparisonOperator(BOOLEAN_PATTERN, "=null=");
	private final ComparisonOperator emptyOp = new ComparisonOperator(BOOLEAN_PATTERN, "=empty=");
	private final ComparisonOperator equalOp = new ComparisonOperator("==", "=eq=");
	private final ComparisonOperator notEqualOp = new ComparisonOperator("!=", "=ne=");
	private final ComparisonOperator lessThanOp = new ComparisonOperator("<", "=lt=");
	private final ComparisonOperator lessThanOrEqualOp = new ComparisonOperator("<=", "=le=");
	private final ComparisonOperator greaterThanOp = new ComparisonOperator(">", "=gt=");
	private final ComparisonOperator greaterThanOrEqualOp = new ComparisonOperator(">=", "=ge=");
	private final ComparisonOperator inOp = new ComparisonOperator(true, ">>", "=in=");
	private final ComparisonOperator outOp = new ComparisonOperator(true, "<<", "=out=");
	private final ComparisonOperator betweenOp = new ComparisonOperator(2, "><", "=between=");
	private final ComparisonOperator notBetweenOp = new ComparisonOperator(2, "<>", "=notBetween=");

	private final Set<ComparisonOperator> operators = Set.of(
			nullOp,
			emptyOp,
			equalOp,
			notEqualOp,
			lessThanOp,
			lessThanOrEqualOp,
			greaterThanOp,
			greaterThanOrEqualOp,
			inOp,
			outOp,
			betweenOp,
			notBetweenOp);

}
