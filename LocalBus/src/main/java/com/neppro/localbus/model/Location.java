package com.neppro.localbus.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity(name="location")
public class Location implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "location_id")
	private String locationId;
	
	@Column(name = "name")
	private String name;

	@OneToMany(cascade=CascadeType.ALL, mappedBy="locationsInRouteId.location")
	private Set<LocationsInRoute> locationsInRoute=new HashSet<LocationsInRoute>();
	

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<LocationsInRoute> getLocationsInRoute() {
		return locationsInRoute;
	}

	public void setLocationsInRoute(Set<LocationsInRoute> locationsInRoute) {
		this.locationsInRoute = locationsInRoute;
	}

	

	
}
