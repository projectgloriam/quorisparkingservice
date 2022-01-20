package com.projectgloriam.parkingservice;


import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements DrawerLocker, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar app_bar;
    AppBarConfiguration appBarConfiguration;
    NavController navController;
    ActionBarDrawerToggle toggle;
    private UserViewModel userModel;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNavigationUI();

        userModel = new ViewModelProvider(this).get(UserViewModel.class);
        userModel.getSession().observe(this, session -> {
            // Perform an action with the latest item data
            userid = session.getuserid();
        });

    }

    private void initNavigationUI() {
        //getting the drawer layout view
        drawerLayout = findViewById(R.id.drawer_layout);

        //setting toolbar as app bar: as its the standard nowadays. Pretty straight forward
        app_bar = findViewById(R.id.app_bar);

        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, app_bar, R.string.ACCEPT, R.string.CANCEL);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Setting up nav controller (a navigation function) for nav fragment view
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        appBarConfiguration =  new AppBarConfiguration.Builder(navController.getGraph())
                .setDrawerLayout(drawerLayout)
                .build();

        setSupportActionBar(app_bar);

        //adding navigation view of the drawer to navigation UI
        navView = findViewById(R.id.nav_view);

        setupNavController();

    }

    private void setupNavController(){

        //setup Navigation View with Navigation UI
        NavigationUI.setupWithNavController(app_bar, navController, appBarConfiguration);
    }

    public void setDrawerEnabled(boolean enabled) {
        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawerLayout.setDrawerLockMode(lockMode);
        toggle.setDrawerIndicatorEnabled(enabled);

        //TODO: Add the below code to any fragment that needs to disable drawer
        //((DrawerLocker) getActivity()).setDrawerEnabled(false);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.settings: {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, new SettingsFragment())
                        .commit();
                break;
            }
            case R.id.park_list:{
                setupNavController();
                break;
            }
            case R.id.logout:{
                logout();
                break;
            }
        }

        return false;
    }

    public  void logout(){

        userModel.getSession().observe(this, session -> {
        // Perform an action with the latest item data
            session.close();
        });
    }

}