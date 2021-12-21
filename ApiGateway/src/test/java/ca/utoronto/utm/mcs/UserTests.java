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
Please write your tests for the User Microservice in this class. 
*/

public class UserTests {
    final static String API_URL = "http://apigateway:8000";
    private static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @BeforeAll
    static void setup() throws JSONException, IOException, InterruptedException {
        JSONObject prepRequest1 = new JSONObject()
                .put("name", "Sample Name")
                .put("email", "sample@gmail.com")
                .put("password", "Sample1!");
        HttpResponse<String> prepResponse1 = sendRequest(" /user/register", "POST", prepRequest1.toString());
    }

    @Test
    public void userLoginPass() throws JSONException, IOException, InterruptedException{
        JSONObject confirmRequest = new JSONObject()
                .put("email", "sample@gmail.com")
                .put("password", "Sample1!");
        HttpResponse<String> confirmResponse = sendRequest("/user/login", "POST", confirmRequest.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmResponse.statusCode());
    }

    @Test
    public void userLoginFail() throws JSONException, IOException, InterruptedException{
        JSONObject confirmRequest = new JSONObject()
                .put("email", "doesnotexist@gmail.com")
                .put("password", "Doesnotexist1!");
        HttpResponse<String> confirmResponse = sendRequest("/user/login", "POST", confirmRequest.toString());
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, confirmResponse.statusCode());
    }

    @Test
    public void userRegisterPass() throws JSONException, IOException, InterruptedException{
        JSONObject confirmRequest = new JSONObject()
                .put("name", "Another Name")
                .put("email", "another@gmail.com")
                .put("password", "another1!");
        HttpResponse<String> confirmResponse = sendRequest(" /user/register", "POST", confirmRequest.toString());
        assertEquals(HttpURLConnection.HTTP_OK, confirmResponse.statusCode());
    }

    @Test
    public void userRegisterFail() throws JSONException, IOException, InterruptedException{
        JSONObject confirmRequest = new JSONObject()
                .put("name", "Bad Request")
                .put("password", "BadPassword1!");
        HttpResponse<String> confirmResponse = sendRequest("/user/register", "POST", confirmRequest.toString());
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, confirmResponse.statusCode());
    }
}
