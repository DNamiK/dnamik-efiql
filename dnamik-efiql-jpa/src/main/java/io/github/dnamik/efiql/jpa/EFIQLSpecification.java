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

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;

import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.hibernate.query.criteria.internal.path.SingularAttributePath;
import org.springframework.data.jpa.domain.Specification;

import io.github.dnamik.efiql.core.EFIQLCoreOperators;
import io.github.dnamik.efiql.core.ast.ComparisonOperator;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * EFIQLSpecification
 *
 * @version     1.0.0
 * @since       May 25, 2022
 * @author      Kristijan Georgiev
 *
 * @param   <T>
 */

@Value
@RequiredArgsConstructor
public class EFIQLSpecification<T> implements Specification<T> {

	private static final long serialVersionUID = -8873270151558661040L;

	private static final Set<ComparisonOperator> LENGTH_SIZE_OPERATORS = Set.of(
			EFIQLCoreOperators.getInstance().getGreaterThanOp(),
			EFIQLCoreOperators.getInstance().getGreaterThanOrEqualOp(),
			EFIQLCoreOperators.getInstance().getLessThanOp(),
			EFIQLCoreOperators.getInstance().getLessThanOrEqualOp(),
			EFIQLCoreOperators.getInstance().getBetweenOp(),
			EFIQLCoreOperators.getInstance().getNotBetweenOp());

