package com.coded2.wearbatteryalert;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnBatteryChargeListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    private boolean isCharging;
    private int current_screen;

    private static final int HOME =      1;
    private static final int SETTINGS =  2;
    private static final int ABOUT =     3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        showHomeFragment();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if(current_screen!=HOME){
                showHomeFragment();
                return;
            }

            super.onBackPressed();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            showAboutFragment();
        } else if (id == R.id.nav_settings) {
            showSettingsFragment();
        }else{
            showHomeFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSettingsFragment() {
        Log.d(Constants.PACKAGE_NAME,"show settings fragment");
        SettingsFragment fragment = new SettingsFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_view,fragment).commit();
        current_screen = SETTINGS;
    }

    private void showHomeFragment() {
        Log.d(Constants.PACKAGE_NAME,"show home fragment");
        HomeFragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_view,fragment).commit();
        current_screen = HOME;
    }

    private void showAboutFragment() {
        Log.d(Constants.PACKAGE_NAME,"show about fragment");
        AboutFragment fragment = new AboutFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_view,fragment).commit();
        current_screen = ABOUT;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent itService = new Intent(this,ChargeMonitorService.class);
        final boolean isServiceEnabled = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(this.getResources().getString(R.string.key_service_enable), true);
        if(isCharging && isServiceEnabled)
            startService(itService);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Intent itService = new Intent(this,ChargeMonitorService.class);
        stopService(itService);
    }

    @Override
    public void OnBatteryCharge(boolean isCharging) {
        this.isCharging =  isCharging;
    }
}