/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.collection.dereferenced;

import java.util.HashSet;

import org.hibernate.collection.spi.AbstractPersistentCollection;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.EntityEntry;

import org.hibernate.testing.orm.junit.JiraKey;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Gail Badner
 */
@DomainModel(
		annotatedClasses = {
				UnversionedCascadeOne.class,
				Many.class
		}
)
@SessionFactory
public class UnversionedCascadeDereferencedCollectionTest extends AbstractDereferencedCollectionTest {

	@Test
	@JiraKey(value = "HHH-9777")
	public void testMergeNullCollection(SessionFactoryScope scope) {
		UnversionedCascadeOne unversionedCascadeOne = new UnversionedCascadeOne();
		scope.inTransaction(
				session -> {
					assertNull( unversionedCascadeOne.getManies() );
					session.persist( unversionedCascadeOne );
					assertNull( unversionedCascadeOne.getManies() );
					EntityEntry eeOne = getEntityEntry( session, unversionedCascadeOne );
					assertNull( eeOne.getLoadedValue( "manies" ) );
					session.flush();
					assertNull( unversionedCascadeOne.getManies() );
					assertNull( eeOne.getLoadedValue( "manies" ) );
				}
		);


		final String role = UnversionedCascadeOne.class.getName() + ".manies";

		scope.inTransaction(
				session -> {
					UnversionedCascadeOne one = (UnversionedCascadeOne) session.merge( unversionedCascadeOne );

					// after merging, one.getManies() should still be null;
					// the EntityEntry loaded state should contain a PersistentCollection though.

					assertNull( one.getManies() );
					EntityEntry eeOne = getEntityEntry( session, one );
					AbstractPersistentCollection maniesEEOneStateOrig = (AbstractPersistentCollection) eeOne.getLoadedValue(
							"manies" );
					assertNotNull( maniesEEOneStateOrig );

					// Ensure maniesEEOneStateOrig has role, key, and session properly defined (even though one.manies == null)
					assertEquals( role, maniesEEOneStateOrig.getRole() );
					assertEquals( one.getId(), maniesEEOneStateOrig.getKey() );
					assertSame( session, maniesEEOneStateOrig.getSession() );

					// Ensure there is a CollectionEntry for maniesEEOneStateOrig and that the role, persister, and key are set properly.
					CollectionEntry ceManiesOrig = getCollectionEntry( session, maniesEEOneStateOrig );
					assertNotNull( ceManiesOrig );
					assertEquals( role, ceManiesOrig.getRole() );
                    assertSame(
                            scope.getSessionFactory().getRuntimeMetamodels()
									.getMappingMetamodel()
									.getCollectionDescriptor(role),
							ceManiesOrig.getLoadedPersister()
					);
					assertEquals( one.getId(), ceManiesOrig.getKey() );

					session.flush();

					// Ensure the same EntityEntry is being used.
					assertSame( eeOne, getEntityEntry( session, one ) );

					// Ensure one.getManies() is still null.
					assertNull( one.getManies() );

					// Ensure CollectionEntry for maniesEEOneStateOrig is no longer in the PersistenceContext.
					assertNull( getCollectionEntry( session, maniesEEOneStateOrig ) );

					// Ensure the original CollectionEntry has role, persister, and key set to null.
					assertNull( ceManiesOrig.getRole() );
					assertNull( ceManiesOrig.getLoadedPersister() );
					assertNull( ceManiesOrig.getKey() );

					// Ensure the PersistentCollection (that was previously returned by eeOne.getLoadedState())
					// has key and role set to null.
					assertNull( maniesEEOneStateOrig.getKey() );
					assertNull( maniesEEOneStateOrig.getRole() );

					// Ensure eeOne.getLoadedState() returns null for collection after flush.
					assertNull( eeOne.getLoadedValue( "manies" ) );

					// Ensure the session in maniesEEOneStateOrig has been unset.
					assertNull( maniesEEOneStateOrig.getSession() );
				}
		);
	}

