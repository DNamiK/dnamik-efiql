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

import static io.github.dnamik.efiql.core.ast.LogicalOperator.AND;

import io.github.dnamik.efiql.core.EFIQLCoreVisitor;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * AndNode
 *
 * @version 1.0.0
 * @since   May 23, 2022
 * @author  Kristijan Georgiev
 *
 */
@Value
@EqualsAndHashCode(callSuper = true)
public final class AndNode extends LogicalNode {

	public AndNode(int childrenSize) {
		super(AND, childrenSize);
	}

	@Override
	public <T> T accept(EFIQLCoreVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
