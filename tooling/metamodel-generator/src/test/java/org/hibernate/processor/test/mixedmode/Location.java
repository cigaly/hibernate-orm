/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.processor.test.mixedmode;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * @author Hardy Ferentschik
 */
@Entity
@Access(AccessType.FIELD)
public class Location {
	@Id
	private long id;
	private String description;
	// implicitly embedded
	private Coordinates coordinates;
	@Embedded
	private ZeroCoordinates zeroCoordinates;

	public Coordinates getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ZeroCoordinates getZeroCoordinates() {
		return zeroCoordinates;
	}

	public void setZeroCoordinates(ZeroCoordinates zeroCoordinates) {
		this.zeroCoordinates = zeroCoordinates;
	}
}
