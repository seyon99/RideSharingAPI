package ca.utoronto.utm.mcs;

import com.mongodb.util.JSON;
import com.sun.net.httpserver.HttpExchange;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class TripConf extends Endpoint{
    /**
     * POST /trip/confirm
     *
     * @body uid, radius
     * @return 200, 400, 404, 500
     * Send a request for the trip that a passenger requests and return the status.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("driver") && body.has("passenger") && body.has("startTime")) {
            String driverUid = body.getString("driver");
            String passengerUid = body.getString("passenger");
            long startTime = body.getLong("startTime"); // is it a long or timestamp object?

            ObjectId idObj = this.dao.confirm(driverUid, passengerUid, startTime);
            String idObjStr = JSON.serialize(idObj);
            JSONObject id = new JSONObject(idObjStr);

            JSONObject res = new JSONObject();
            res.put("data", id);
            this.sendResponse(r, res, 200);
            return;

        } else {
            this.sendStatus(r, 400, true);
            return;
        }
    }
}