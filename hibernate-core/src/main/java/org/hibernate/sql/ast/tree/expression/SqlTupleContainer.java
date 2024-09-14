/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.sql.ast.tree.expression;

import org.hibernate.sql.ast.tree.SqlAstNode;

/**
 * @author Steve Ebersole
 */
public interface SqlTupleContainer {
	SqlTuple getSqlTuple();

	static SqlTuple getSqlTuple(SqlAstNode expression) {
		if ( expression instanceof SqlTupleContainer ) {
			return ( (SqlTupleContainer) expression ).getSqlTuple();
		}
		return null;
	}
}
