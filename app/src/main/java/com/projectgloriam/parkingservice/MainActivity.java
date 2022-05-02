package com.projectgloriam.parkingservice;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
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

public class MainActivity extends AppCompatActivity implements
        MapsFragment.OnFragmentInteractionListener,
        ParkFragment.OnFragmentInteractionListener,
        PaymentFragment.OnFragmentInteractionListener,
        TicketDetailsFragment.OnFragmentInteractionListener{

    public DrawerLayout drawerLayout;
    private NavigationView navView;
    public Toolbar app_bar;
    public AppBarConfiguration appBarConfiguration;
    public NavController navController;
    public ActionBarDrawerToggle toggle;
    public UserViewModel userModel;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**/

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

        navView.setNavigationItemSelectedListener( item -> {
            switch (item.getItemId()) {

                case R.id.logout: {
                    logout();
                    break;
                }

                default: {
                    // Fallback for all other (normal) cases.
                    boolean handled = NavigationUI.onNavDestinationSelected(item, navController);

                    // This is usually done by the default ItemSelectedListener.
                    // But there can only be one! Unfortunately.
                    if (handled) drawerLayout.closeDrawer(navView);

                    // return the result of NavigationUI call
                    return handled;
                }
            }

            return false;
        });

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                int lockMode;
                if(navDestination.getId() == R.id.loginFragment || navDestination.getId() == R.id.userFragment || navDestination.getId() == R.id.mapsFragment) {
                    app_bar.setVisibility(View.GONE);

                    lockMode = DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
                    drawerLayout.setDrawerLockMode(lockMode);
                    toggle.setDrawerIndicatorEnabled(false);
                } else {
                    app_bar.setVisibility(View.VISIBLE);

                    lockMode = DrawerLayout.LOCK_MODE_UNLOCKED;
                    drawerLayout.setDrawerLockMode(lockMode);
                    toggle.setDrawerIndicatorEnabled(true);
                }
            }
        });

        //setup Navigation View with Navigation UI
        NavigationUI.setupWithNavController(app_bar, navController, appBarConfiguration);

    }

    public  void logout(){

        userModel.getSession().observe(this, session -> {
        // Perform an action with the latest item data
            session.close();
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}