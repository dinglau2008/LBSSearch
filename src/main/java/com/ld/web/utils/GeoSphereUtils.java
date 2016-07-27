package com.ld.web.utils;

import com.ld.web.model.GeoPosition;
import com.ld.web.model.GeoSpherePosition;

/**
 * Created by liuding on 7/27/16.
 */
public class GeoSphereUtils {

    private GeoSphereUtils() {
    }

    public static GeoSpherePosition convertGeoToSphere(GeoPosition geoPos) {
        GeoSpherePosition spherePosition = new GeoSpherePosition();
        double latRadian = Math.toRadians(geoPos.getLatitude());
        double longRadian = Math.toRadians(geoPos.getLongitude());
        spherePosition.setX(Math.cos(latRadian) * Math.cos(longRadian));
        spherePosition.setY(Math.cos(latRadian) * Math.sin(longRadian));
        spherePosition.setZ(Math.sin(latRadian));
        return spherePosition;
    }

    public static double[] getUnitSphereCoordinates(GeoPosition geoPos) {
        double[] coordinates = new double[3];
        double latRadian = Math.toRadians(geoPos.getLatitude());
        double longRadian = Math.toRadians(geoPos.getLongitude());
        coordinates[0] = Math.cos(latRadian) * Math.cos(longRadian);
        coordinates[1] = Math.cos(latRadian) * Math.sin(longRadian);
        coordinates[2] = Math.sin(latRadian);
        return coordinates;
    }

    public static double getTunnelDistanceInM(GeoPosition geoPos1, GeoPosition geoPos2) {
        GeoSpherePosition spherePosition1 = convertGeoToSphere(geoPos1);
        GeoSpherePosition spherePosition2 = convertGeoToSphere(geoPos2);
        double detX = spherePosition1.getX() - spherePosition2.getX();
        double detY = spherePosition1.getY() - spherePosition2.getY();
        double detZ = spherePosition1.getZ() - spherePosition2.getZ();
        double distance = 6371009.0D * Math.sqrt(detX * detX + detY * detY + detZ * detZ);
        return distance;
    }

    public static double convertTunnelDistance2UnitSphereDistance(Double tunnelDistance) {
        return tunnelDistance.doubleValue() < 0.0D?0.0D:tunnelDistance.doubleValue() / 6371009.0D;
    }
}
