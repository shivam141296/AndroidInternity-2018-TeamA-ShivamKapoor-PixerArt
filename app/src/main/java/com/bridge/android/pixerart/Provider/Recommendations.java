package com.bridge.android.pixerart.Provider;

import android.content.SearchRecentSuggestionsProvider;

public class Recommendations extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "com.bridge.android.pixerart" + ".Recommendations";
    public static int MODE = DATABASE_MODE_QUERIES;

    public Recommendations(){ setupSuggestions(AUTHORITY,MODE);}

}
