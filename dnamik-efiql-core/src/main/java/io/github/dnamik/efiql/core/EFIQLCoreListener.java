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
import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.Set;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.RuleContext;

import io.github.dnamik.efiql.core.EFIQLParser.AndContext;
import io.github.dnamik.efiql.core.EFIQLParser.ComparisonContext;
import io.github.dnamik.efiql.core.EFIQLParser.OrContext;
import io.github.dnamik.efiql.core.ast.AggregateOperator;
import io.github.dnamik.efiql.core.ast.AndNode;
import io.github.dnamik.efiql.core.ast.ComparisonNode;
import io.github.dnamik.efiql.core.ast.ComparisonOperator;
import io.github.dnamik.efiql.core.ast.LogicalNode;
import io.github.dnamik.efiql.core.ast.Node;
import io.github.dnamik.efiql.core.ast.OrNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * EFIQLCoreListener
 *
 * @version 1.0.0
 * @since   May 23, 2022
 * @author  Kristijan Georgiev
 *
 */
@RequiredArgsConstructor
public class EFIQLCoreListener extends EFIQLBaseListener {

	private static final Pattern SINGLE_QUOTES_PATTERN = compile("^'.*'$", CASE_INSENSITIVE);
	private static final Pattern DOUBLE_QUOTES_PATTERN = compile("^\".*\"$", CASE_INSENSITIVE);

	private final Set<ComparisonOperator> comparisonOperators;

	@Getter
	private Node root;

	private LogicalNode currentNode;

	private LogicalNode currentParentNode;

	@Override
	public void enterOr(OrContext ctx) {
		if (addOrNode(ctx)) {
			currentParentNode = currentNode;

			if (root == null) {
				currentNode = new OrNode(ctx.and().size());
				root = currentNode;
			} else {
				final var newNode = new OrNode(ctx.and().size());
				currentNode.getChildren().add(newNode);
				currentNode = newNode;
			}
		}
	}

	@Override
	public void enterAnd(AndContext ctx) {
		if (addAndNode(ctx)) {
			currentParentNode = currentNode;

			final int childrenSize;
			if (ctx.constraint().size() > 1) {
				childrenSize = ctx.constraint().size();
			} else {
				childrenSize = ctx.constraint(0).group().or().and().size();
			}

			if (root == null) {
				currentNode = new AndNode(childrenSize);
				root = currentNode;
			} else {
				final var newNode = new AndNode(childrenSize);
				currentNode.getChildren().add(newNode);
				currentNode = newNode;
			}
		}
	}

	@Override
	public void enterComparison(ComparisonContext ctx) {
		final var newNode = new ComparisonNode(
				ctx.selector().getText(),
				ctx.selector()
						.property()
						.stream()
						.map(RuleContext::getText)
						.collect(toUnmodifiableList()),
				comparisonOperators.stream()
						.filter(c -> c.matches(ctx.comparator().getText()))
						.findFirst()
						.orElseThrow(() -> new IllegalArgumentException("Invalid Comparison Operator")),
				ctx.arguments()
						.value()
						.stream()
						.map(RuleContext::getText)
						.map(this::parseValue)
						.collect(toUnmodifiableList()),
				ctx.selector().aggregate() == null ? null : new AggregateOperator(ctx.selector().aggregate().getText()));

		if (root == null) {
			root = newNode;
		} else {
			currentNode.getChildren().add(newNode);
		}
	}

	@Override
	public void exitAnd(AndContext ctx) {
		if (root != null && addAndNode(ctx)) {
			currentNode = currentParentNode;
		}
	}

	@Override
	public void exitOr(OrContext ctx) {
		if (root != null && addOrNode(ctx)) {
			currentNode = currentParentNode;
		}
	}

	private boolean addOrNode(OrContext ctx) {
		return ctx.and().size() > 1;
	}

	private boolean addAndNode(AndContext ctx) {
		return ctx.constraint().size() > 1
				|| ctx.constraint().size() == 1 && ctx.constraint(0).group() != null && ctx.constraint(0).group().or().and().size() > 1;
	}

	private String parseValue(String s) {
		return SINGLE_QUOTES_PATTERN.matcher(s).matches() || DOUBLE_QUOTES_PATTERN.matcher(s).matches() ? cutOrEmpty(s) : s;
	}

	private String cutOrEmpty(String s) {
		return s.length() > 2 ? s.substring(1, s.length() - 1) : "";
	}

}
