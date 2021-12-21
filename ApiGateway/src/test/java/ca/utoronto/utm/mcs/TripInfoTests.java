package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
Please write your tests for the TripInfo Microservice in this class. 
*/

public class TripInfoTests {
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

    private static HttpResponse<String> sendRequest2(String endpoint, String method, String reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
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
        JSONObject confirmRequest = new JSONObject()
                .put("driver", "ID2345678")
                .put("passenger", "ID1234567")
                .put("startTime", 1638322747);
        HttpResponse<String> confirmResponse = sendRequest("/trip/confirm", "POST", confirmRequest.toString());
    }

    @Test
    public void tripRequestPass() throws JSONException, IOException, InterruptedException{
        JSONObject confirmRequest = new JSONObject()
                .put("uid", "ID1234567")
                .put("radius", 50);
        HttpResponse<String> confirmResponse = sendRequest("/trip/request", "POST", confirmRequest.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmResponse.statusCode());
    }

    @Test
    public void tripRequestFail() throws JSONException, IOException, InterruptedException{
        JSONObject confirmRequest = new JSONObject()
                .put("uid", "ID1244567")
                .put("radius", 50);
        HttpResponse<String> confirmResponse = sendRequest("/trip/request", "POST", confirmRequest.toString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, confirmResponse.statusCode());
    }

    @Test
    public void tripConfirmPass() throws JSONException, IOException, InterruptedException{
        JSONObject confirmRequest = new JSONObject()
                .put("driver", "ID2345678")
                .put("passenger", "ID1234567")
                .put("startTime", 1638322189);
        HttpResponse<String> confirmResponse = sendRequest("/trip/confirm", "POST", confirmRequest.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmResponse.statusCode());
    }

    @Test
    public void tripConfirmFail() throws JSONException, IOException, InterruptedException{
        JSONObject confirmRequest = new JSONObject()
                .put("driver", "ID2345678")
                .put("startTime", 1638322189);
        HttpResponse<String> confirmResponse = sendRequest("/trip/confirm", "POST", confirmRequest.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmResponse.statusCode());
    }

    @Test
    public void patchTripPass() throws JSONException, IOException, InterruptedException{
        String _id = "ID9876543";

        String reqUrl = "http://apigateway:8000/trip/%s";
        String urlUpdate = String.format(reqUrl, _id);

        JSONObject confirmRequest = new JSONObject()
                .put("distance", 6)
                .put("endTime", 1638322189)
                .put("timeElapsed", "21 minutes")
                .put("discount", 0)
                .put("totalCost", 28.00)
                .put("driverPayout", 18.20);

        HttpResponse<String> confirmResponse = sendRequest2(urlUpdate, "PATCH",confirmRequest.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmResponse.statusCode());
    }

    @Test
    public void patchTripFail() throws JSONException, IOException, InterruptedException{
        String _id = "ID9876543";

        String reqUrl = "http://apigateway:8000/trip/%s";
        String urlUpdate = String.format(reqUrl, _id);

        JSONObject confirmRequest = new JSONObject()
                .put("distance", 6)
                .put("endTime", 1638322189)
                .put("timeElapsed", "21 minutes")
                .put("discount", 0)
                .put("totalCost", 28.00);

        HttpResponse<String> confirmResponse = sendRequest2(urlUpdate, "PATCH",confirmRequest.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmResponse.statusCode());
    }

    @Test
    public void tripsForPassengerPass () throws JSONException, IOException, InterruptedException{
        String uid = "ID1234567";

        String reqUrl = "http://apigateway:8000/trip/passenger/:%s";
        String urlUpdate = String.format(reqUrl, uid);

        HttpResponse<String> confirmResponse = sendRequest(urlUpdate, "GET");
        assertEquals(HttpURLConnection.HTTP_OK, confirmResponse.statusCode());
    }

    @Test
    public void tripsForPassengerFail() throws JSONException, IOException, InterruptedException{
        String uid = "";

        String reqUrl = "http://apigateway:8000/trip/passenger/:%s";
        String urlUpdate = String.format(reqUrl, uid);

        HttpResponse<String> confirmResponse = sendRequest(urlUpdate, "GET");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmResponse.statusCode());
    }

    @Test
    public void tripsForDriverPass() throws JSONException, IOException, InterruptedException{
        String uid = "ID2345678";

        String reqUrl = "http://apigateway:8000/trip/driver/%s";
        String urlUpdate = String.format(reqUrl, uid);

        HttpResponse<String> confirmResponse = sendRequest(urlUpdate, "GET");
        assertEquals(HttpURLConnection.HTTP_OK, confirmResponse.statusCode());
    }

    @Test
    public void tripsForDriverFail() throws JSONException, IOException, InterruptedException{
        String uid = "";

        String reqUrl = "http://apigateway:8000/trip/driver/%s";
        String urlUpdate = String.format(reqUrl, uid);

        HttpResponse<String> confirmResponse = sendRequest(urlUpdate, "GET");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmResponse.statusCode());
    }

    @Test
    public void driverTimePass() throws JSONException, IOException, InterruptedException{
        String _id = "ID9876543";

        String reqUrl = "http://apigateway:8000/trip/driverTime/%s";
        String urlUpdate = String.format(reqUrl, _id);

        HttpResponse<String> confirmResponse = sendRequest(urlUpdate, "GET");
        assertEquals(HttpURLConnection.HTTP_OK, confirmResponse.statusCode());
    }

    @Test
    public void driverTimeFail() throws JSONException, IOException, InterruptedException{
        String _id = "";

        String reqUrl = "http://apigateway:8000/trip/driverTime/%s";
        String urlUpdate = String.format(reqUrl, _id);

        HttpResponse<String> confirmResponse = sendRequest(urlUpdate, "GET");
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmResponse.statusCode());
    }
}
