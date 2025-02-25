/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.processor.test.data.orderby;

import jakarta.data.Order;
import jakarta.data.page.PageRequest;
import org.hibernate.processor.test.util.CompilationTest;
import org.hibernate.processor.test.util.WithClasses;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.processor.test.util.TestUtil.assertMetamodelClassGeneratedFor;
import static org.hibernate.processor.test.util.TestUtil.assertPresenceOfMethodInMetamodelFor;
import static org.hibernate.processor.test.util.TestUtil.getMetaModelSourceAsString;

@WithClasses({NaturalNumber.class, NaturalNumberRepository.class})
public class OrderByTest extends CompilationTest {

	@Test
	public void test() {
		final String modelSource = getMetaModelSourceAsString( NaturalNumberRepository.class );
		System.out.println( modelSource );
		assertMetamodelClassGeneratedFor( NaturalNumberRepository.class );

		assertPresenceOfMethodInMetamodelFor(
				NaturalNumberRepository.class, "oddsFrom21To", Long.TYPE, PageRequest.class );
		assertThat( modelSource.toLowerCase() )
				.contains( "select id from naturalnumber where isodd = true and id between 21 and ?1 order by id asc" );
		assertThat( modelSource )
				.doesNotContain( "_orders.add(asc(Long.class, \"id\"));" );

		assertPresenceOfMethodInMetamodelFor(
				NaturalNumberRepository.class, "findByIsOdd", Boolean.TYPE );
		assertThat( modelSource.toLowerCase() )
				.contains( "from naturalnumber" );
		assertThat( modelSource.toLowerCase() )
				.doesNotContain( "from naturalnumber order by" );

		assertPresenceOfMethodInMetamodelFor(
				NaturalNumberRepository.class, "everyNumber", PageRequest.class, Order.class );
		assertThat( modelSource )
				.contains( "_orders.add(asc(NaturalNumber.class, \"id\"));" );
		assertThat( modelSource )
				.contains( ".setOrder(_orders)" );
	}
}
