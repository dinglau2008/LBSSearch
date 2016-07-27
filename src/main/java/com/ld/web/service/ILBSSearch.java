package com.ld.web.service;

import com.ld.web.model.GeoPosition;

import java.util.List;

/**
 * Created by liuding on 7/27/16.
 */
public interface ILBSSearch <Value> {

    public void addPOIGeo(GeoPosition geoPosition, Value poi);

    public void deletePOIGeo(GeoPosition geoPosition, Value poi);


    public List<Value> getNeighborPOIsWithinDistance(GeoPosition geoPosition, double distanceInM, int maxSize);

    public List<GeoPosition> getNeighborGeoPositionsWithinDistance(GeoPosition geoPosition, double distanceInM, int maxSize);

}
