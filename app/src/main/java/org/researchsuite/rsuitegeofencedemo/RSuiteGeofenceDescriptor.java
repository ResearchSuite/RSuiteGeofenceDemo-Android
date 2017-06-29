package org.researchsuite.rsuitegeofencedemo;

/**
 * Created by jameskizer on 6/13/17.
 */

public class RSuiteGeofenceDescriptor {

    final public String identifier;
    final public double latitude;
    final public double longitude;
    final public float radius;
    final public int loiteringDelay;

    public RSuiteGeofenceDescriptor(String identifier, double latitude, double longitude, float radius, int loiteringDelay) {
        this.identifier = identifier;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.loiteringDelay = loiteringDelay;
    }
}
