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

import static org.antlr.v4.runtime.CharStreams.fromString;

import java.util.Set;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import io.github.dnamik.efiql.core.ast.ComparisonOperator;
import io.github.dnamik.efiql.core.ast.Node;

/**
 * EFIQL
 *
 * @version 1.0.0
 * @since   May 23, 2022
 * @author  Kristijan Georgiev
 *
 */
public class EFIQL {

	private final Set<ComparisonOperator> operators;

	private final EFIQLCoreErrorListener errorListener = new EFIQLCoreErrorListener();

	public EFIQL() {
		operators = EFIQLCoreOperators.getInstance().getOperators();
	}

	public EFIQL(Set<ComparisonOperator> operators) {
		this.operators = operators;
	}

	public Node parse(String s) {
		final var lexer = new EFIQLLexer(fromString(s));
		lexer.removeErrorListeners();
		lexer.addErrorListener(errorListener);

		final var parser = new EFIQLParser(new CommonTokenStream(lexer));
		parser.removeErrorListeners();
		parser.addErrorListener(errorListener);

		try {
			final var listener = new EFIQLCoreListener(operators);
			ParseTreeWalker.DEFAULT.walk(listener, parser.root());
			return listener.getRoot();
		} catch (final Exception e) {
			throw new IllegalArgumentException("Unable to parse string");
		}
	}

}
