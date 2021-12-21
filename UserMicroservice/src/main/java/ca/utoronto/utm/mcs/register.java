package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class register extends Endpoint{
    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("name") && body.has("email") && body.has("password")) {
            try {
                this.dao.postUser(body.getString("name"), body.getString("email"), body.getString("password"));
                this.sendStatus(r, 200);
            } catch (Exception e) {
                this.sendStatus(r, 500);
            }
        }
        else {
            this.sendStatus(r, 400);
        }
    }
}
