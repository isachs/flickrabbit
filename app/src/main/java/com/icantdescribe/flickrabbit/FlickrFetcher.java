package com.icantdescribe.flickrabbit;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
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
    private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
    private static final String METHOD_FAVES = "flickr.favorites.getList";
    private static final String METHOD_SIZES = "flickr.photos.getSizes";
    private static final String METHOD_INFO = "flickr.photos.getInfo";
    private static final String METHOD_USERNAME = "flickr.people.findByUsername";
    private static final String METHOD_USER_URL = "flickr.urls.lookupUser";

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

    public List<Photo> fetchItems(String user, int num, int fetchPoolSize) {

        List<Photo> items = new ArrayList<>();
        String userId = "";

        if (user.contains("@")) { // if user string doesn't contain '@' it's a username from prefs, not a userid
            userId = user;
        } else {
            try {
                String url = Uri.parse(ENDPOINT)
                        .buildUpon()
                        .appendQueryParameter("method", METHOD_USERNAME)
                        .appendQueryParameter("api_key", API_KEY)
                        .appendQueryParameter("username", user)
                        .appendQueryParameter("format", "json")
                        .appendQueryParameter("nojsoncallback", "1")
                        .build().toString();
                String jsonString = getUrlString(url);
                Log.i(TAG, "Received JSON: " + jsonString);
                JSONObject jsonBody = new JSONObject(jsonString);

                if (jsonBody.getString("stat").equals("fail")) { // input id wasn't a username, try url method
                    String url2 = Uri.parse(ENDPOINT)
                            .buildUpon()
                            .appendQueryParameter("method", METHOD_USER_URL)
                            .appendQueryParameter("api_key", API_KEY)
                            .appendQueryParameter("url", "flickr.com/photos/" + user)
                            .appendQueryParameter("format", "json")
                            .appendQueryParameter("nojsoncallback", "1")
                            .build().toString();
                    String jsonString2 = getUrlString(url2);
                    Log.i(TAG, "Received JSON: " + jsonString2);
                    JSONObject jsonBody2 = new JSONObject(jsonString2);

                    JSONObject userJsonObject = jsonBody2.getJSONObject("user");
                    userId = userJsonObject.getString("id");
                } else {
                    JSONObject userJsonObject = jsonBody.getJSONObject("user");
                    userId = userJsonObject.getString("id");
                }
            } catch (JSONException je) {
                Log.e(TAG, "Failed to parse JSON", je);
            } catch (IOException ioe) {
                Log.e(TAG, "Failed to fetch items", ioe);
            }
        }

        try {
            String url = Uri.parse(ENDPOINT)
                    .buildUpon()
                    .appendQueryParameter("method", METHOD_FAVES)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("user_id", userId)
                    .appendQueryParameter("per_page", String.valueOf(fetchPoolSize))
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            Log.i(TAG, "Received JSON length: " + Integer.toString(jsonString.length()));
            JSONObject jsonBody = new JSONObject(jsonString);
            items = parseItems(items, jsonBody, num);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        return items;
    }

    public String fetchBiggestPhotoUri(String id) {

        String uri = new String();

        try {
            String url = Uri.parse(ENDPOINT)
                    .buildUpon()
                    .appendQueryParameter("method", METHOD_SIZES)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("photo_id", id)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            uri = parseSizes(uri, jsonBody);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        return uri;
    }

    public String fetchPhotoPageUri(String id) {

        String uri = new String();

        try {
            String url = Uri.parse(ENDPOINT)
                    .buildUpon()
                    .appendQueryParameter("method", METHOD_INFO)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("photo_id", id)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            uri = parsePhotoPageUri(uri, jsonBody);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }

        return uri;
    }

    private List<Photo> parseItems(List<Photo> items, JSONObject jsonBody, int num) throws IOException, JSONException {

        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");

        ArrayList<Integer> rand = randomIndices(num, photoJsonArray.length());

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

    private String parseSizes(String uri, JSONObject jsonBody) throws IOException, JSONException {

        JSONObject sizesJsonObject = jsonBody.getJSONObject("sizes");
        JSONArray sizeJsonArray = sizesJsonObject.getJSONArray("size");

        JSONObject sizeJsonObject = sizeJsonArray.getJSONObject(sizeJsonArray.length() - 1);

        String uriOut = sizeJsonObject.getString("source");

        Log.d(TAG, "parseSizes " + uriOut);

        return uriOut;
    }

    private String parsePhotoPageUri(String uri, JSONObject jsonBody) throws IOException, JSONException {

        JSONObject photoJsonObject = jsonBody.getJSONObject("photo");
        JSONObject ownerJsonObject = photoJsonObject.getJSONObject("owner");

        String uriOut = "https://www.flickr.com/photos/" + ownerJsonObject.getString("nsid") + "/" + photoJsonObject.getString("id") + "/";

        Log.d(TAG, "parsePhotoPageUri " + uriOut);

        return uriOut;
    }

    public ArrayList<Integer> randomIndices(int len, int num) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        ArrayList<Integer> outList = new ArrayList<Integer>();
        for (int i=1; i<num; i++) {
            list.add(new Integer(i));
        }
        Collections.shuffle(list);
        for (int i=0; i<(Math.min(len,num-1)); i++) {
            outList.add(list.get(i));
        }
        return outList;
    }
}
