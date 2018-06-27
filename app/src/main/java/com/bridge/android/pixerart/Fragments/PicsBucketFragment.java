package com.bridge.android.pixerart.Fragments;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SearchView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bridge.android.pixerart.Adapters.PicsBucketAdapter;
import com.bridge.android.pixerart.Model.BucketItem;
import com.bridge.android.pixerart.Provider.Recommendations;
import com.bridge.android.pixerart.R;
import com.bridge.android.pixerart.api.ApiHandler;
import com.reginald.swiperefresh.CustomSwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class PicsBucketFragment extends Fragment {

    private static final String TAG = "PicsBucketFragment";
    private static final int COLUMN_NUM = 3;
    private static final int ITEM_PER_PAGE = 100;
    private RequestQueue mRq;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private CustomSwipeRefreshLayout mCustomSwipeRefreshLayout;
    private PicsBucketAdapter mAdapter;
    private ArrayList<BucketItem> mItems;
    private boolean mLoading = false;
    private boolean mHasMore = true;
    private SearchView mSearchView;
    private GridView mGridView;

    public PicsBucketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pics_bucket, container, false);
        mRq = Volley.newRequestQueue(getActivity());
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                int totalItem = mLayoutManager.getItemCount();
                int lastItemPos = mLayoutManager.findLastVisibleItemPosition();
                if (mHasMore && !mLoading && totalItem - 1 != lastItemPos) {
                    startLoading();
                }
            }
        });

        mLayoutManager = new GridLayoutManager(getActivity(), COLUMN_NUM);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PicsBucketAdapter(getActivity(), new ArrayList<BucketItem>());
        mRecyclerView.setAdapter(mAdapter);
        mCustomSwipeRefreshLayout = (CustomSwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mCustomSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        startLoading();
        return view;
    }

    public void refresh() {
        mAdapter.clear();
        startLoading();
    }

    private void startLoading() {
        mLoading = true;
        int totalItem = mLayoutManager.getItemCount();
        final int page = totalItem / ITEM_PER_PAGE + 1;
        String query = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(ApiHandler.PREF_SEARCH_QUERY, null);
        String url = ApiHandler.getInstance().getItemUrl(query, page);
        JsonObjectRequest request = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse " + response);

                List<BucketItem> result = new ArrayList<>();

                try {
                    JSONObject photos = response.getJSONObject("photos");

                    if (photos.getInt("pages") == page) {
                        mHasMore = false;
                    }

                    JSONArray photoArr = photos.getJSONArray("photo");
                    for (int i = 0; i < photoArr.length(); i++) {
                        JSONObject itemObj = photoArr.getJSONObject(i);
                        BucketItem item = new BucketItem(
                                itemObj.getString("id"),
                                itemObj.getString("secret"),
                                itemObj.getString("server"),
                                itemObj.getString("farm")
                        );

                        result.add(item);
                    }
                } catch (JSONException e) {

                }
                mAdapter.addAll(result);
                mAdapter.notifyDataSetChanged();
                mLoading = false;
                mCustomSwipeRefreshLayout.refreshComplete();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        request.setTag(TAG);
        mRq.add(request);

    }

    private void stopLoading() {
        if (mRq != null) {
            mRq.cancelAll(TAG);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLoading();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        mSearchView = (SearchView) searchItem.getActionView();

        if (mSearchView != null) {
            mSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionSelect(int position) {
                    String suggestion = getSuggestion(position);

                    if (mSearchView != null && suggestion != null) {
                        mSearchView.setQuery(suggestion, true);
                    }
                    return true;
                }

                @Override
                public boolean onSuggestionClick(int position) {
                    String suggestion = getSuggestion(position);

                    if (mSearchView != null && suggestion != null) {
                        mSearchView.setQuery(suggestion, true);
                    }

                    return true;
                }

                private String getSuggestion(int position) {
                    String suggest = null;

                    if (mSearchView != null) {
                        Cursor cursor = (Cursor) mSearchView.getSuggestionsAdapter().getItem(position);
                        suggest = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                    }
                    return suggest;
                }
            });
        }

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        ComponentName name = getActivity().getComponentName();
        SearchableInfo searchInfo = searchManager.getSearchableInfo(name);
        mSearchView.setSearchableInfo(searchInfo);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean selectionHandled = false;

        switch (item.getItemId()) {
            case R.id.menu_item_search:

                getActivity().onSearchRequested();
                selectionHandled = true;
                break;

            case R.id.menu_item_delete:
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(),
                        Recommendations.AUTHORITY, Recommendations.MODE);
                suggestions.clearHistory();

                if (mSearchView != null) {
                    mSearchView.setQuery("", false);
                    mSearchView.setIconified(false);
                }

                PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .edit()
                        .putString(ApiHandler.PREF_SEARCH_QUERY, null)
                        .commit();

                refresh();
                selectionHandled = true;
                break;

            default:
                selectionHandled = super.onOptionsItemSelected(item);
                break;
        }
        return selectionHandled;
    }
}