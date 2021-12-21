package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.Iterator;

public class TripReq extends Endpoint{
    /**
     * POST /trip/request/
     *
     * @body uid, radius
     * @return 200, 400, 404, 500
     * Send a request for the trip that a passenger requests and return the status.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("uid") && body.has("radius")) {
            String uid = body.getString("uid");
            int radius = body.getInt("radius");
            // use GET /location/nearbyDriver/:uid?radius= in location microservice
            String reqUrl = "http://locationmicroservice:8000/location/nearbyDriver/%s?radius=%d";
            String urlUpdate = String.format(reqUrl, uid, radius);
            // make an GET request using url update string

            JSONObject nearbyObj = null;

            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlUpdate))
                        .method("GET", HttpRequest.BodyPublishers.ofString(""))
                        .build();
                HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (resp.statusCode() == 200) {
                    nearbyObj = new JSONObject(resp.body()); // contains response body
                }
            } catch (Exception e) {
                this.sendStatus(r, 400, true);
                return;
            }

            JSONObject data = new JSONObject();
            JSONArray drivers = new JSONArray();
            if (nearbyObj != null) {
                data = nearbyObj.getJSONObject("data");
                Iterator<String> keys = data.keys();

                while(keys.hasNext()) {
                    String key = keys.next();
                    drivers.put(key);
                }
            }

            if (drivers.length() == 0) {
                this.sendStatus(r, 404, true);
                return;
            }
            JSONObject res = new JSONObject();
            res.put("data", drivers);
            this.sendResponse(r, res, 200);
            return;

        } else {
            this.sendStatus(r, 400, true);
            return;
        }
    }
}
