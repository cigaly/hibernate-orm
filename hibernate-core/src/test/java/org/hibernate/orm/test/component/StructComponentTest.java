/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.component;

import org.hibernate.annotations.Struct;

import org.hibernate.testing.orm.junit.DialectFeatureChecks;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@DomainModel(
		annotatedClasses = StructComponentTest.Book.class
)
@SessionFactory
@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsStructAggregate.class)
public class StructComponentTest {

	@BeforeEach
	public void setUp(SessionFactoryScope scope){
		scope.inTransaction(
				session -> {
					Publisher ebookPublisher = new Publisher();
					ebookPublisher.setName( "eprint" );

					Publisher paperPublisher = new Publisher();
					paperPublisher.setName( "paperbooks" );

					Book book = new Book();
					book.title = "Hibernate";
					book.author = "Steve";
					book.ebookPublisher = ebookPublisher;
					book.paperBackPublisher = paperPublisher;

					session.persist( book );
				}
		);
	}

	@AfterEach
	public void tearDown(SessionFactoryScope scope){
		scope.inTransaction(
				session ->
						session.createQuery( "delete from Book" ).executeUpdate()
		);
	}

	@Test
	public void testGet(SessionFactoryScope scope){
		scope.inTransaction(
				session -> {
					session.createQuery( "from Book" ).list();
				}
		);
	}


	@Entity(name = "Book")
	public static class Book {

		@Id
		@GeneratedValue
		private Long id;

		private String title;

		private String author;

		@Column(name = "ebook_publisher")
		private Publisher ebookPublisher;
		private Publisher paperBackPublisher;
	}

	@Embeddable
	@Struct( name = "publisher_type")
	public static class Publisher {

		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

}
