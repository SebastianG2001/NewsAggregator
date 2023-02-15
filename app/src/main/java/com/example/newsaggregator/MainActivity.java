package com.example.newsaggregator;
import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.newsaggregator.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
public class MainActivity extends AppCompatActivity {
    String apiKey = "c0cb9d28e8514333bb218e2cbb7d3f83";
    private ActivityMainBinding binding;
    private Menu optionsMenu;
    private Menu navigation_menu;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Source[] sourcesArr;
    private HashMap<String, Integer> colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initColors();
        initDrawer();
        initNavigationMenu();
    }

    private void initColors() {
        colors = new HashMap<>();
        colors.put("All", Color.BLACK);
        colors.put("business", Color.GREEN);
        colors.put("entertainment", Color.RED);
        colors.put("general", Color.rgb(255, 215, 0));
        colors.put("health", Color.rgb(0, 139, 139));
        colors.put("science", Color.BLUE);
        colors.put("sports", Color.rgb(125, 249, 255));
        colors.put("technology", Color.rgb(255, 16, 240));
    }

    private void initDrawer() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, binding.myDrawerLayout, R.string.nav_open, R.string.nav_close);
        binding.myDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        binding.navigationMenu.setNavigationItemSelectedListener(menuItem -> {
            // set item as selected to persist highlight
            menuItem.setChecked(true);
            // close drawer when item is tapped
            binding.myDrawerLayout.closeDrawer(GravityCompat.START, true);
            Objects.requireNonNull(getSupportActionBar()).setTitle(menuItem.getTitle().toString());
            String sourceId = SourceId(menuItem.getTitle());
            final String url = "https://newsapi.org/v2/top-headlines?sources=" + sourceId + "&apiKey=" + apiKey;

            RequestQueue mRequestQueue;
            //RequestQueue initialized
            mRequestQueue = Volley.newRequestQueue(this);

            //String Request initialized
            StringRequest mStringRequest = new StringRequest(Request.Method.GET, url, response -> {
                TopHeadline newHeadline;
                navigation_menu = binding.navigationMenu.getMenu();
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    JSONArray articlesObj = jsonObj.getJSONArray("articles");
                    TopHeadline[] headlinesArr = new TopHeadline[articlesObj.length()];
                    for (int i = 0; i < articlesObj.length(); i++) {
                        JSONObject articleObj = articlesObj.getJSONObject(i);
                        newHeadline = new TopHeadline();
                        newHeadline.author = articleObj.getString("author");
                        newHeadline.title = articleObj.getString("title");
                        newHeadline.description = articleObj.getString("description");
                        newHeadline.url = articleObj.getString("url");
                        newHeadline.urlToImage = articleObj.getString("urlToImage");
                        newHeadline.publishedAt = articleObj.getString("publishedAt");
                        headlinesArr[i] = newHeadline;
                    }

                    setTopHeadLines(headlinesArr);
                } catch (Exception ignored) {

                }
            }, error -> Log.i(TAG, "Error :" + error.toString())) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("User-Agent", "News-App");
                    return headers;
                }
            };
            mRequestQueue.add(mStringRequest);
            //int[] images = {R.drawable.ic_baseline_blur_linear_24, R.drawable.brokenimage, R.drawable.loading, R.drawable.noimage};
            return true;
        });
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setTopHeadLines(TopHeadline[] headlinesArr) {
        ViewPager2Adapter adapter = new ViewPager2Adapter(headlinesArr);
        binding.viewpager2.setAdapter(adapter);
        // To get swipe event of viewpager2
        binding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            // triggered when you select a new page
            @SuppressLint("DefaultLocale")
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                binding.tvPageNumber.setText(String.format("%d of %d", position + 1, headlinesArr.length));
            }
        });
    }

    private String SourceId(CharSequence title) {
        for (Source source : sourcesArr) {
            if (source.name.equals(title.toString())) return source.id;
        }
        return "";
    }

    private void initNavigationMenu() {
        final String url = "https://newsapi.org/v2/sources?apiKey=" + apiKey;
        RequestQueue mRequestQueue;
        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);

        //String Request initialized
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, url, response -> {

            Source newSources;
            navigation_menu = binding.navigationMenu.getMenu();
            try {
                JSONObject jsonObj = new JSONObject(response);
                JSONArray sourcesObj = jsonObj.getJSONArray("sources");
                sourcesArr = new Source[sourcesObj.length()];
                for (int i = 0; i < sourcesObj.length(); i++) {
                    JSONObject sourceObj = sourcesObj.getJSONObject(i);
                    newSources = new Source();
                    newSources.id = sourceObj.getString("id");
                    newSources.name = sourceObj.getString("name");
                    newSources.category = sourceObj.getString("category");
                    sourcesArr[i] = newSources;
                }
                createOptionsMenu();
                setNavigationDrawer("All");
            } catch (Exception e) {

            }
        }, error -> Log.i(TAG, "Error :" + error.toString())) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "News-App");
                return headers;
            }
        };
        mRequestQueue.add(mStringRequest);
    }

    void createOptionsMenu() {
        if (sourcesArr != null) {
            optionsMenu.removeGroup(0);
            optionsMenu.add("All");
            HashSet<String> categories = new HashSet<>();
            for (Source source : sourcesArr) {
                categories.add(source.category);
            }
            for (String category : categories) {
                SpannableString s = new SpannableString(category);
                s.setSpan(new ForegroundColorSpan(getColors(category)), 0, s.length(), 0);
                optionsMenu.add(s);
            }
        }
    }

    private int getColors(String category) {
        try {
            return colors.get(category);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu optionsMenu) {
        getMenuInflater().inflate(R.menu.options_menu, optionsMenu);
        this.optionsMenu = optionsMenu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return super.onOptionsItemSelected(item);
        }
        setNavigationDrawer(item.getTitle());
        return true;
    }

    private void setNavigationDrawer(CharSequence selectedCategory) {
        navigation_menu.removeGroup(0);
        int sourceCount = 0;
        for (Source source : sourcesArr) {
            if ((selectedCategory.toString().equals(source.category)) || selectedCategory.equals("All")) {
                SpannableString s = new SpannableString(source.name);
                s.setSpan(new ForegroundColorSpan(getColors(source.category)), 0, s.length(), 0);
                navigation_menu.add(s);
                sourceCount++;
            }
        }
        Objects.requireNonNull(getSupportActionBar()).setTitle("News Gateway (" + sourceCount + ")");
    }
}