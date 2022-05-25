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

import org.springframework.data.jpa.domain.Specification;

import io.github.dnamik.efiql.core.EFIQLCoreVisitor;
import io.github.dnamik.efiql.core.ast.AndNode;
import io.github.dnamik.efiql.core.ast.ComparisonNode;
import io.github.dnamik.efiql.core.ast.OrNode;

/**
 * EFIQLSpecificationVisitor
 *
 * @version     1.0.0
 * @since       May 25, 2022
 * @author      Kristijan Georgiev
 *
 * @param   <T>
 */
public class EFIQLSpecificationVisitor<T> implements EFIQLCoreVisitor<Specification<T>> {

	private final EFIQLSpecificationBuilder<T> builder = new EFIQLSpecificationBuilder<>();

	@Override
	public Specification<T> visit(AndNode node) {
		return builder.createSpecification(node);
	}

	@Override
	public Specification<T> visit(OrNode node) {
		return builder.createSpecification(node);
	}

	@Override
	public Specification<T> visit(ComparisonNode node) {
		return builder.createSpecification(node);
	}

}
