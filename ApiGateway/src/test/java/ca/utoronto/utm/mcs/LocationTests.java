package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
/*
Please write your tests for the Location Microservice in this class. 
*/

public class LocationTests {
    final static String API_URL = "http://apigateway:8000";
    private static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> sendRequest(String endpoint, String method) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(""))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @BeforeAll
    static void setup() throws JSONException, IOException, InterruptedException {
        JSONObject prepRequest1 = new JSONObject()
                .put("uid", "ID1234567")
                .put("is_driver", false);
        HttpResponse<String> prepResponse1 = sendRequest(" /user/register", "POST", prepRequest1.toString());
        JSONObject prepRequest2 = new JSONObject()
                .put("uid", "ID2345678")
                .put("is_driver", true);
        HttpResponse<String> prepResponse2 = sendRequest(" /user/register", "POST", prepRequest2.toString());
    }

    @Test
    public void getNearbyDriverPass() throws JSONException, IOException, InterruptedException{
        String uid = "ID1234567";
        int radius = 50;

        String reqUrl = "http://apigateway:8000/location/nearbyDriver/%s?radius=%d";
        String urlUpdate = String.format(reqUrl, uid, radius);

        HttpResponse<String> confirmResponse = sendRequest(urlUpdate, "GET");
        assertEquals(HttpURLConnection.HTTP_OK, confirmResponse.statusCode());
    }

    @Test
    public void getNearbyDriverFail() throws JSONException, IOException, InterruptedException{
        String uid = "ID1234237";
        int radius = 15;

        String reqUrl = "http://apigateway:8000/location/nearbyDriver/%s?radius=%d";
        String urlUpdate = String.format(reqUrl, uid, radius);

        HttpResponse<String> confirmResponse = sendRequest(urlUpdate, "GET");
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, confirmResponse.statusCode());
    }

    @Test
    public void getNavigationPass() throws JSONException, IOException, InterruptedException{
        String driverUID = "ID2345678";
        String passengerUid = "ID1234567";

        String reqUrl = "http://apigateway:8000/location/navigation/%s?passengerUid=%s";
        String urlUpdate = String.format(reqUrl, driverUID, passengerUid);

        HttpResponse<String> confirmResponse = sendRequest(urlUpdate, "GET");
        assertEquals(HttpURLConnection.HTTP_OK, confirmResponse.statusCode());
    }

    @Test
    public void getNavigationFail() throws JSONException, IOException, InterruptedException{
        String driverUID = "ID5345678";
        String passengerUid = "ID1234967";

        String reqUrl = "http://apigateway:8000/location/navigation/%s?passengerUid=%s";
        String urlUpdate = String.format(reqUrl, driverUID, passengerUid);

        HttpResponse<String> confirmResponse = sendRequest(urlUpdate, "GET");
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, confirmResponse.statusCode());
    }
}
