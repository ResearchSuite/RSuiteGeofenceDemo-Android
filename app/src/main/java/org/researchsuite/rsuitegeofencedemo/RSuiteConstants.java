package org.researchsuite.rsuitegeofencedemo;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jameskizer on 6/13/17.
 */

public class RSuiteConstants {

    private RSuiteConstants() {}

    private static final String PACKAGE_NAME = "org.researchsuite.RSuiteGeofenceDemo";

    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    static final int LOITERING_DELAY_MINUTE = 60*1000;

    static final double HOME_LAT = 40.687671;
    static final double HOME_LON = -73.957489;

    static final double WORK_LAT = 40.741355;
    static final double WORK_LON = -74.003203;

    static final List<RSuiteGeofenceDescriptor> GEOFENCE_LOCATIONS = new ArrayList<>();
    static {

        GEOFENCE_LOCATIONS.add(new RSuiteGeofenceDescriptor("HOME_50_0", HOME_LAT, HOME_LON, 50.0f, 0));
        GEOFENCE_LOCATIONS.add(new RSuiteGeofenceDescriptor("HOME_50_1", HOME_LAT, HOME_LON, 50.0f, LOITERING_DELAY_MINUTE));
        GEOFENCE_LOCATIONS.add(new RSuiteGeofenceDescriptor("HOME_50_5", HOME_LAT, HOME_LON, 50.0f, 5*LOITERING_DELAY_MINUTE));

        GEOFENCE_LOCATIONS.add(new RSuiteGeofenceDescriptor("HOME_150_0", HOME_LAT, HOME_LON, 150.0f, 0));
        GEOFENCE_LOCATIONS.add(new RSuiteGeofenceDescriptor("HOME_150_1", HOME_LAT, HOME_LON, 150.0f, LOITERING_DELAY_MINUTE));
        GEOFENCE_LOCATIONS.add(new RSuiteGeofenceDescriptor("HOME_150_5", HOME_LAT, HOME_LON, 150.0f, 5*LOITERING_DELAY_MINUTE));

        GEOFENCE_LOCATIONS.add(new RSuiteGeofenceDescriptor("HOME_300_0", HOME_LAT, HOME_LON, 300.0f, 0));
        GEOFENCE_LOCATIONS.add(new RSuiteGeofenceDescriptor("HOME_300_1", HOME_LAT, HOME_LON, 300.0f, LOITERING_DELAY_MINUTE));
        GEOFENCE_LOCATIONS.add(new RSuiteGeofenceDescriptor("HOME_300_5", HOME_LAT, HOME_LON, 300.0f, 5*LOITERING_DELAY_MINUTE));

        GEOFENCE_LOCATIONS.add(new RSuiteGeofenceDescriptor("WORK_50_0", WORK_LAT, WORK_LON, 50.0f, 0));
        GEOFENCE_LOCATIONS.add(new RSuiteGeofenceDescriptor("WORK_50_1", WORK_LAT, WORK_LON, 50.0f, LOITERING_DELAY_MINUTE));
        GEOFENCE_LOCATIONS.add(new RSuiteGeofenceDescriptor("WORK_50_5", WORK_LAT, WORK_LON, 50.0f, 5*LOITERING_DELAY_MINUTE));

        GEOFENCE_LOCATIONS.add(new RSuiteGeofenceDescriptor("WORK_150_0", WORK_LAT, WORK_LON, 150.0f, 0));
        GEOFENCE_LOCATIONS.add(new RSuiteGeofenceDescriptor("WORK_150_1", WORK_LAT, WORK_LON, 150.0f, LOITERING_DELAY_MINUTE));
        GEOFENCE_LOCATIONS.add(new RSuiteGeofenceDescriptor("WORK_150_5", WORK_LAT, WORK_LON, 150.0f, 5*LOITERING_DELAY_MINUTE));

        GEOFENCE_LOCATIONS.add(new RSuiteGeofenceDescriptor("WORK_300_0", WORK_LAT, WORK_LON, 300.0f, 0));
        GEOFENCE_LOCATIONS.add(new RSuiteGeofenceDescriptor("WORK_300_1", WORK_LAT, WORK_LON, 300.0f, LOITERING_DELAY_MINUTE));
        GEOFENCE_LOCATIONS.add(new RSuiteGeofenceDescriptor("WORK_300_5", WORK_LAT, WORK_LON, 300.0f, 5*LOITERING_DELAY_MINUTE));
    }

}
