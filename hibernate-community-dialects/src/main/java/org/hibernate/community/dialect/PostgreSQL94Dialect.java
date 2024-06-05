/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.community.dialect;

import org.hibernate.dialect.DatabaseVersion;

/**
 * An SQL dialect for Postgres 9.4 and later.
 * Adds support for various date and time functions
 *
 * @deprecated use {@code PostgreSQLLegacyDialect(940)}
 */
@Deprecated
public class PostgreSQL94Dialect extends PostgreSQLLegacyDialect {

	public PostgreSQL94Dialect() {
		super( DatabaseVersion.make( 9, 4 ) );
	}

}
