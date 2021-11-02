package fhsmc.minecraft.gary;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class GoogeOAuth {
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
}