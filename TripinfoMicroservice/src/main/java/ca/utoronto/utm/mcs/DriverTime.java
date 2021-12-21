package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class DriverTime extends Endpoint{

    /**
     * GET /trip/driver/:uid
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
            String _id = params[3];
            JSONObject uids = this.dao.getDriverTime(_id);
            if (uids != null) {
                String passUid = uids.getString("passenger");
                String driverUid = uids.getString("driver");
                String reqUrl = "http://locationmicroservice:8000/location/navigation/%s?passengerUid=%s";
                String urlUpdate = String.format(reqUrl, driverUid, passUid);

                // start sending request to location microservice
                JSONObject nav = null;
                int total_time = 0;

                try {
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(urlUpdate))
                            .method("GET", HttpRequest.BodyPublishers.ofString(""))
                            .build();
                    HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (resp.statusCode() == 200) {
                        nav = new JSONObject(resp.body()); // contains response body
                        JSONObject data = nav.getJSONObject("data");
                        total_time = data.getInt("total_time");
                    }
                } catch (Exception e) {
                    this.sendStatus(r, 400, true);
                    return;
                }

                // end
                JSONObject retVal = new JSONObject();
                retVal.put("arrival_time", total_time);
                JSONObject res = new JSONObject();
                res.put("status", "OK");
                res.put("data", retVal);

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
