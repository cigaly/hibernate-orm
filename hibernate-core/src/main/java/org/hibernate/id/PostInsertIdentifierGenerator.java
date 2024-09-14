/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.id;

import org.hibernate.generator.EventType;
import org.hibernate.generator.EventTypeSets;
import org.hibernate.generator.OnExecutionGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.util.EnumSet;
import java.util.Properties;

import static org.hibernate.generator.EventTypeSets.INSERT_ONLY;

/**
 * The counterpart to {@link IdentifierGenerator} for values generated by the database.
 * This interface is no longer the only way to handle database-generate identifiers.
 * Any {@link OnExecutionGenerator} with timing {@link EventTypeSets#INSERT_ONLY} may now
 * be used.
 *
 * @see IdentifierGenerator
 *
 * @author Gavin King
 */
public interface PostInsertIdentifierGenerator extends OnExecutionGenerator, Configurable {

	/**
	 * @return {@link EventTypeSets#INSERT_ONLY}
	 */
	@Override
	default EnumSet<EventType> getEventTypes() {
		return INSERT_ONLY;
	}

	/**
	 * @return {@code false}, since we don't usually have a meaningful property value
	 *         for generated identifiers
	 */
	@Override
	default boolean writePropertyValue() {
		return false;
	}

	/**
	 * Noop default implementation. May be overridden by subtypes.
	 */
	@Override
	default void configure(Type type, Properties parameters, ServiceRegistry serviceRegistry) {}
}
