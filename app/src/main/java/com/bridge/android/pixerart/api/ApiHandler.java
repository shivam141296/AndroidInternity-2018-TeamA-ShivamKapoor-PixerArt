package com.bridge.android.pixerart.api;

import android.net.Uri;

public class ApiHandler {
    private static final String TAG = ApiHandler.class.getSimpleName();
    public static final String API_KEY = "505fa5f2cf394c8467be4651ebf52b1e";
    public static final String PREF_SEARCH_QUERY = "searchQuery";
    private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
    private static final String METHOD_GETRECENT = "flickr.photos.getRecent";
    private static final String METHOD_SEARCH = "flickr.photos.search";
    private static volatile ApiHandler instance = null;
    private static final String FLICKR_URL = "http://flickr.com/photo.gne?id=%s";
    private static final String METHOD_GETINFO = "flickr.photos.getInfo";


    private ApiHandler() {

    }

    public static ApiHandler getInstance() {
        if (instance == null) {
            synchronized (ApiHandler.class) {
                if (instance == null) {
                    instance = new ApiHandler();
                }
            }
        }
        return instance;
    }

    public static String getItemUrl(String query, int page) {
        String url;
        if (query != null) {
            url = Uri.parse(ENDPOINT).buildUpon()
                    .appendQueryParameter("method", METHOD_SEARCH)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("text", query)
                    .appendQueryParameter("page", String.valueOf(page))
                    .build().toString();
        } else {
            url = Uri.parse(ENDPOINT).buildUpon()
                    .appendQueryParameter("method", METHOD_GETRECENT)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("page", String.valueOf(page))
                    .build().toString();
        }
        return url;
    }

    public static String getFlickrUrl(String id) {
        return String.format(FLICKR_URL, id);
    }

    public static String getPhotoInfoUrl(String id) {
        return Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_GETINFO)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .appendQueryParameter("photo_id", id)
                .build().toString();


    }
}