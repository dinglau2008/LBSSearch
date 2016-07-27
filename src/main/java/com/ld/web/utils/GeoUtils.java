package com.ld.web.utils;

import com.ld.web.model.GeoPosition;

/**
 * Created by liuding on 7/27/16.
 */
public class GeoUtils {
    public static double DELTA_LATITUDE_BASE = 12226.609476D;
    public static double DELTA_LONGITUDE_BASE = 12392.142399999999D;
    private static final double KM_IN_ONE_MILE = 1.609344D;
    private static final double MAGIC_NUMBER = 69.09D;
    public static final double EARTH_RADIUS_INM = 6371009.0D;
    private static final double SQUARE_ROOT_2 = Math.sqrt(2.0D);

    private GeoUtils(){};

    public static boolean isLegalGeoPosition(GeoPosition geoPosition)
    {
        if(geoPosition == null)
        {
            return false;
        }

        if(Double.compare(geoPosition.getLatitude(), -90) < 0 ||
                Double.compare(geoPosition.getLatitude(), 90) > 0)
        {
            return false;
        }


        if(Double.compare(geoPosition.getLongitude(), -180) < 0 ||
                Double.compare(geoPosition.getLongitude(), 180) > 0)
        {
            return false;
        }
        return true;
    }


    public static double getDistanceInM(GeoPosition pos1, GeoPosition pos2) {
        double distance = distanceBetweenInKm(pos1, pos2);
        return distance * 1000.0D;
    }

    public static double getEuclideanGeoDistance(GeoPosition pos1, GeoPosition pos2) {
        double latDiff = pos1.getLatitude() - pos2.getLatitude();
        double longDiff = pos1.getLongitude() - pos2.getLongitude();
        return Math.sqrt(latDiff * latDiff + longDiff * longDiff);
    }

    public static double convertSurfaceDistance2GeoDistance(GeoPosition pos, double straightDistance) {
        double averageDistance = straightDistance / 6371009.0D;
        double radian = Math.toRadians(pos.getLatitude());
        Double cos = Double.valueOf(Math.cos(radian));
        double denominator = 1.0D + cos.doubleValue() * cos.doubleValue();
        double geoDistance = averageDistance / Math.sqrt(denominator);
        return Math.toDegrees(geoDistance) * SQUARE_ROOT_2;
    }

    public static double getTestDistance(GeoPosition pos1, GeoPosition pos2) {
        double detLat = Math.toRadians(pos2.getLatitude() - pos1.getLatitude());
        double detLog = Math.toRadians(pos2.getLongitude() - pos1.getLongitude());
        return 6371009.0D * Math.sqrt(detLat * detLat + Math.cos(detLat) * Math.cos(detLat) * detLog * detLog);
    }

    public static boolean isEffectiveGeo(Double latitude, Double longitude) {
        return longitude.doubleValue() >= -180.0D && longitude.doubleValue() <= 180.0D?latitude.doubleValue() >= -90.0D && latitude.doubleValue() <= 90.0D:false;
    }

    private static double distanceInMiles(double lat1, double lat2, double lon1, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        return dist * 69.09D;
    }

    public static double distanceBetweenInMiles(GeoPosition c1, GeoPosition c2) {
        return distanceInMiles(c1.getLatitude(), c2.getLatitude(), c1.getLongitude(), c2.getLongitude());
    }

    public static double distanceBetweenInKm(GeoPosition c1, GeoPosition c2) {
        return distanceInMiles(c1.getLatitude(), c2.getLatitude(), c1.getLongitude(), c2.getLongitude()) * 1.609344D;
    }


    /** @deprecated */
    @Deprecated
    public static double getGeoRadius(GeoPosition pos, double straightDistance) {
        double radian = Math.toRadians(pos.getLatitude());
        Double cos = Double.valueOf(Math.cos(radian));
        Double base = Double.valueOf(Math.sqrt((DELTA_LATITUDE_BASE + DELTA_LONGITUDE_BASE * cos.doubleValue() * cos.doubleValue()) / 2.0D));
        double geoDistance = straightDistance / 1000.0D / base.doubleValue();
        return geoDistance;
    }

    public static void main(String[] args) throws Exception {
        GeoPosition geoPos = new GeoPosition(40.006194D, 116.488964D);
        GeoPosition geoPos2 = new GeoPosition(40.007194D, 116.488964D);
        System.out.println(getTestDistance(geoPos, geoPos2));
        System.out.println("flat radius is " + convertSurfaceDistance2GeoDistance(geoPos, 1000.0D) + "\t geoRadius is " + getGeoRadius(geoPos, 1000.0D));
        System.out.println("flat radius is " + convertSurfaceDistance2GeoDistance(geoPos, 2000.0D) + "\t geoRadius is " + getGeoRadius(geoPos, 2000.0D));
        System.out.println("flat radius is " + convertSurfaceDistance2GeoDistance(geoPos, 3000.0D) + "\t geoRadius is " + getGeoRadius(geoPos, 3000.0D));
        System.out.println("flat radius is " + convertSurfaceDistance2GeoDistance(geoPos, 4000.0D) + "\t geoRadius is " + getGeoRadius(geoPos, 4000.0D));
        geoPos = new GeoPosition(20.006194D, 116.488964D);
        System.out.println("flat radius is " + convertSurfaceDistance2GeoDistance(geoPos, 1000.0D) + "\t geoRadius is " + getGeoRadius(geoPos, 1000.0D));
        System.out.println("flat radius is " + convertSurfaceDistance2GeoDistance(geoPos, 2000.0D) + "\t geoRadius is " + getGeoRadius(geoPos, 2000.0D));
        System.out.println("flat radius is " + convertSurfaceDistance2GeoDistance(geoPos, 3000.0D) + "\t geoRadius is " + getGeoRadius(geoPos, 3000.0D));
        System.out.println("flat radius is " + convertSurfaceDistance2GeoDistance(geoPos, 4000.0D) + "\t geoRadius is " + getGeoRadius(geoPos, 4000.0D));
        geoPos = new GeoPosition(80.006194D, 116.488964D);
        System.out.println("flat radius is " + convertSurfaceDistance2GeoDistance(geoPos, 1000.0D) + "\t geoRadius is " + getGeoRadius(geoPos, 1000.0D));
        System.out.println("flat radius is " + convertSurfaceDistance2GeoDistance(geoPos, 2000.0D) + "\t geoRadius is " + getGeoRadius(geoPos, 2000.0D));
        System.out.println("flat radius is " + convertSurfaceDistance2GeoDistance(geoPos, 3000.0D) + "\t geoRadius is " + getGeoRadius(geoPos, 3000.0D));
        System.out.println("flat radius is " + convertSurfaceDistance2GeoDistance(geoPos, 4000.0D) + "\t geoRadius is " + getGeoRadius(geoPos, 4000.0D));
        GeoPosition pos1 = new GeoPosition(40.006194D, 116.488964D);
        double geoDiffernece = getGeoRadius(pos1, 50000.0D);
        System.out.println(geoDiffernece);
        GeoPosition pos2 = new GeoPosition(39.918872D, 116.413159D);
        double distance = getDistanceInM(pos1, pos2);
        System.out.println(distance);
    }

}
