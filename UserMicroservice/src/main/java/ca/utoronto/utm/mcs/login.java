package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class login extends Endpoint{
    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("email") && body.has("password")) {
            // make query and get required data, return 500 if error
            ResultSet rs;
            boolean resultHasNext;
            try {
                rs = this.dao.getUsersFromEmail(body.getString("email"));
                resultHasNext = rs.next();
            }
            catch (SQLException e) {
                this.sendStatus(r, 500, true);
                return;
            }

            // check if user was found, return 404 if not found
            if (!resultHasNext) {
                this.sendStatus(r, 404, true);
                return;
            }

            // get data
            String name;
            String email;
            int rides;
            Boolean isDriver;
            JSONArray availableCoupons;
            JSONArray redeemedCoupons;
            try {
                name = rs.getString("name");
                email = rs.getString("email");
                rides = rs.getInt("rides");
                isDriver = rs.getBoolean("isdriver");
                availableCoupons = new JSONArray(rs.getString("availableCoupons").replace("{", "[").replace("}", "]"));
                redeemedCoupons = new JSONArray(rs.getString("redeemedCoupons").replace("{", "[").replace("}", "]"));
            }
            catch (SQLException e) {
                this.sendStatus(r, 500, true);
                return;
            }

            // making the response
            JSONObject resp = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("name", name);
            data.put("email", email);
            data.put("rides", rides);
            data.put("isDriver", isDriver);
            data.put("availableCoupons", availableCoupons);
            data.put("redeemedCoupons", redeemedCoupons);
            resp.put("data", data);

            this.sendResponse(r, resp, 200);
        } else {
            this.sendStatus(r, 400);
        }
    }
}
