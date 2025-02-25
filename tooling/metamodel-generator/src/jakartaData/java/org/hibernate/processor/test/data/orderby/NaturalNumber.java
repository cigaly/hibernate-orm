/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.processor.test.data.orderby;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import java.io.Serial;
import java.io.Serializable;

@Entity
public class NaturalNumber implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	public enum NumberType {
		ONE, PRIME, COMPOSITE
	}

	@Id
	private long id; //AKA the value

	private boolean isOdd;

	private Short numBitsRequired;

	// Sorting on enum types is vendor-specific in Jakarta Data.
	// Use numTypeOrdinal for sorting instead.
	@Enumerated(EnumType.STRING)
	private NumberType numType; // enum of ONE | PRIME | COMPOSITE

	private int numTypeOrdinal; // ordinal value of numType

	private long floorOfSquareRoot;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isOdd() {
		return isOdd;
	}

	public void setOdd(boolean isOdd) {
		this.isOdd = isOdd;
	}

	public Short getNumBitsRequired() {
		return numBitsRequired;
	}

	public void setNumBitsRequired(Short numBitsRequired) {
		this.numBitsRequired = numBitsRequired;
	}

	public NumberType getNumType() {
		return numType;
	}

	public void setNumType(NumberType numType) {
		this.numType = numType;
	}

	public int getNumTypeOrdinal() {
		return numTypeOrdinal;
	}

	public void setNumTypeOrdinal(int value) {
		numTypeOrdinal = value;
	}

	public long getFloorOfSquareRoot() {
		return floorOfSquareRoot;
	}

	public void setFloorOfSquareRoot(long floorOfSquareRoot) {
		this.floorOfSquareRoot = floorOfSquareRoot;
	}
}
