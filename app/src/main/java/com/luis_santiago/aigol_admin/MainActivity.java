package com.luis_santiago.aigol_admin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.luis_santiago.aigol_admin.UI.fragment.BundesligaFragment;
import com.luis_santiago.aigol_admin.UI.fragment.ErediviseFragment;
import com.luis_santiago.aigol_admin.UI.fragment.LaLigaFragement;
import com.luis_santiago.aigol_admin.UI.fragment.LigaMxFragment;
import com.luis_santiago.aigol_admin.UI.fragment.Ligue1Fragment;
import com.luis_santiago.aigol_admin.UI.fragment.PremierLeagueFragment;
import com.luis_santiago.aigol_admin.UI.fragment.SerieAFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolBar();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setUpFragment(new LigaMxFragment());
    }

    private void setUpToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            finish();
        } else {
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id == R.id.nav_LigaMx){
            setUpFragment(new LigaMxFragment());
        }
        else if(id == R.id.nav_la_liga){
            setUpFragment(new LaLigaFragement());
        }

        else if(id == R.id.nav_ligue_1){
            setUpFragment(new Ligue1Fragment());
        }
        else if(id == R.id.nav_bundesliga){
            setUpFragment(new BundesligaFragment());
        }
        else if(id == R.id.nav_eredivisie){
            setUpFragment(new ErediviseFragment());
        }
        else if(id == R.id.nav_premier_league){
            setUpFragment(new PremierLeagueFragment());
        }
        else if(id == R.id.nav_serie_a){
            setUpFragment(new SerieAFragment());
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setUpFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_EXIT_MASK)
                .addToBackStack(null)
                .commit();
    }



}
