/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.bytecode.enhancement.lazy.proxy.index_out_of_bounds;

import org.hibernate.orm.test.bytecode.enhancement.lazy.proxy.index_out_of_bounds.base.EmbeddableType;
import org.hibernate.orm.test.bytecode.enhancement.lazy.proxy.index_out_of_bounds.derived.TestEntity;
import org.hibernate.testing.bytecode.enhancement.EnhancementOptions;
import org.hibernate.testing.bytecode.enhancement.extension.BytecodeEnhanced;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.JiraKey;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DomainModel(
		annotatedClasses = {
				TestEntity.class
		}
)
@SessionFactory
@BytecodeEnhanced
@EnhancementOptions(lazyLoading = true, inlineDirtyChecking = true)
@JiraKey( "HHH-19312" )
public class BytecodeProviderIndexOutOfBoundsTest {

	@Test
	public void testIt(SessionFactoryScope scope) {
		// Just a smoke test; the original failure happened during bytecode enhancement.
		Long id = scope.fromTransaction( s -> {
			TestEntity testEntity = new TestEntity();
			EmbeddableType embedded = new EmbeddableType();
			embedded.setField( "someValue" );
			testEntity.setEmbeddedField( embedded );
			s.persist( testEntity );
			return testEntity.getId();
		} );
		scope.inTransaction( s -> {
			TestEntity testEntity = s.find( TestEntity.class, id );
			assertThat( testEntity.getEmbeddedField().getField() ).isEqualTo( "someValue" );
		} );
	}

}
