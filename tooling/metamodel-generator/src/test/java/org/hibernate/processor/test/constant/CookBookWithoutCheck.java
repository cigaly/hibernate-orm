/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.processor.test.constant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;

@Entity
@NamedQuery(name = "#findGoodBooks",
		query = "from CookBookWithoutCheck where bookType = org.hibernate.processor.test.constant.NumericBookType.GOOD")
public class CookBookWithoutCheck {

	@Id
	String isbn;
	String title;
	int bookType;
}
