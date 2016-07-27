package com.ld.web.service.impl;

import com.ld.web.model.GeoPosition;
import com.ld.web.service.ILBSSearch;
import com.ld.web.utils.GeoSphereUtils;
import com.ld.web.utils.GeoUtils;
import net.sf.javaml.core.kdtree.KDTree;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by liuding on 7/27/16.
 */
public class LBSSearchImpl<Value>  implements ILBSSearch<Value> {

    public final static double MAX_ALLOWED_DISTANCE = GeoUtils.EARTH_RADIUS_INM/10;
    public final static int MAX_ALLOWED_COUNT = 50000;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private KDTree geoKDTree = new KDTree(3);


    private ConcurrentHashMap<GeoPosition, ConcurrentSkipListSet<Value>> position2POIs =
            new ConcurrentHashMap<>();


    public void addPOIGeo(GeoPosition geoPosition, Value poi) {

        if(GeoUtils.isLegalGeoPosition(geoPosition))
        {
            throw new IllegalStateException();
        }

        double[] coordinates = GeoSphereUtils.getUnitSphereCoordinates(geoPosition);

        //insert int k-d tree to preserve geoPosition
        geoKDTree.insert(coordinates, geoPosition);

        ConcurrentSkipListSet<Value> POIs = position2POIs.get(geoPosition);

        if(CollectionUtils.isEmpty(POIs))
        {
            ConcurrentSkipListSet<Value> tmpPOIs = new ConcurrentSkipListSet<Value>();

            POIs = position2POIs.putIfAbsent(geoPosition, tmpPOIs);

            if(POIs == null)
            {
                POIs = tmpPOIs;
            }
        }

        POIs.add(poi);
    }

    public void deletePOIGeo(GeoPosition geoPosition, Value poi) {

        if(GeoUtils.isLegalGeoPosition(geoPosition))
        {
            throw new IllegalStateException();
        }
        ConcurrentSkipListSet<Value> POIs = position2POIs.get(geoPosition);

        if(CollectionUtils.isNotEmpty(POIs) && POIs.contains(poi))
        {
            POIs.remove(poi);
            if(POIs.isEmpty())
            {
                position2POIs.remove(geoPosition);
                double[] coordinates = GeoSphereUtils.getUnitSphereCoordinates(geoPosition);
                geoKDTree.delete(coordinates);

            }
        }

    }

    @Override
    public List<Value> getNeighborPOIsWithinDistance(GeoPosition geoPosition, double distanceInM, int maxSize) {
        List<GeoPosition> sortedGeoPositions = getNeighborGeoPositionsWithinDistance(geoPosition, distanceInM, maxSize);

        if(CollectionUtils.isEmpty(sortedGeoPositions))
        {
            return Collections.emptyList();
        }

        List<Value> POIs = new LinkedList<>();
        for(GeoPosition position : sortedGeoPositions)
        {
            ConcurrentSkipListSet<Value> tmpPois = position2POIs.get(geoPosition);

            if(CollectionUtils.isNotEmpty(tmpPois))
            {
                POIs.addAll(tmpPois);
            }
        }

        return POIs;
    }

    @Override
    public List<GeoPosition> getNeighborGeoPositionsWithinDistance(GeoPosition geoPosition, double distanceInM, int maxSize) {

        if(GeoUtils.isLegalGeoPosition(geoPosition))
        {
            throw new IllegalStateException();
        }
        if(distanceInM <=0 || maxSize <= 0)
        {
            throw new IllegalArgumentException();
        }
        double[] coordinates = GeoSphereUtils.getUnitSphereCoordinates(geoPosition);


        if(maxSize > MAX_ALLOWED_COUNT)
        {
            maxSize = MAX_ALLOWED_COUNT;
        }

        if(distanceInM > MAX_ALLOWED_DISTANCE)
        {
            distanceInM = MAX_ALLOWED_DISTANCE;
        }

        double radius = GeoSphereUtils.convertTunnelDistance2UnitSphereDistance(distanceInM);
        GeoPosition[] positions = (GeoPosition[]) geoKDTree.nearest(coordinates, maxSize, radius);

        List<GeoPosition> sortedGeoPositions = sortGeoByDistance(geoPosition, positions);

        return sortedGeoPositions;
    }




    private List<GeoPosition> sortGeoByDistance(GeoPosition origin, GeoPosition[] positions)
    {
        if(positions == null || positions.length == 0)
        {
            return Collections.emptyList();
        }

        TreeMap<Double, List<GeoPosition>> distance2Geos = new TreeMap<Double, List<GeoPosition>>();

        for(GeoPosition pos: positions)
        {
            GeoPosition geo = (GeoPosition)pos;
            double distance = GeoUtils.getDistanceInM(origin, geo);
            List<GeoPosition> geoList = distance2Geos.get(distance);
            if(geoList == null)
            {
                geoList = new ArrayList<GeoPosition>();
                distance2Geos.put(distance, geoList);
            }
            geoList.add(geo);
        }

        List<GeoPosition> sorttedPositions = new LinkedList<GeoPosition>();
        for(Double distance : distance2Geos.keySet())
        {
            sorttedPositions.addAll(distance2Geos.get(distance));
        }

        return sorttedPositions;
    }


}
