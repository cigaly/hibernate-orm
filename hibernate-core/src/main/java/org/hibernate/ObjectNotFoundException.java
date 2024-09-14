/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate;

/**
 * Thrown when {@link Session#find(Class, Object)} fails to select a row
 * with the given primary key (identifier value).
 * <p>
 * On the other hand, this exception might not be thrown immediately by
 * {@link Session#getReference(Class, Object)} is called, even when there
 * was no row on the database, because {@code getReference()} returns a
 * proxy if possible. Programs should use {@code Session.find()} to test
 * if a row exists in the database.
 * <p>
 * Like all Hibernate exceptions, this one is considered unrecoverable.
 *
 * @author Gavin King
 */
public class ObjectNotFoundException extends UnresolvableObjectException {
	/**
	 * Constructs a {@code ObjectNotFoundException} using the given information.
	 *
	 *  @param identifier The identifier of the entity
	 * @param entityName The name of the entity
	 */
	public ObjectNotFoundException(Object identifier, String entityName) {
		super( identifier, entityName );
	}

	public ObjectNotFoundException(String entityName, Object identifier) {
		this( identifier, entityName );
	}
}
