/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.annotations.id.sequences.entities;
import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * sample of Sequance generator
 *
 * @author Emmanuel Bernard
 */
@Entity
@SuppressWarnings("serial")
public class Shoe implements Serializable {
	private Long id;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_GEN")
	public Long getId() {
		return id;
	}

	public void setId(Long long1) {
		id = long1;
	}
}
