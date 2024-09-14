/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.aggregation;

import java.util.UUID;

import org.hibernate.testing.orm.junit.JiraKey;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@DomainModel(annotatedClasses = { UuidAggregationTest.UuidIdentifiedEntity.class })
@SessionFactory
public class UuidAggregationTest {

	@Entity(name = "UuidIdentifiedEntity")
	public static class UuidIdentifiedEntity {

		@Id
		private UUID id;

		public UuidIdentifiedEntity() {
			super();
		}

		public UUID getId() {
			return id;
		}

		public void setId(final UUID id) {
			this.id = id;
		}
	}

	@Test
	@JiraKey(value = "HHH-15495")
	public void testMaxUuid(SessionFactoryScope scope) {
		scope.inTransaction(
				s -> {
					s.createSelectionQuery( "select max(id) from UuidIdentifiedEntity", UUID.class ).getSingleResult();
				}
		);
	}
}
