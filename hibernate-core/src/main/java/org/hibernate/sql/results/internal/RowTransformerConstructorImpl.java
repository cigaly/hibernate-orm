/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */

package org.hibernate.sql.results.internal;

import jakarta.persistence.TupleElement;
import org.hibernate.InstantiationException;
import org.hibernate.sql.results.spi.RowTransformer;

import java.lang.reflect.Constructor;
import java.util.List;

import org.hibernate.query.sqm.SqmExpressible;
import org.hibernate.query.sqm.tree.SqmExpressibleAccessor;

import static org.hibernate.query.sqm.tree.expression.Compatibility.areAssignmentCompatible;

/**
 * {@link RowTransformer} instantiating an arbitrary class
 *
 * @author Gavin King
 */
public class RowTransformerConstructorImpl<T> implements RowTransformer<T> {
	private final Class<T> type;
	private final Constructor<T> constructor;

	public RowTransformerConstructorImpl(Class<T> type, TupleMetadata tupleMetadata) {
		this.type = type;
		final List<TupleElement<?>> elements = tupleMetadata.getList();
		final Class<?>[] sig = new Class[elements.size()];
		for (int i = 0; i < elements.size(); i++) {
			sig[i] = resolveElementJavaType( elements.get( i ) );
		}
		if ( sig.length == 1 && sig[0] == null ) {
			// Can not (properly) resolve constructor for single null element
			throw new InstantiationException( "Cannot instantiate query result type, argument types are unknown ", type );
		}
		try {
			constructor = findMatchingConstructor( type, sig );
			constructor.setAccessible( true );
		}
		catch (Exception e) {
			//TODO try again with primitive types
			throw new InstantiationException( "Cannot instantiate query result type ", type, e );
		}
	}

	private static Class<?> resolveElementJavaType(TupleElement<?> element) {
		if ( element instanceof SqmExpressibleAccessor ) {
			final SqmExpressible<?> expressible = ( (SqmExpressibleAccessor<?>) element ).getExpressible();
			if ( expressible != null && expressible.getExpressibleJavaType() != null ) {
				return expressible.getExpressibleJavaType().getJavaTypeClass();
			}
		}

		return element.getJavaType();
	}

	private Constructor<T> findMatchingConstructor(Class<T> type, Class<?>[] sig) throws Exception {
		try {
			return type.getDeclaredConstructor( sig );
		}
		catch (NoSuchMethodException | SecurityException e) {
			constructor_loop:
			for ( final Constructor<?> constructor : type.getDeclaredConstructors() ) {
				final Class<?>[] parameterTypes = constructor.getParameterTypes();
				if ( parameterTypes.length == sig.length ) {
					for ( int i = 0; i < sig.length; i++ ) {
						final Class<?> parameterType = parameterTypes[i];
						final Class<?> argType = sig[i];
						final boolean assignmentCompatible;
						assignmentCompatible =
								argType == null && !parameterType.isPrimitive()
								|| areAssignmentCompatible(
										parameterType,
										argType
								);
						if ( !assignmentCompatible ) {
							continue constructor_loop;
						}
					}
					return (Constructor<T>) constructor;
				}
			}
			throw e;
		}
	}

	@Override
	public T transformRow(Object[] row) {
		try {
			return constructor.newInstance( row );
		}
		catch (Exception e) {
			throw new InstantiationException( "Cannot instantiate query result type", type, e );
		}
	}

	@Override
	public int determineNumberOfResultElements(int rawElementCount) {
		return 1;
	}
}
