/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.annotations.cid;

import jakarta.persistence.*;

@Entity
@IdClass(FlightSegmentId.class)
@Table(name = "flight_segment")
public class FlightSegment {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    @Id
    @Column(name = "segment_number")
    private Integer segmentNumber;

    @OneToOne(mappedBy = "segment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FlightSegmentConfiguration configuration;

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Integer getSegmentNumber() {
        return segmentNumber;
    }

    public void setSegmentNumber(Integer segmentNumber) {
        this.segmentNumber = segmentNumber;
    }

    public FlightSegmentConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(FlightSegmentConfiguration configuration) {
        configuration.setSegment(this);
        this.configuration = configuration;
    }

}
