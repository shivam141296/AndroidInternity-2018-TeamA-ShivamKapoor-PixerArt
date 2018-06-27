package com.bridge.android.pixerart.utils;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bridge.android.pixerart.Fragments.PicsBucketFragment;
import com.bridge.android.pixerart.Provider.Recommendations;
import com.bridge.android.pixerart.R;
import com.bridge.android.pixerart.api.ApiHandler;

public class PicsBucket extends AppCompatActivity {
    private static final String TAG = PicsBucket.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pics_bucket);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        manageIntent(intent);

    }

    private void manageIntent(Intent intent) {

        if(Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions recommendations = new SearchRecentSuggestions(this,
                    Recommendations.AUTHORITY, Recommendations.MODE);

            recommendations.saveRecentQuery(query, null);
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(ApiHandler.PREF_SEARCH_QUERY, query)
                    .commit();

            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentById(R.id.pics_bucket_fragment);
            Log.d("shivam","shivam");

            if(fragment!= null) {
                ( (PicsBucketFragment) fragment).refresh();
            }
        }
    }
}
