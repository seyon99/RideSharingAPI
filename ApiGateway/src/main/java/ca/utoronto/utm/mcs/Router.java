package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Router extends Endpoint{
    private static HttpResponse<String> sendRequest(String API_URL, String endpoint, String method, String reqBody) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public void handleRequest(HttpExchange r) throws JSONException, IOException {
        String path = r.getRequestURI().toString();
        // example path http://apigateway:8000/location/nearbyDriver/%s?radius=%d"
        String[] params = path.split("/");

        String ms = params[1];
        String APP_URI = "";

        // determine which microservice to send request to
        switch (ms) {
            case "location" -> APP_URI = "http://locationmicroservice:8000";
            case "trip" -> APP_URI = "http://tripinfomicroservice:8000";
            case "user" -> APP_URI = "http://usermicroservice:8000";
            default -> this.sendStatus(r, 400, true);
        }

        String[] uriArgs = path.split(ms);
        String endpoint = "/" + ms + uriArgs[1];

        String method = r.getRequestMethod();
        String reqBody = r.getRequestBody().toString();

        try {
            // make a request to a microservice (ms)
            HttpResponse<String> response = sendRequest(APP_URI, endpoint, method, reqBody);
            // send the response back to apigateway endpoint using methods from Endpoint.java
            JSONObject body = new JSONObject(response.body());
            this.sendResponse(r, body, response.statusCode());
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500, true);
        }
    }
}
