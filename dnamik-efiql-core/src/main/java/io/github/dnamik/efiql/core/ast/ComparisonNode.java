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

import java.util.List;

import io.github.dnamik.efiql.core.EFIQLCoreVisitor;
import lombok.Value;

/**
 * ComparisonNode
 *
 * @version 1.0.0
 * @since   May 23, 2022
 * @author  Kristijan Georgiev
 *
 */
@Value
public final class ComparisonNode implements Node {

	private final String selector;

	private final List<String> properties;

	private final ComparisonOperator comparisonOperator;

	private final List<String> arguments;

	private final AggregateOperator aggregateOperator;

	public ComparisonNode(String selector,
			List<String> properties,
			ComparisonOperator comparisonOperator,
			List<String> arguments,
			AggregateOperator aggregateOperator) {
		this.selector = selector;
		this.properties = properties;
		this.comparisonOperator = comparisonOperator;
		this.arguments = arguments;
		this.aggregateOperator = aggregateOperator;
	}

	@Override
	public <T> T accept(EFIQLCoreVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
