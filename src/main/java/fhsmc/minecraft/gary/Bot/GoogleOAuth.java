package fhsmc.minecraft.gary.Bot;

import fhsmc.minecraft.gary.Config;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class GoogleOAuth {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient httpClient = new OkHttpClient();

    private static String get(String url) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }

    private static String post(String url, String jsonBody) throws IOException{
        RequestBody body = RequestBody.create(JSON, jsonBody);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static JSONObject startAuthFlow() throws IOException {
        String url = "https://oauth2.googleapis.com/device/code?client_id="
                + Config.getString("google.client-id")
                + "&scope=email";
        String data = post(url, "{}");
        JSONObject dataObject = new JSONObject(data);
        return dataObject;
    }

    public static JSONObject pollGoogleAuth(String deviceCode) throws IOException {
        String url = "https://oauth2.googleapis.com/token"
                + "?client_id=" + Config.getString("google.client-id")
                + "&client_secret=" + Config.getString("google.client-secret")
                + "&device_code=" + deviceCode
                + "&grant_type=urn:ietf:params:oauth:grant-type:device_code";

        String data = post(url, "{}");
        JSONObject dataObject = new JSONObject(data);
        return dataObject;
    }
}