/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.dialect.function.array;

import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.query.sqm.function.AbstractSqmSelfRenderingFunctionDescriptor;
import org.hibernate.query.sqm.produce.function.StandardArgumentsValidators;
import org.hibernate.query.sqm.produce.function.StandardFunctionReturnTypeResolvers;
import org.hibernate.type.spi.TypeConfiguration;

import org.jboss.logging.Logger;

import java.lang.invoke.MethodHandles;

/**
 * Encapsulates the validator, return type and argument type resolvers for the array_contains function.
 * Subclasses only have to implement the rendering.
 */
public abstract class AbstractArrayContainsFunction extends AbstractSqmSelfRenderingFunctionDescriptor {

	protected static final DeprecationLogger LOG = Logger.getMessageLogger( MethodHandles.lookup(), DeprecationLogger.class, AbstractArrayContainsFunction.class.getName() );

	protected final boolean nullable;

	public AbstractArrayContainsFunction(boolean nullable, TypeConfiguration typeConfiguration) {
		super(
				"array_contains" + ( nullable ? "_nullable" : "" ),
				StandardArgumentsValidators.composite(
						StandardArgumentsValidators.exactly( 2 ),
						ArrayContainsArgumentValidator.INSTANCE
				),
				StandardFunctionReturnTypeResolvers.invariant( typeConfiguration.standardBasicTypeForJavaType( Boolean.class ) ),
				ArrayContainsArgumentTypeResolver.INSTANCE
		);
		this.nullable = nullable;
	}

	@Override
	public String getArgumentListSignature() {
		return "(ARRAY haystackArray, OBJECT needleElementOrArray)";
	}
}
