/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.dialect.function.array;

import org.hibernate.metamodel.mapping.MappingModelExpressible;
import org.hibernate.query.sqm.produce.function.FunctionArgumentTypeResolver;
import org.hibernate.query.sqm.sql.SqmToSqlAstConverter;
import org.hibernate.query.sqm.tree.SqmTypedNode;
import org.hibernate.query.sqm.tree.expression.SqmExpression;
import org.hibernate.query.sqm.tree.expression.SqmFunction;

/**
 * A {@link FunctionArgumentTypeResolver} that resolves the argument types for the {@code array_includes} function.
 */
public class ArrayIncludesArgumentTypeResolver implements FunctionArgumentTypeResolver {

	public static final FunctionArgumentTypeResolver INSTANCE = new ArrayIncludesArgumentTypeResolver();

	@Override
	public MappingModelExpressible<?> resolveFunctionArgumentType(
			SqmFunction<?> function,
			int argumentIndex,
			SqmToSqlAstConverter converter) {
		if ( argumentIndex == 0 ) {
			final SqmTypedNode<?> node = function.getArguments().get( 1 );
			if ( node instanceof SqmExpression<?> ) {
				return converter.determineValueMapping( (SqmExpression<?>) node );
			}
		}
		else if ( argumentIndex == 1 ) {
			final SqmTypedNode<?> node = function.getArguments().get( 0 );
			if ( node instanceof SqmExpression<?> ) {
				return converter.determineValueMapping( (SqmExpression<?>) node );
			}
		}
		return null;
	}
}
