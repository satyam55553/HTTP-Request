package com.example.httpapi;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.RequiresApi;

public class QueryUtils {
    //Tag for the log messages
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    public static ArrayList<String> extractJsonFromUserDb(String jsonResponse) {
        Log.v(LOG_TAG, "Extracting JSON from User DB");
        ArrayList<String> nameArray = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray itemsArray = root.getJSONArray("data");
//            JSONArray itemsArray = root.getJSONArray("msg");
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject iItem = itemsArray.getJSONObject(i);//Accessing i'th element of JSONArray items
                String message = iItem.getString("fname");
                nameArray.add(message);
//                JSONObject msgJSON = iItem.getJSONObject("volumeInfo");
//                String message = msgJSON.getString("title");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nameArray;
    }

    //parsing json response
    public static String extractJson(String jsonResponse) {
        Log.v(LOG_TAG, "Extracting JSON");
        String msg = "";
        try {
            JSONObject root = new JSONObject(jsonResponse);
            msg = root.getString("msg");
//            JSONArray itemsArray = root.getJSONArray("msg");
//            for (int i = 0; i < itemsArray.length(); i++) {
//                JSONObject iItem = itemsArray.getJSONObject(i);//Accessing i'th element of JSONArray items
//                JSONObject msgJSON = iItem.getJSONObject("volumeInfo");
//                String message = msgJSON.getString("title");
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msg;
    }

    // Make an HTTP request to the given URL and return a String as the response.
    static String makeHttpRequest(URL url) throws IOException {
        Log.v(LOG_TAG, "I'm making HTTP Request");

        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();//open connection at url
            urlConnection.setRequestMethod("GET");//set the request method-GET,PUT or DELETE
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();//connect to the server
            int responseCode = urlConnection.getResponseCode();//store the received response code from the server
            //if connection is successful then response code is 200
            if (responseCode == 200) {
                inputStream = urlConnection.getInputStream();//server sends data as InputStream
                jsonResponse = readFromStream(inputStream);//convert received InputStream to String jsonResponse
            } else {
                Log.e(LOG_TAG, "Status Code is " + responseCode);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException while making HTTP request ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static String makeHttpPostRequest(URL url, String data) {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();//open connection at url
            urlConnection.setRequestMethod("POST");//set the request method-GET,PUT or DELETE
            urlConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
//            urlConnection.setDoInput(true);
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            Log.i("QueryUtils.this", "Inside Url Connection");
            //Writing to server

            try (OutputStream os = urlConnection.getOutputStream()) {
                String jsonInput = getJSONdata(data);
                Log.i("QueryUtils.this", "jsonIp= " + jsonInput);
                byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
                Log.i("QueryUtils.this", "Writing to server");
            }

            int responseCode = urlConnection.getResponseCode();//store the received response code from the server
            //if connection is successful then response code is 200
            if (responseCode == 200) {
                inputStream = urlConnection.getInputStream();//server sends data as InputStream
                jsonResponse = readFromStream(inputStream);//convert received InputStream to String jsonResponse
            } else {
                Log.e(LOG_TAG, "Status Code is " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonResponse;
    }

    static URL createUrl(String stringUrl) {
        Log.v(LOG_TAG, "I'm creating URL");
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        Log.v(LOG_TAG, "I'm in reading from stream");
        StringBuilder stringBuilder = new StringBuilder();//Better than String

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();//reading the first line of the inputStream
            while (line != null) {
                stringBuilder.append(line);//adding the line(of the inputStream to the StringBuilder)
                line = reader.readLine();//reading the next line of inputStream
            }
        }
        return stringBuilder.toString();//converting StringBuilder output to String(The jsonResponse code)
    }

    static String getJSONdata(String data) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("name", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

}

