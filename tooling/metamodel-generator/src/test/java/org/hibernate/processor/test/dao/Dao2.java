/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.processor.test.dao;

import java.util.List;

import org.hibernate.annotations.processing.HQL;

import jakarta.persistence.EntityManager;

public interface Dao2 {

    EntityManager getEntityManager();

    // Simple name
    @HQL("from Book b where b.type = Magazine")
    List<Book> findMagazines1();

    // Simple qualified name
    @HQL("from Book b where b.type = Type.Magazine")
    List<Book> findMagazines2();

    // Canonical FQN
    @HQL("from Book b where b.type = org.hibernate.processor.test.dao.Book.Type.Magazine")
    List<Book> findMagazines3();

    // Binary FQN
    @HQL("from Book b where b.type = org.hibernate.processor.test.dao.Book$Type.Magazine")
    List<Book> findMagazines4();
}
