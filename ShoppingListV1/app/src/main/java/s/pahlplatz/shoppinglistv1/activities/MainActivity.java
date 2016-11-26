package s.pahlplatz.shoppinglistv1.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import s.pahlplatz.shoppinglistv1.R;
import s.pahlplatz.shoppinglistv1.fragments.FragmentAdd;
import s.pahlplatz.shoppinglistv1.fragments.FragmentAll;
import s.pahlplatz.shoppinglistv1.fragments.FragmentCheckList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean doubleBackToExitPressedOnce = false;

    private static void hideKeyboard(Context ctx)
    {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // Check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Initialize activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSharedPreferences("settings", MODE_PRIVATE).getInt("userid", -1) == -1)
        {
            // Create login activity
            Intent loginIntent = new Intent(this, LoginActivity.class);

            // Prevent backwards navigation
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Start login activity
            startActivity(loginIntent);
            this.finish();
        } else
        {
            // Load fragment
            if (savedInstanceState == null)
            {
                Fragment fragment = null;
                Class fragmentClass;
                fragmentClass = FragmentAdd.class;

                try
                {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            }

            // Create toolbar
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // Create DrawerLayout
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            // Create NavigationView
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to leave", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.defaultmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings)
        {
            // Create login activity
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        hideKeyboard(this);
        Fragment fragment = null;
        Class fragmentClass = null;

        switch(item.getItemId())
        {
            case R.id.nav_add:
                fragmentClass = FragmentAdd.class;
                break;
            case R.id.nav_allproducts:
                fragmentClass = FragmentAll.class;
                break;
            case R.id.nav_list:
                fragmentClass = FragmentCheckList.class;
                break;
        }
        try
        {
            if (fragmentClass != null)
            {
                fragment = (Fragment) fragmentClass.newInstance();
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG, "onNavigationItemSelected: Couldn't create fragment", ex);
        }

        // Switch fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Close drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
