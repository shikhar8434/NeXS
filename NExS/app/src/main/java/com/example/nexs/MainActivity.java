package com.example.nexs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nexs.adapters.SportsNewsAdapter;
import com.example.nexs.models.Article;
import com.example.nexs.models.ArticleResponse;
import com.example.nexs.models.NewCard;
import com.example.nexs.services.NexsApi;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    public static Retrofit retrofit;
    public static NexsApi api;
    public static final String BASE_URL = "https://nexs-backend.vercel.app";

    Toolbar mainToolBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    //Remove this
    Button feedButton;

    //RecyclerViews
    RecyclerView sportsRv;
    RecyclerView eduRv;
    RecyclerView internationalRv;

    RecyclerView.LayoutManager horizontalLayoutManager;
    RecyclerView.LayoutManager horizontalLayoutManager2;
    RecyclerView.LayoutManager horizontalLayoutManager3;
    SportsNewsAdapter sportsNewsAdapter;
    SportsNewsAdapter educationNewsAdapter;
    SportsNewsAdapter internationalNewsAdapter;

    //temporary data
    ArrayList<NewCard> tempData;
    ArrayList<NewCard> tempData2;
    ArrayList<NewCard> tempData3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRetrofit();

        tempData = new ArrayList<>();
        tempData.add(new NewCard("Glenn Maxwell bought by RCB", R.drawable.maxwell));
        tempData.add(new NewCard("Glenn Maxwell bought by RCB", R.drawable.maxwell));
        tempData.add(new NewCard("Glenn Maxwell bought by RCB", R.drawable.maxwell));

        tempData2 = new ArrayList<>();
        tempData2.add(new NewCard("New NEP policy", R.drawable.edu_min));
        tempData2.add(new NewCard("New NEP policy", R.drawable.edu_min));
        tempData2.add(new NewCard("New NEP policy", R.drawable.edu_min));

        tempData3 = new ArrayList<>();
        tempData3.add(new NewCard("Biden wins", R.drawable.biden));
        tempData3.add(new NewCard("Biden wins", R.drawable.biden));
        tempData3.add(new NewCard("Biden wins", R.drawable.biden));

        mainToolBar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_drawer);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);

        feedButton = findViewById(R.id.feed_button);
        feedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FeedActivity.class));
            }
        });

        sportsRv = findViewById(R.id.sports_news_rv);
        sportsRv.setHasFixedSize(true);

        eduRv = findViewById(R.id.education_news_rv);
        eduRv.setHasFixedSize(true);

        internationalRv = findViewById(R.id.international_news_rv);
        internationalRv.setHasFixedSize(true);

        horizontalLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, true);
        horizontalLayoutManager2 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, true);
        horizontalLayoutManager3 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, true);
        sportsNewsAdapter = new SportsNewsAdapter(tempData);
        educationNewsAdapter = new SportsNewsAdapter(tempData2);
        internationalNewsAdapter = new SportsNewsAdapter(tempData3);


        sportsRv.setLayoutManager(horizontalLayoutManager);
        sportsRv.setAdapter(sportsNewsAdapter);

        eduRv.setLayoutManager(horizontalLayoutManager2);
        eduRv.setAdapter(educationNewsAdapter);

        internationalRv.setLayoutManager(horizontalLayoutManager3);
        internationalRv.setAdapter(internationalNewsAdapter);

    }

    private void setRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(NexsApi.class);
        /*Call<ArticleResponse> call = api.operateArticle(NexsApi.GET_ALL, new Article(), null, null);
        call.enqueue(new Callback<ArticleResponse>() {
            @Override
            public void onResponse(Call<ArticleResponse> call, Response<ArticleResponse> response) {
                Toast.makeText(MainActivity.this, response.body().getArticles().size() + "", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ArticleResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNotLoggedIn()) {
            navigationView.getMenu().getItem(2).setTitle("Log In").setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_login_24));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_search:
                onSearchRequested();
                return true;
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isNotLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() == null;
    }

    private NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_profile:
                    Toast.makeText(MainActivity.this, "Profile", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                case R.id.nav_bookmarks:
                    Toast.makeText(MainActivity.this, "Bookmarks", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
                default:
                    loginOrLogout();
                    drawerLayout.closeDrawer(GravityCompat.START);
            }
            ;
            return false;
        }
    };

    private void loginOrLogout() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_LONG).show();
            recreate();
        }
    }
}
