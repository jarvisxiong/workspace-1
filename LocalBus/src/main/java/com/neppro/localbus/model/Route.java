package com.neppro.localbus.model;

import java.io.Serializable;
import java.sql.Time;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


@Entity(name="route")
public class Route implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "route_id")
	private String routeId;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "start_point")
	private String startPoint;
	
	@Column(name = "end_point")
	private String endPoint;
	
	@Column(name = "start_time")
	private Time startTime;
	
	@Column(name = "end_time")
	private Time endTime;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="locationsInRouteId.route")
	private Set<LocationsInRoute> locationsInRoute=new HashSet<LocationsInRoute>();
	
	@Column(name = "minimum_fare")
	private double minimumFare;
	
	@Column(name = "increased_fare_by")
	private double increasedFareBy;
	
	@Column(name="number_of_stops_included_in_minimum_fare")
	private int numberOfStopsIncludedInMinimumFare;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="committee_id")
	private Committee committee;
	
	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
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

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Time getEndTime() {
		return endTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}

	public String getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(String startPoint) {
		this.startPoint = startPoint;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(String endPoint) {
		this.endPoint = endPoint;
	}

	public double getMinimumFare() {
		return minimumFare;
	}

	public void setMinimumFare(double minimumFare) {
		this.minimumFare = minimumFare;
	}

	public double getIncreasedFareBy() {
		return increasedFareBy;
	}

	public void setIncreasedFareBy(double increasedFareBy) {
		this.increasedFareBy = increasedFareBy;
	}

	public int getNumberOfStopsIncludedInMinimumFare() {
		return numberOfStopsIncludedInMinimumFare;
	}

	public void setNumberOfStopsIncludedInMinimumFare(
			int numberOfStopsIncludedInMinimumFare) {
		this.numberOfStopsIncludedInMinimumFare = numberOfStopsIncludedInMinimumFare;
	}

	public Committee getCommittee() {
		return committee;
	}

	public void setCommittee(Committee committee) {
		this.committee = committee;
	}
	
}
