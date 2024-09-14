/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.persister.entity;

import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLSelect;
import org.hibernate.annotations.SQLUpdate;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.sql.model.jdbc.JdbcMutationOperation;

import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.RequiresDialect;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Test;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Laabidi RAISSI
 */
@DomainModel(annotatedClasses = {
		CustomSqlSchemaResolvingIdentityTest.CustomEntity.class, CustomSqlSchemaResolvingIdentityTest.Dummy.class
})
@SessionFactory
@RequiresDialect(H2Dialect.class)
public class CustomSqlSchemaResolvingIdentityTest {

	@Test
	public void testSchemaNotReplacedInCustomSQL(SessionFactoryScope scope) throws Exception {

		String className = CustomEntity.class.getName();

        final AbstractEntityPersister persister = (AbstractEntityPersister) scope.getSessionFactory().getMappingMetamodel().getEntityDescriptor(className);
		String insertQuery = ( (JdbcMutationOperation) persister.getInsertCoordinator().getStaticMutationOperationGroup().getSingleOperation() ).getSqlString();
		String updateQuery = ( (JdbcMutationOperation) persister.getUpdateCoordinator().getStaticMutationOperationGroup().getSingleOperation() ).getSqlString();
		String deleteQuery = ( (JdbcMutationOperation) persister.getDeleteCoordinator().getStaticMutationOperationGroup().getSingleOperation() ).getSqlString();

		assertEquals( "Incorrect custom SQL for insert in  Entity: " + className,
				"INSERT INTO FOO (name) VALUES (?)", insertQuery );

		assertEquals( "Incorrect custom SQL for delete in  Entity: " + className,
				"DELETE FROM FOO WHERE id = ?", deleteQuery );

		assertEquals( "Incorrect custom SQL for update in  Entity: " + className,
				"UPDATE FOO SET name = ? WHERE id = ?", updateQuery );

		CustomEntity _entitty = scope.fromTransaction( session -> {
			CustomEntity entity = new CustomEntity();
			session.persist( entity );

			return entity;
		} );

		scope.inTransaction( session -> {
			CustomEntity entity = session.find( CustomEntity.class, 1 );
			assertNotNull(entity);

			entity.name = "Vlad";
		} );

		scope.inTransaction( session -> {
			CustomEntity entity = session.find( CustomEntity.class, _entitty.id );
			session.remove( entity );
		} );

		scope.inTransaction( session -> {
			CustomEntity entity = session.find( CustomEntity.class, _entitty.id );
			assertNull(entity);
		} );
	}

	@Entity(name = "CardWithCustomSQL")
	@SQLSelect(sql = "SELECT id, name FROM {h-schema}FOO WHERE id = ?")
	@SQLInsert(sql = "INSERT INTO {h-schema}FOO (name) VALUES (?)")
	@SQLDelete(sql = "DELETE FROM {h-schema}FOO WHERE id = ?", check = ResultCheckStyle.COUNT)
	@SQLUpdate(sql = "UPDATE {h-schema}FOO SET name = ? WHERE id = ? ")
	public static class CustomEntity {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		public Integer id;

		private String name;
	}

	@Entity(name = "Dummy")
	@Table(name = "FOO")
	public static class Dummy {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		public Integer id;

		private String name;
	}
}
