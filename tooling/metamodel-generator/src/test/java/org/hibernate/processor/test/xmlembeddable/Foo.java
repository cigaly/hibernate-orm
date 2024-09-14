/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.processor.test.xmlembeddable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * @author Hardy Ferentschik
 */
@Entity
public class Foo {
	@Id
	long id;
}
