package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Passenger extends Endpoint{

    /**
     * GET /trip/passenger/:uid
     * @param :uid
     * @return 200, 400, 404, 500
     * Get all the trips that the certain passenger has.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }

        try {
            String uid = params[3];
            JSONObject trips = this.dao.getPassengerTrips(uid);
            if (trips.getJSONArray("trips").length() > 0) {

                JSONObject res = new JSONObject();
                res.put("status", "OK");
                res.put("data", trips);

                this.sendResponse(r, res, 200);
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