	private final transient String property;
	private final transient ComparisonOperator operator;
	private final transient List<String> arguments;

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Predicate toPredicate(Root<T> r, CriteriaQuery<?> query, CriteriaBuilder cb) {
		final var root = parseProperty(property, r);

		if (operator.equals(EFIQLCoreOperators.getInstance().getNullOp())) {
			return parseBoolean(arguments.get(0)) ? cb.isNull(root) : cb.isNotNull(root);
		} else if (operator.equals(EFIQLCoreOperators.getInstance().getEmptyOp())) {
			return parseBoolean(arguments.get(0)) ? getEmpty(root, cb) : cb.not(getEmpty(root, cb));
		}

		final var args = castArguments(root.getJavaType());
		final var arg = args.get(0);

		if (operator.equals(EFIQLCoreOperators.getInstance().getEqualOp())) {
			return arg instanceof String ? cb.like(root, parseLikeQuery((String) arg)) : cb.equal(sizeOrRoot(root, cb), arg);
		} else if (operator.equals(EFIQLCoreOperators.getInstance().getNotEqualOp())) {
			return arg instanceof String ? cb.notLike(root, parseLikeQuery((String) arg)) : cb.notEqual(sizeOrRoot(root, cb), arg);
		} else if (operator.equals(EFIQLCoreOperators.getInstance().getGreaterThanOp())) {
			return cb.greaterThan(sizeLengthOrRoot(root, cb), (Comparable) arg);
		} else if (operator.equals(EFIQLCoreOperators.getInstance().getGreaterThanOrEqualOp())) {
			return cb.greaterThanOrEqualTo(sizeLengthOrRoot(root, cb), (Comparable) arg);
		} else if (operator.equals(EFIQLCoreOperators.getInstance().getLessThanOp())) {
			return cb.lessThan(sizeLengthOrRoot(root, cb), (Comparable) arg);
		} else if (operator.equals(EFIQLCoreOperators.getInstance().getLessThanOrEqualOp())) {
			return cb.lessThanOrEqualTo(sizeLengthOrRoot(root, cb), (Comparable) arg);
		} else if (operator.equals(EFIQLCoreOperators.getInstance().getInOp())) {
			return root.in(args);
		} else if (operator.equals(EFIQLCoreOperators.getInstance().getOutOp())) {
			return cb.not(root.in(args));
		} else if (operator.equals(EFIQLCoreOperators.getInstance().getBetweenOp())) {
			return cb.between(sizeLengthOrRoot(root, cb), (Comparable) args.get(0), (Comparable) args.get(1));
		} else if (operator.equals(EFIQLCoreOperators.getInstance().getNotBetweenOp())) {
			return cb.not(cb.between(sizeLengthOrRoot(root, cb), (Comparable) args.get(0), (Comparable) args.get(1)));
		} else {
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Path<String> parseProperty(String property, Root root) {
		if (property.contains(".")) {
			final var pathSteps = property.split("\\.");

			var path = root.get(pathSteps[0]);

			From lastFrom = root;

			for (var i = 1; i <= pathSteps.length - 1; i++) {
				if (path instanceof PluralAttributePath) {
					final var join = getJoin(((PluralAttributePath) path).getAttribute(), lastFrom);
					path = join.get(pathSteps[i]);
					lastFrom = join;
				} else if (path instanceof SingularAttributePath) {
					final var attr = ((SingularAttributePath) path).getAttribute();
					if (attr.getPersistentAttributeType() != Attribute.PersistentAttributeType.BASIC) {
						final var join = lastFrom.join(attr, JoinType.LEFT);
						path = join.get(pathSteps[i]);
						lastFrom = join;
					} else {
						path = path.get(pathSteps[i]);
					}
				} else {
					path = path.get(pathSteps[i]);
				}
			}

			return path;
		} else {
			return root.get(property);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Join getJoin(PluralAttribute attr, From from) {
		switch (attr.getCollectionType()) {
		case SET:
			return from.join((SetAttribute) attr);
		case LIST:
			return from.join((ListAttribute) attr);
		case MAP:
			return from.join((MapAttribute) attr);
		default:
			return from.join((CollectionAttribute) attr);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List castArguments(final Class type) {
		return arguments.stream().map(arg -> {
			if (type.equals(boolean.class) || type.equals(Boolean.class)) {
				return parseBoolean(arg);
			} else if (type.equals(byte.class) || type.equals(Byte.class)) {
				return Byte.parseByte(arg);
			} else if (type.equals(short.class) || type.equals(Short.class)) {
				return Short.parseShort(arg);
			} else if (type.equals(int.class) || type.equals(Integer.class)) {
				return parseInt(arg);
			} else if (type.equals(long.class) || type.equals(Long.class)) {
				return parseLong(arg);
			} else if (type.equals(float.class) || type.equals(Float.class)) {
				return Float.parseFloat(arg);
			} else if (type.equals(double.class) || type.equals(Double.class)) {
				return Double.parseDouble(arg);
			} else if (type.equals(BigInteger.class)) {
				return new BigInteger(arg);
			} else if (type.equals(BigDecimal.class)) {
				return new BigDecimal(arg);
			} else if (type.equals(char.class) || type.equals(Character.class)) {
				return arg.charAt(0);
			} else if (type.equals(Year.class)) {
				return Year.parse(arg);
			} else if (type.equals(YearMonth.class)) {
				return YearMonth.parse(arg);
			} else if (type.equals(Instant.class)) {
				return Instant.parse(arg);
			} else if (type.equals(LocalTime.class)) {
				return LocalTime.parse(arg);
			} else if (type.equals(LocalDate.class)) {
				return LocalDate.parse(arg);
			} else if (type.equals(LocalDateTime.class)) {
				return LocalDateTime.parse(arg);
			} else if (type.equals(OffsetTime.class)) {
				return OffsetTime.parse(arg);
			} else if (type.equals(OffsetDateTime.class)) {
				return OffsetDateTime.parse(arg);
			} else if (type.equals(ZonedDateTime.class)) {
				return ZonedDateTime.parse(arg);
			} else if (type.isEnum()) {
				return Enum.valueOf((Class<Enum>) type, arg);
			} else if (LENGTH_SIZE_OPERATORS.contains(operator)
					&& (Collection.class.isAssignableFrom(type) || CharSequence.class.isAssignableFrom(type))) {
				return parseInt(arg);
			} else if ((operator.equals(EFIQLCoreOperators.getInstance().getEqualOp())
					|| operator.equals(EFIQLCoreOperators.getInstance().getNotEqualOp()))
					&& Collection.class.isAssignableFrom(type)) {
				return parseInt(arg);
			} else {
				return arg;
			}
		}).toList();
	}

	@SuppressWarnings("unchecked")
	private Predicate getEmpty(Path<String> root, CriteriaBuilder cb) {
		return CharSequence.class.isAssignableFrom(root.getJavaType()) ? cb.equal(cb.length(root), 0) : cb.isEmpty(toRootType(root));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Expression sizeOrRoot(Path<String> root, CriteriaBuilder cb) {
		if (Collection.class.isAssignableFrom(root.getJavaType())) {
			return cb.size(toRootType(root));
		} else {
			return root;
		}
	}

	@SuppressWarnings({ "rawtypes" })
	private Expression sizeLengthOrRoot(Path<String> root, CriteriaBuilder cb) {
		if (CharSequence.class.isAssignableFrom(root.getJavaType())) {
			return cb.length(root);
		} else {
			return sizeOrRoot(root, cb);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Expression toRootType(Path<String> root) {
		return root.as((Class) root.getJavaType());
	}

	private static String parseLikeQuery(String s) {
		return s.replace('*', '%');
	}

}
