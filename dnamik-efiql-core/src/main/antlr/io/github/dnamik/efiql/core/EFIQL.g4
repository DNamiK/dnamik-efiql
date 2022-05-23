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
grammar EFIQL;

@header {
    package io.github.dnamik.efiql.core;
}

root: or? EOF;

or: and (OR and)*;

and: constraint (AND constraint)*;

constraint: group | comparison;

group: SBO or SBC;

comparison: selector comparator arguments;

selector:
	property (
		((DOT property)+ (BBO aggregate BBC)?)
		| ((DOT property)*)
	);

property: PROPERTY;

aggregate: PROPERTY;

comparator: (
		(
			(EQ | EXCLAMATION | LT | GT) PROPERTY? (
				EQ
				| EXCLAMATION
				| LT
				| GT
			)
		)
		| (LT | GT)
	);

arguments: (
		(value (COMMA value)*)
		| (MBO value (COMMA value)* MBC)
	)
	| value;

value:
	~(
		'('
		| ')'
		| '['
		| ']'
		| '{'
		| '}'
		| ':'
		| ';'
		| ','
		| '='
		| '<'
		| '>'
		| '!'
		| '\''
		| '"'
		| '.'
	)+
	| SINGLE_QUOTE
	| DOUBLE_QUOTE;

SBO: '(';

OR: ':';

PROPERTY: [a-zA-Z_] [a-zA-Z0-9_]*;

DOT: '.';

BBO: '{';
BBC: '}';

EQ: '=';

EXCLAMATION: '!';

LT: '<';
GT: '>';

MBO: '[';
MBC: ']';

SINGLE_QUOTE: '\'' ('\\\'' | ~('\''))* '\'';
DOUBLE_QUOTE: '"' ('\\"' | ~('"'))* '"';

COMMA: ',';

AND: ';';

SBC: ')';

WS: [ \t\f]+ -> skip;
NL: '\r'? '\n' -> skip;

ANY: .;
