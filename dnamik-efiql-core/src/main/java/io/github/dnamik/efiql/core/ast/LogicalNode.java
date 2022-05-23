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

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * LogicalNode
 *
 * @version 1.0.0
 * @since   May 23, 2022
 * @author  Kristijan Georgiev
 *
 */
@Data
@RequiredArgsConstructor
public abstract class LogicalNode implements Node {

	protected final LogicalOperator logicalOperator;

	protected final List<Node> children;

	protected LogicalNode(LogicalOperator logicalOperator) {
		this.logicalOperator = logicalOperator;
		this.children = new ArrayList<>();
	}

	protected LogicalNode(LogicalOperator logicalOperator, int childrenSize) {
		this.logicalOperator = logicalOperator;
		this.children = new ArrayList<>(childrenSize);
	}

}
