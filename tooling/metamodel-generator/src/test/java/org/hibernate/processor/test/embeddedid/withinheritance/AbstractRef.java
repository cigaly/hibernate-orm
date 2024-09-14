/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.processor.test.embeddedid.withinheritance;

import java.io.Serializable;

/**
 * @author Hardy Ferentschik
 */
public abstract class AbstractRef implements Serializable {
	private final int id;

	protected AbstractRef() {
		// required by JPA
		id = 0;
	}

	protected AbstractRef(final int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
