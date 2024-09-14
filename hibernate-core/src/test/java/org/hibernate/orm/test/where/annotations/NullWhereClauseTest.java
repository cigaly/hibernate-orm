/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.where.annotations;

import org.hibernate.annotations.SQLRestriction;
import org.hibernate.cfg.AvailableSettings;

import org.hibernate.testing.orm.junit.JiraKey;
import org.hibernate.testing.orm.junit.EntityManagerFactoryScope;
import org.hibernate.testing.orm.junit.Jpa;
import org.hibernate.testing.orm.junit.Setting;
import org.junit.jupiter.api.Test;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Jpa(
		annotatedClasses = NullWhereClauseTest.Person.class,
		properties = {
				@Setting(name = AvailableSettings.GLOBALLY_QUOTED_IDENTIFIERS, value = "true"),
		}
)
@JiraKey(value = "HHH-15358")
public class NullWhereClauseTest {

	@Test
	public void testIt(EntityManagerFactoryScope scope) {
		scope.inTransaction(
				entityManager ->
						entityManager.persist( new Person( 1, "Andrea" ) )
		);
	}

	@Entity
	@Table(name = "person")
	@SQLRestriction("`used` IS NULL")
	public static class Person {
		@Id
		private Integer id;

		private String name;

		private String used;

		public Person() {
			super();
		}

		public Person(Integer id, String name) {
			this.id = id;
			this.name = name;
		}
	}
}