	@Test
	@JiraKey(value = "HHH-9777")
	public void testGetAndNullifyCollection(SessionFactoryScope scope) {
		UnversionedCascadeOne unversionedCascadeOne = new UnversionedCascadeOne();
		scope.inTransaction(
				session -> {
					assertNull( unversionedCascadeOne.getManies() );
					session.persist( unversionedCascadeOne );
					assertNull( unversionedCascadeOne.getManies() );
					EntityEntry eeOne = getEntityEntry( session, unversionedCascadeOne );
					assertNull( eeOne.getLoadedValue( "manies" ) );
					session.flush();
					assertNull( unversionedCascadeOne.getManies() );
					assertNull( eeOne.getLoadedValue( "manies" ) );
				}
		);


		final String role = UnversionedCascadeOne.class.getName() + ".manies";

		scope.inTransaction(
				session -> {
					UnversionedCascadeOne one = session.get(
							UnversionedCascadeOne.class,
							unversionedCascadeOne.getId()
					);

					// When returned by Session.get(), one.getManies() will return a PersistentCollection;
					// the EntityEntry loaded state should contain the same PersistentCollection.

					EntityEntry eeOne = getEntityEntry( session, one );
					assertNotNull( one.getManies() );
					AbstractPersistentCollection maniesEEOneStateOrig = (AbstractPersistentCollection) eeOne.getLoadedValue(
							"manies" );
					assertSame( one.getManies(), maniesEEOneStateOrig );

					// Ensure maniesEEOneStateOrig has role, key, and session properly defined (even though one.manies == null)
					assertEquals( role, maniesEEOneStateOrig.getRole() );
					assertEquals( one.getId(), maniesEEOneStateOrig.getKey() );
					assertSame( session, maniesEEOneStateOrig.getSession() );

					// Ensure there is a CollectionEntry for maniesEEOneStateOrig and that the role, persister, and key are set properly.
					CollectionEntry ceManies = getCollectionEntry( session, maniesEEOneStateOrig );
					assertNotNull( ceManies );
					assertEquals( role, ceManies.getRole() );
                    assertSame(
                            scope.getSessionFactory().getRuntimeMetamodels()
									.getMappingMetamodel()
									.getCollectionDescriptor(role),
							ceManies.getLoadedPersister()
					);
					assertEquals( one.getId(), ceManies.getKey() );

					// nullify collection
					one.setManies( null );

					session.flush();

					// Ensure the same EntityEntry is being used.
					assertSame( eeOne, getEntityEntry( session, one ) );

					// Ensure one.getManies() is still null.
					assertNull( one.getManies() );

					// Ensure CollectionEntry for maniesEEOneStateOrig is no longer in the PersistenceContext.
					assertNull( getCollectionEntry( session, maniesEEOneStateOrig ) );

					// Ensure the original CollectionEntry has role, persister, and key set to null.
					assertNull( ceManies.getRole() );
					assertNull( ceManies.getLoadedPersister() );
					assertNull( ceManies.getKey() );

					// Ensure the PersistentCollection (that was previously returned by eeOne.getLoadedState())
					// has key and role set to null.
					assertNull( maniesEEOneStateOrig.getKey() );
					assertNull( maniesEEOneStateOrig.getRole() );

					// Ensure eeOne.getLoadedState() returns null for collection after flush.
					assertNull( eeOne.getLoadedValue( "manies" ) );

					// Ensure the session in maniesEEOneStateOrig has been unset.
					assertNull( maniesEEOneStateOrig.getSession() );
				}
		);
	}

