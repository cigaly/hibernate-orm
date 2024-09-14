/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.boot.models.annotations.internal;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.annotations.BatchSize;
import org.hibernate.models.spi.SourceModelBuildingContext;

@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
@jakarta.annotation.Generated("org.hibernate.orm.build.annotations.ClassGeneratorProcessor")
public class BatchSizeAnnotation implements BatchSize {
	private int size;

	/**
	 * Used in creating dynamic annotation instances (e.g. from XML)
	 */
	public BatchSizeAnnotation(SourceModelBuildingContext modelContext) {
	}

	/**
	 * Used in creating annotation instances from JDK variant
	 */
	public BatchSizeAnnotation(BatchSize annotation, SourceModelBuildingContext modelContext) {
		this.size = annotation.size();
	}

	/**
	 * Used in creating annotation instances from Jandex variant
	 */
	public BatchSizeAnnotation(Map<String, Object> attributeValues, SourceModelBuildingContext modelContext) {
		this.size = (int) attributeValues.get( "size" );
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return BatchSize.class;
	}

	@Override
	public int size() {
		return size;
	}

	public void size(int value) {
		this.size = value;
	}


}
