/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.annotations.id.sequences.entities;
import java.io.Serializable;

/**
 * @author Emmanuel Bernard
 */
@SuppressWarnings("serial")
public class Location implements Serializable {
	public double longitude;
	public double latitude;

	public boolean equals(Object o) {
		if ( this == o ) return true;
		if ( o == null || getClass() != o.getClass() ) return false;

		final Location location = (Location) o;

		if ( Double.compare( location.latitude, latitude ) != 0 ) return false;
		if ( Double.compare( location.longitude, longitude ) != 0 ) return false;

		return true;
	}

	public int hashCode() {
		int result;
		long temp;
		temp = longitude != +0.0d ? Double.doubleToLongBits( longitude ) : 0L;
		result = (int) ( temp ^ ( temp >>> 32 ) );
		temp = latitude != +0.0d ? Double.doubleToLongBits( latitude ) : 0L;
		result = 29 * result + (int) ( temp ^ ( temp >>> 32 ) );
		return result;
	}
}