	@Test
	@JiraKey(value = "HHH-9777")
	public void testGetAndReplaceCollection(SessionFactoryScope scope) {
		UnversionedCascadeOne unversionedCascadeOne = new UnversionedCascadeOne();
		scope.inTransaction(
				session -> {
					assertNull( unversionedCascadeOne.getManies() );
					session.persist( unversionedCascadeOne );
					assertNull( unversionedCascadeOne.getManies() );
					EntityEntry eeOne = getEntityEntry( session, unversionedCascadeOne );
					assertNull( eeOne.getLoadedValue( "manies" ) );
					session.flush();
					assertNull( unversionedCascadeOne.getManies() );
					assertNull( eeOne.getLoadedValue( "manies" ) );
				}
		);


		final String role = UnversionedCascadeOne.class.getName() + ".manies";

		scope.inTransaction(
				session -> {
					UnversionedCascadeOne one = session.get(
							UnversionedCascadeOne.class,
							unversionedCascadeOne.getId()
					);

					// When returned by Session.get(), one.getManies() will return a PersistentCollection;
					// the EntityEntry loaded state should contain the same PersistentCollection.

					EntityEntry eeOne = getEntityEntry( session, one );
					assertNotNull( one.getManies() );
					AbstractPersistentCollection maniesEEOneStateOrig = (AbstractPersistentCollection) eeOne.getLoadedValue(
							"manies" );
					assertSame( one.getManies(), maniesEEOneStateOrig );

					// Ensure maniesEEOneStateOrig has role, key, and session properly defined (even though one.manies == null)
					assertEquals( role, maniesEEOneStateOrig.getRole() );
					assertEquals( one.getId(), maniesEEOneStateOrig.getKey() );
					assertSame( session, maniesEEOneStateOrig.getSession() );

					// Ensure there is a CollectionEntry for maniesEEOneStateOrig and that the role, persister, and key are set properly.
					CollectionEntry ceManiesOrig = getCollectionEntry( session, maniesEEOneStateOrig );
					assertNotNull( ceManiesOrig );
					assertEquals( role, ceManiesOrig.getRole() );
                    assertSame(
                            scope.getSessionFactory().getRuntimeMetamodels()
									.getMappingMetamodel()
									.getCollectionDescriptor(role),
							ceManiesOrig.getLoadedPersister()
					);
					assertEquals( one.getId(), ceManiesOrig.getKey() );

					// replace collection
					one.setManies( new HashSet<>() );

					session.flush();

					// Ensure the same EntityEntry is being used.
					assertSame( eeOne, getEntityEntry( session, one ) );

					// Ensure CollectionEntry for maniesEEOneStateOrig is no longer in the PersistenceContext.
					assertNull( getCollectionEntry( session, maniesEEOneStateOrig ) );

					// Ensure the original CollectionEntry has role, persister, and key set to null.
					assertNull( ceManiesOrig.getRole() );
					assertNull( ceManiesOrig.getLoadedPersister() );
					assertNull( ceManiesOrig.getKey() );

					// Ensure the PersistentCollection (that was previously returned by eeOne.getLoadedState())
					// has key and role set to null.
					assertNull( maniesEEOneStateOrig.getKey() );
					assertNull( maniesEEOneStateOrig.getRole() );

					// one.getManies() should be "wrapped" by a PersistentCollection now; role, key, and session should be set properly.
					assertTrue( PersistentCollection.class.isInstance( one.getManies() ) );
					assertEquals( role, ( (PersistentCollection) one.getManies() ).getRole() );
					assertEquals( one.getId(), ( (PersistentCollection) one.getManies() ).getKey() );
					assertSame( session, ( (AbstractPersistentCollection) one.getManies() ).getSession() );

					// Ensure eeOne.getLoadedState() contains the new collection.
					assertSame( one.getManies(), eeOne.getLoadedValue( "manies" ) );

					// Ensure there is a new CollectionEntry for the new collection and that role, persister, and key are set properly.
					CollectionEntry ceManiesAfterReplace = getCollectionEntry(
							session,
							(PersistentCollection) one.getManies()
					);
					assertNotNull( ceManiesAfterReplace );
					assertEquals( role, ceManiesAfterReplace.getRole() );
                    assertSame(
                            scope.getSessionFactory().getRuntimeMetamodels()
									.getMappingMetamodel()
									.getCollectionDescriptor(role),
							ceManiesAfterReplace.getLoadedPersister()
					);
					assertEquals( one.getId(), ceManiesAfterReplace.getKey() );

					// Ensure the session in maniesEEOneStateOrig has been unset.
					assertNull( maniesEEOneStateOrig.getSession() );
				}
		);
	}

}
