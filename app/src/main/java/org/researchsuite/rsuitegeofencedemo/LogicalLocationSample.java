package org.researchsuite.rsuitegeofencedemo;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.android.gms.location.Geofence;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import edu.cornell.tech.foundry.omhclient.OMHAcquisitionProvenance;
import edu.cornell.tech.foundry.omhclient.OMHDataPoint;
import edu.cornell.tech.foundry.omhclient.OMHDataPointBuilder;
import edu.cornell.tech.foundry.omhclient.OMHSchema;

/**
 * Created by jameskizer on 6/13/17.
 */

public class LogicalLocationSample extends OMHDataPointBuilder {

    public enum Action {
        UNKNOWN, ENTER, EXIT
    }

    public String getIdentifier() {
        return identifier;
    }

    private String identifier;
    private Action action;
    private UUID datapointId;
    private OMHAcquisitionProvenance acquisitionProvenance;

    public LogicalLocationSample(Context context, String identifier, Action action) {
        this.identifier = identifier;
        this.action = action;
        this.datapointId = UUID.randomUUID();
        this.acquisitionProvenance = new OMHAcquisitionProvenance(context.getPackageName(), new Date(), OMHAcquisitionProvenance.OMHAcquisitionProvenanceModality.SENSED);
    }

    public String getDataPointID() {
        return this.datapointId.toString();
    }

    public Date getCreationDateTime() {
        return new Date();
    }

    public OMHSchema getSchema() {
        return new OMHSchema("logical-location", "cornell", "1.0");
    }

    @Nullable
    public OMHAcquisitionProvenance getAcquisitionProvenance() {
        return this.acquisitionProvenance;
    }

    public JSONObject getBody() {
        HashMap map = new HashMap();
        HashMap effectiveTimeMap = new HashMap();
        effectiveTimeMap.put("date_time", OMHDataPoint.stringFromDate(this.getCreationDateTime()));
        map.put("effective_time_frame", effectiveTimeMap);
        map.put("location", this.identifier);
        switch (this.action) {
            case ENTER:
                map.put("action", "enter");
                break;
            case EXIT:
                map.put("action", "exit");
                break;
            default:
                map.put("action", "unknown");
                break;
        }

        return new JSONObject(map);
    }

}
