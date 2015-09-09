package com.oakraw.oakrawapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;
import com.oakraw.oakrawapp.AppController;
import com.oakraw.oakrawapp.MainActivity;
import com.oakraw.oakrawapp.R;
import com.oakraw.oakrawapp.adapter.BlogAdapter;
import com.oakraw.oakrawapp.model.BlogList;
import com.oakraw.oakrawapp.model.CategoryColor;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Rawipol on 4/24/15 AD.
 */
public class BlogDetailFragment extends Fragment {
    private int page;
    private ProgressBar progressBar;
    private WebView webview;
    private boolean mHasToRestoreState;
    private float mProgressToRestore;
    private Bundle webViewBundle;
    private FloatingActionButton fab;
    private FrameLayout noInternet;

    public static BlogDetailFragment newInstance(int page) {
        BlogDetailFragment gridviewFragment = new BlogDetailFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        gridviewFragment.setArguments(args);
        return gridviewFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("page", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blog_detail, container, false);

        if(MainActivity.isHandHeld) {
            ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        webview = (WebView) rootView.findViewById(R.id.webView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.pb);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);

        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient());
        webview.setWebViewClient(new CustomWebViewClient());
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setBuiltInZoomControls(true);

        if (savedInstanceState == null) {
            webview.loadUrl("http://oakraw.com/api/get_blog.php?id=" + page);
        } else {
            progressBar.setVisibility(View.GONE);
            webview.restoreState(savedInstanceState);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, "http://oakraw.com/blog/"+page);
                startActivity(Intent.createChooser(i, "Share URL"));
            }
        });


        return rootView;
    }

    public class CustomWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(i);
            return true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webview.saveState(outState);
    }


    @Override
    public void onPause() {
        super.onPause();

        webViewBundle = new Bundle();
        webview.saveState(webViewBundle);
    }

}
