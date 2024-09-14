/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.testing.orm.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Composite annotation for applying extensions needed for managing
 * a StandardServiceRegistry as part of the test lifecycle.
 *
 * @author Steve Ebersole
 */
@Inherited
@Retention( RetentionPolicy.RUNTIME )
@Target({ElementType.TYPE, ElementType.METHOD})

@TestInstance( TestInstance.Lifecycle.PER_CLASS )

@ExtendWith( FailureExpectedExtension.class )
@ExtendWith( ServiceRegistryExtension.class )
@ExtendWith( ServiceRegistryParameterResolver.class )
public @interface ServiceRegistryFunctionalTesting {
}
