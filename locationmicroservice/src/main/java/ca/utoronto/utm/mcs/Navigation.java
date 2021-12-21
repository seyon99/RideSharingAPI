package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.io.IOException;

public class Navigation extends Endpoint{
    /**
     * GET /location/navigation/:driver?passengerUid=
     * @param :driverUid
     * @param :passengerUid
     * @return 200, 400, 404, 500
     * Get a list which contains the routing of the drive, each step’s time and the total time of the trip with
     * traffic information of the road.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] params = r.getRequestURI().toString().split("/");

        if (params.length != 4 || params[1].isEmpty() || params[2].isEmpty() || params[3].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }
        String driverUid;
        String passengerUid;
        try {
            String[] inputs = params[3].split("\\?radius="); // need to catch exception if regex
            if (inputs[0].isEmpty() || inputs[1].isEmpty()) {
                this.sendStatus(r, 400, true);
                return;
            }
            driverUid = inputs[0];
            passengerUid = inputs[1];
        } catch (Exception e) {
            this.sendStatus(r, 400, true);
            return;
        }

        try {
            Result result = this.dao.getBestRoute(driverUid, passengerUid);
            if (result.hasNext()) {
                JSONObject res = new JSONObject();
                JSONObject route = new JSONObject();
                JSONArray routeArray = new JSONArray();
                int totalTime = 0;
                while (result.hasNext()) { // iterating correctly?
                    JSONObject insideRes = new JSONObject();
                    Record road = result.next();
                    String roadName = road.get("n.name").asString();
                    Boolean is_traffic = road.get("n.is_traffic").asBoolean();
                    int time = road.get("r.travel_time").asInt();
                    totalTime += time;
                    insideRes.put("street", roadName);
                    insideRes.put("time", time);
                    insideRes.put("is_traffic", is_traffic);
                    routeArray.put(insideRes);
                }

                route.put("route", routeArray);

                res.put("total_time", totalTime);
                res.put("route", routeArray);

                this.sendResponse(r, res, 200); // should send response to be modified to add "data" label
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
