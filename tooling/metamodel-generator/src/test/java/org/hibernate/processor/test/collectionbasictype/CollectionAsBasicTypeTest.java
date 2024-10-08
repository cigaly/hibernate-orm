/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.processor.test.collectionbasictype;

import org.hibernate.processor.test.util.CompilationTest;
import org.hibernate.processor.test.util.TestForIssue;
import org.hibernate.processor.test.util.WithClasses;

import org.junit.Test;

import static org.hibernate.processor.test.util.TestUtil.assertAttributeTypeInMetaModelFor;
import static org.hibernate.processor.test.util.TestUtil.assertListAttributeTypeInMetaModelFor;
import static org.hibernate.processor.test.util.TestUtil.assertMetamodelClassGeneratedFor;

/**
 * @author helloztt
 */
public class CollectionAsBasicTypeTest extends CompilationTest {

	@Test
	@TestForIssue(jiraKey = "HHH-12338")
	@WithClasses({Goods.class, Product.class})
	public void testConvert() throws ClassNotFoundException, NoSuchFieldException {
		assertMetamodelClassGeneratedFor(Product.class);
		assertMetamodelClassGeneratedFor(Goods.class);
		assertListAttributeTypeInMetaModelFor(
				Goods.class,
				"productList",
				Product.class,
				"ListAttribute generic type should be Product"
		);
		assertAttributeTypeInMetaModelFor(
				Goods.class,
				"tags",
				Goods.class.getDeclaredField("tags").getGenericType(),
				"Wrong meta model type"
		);

	}

	@Test
	@TestForIssue(jiraKey = "HHH-12338")
	@WithClasses({Person.class})
	public void testListType() throws ClassNotFoundException, NoSuchFieldException {
		assertMetamodelClassGeneratedFor(Person.class);

		assertAttributeTypeInMetaModelFor(
				Person.class,
				"phones",
				Person.class.getDeclaredField("phones").getGenericType(),
				"Wrong meta model type"
		);

	}

	@Test
	@TestForIssue(jiraKey = "HHH-12338")
	@WithClasses({PersonPhone.class})
	public void testListTypeWithImport() throws ClassNotFoundException, NoSuchFieldException {
		assertMetamodelClassGeneratedFor(PersonPhone.class);

		assertAttributeTypeInMetaModelFor(
				PersonPhone.class,
				"phones",
				PersonPhone.class.getDeclaredField("phones").getGenericType(),
				"Wrong meta model type"
		);

	}

	@Test
	@TestForIssue(jiraKey = "HHH-12338")
	@WithClasses({PhoneBook.class})
	public void testMapType() throws ClassNotFoundException, NoSuchFieldException {
		assertMetamodelClassGeneratedFor( PhoneBook.class );

		assertAttributeTypeInMetaModelFor(
				PhoneBook.class,
				"phones",
				PhoneBook.class.getDeclaredField( "phones" ).getGenericType(),
				"Wrong meta model type"
		);

	}

	@Test
	@TestForIssue(jiraKey = "HHH-14724")
	@WithClasses({ Like.class, ConcreteLike.class })
	public void testIntersectionType() {
		assertMetamodelClassGeneratedFor( ConcreteLike.class );
	}

	@Test
	@TestForIssue(jiraKey = "HHH-14724")
	@WithClasses({ EnumHolder.class })
	public void testRecursiveTypeVariable() {
		assertMetamodelClassGeneratedFor( EnumHolder.class );
	}
}
