package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Trip extends Endpoint {
    /**
     * PATCH /trip/:_id
     *
     * @param :uid
     * @return 200, 400, 404, 500
     * Adding extra information when the trip is done.
     * @body distance, endTime, timeElapsed, discount, totalCost, driverPayout
     */

    @Override
    public void handlePatch(HttpExchange r) throws IOException, JSONException {

        String[] params = r.getRequestURI().toString().split("/");
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));

        if (params.length != 3 || params[2].isEmpty() || body.length() != 6) {
            this.sendStatus(r, 400, true);
            return;
        }

        String _id = params[2];

        int distance = body.getInt("distance");
        long endTime = body.getLong("endTime");
        String timeElapsed = body.getString("timeElapsed");
        int discount = body.getInt("discount");
        double totalCost = body.getDouble("totalCost");
        double driverPayout = body.getDouble("driverPayout");

        try {
            boolean res = this.dao.patchTrip(_id, distance, endTime, timeElapsed, discount, totalCost, driverPayout);
            if (res) {
                this.sendStatus(r, 200);
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
