/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.sql.results;

import org.hibernate.HibernateException;
import org.hibernate.sql.results.graph.DomainResult;

/**
 * Base for problems creating {@link DomainResult}
 * instances
 *
 * @author Steve Ebersole
 */
public class DomainResultCreationException extends HibernateException {
	public DomainResultCreationException(String message) {
		super( message );
	}

	public DomainResultCreationException(String message, Throwable cause) {
		super( message, cause );
	}
}
