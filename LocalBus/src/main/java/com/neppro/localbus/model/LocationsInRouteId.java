package com.neppro.localbus.model;

import java.io.Serializable;

import javax.persistence.CascadeType;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class LocationsInRouteId implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="location_id")
	private Location location;
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="route_id")
	private Route route;
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

		
}
