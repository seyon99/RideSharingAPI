package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.io.IOException;

public class Nearby extends Endpoint{ // should extend Endpoint?

    /**
     * GET /location/nearbyDriver/:uid?radius=
     * @param :uid
     * @param :radius
     * @return 200, 400, 404, 500
     * Get the drivers that are in a radius that the user defined from the user’s current location
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] params = r.getRequestURI().toString().split("/");

        if (params.length != 4 || params[1].isEmpty() || params[2].isEmpty() || params[3].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }
        String uid;
        String strRadius;
        try {
            String[] inputs = params[3].split("\\?radius="); // need to catch exception if regex
            if (inputs[0].isEmpty() || inputs[1].isEmpty()) {
                this.sendStatus(r, 400, true);
                return;
            }
            uid = inputs[0];
            strRadius = inputs[1];
        } catch (Exception e) {
            this.sendStatus(r, 400, true);
            return;
        }

        try {
            int radius = Integer.parseInt(strRadius); // should account for edge case where radius is negative?
            Result result = this.dao.getUserLocationByUid(uid);
            // driver with uid current location
            Double longitude;
            Double latitude;
            int radiusInMeters = radius * 1000; //convert radius to meters

            if (result.hasNext()) {
                Record user = result.next();
                longitude = user.get("n.longitude").asDouble();
                latitude = user.get("n.latitude").asDouble();

            } else {
                this.sendStatus(r, 404, true);
                return;
            }

            Result result1 = this.dao.nearestDrivers(longitude, latitude, radius);

            if (result1.hasNext()) {
                JSONObject res = new JSONObject();
                while (result1.hasNext()) { // iterating correctly?
                    Record user = result1.next();
                    String uidTemp = user.get("n.uid").asString();
                    if (uidTemp.equals(uid)) continue;
                    JSONObject insideRes = new JSONObject();
                    Double longitudeTemp = user.get("n.longitude").asDouble();
                    Double latitudeTemp = user.get("n.latitude").asDouble();
                    String street = user.get("n.street").asString();
                    insideRes.put("longitude", longitudeTemp);
                    insideRes.put("latitude", latitudeTemp);
                    insideRes.put("street", street);
                    res.put(uidTemp, insideRes);
                }
                JSONObject retTemp = new JSONObject();
                retTemp.put("data", res); // perhaps this is not required ... change after testing

                this.sendResponse(r, retTemp, 200); // should send response to be modified to add "data" label
                return;
            } else {
                this.sendStatus(r, 404, true);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500, true);
            return;
        }
    }
}
