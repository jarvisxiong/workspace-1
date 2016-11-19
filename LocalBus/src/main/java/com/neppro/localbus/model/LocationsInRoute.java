package com.neppro.localbus.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity(name="locationsInRoute")
public class LocationsInRoute implements Serializable{

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private LocationsInRouteId locationsInRouteId;

	@Column(name="sort_order")
	private int sortOrder;

	
	public LocationsInRouteId getLocationsInRouteId() {
		return locationsInRouteId;
	}

	public void setLocationsInRouteId(LocationsInRouteId locationsInRouteId) {
		this.locationsInRouteId = locationsInRouteId;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}


}
