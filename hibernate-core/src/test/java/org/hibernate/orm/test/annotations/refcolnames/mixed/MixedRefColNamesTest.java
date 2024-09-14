/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.annotations.refcolnames.mixed;

import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@DomainModel(annotatedClasses = {Region.class, Town.class})
@SessionFactory
public class MixedRefColNamesTest {
    @Test
    public void test(SessionFactoryScope scope) {
        Region region = new Region();
        PostalCode postalCode = new PostalCode();
        postalCode.countryCode = "ES";
        postalCode.zipCode = 69;
        region.postalCode = postalCode;
        Town town = new Town();
        TownCode townCode = new TownCode();
        townCode.town = "Barcelona";
        townCode.countryCode = "ES";
        townCode.zipCode = 69;
        town.region = region;
        town.townCode = townCode;
        scope.inTransaction(s -> {
            s.persist(region);
            town.regionId = region.id;
            s.persist(town);
        });
        scope.inTransaction(s -> {
            Town t = s.createQuery("from Town join fetch region", Town.class).getSingleResult();
            Assertions.assertNotNull(t);
        });
    }
}
