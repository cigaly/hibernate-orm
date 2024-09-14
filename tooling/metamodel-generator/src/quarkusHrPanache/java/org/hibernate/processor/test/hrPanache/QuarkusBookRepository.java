/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.processor.test.hrPanache;

import java.util.List;

import org.hibernate.annotations.processing.Find;
import org.hibernate.annotations.processing.HQL;

import io.smallrye.mutiny.Uni;

public interface QuarkusBookRepository {
    @Find
    public Uni<List<PanacheBook>> findBook(String isbn);

    @HQL("WHERE isbn = :isbn")
    public Uni<List<PanacheBook>> hqlBook(String isbn);

    @HQL("DELETE FROM PanacheBook")
    public Uni<Void> deleteAllBooksVoid();

    @HQL("DELETE FROM PanacheBook")
    public Uni<Integer> deleteAllBooksInt();
}
