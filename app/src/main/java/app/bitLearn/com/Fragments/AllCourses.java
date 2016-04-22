package app.bitLearn.com.Fragments;


import android.app.ProgressDialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.bitLearn.com.Adapters.GalleryAdapter;
import app.bitLearn.com.R;
import app.bitLearn.com.Utils.AppController;
import app.bitLearn.com.Utils.GridSpacingItemDecoration;
import app.bitLearn.com.Utils.Image;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllCourses extends Fragment {

    private String TAG = AllCourses.class.getSimpleName();
    private static final String endpoint = "http://api.androidhive.info/json/glide.json";
    private ArrayList<Image> images = new ArrayList<>();
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private GridSpacingItemDecoration spacingItemDecoration;
    private RecyclerView recyclerView;


    int spanCount = 2; // 3 columns
    int spacing = 5; // 50px
    boolean includeEdge = true;


    public AllCourses() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_courses, container, false );
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mAdapter = new GalleryAdapter(getContext(), images);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        recyclerView.setAdapter(mAdapter);
        fetchImages();
        return view;
    }


    private void fetchImages() {
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Downloading json...");
        pDialog.show();

        JsonArrayRequest req = new JsonArrayRequest(endpoint,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();

                        images.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Image image = new Image();
                                image.setName(jsonObject.getString("name"));
                                JSONObject url = jsonObject.getJSONObject("url");
                                image.setSmall(url.getString("small"));
                                image.setMedium(url.getString("medium"));
                                image.setLarge(url.getString("large"));
                                image.setTimestamp(jsonObject.getString("timestamp"));

                                images.add(image);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                pDialog.hide();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);

    }
}
