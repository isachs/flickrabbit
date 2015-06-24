package com.icantdescribe.flickrabbit;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlickrFetcher {

    private static final String TAG = "FlickrFetcher";

    private static final String API_KEY = "9b19a5f825a6e38ac4d17da55c9ee07a";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[2048];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<Photo> fetchItems(String user) {

        List<Photo> items = new ArrayList<>();

        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.favorites.getList")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("user_id", user)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            items = parseItems(items, jsonBody);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        return items;
    }

    private List<Photo> parseItems(List<Photo> items, JSONObject jsonBody) throws IOException, JSONException {

        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        ArrayList<Integer> rand = randomIndices(10, photoJsonArray.length()); // 10 items for now - from prefs later

        for (int i = 0; i < rand.size(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(rand.get(i));
            Photo photo = new Photo(photoJsonObject.getString("id"),
                    photoJsonObject.getString("owner"),
                    photoJsonObject.getString("secret"),
                    photoJsonObject.getString("server"),
                    photoJsonObject.getString("farm"));
            items.add(photo);
        }

        return items;
    }

    public ArrayList<Integer> randomIndices(int len, int num) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        ArrayList<Integer> outList = new ArrayList<Integer>();
        for (int i=1; i<num; i++) { // for 100 images returned
            list.add(new Integer(i));
        }
        Collections.shuffle(list);
        for (int i=0; i<(Math.min(len,num-1)); i++) {
            outList.add(list.get(i));
        }
        return outList;
    }
}
