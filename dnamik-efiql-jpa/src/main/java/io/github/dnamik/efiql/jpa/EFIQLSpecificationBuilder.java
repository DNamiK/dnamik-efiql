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
package io.github.dnamik.efiql.jpa;

import static io.github.dnamik.efiql.core.ast.LogicalOperator.AND;
import static io.github.dnamik.efiql.core.ast.LogicalOperator.OR;
import static org.springframework.data.jpa.domain.Specification.where;

import java.util.Objects;

import org.springframework.data.jpa.domain.Specification;

import io.github.dnamik.efiql.core.ast.ComparisonNode;
import io.github.dnamik.efiql.core.ast.LogicalNode;
import io.github.dnamik.efiql.core.ast.Node;

/**
 * EFIQLSpecificationBuilder
 *
 * @version     1.0.0
 * @since       May 25, 2022
 * @author      Kristijan Georgiev
 *
 * @param   <T>
 */
public class EFIQLSpecificationBuilder<T> {

	public Specification<T> createSpecification(Node node) {
		if (node instanceof LogicalNode) {
			return createSpecification((LogicalNode) node);
		} else if (node instanceof ComparisonNode) {
			return createSpecification((ComparisonNode) node);
		} else {
			return null;
		}
	}

	private Specification<T> createSpecification(LogicalNode logicalNode) {
		final var specs = logicalNode.getChildren().stream().map(this::createSpecification).filter(Objects::nonNull).toList();

		var result = specs.get(0);

		if (logicalNode.getLogicalOperator() == AND) {
			for (final Specification<T> spec : specs) {
				result = where(result).and(spec);
			}
		} else if (logicalNode.getLogicalOperator() == OR) {
			for (final Specification<T> spec : specs) {
				result = where(result).or(spec);
			}
		}

		return result;
	}

	public Specification<T> createSpecification(ComparisonNode comparisonNode) {
		return where(new EFIQLSpecification<T>(comparisonNode.getSelector(), comparisonNode.getComparisonOperator(), comparisonNode.getArguments()));
	}

}
