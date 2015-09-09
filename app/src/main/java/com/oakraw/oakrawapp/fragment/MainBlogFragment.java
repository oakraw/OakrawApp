package com.oakraw.oakrawapp.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.oakraw.oakrawapp.AppController;
import com.oakraw.oakrawapp.MainActivity;
import com.oakraw.oakrawapp.R;
import com.oakraw.oakrawapp.adapter.BlogAdapter;
import com.oakraw.oakrawapp.model.BlogList;
import com.oakraw.oakrawapp.model.CategoryColor;
import com.oakraw.oakrawapp.util.Toolbox;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Rawipol on 4/24/15 AD.
 */
public class MainBlogFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private BlogAdapter mAdapter;
    private ArrayList<BlogList> blogList = new ArrayList<>();
    private ProgressBar pb;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int LIMIT = 5;
    private int PAGE = 0;
    private FrameLayout noInternet;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_blog, container, false);
        if(MainActivity.isHandHeld) {
            ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        getColor();
        request();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.cardList);
        noInternet = (FrameLayout)rootView.findViewById(R.id.no_internet);


        pb = (ProgressBar) rootView.findViewById(R.id.pb);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new BlogAdapter(getActivity(), blogList, new BlogAdapter.OnBlogClickListener() {
            @Override
            public void onClick(int blog) {
                //Toast.makeText(getActivity(), blog + "", Toast.LENGTH_SHORT).show();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                transaction.replace(R.id.fragment_container, BlogDetailFragment.newInstance(blog), "MAIN");
                if(MainActivity.isHandHeld) {
                    transaction.addToBackStack(null);
                }
                transaction.commit();
            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);


        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount - 1) {
                        loading = false;
                        pb.setVisibility(View.VISIBLE);
                        request();
                    }
                }
            }
        });

        return rootView;
    }

    private void request() {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        String url = "http://oakraw.com/api/get_main_blog.php?page=" + PAGE + "&limit=" + LIMIT;

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        pb.setVisibility(View.GONE);
                        Gson gson = new Gson();
                        BlogList[] list = gson.fromJson(response.toString(), BlogList[].class);
                        //setList(list);
                        for (BlogList b : list) {
                            blogList.add(b);
                        }
                        mAdapter.notifyDataSetChanged();
                        loading = true;
                        PAGE++;
                        if (list.length == 0) {
                            loading = false;
                            Toast.makeText(getActivity(), "Out of content", Toast.LENGTH_SHORT).show();
                            int padding = Math.round(Toolbox.dipToPixels(getActivity(),2f));
                            mRecyclerView.setPadding(0, padding, 0, padding);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("oakTag", "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(req, tag_json_obj);
    }

    private void getColor() {
        String tag_json_obj = "json_obj_req";

        String url = "http://oakraw.com/api/get_categories.php";

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        pb.setVisibility(View.GONE);
                        Gson gson = new Gson();
                        CategoryColor[] list = gson.fromJson(response.toString(), CategoryColor[].class);
                        //setList(list);
                        AppController.catColor.clear();
                        for (CategoryColor c : list) {
                            AppController.catColor.put(c.category, c.color);
                        }
                        mAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("oakTag", "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(req, tag_json_obj);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //check internet connetion and then check location
        if(hasConnection()){
            noInternet.setVisibility(View.GONE);
        }
        else{
            noInternet.setVisibility(View.VISIBLE);
            noInternet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasConnection()) {
                        noInternet.setVisibility(View.GONE);
                        getColor();
                        request();
                    }
                }
            });
        }
    }

    /*check internet connection*/
    public boolean hasConnection() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;
    }

}
