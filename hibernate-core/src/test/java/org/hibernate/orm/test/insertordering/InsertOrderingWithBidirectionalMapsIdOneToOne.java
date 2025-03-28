/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.insertordering;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;

import org.hibernate.testing.orm.junit.JiraKey;
import org.junit.jupiter.api.Test;

/**
 * @author Vlad Mihalcea
 */
@JiraKey(value = "HHH-9864")
public class InsertOrderingWithBidirectionalMapsIdOneToOne extends BaseInsertOrderingTest {

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] { Address.class, Person.class };
	}

	@Test
	public void testBatching() {
		sessionFactoryScope().inTransaction( session -> {
			Person worker = new Person();
			Person homestay = new Person();

			Address home = new Address();
			Address office = new Address();

			home.addPerson( homestay );

			office.addPerson( worker );

			session.persist( home );
			session.persist( office );

			clearBatches();
		} );

		verifyContainsBatches(
				new Batch( "insert into Address (street,ID) values (?,?)", 2 ),
				new Batch( "insert into Person (name,address_ID) values (?,?)", 2 )
		);
	}

	@Entity(name = "Address")
	public static class Address {
		@Id
		@Column(name = "ID", nullable = false)
		@SequenceGenerator(name = "ID", sequenceName = "ADDRESS_SEQ")
		@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ID")
		private Long id;

		private String street;

		@OneToOne(mappedBy = "address", cascade = CascadeType.PERSIST)
		private Person person;

		public void addPerson(Person person) {
			this.person = person;
			person.address = this;
		}
	}

	@Entity(name = "Person")
	public static class Person {
		@Id
		private Long id;

		private String name;

		@OneToOne
		@MapsId
		private Address address;
	}
}
