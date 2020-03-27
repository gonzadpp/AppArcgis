package com.mycompany.app;

import com.esri.arcgisruntime.geometry.Point;

public class Route {
	private Point startPoint, endPoint;
	private String nameRoute;
	
	public Route() {
		startPoint = null;
		endPoint = null;
		nameRoute = "";
	}
	public Route(String nameRoute, Point startPoint, Point endPoint) {
		this.nameRoute = nameRoute;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}
	public void setNameRoute(String nameRoute) {
		this.nameRoute = nameRoute;
	}
	public String getNameRoute() {
		return nameRoute;
	}
	public void setStarPoint(Point startPoint) {
		this.startPoint = startPoint;
	}
	public Point getStartPoint() {
		return startPoint;
	}
	public void setEndPoint(Point endPoint) {
		this.endPoint = endPoint;
	}
	public Point getEndPoint() {
		return endPoint;
	}
	@Override
	public String toString() {
		return nameRoute;
	}

}
