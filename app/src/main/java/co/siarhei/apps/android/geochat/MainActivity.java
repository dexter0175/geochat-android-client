
package co.siarhei.apps.android.geochat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.androidadvance.androidsurvey.SurveyActivity;
import com.google.android.gms.location.LocationRequest;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.patloew.rxlocation.RxLocation;


import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import co.siarhei.apps.android.geochat.Location.UserLocation;
import co.siarhei.apps.android.geochat.Model.User;
import co.siarhei.apps.android.geochat.UI.Adapters.MainViewPagerAdapter;
import co.siarhei.apps.android.geochat.UI.MainExploreFragment;
import co.siarhei.apps.android.geochat.UI.MainConnectionsFragment;
import co.siarhei.apps.android.geochat.UI.MainThisAreaFragment;
import co.siarhei.apps.android.geochat.Utils.Util;
import durdinapps.rxfirebase2.RxFirestore;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener
        {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Toolbar vToolbar;
    NavigationView vDrawerNavView;
    TabLayout tabHost;
    ViewPager pager;
    MainViewPagerAdapter pagerAdapter;

    DrawerLayout vDrawerLayout;
    SharedPreferences prefs;
    public User user;


    final int FEEDBACK_QUIZ_REQUEST = 1337;
    final int GEOLOCATION_PERMISSION_REQUEST = 1338;
    public CompositeDisposable  cd = new CompositeDisposable();
    public Observable<Location> locationObservable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final AppCompatActivity self = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.prefs = this.getSharedPreferences("geochat", Context.MODE_PRIVATE);

        networkCheck();


        vDrawerLayout =  findViewById(R.id.main_drawerlayout);
        vDrawerNavView =  findViewById(R.id.main_drawer);
        vDrawerNavView.setNavigationItemSelectedListener(this);



        AnimationDrawable animationDrawable =
                (AnimationDrawable) vDrawerNavView.getHeaderView(0).getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();



        vToolbar = findViewById(R.id.main_toolbar);
        final ActionBar actionBar = getSupportActionBar();
        setSupportActionBar(vToolbar);

        if (actionBar != null) {

            actionBar.setTitle(R.string.app_name);
            actionBar.setDisplayHomeAsUpEnabled(true);
            vToolbar.setTitleTextColor(getResources().getColor(R.color.md_black_1000, null));
            vToolbar.setLogo(getResources().getDrawable(R.drawable.geochat_logo,null));
            actionBar.setHomeAsUpIndicator(R.drawable.geochat_logo_large_foreground);
        }

        //setCurrentLocation(53.971756, 27.566803);
        setupMaterialTabs();
        authoriseGeolocationFunctionality();
        setupGeolocationUpdates();
        getUser();

    }

            @SuppressLint("CheckResult")
    private void getUser() {
                DocumentReference doc = firestore.collection("Users").document(mAuth.getUid());
                RxFirestore.getDocument(doc)
                        .subscribe(snapshot->{
                           this.user = snapshot.toObject(User.class);
                            insertUserSpecificLabels(this.user);
                        },throwable -> {});
            }
    public void setCurrentLocation(double _lat, double _long){
        prefs.edit().putLong("locationLat", Double.doubleToRawLongBits(_lat)).apply();
        prefs.edit().putLong("locationLong", Double.doubleToRawLongBits(_long)).apply();

    }
    public void setCurrentLocation(Location _location){
        prefs.edit().putLong("locationLat", Double.doubleToRawLongBits(_location.getLatitude())).apply();
        prefs.edit().putLong("locationLong", Double.doubleToRawLongBits(_location.getLongitude())).apply();
    }
    public void setCurrentRadius(double _radius){
        prefs.edit().putLong("locationRadius", Double.doubleToRawLongBits(_radius)).apply();
    }
    public Location getCurrentLocation() {
        Location _loc = new Location("");
        _loc.setLatitude(Double.longBitsToDouble(prefs.getLong("locationLat", 0)));
        _loc.setLongitude(Double.longBitsToDouble(prefs.getLong("locationLong", 0)));
        return _loc;
    }
    public double getCurrentRadius() {
        return Double.longBitsToDouble(prefs.getLong("locationRadius", 0));
    }





    public void insertUserSpecificLabels (User usr){

        // Drawer Panel Dynamically Change Title
        TextView vDrawerTitle =  vDrawerNavView.getHeaderView(0).findViewById(R.id.main_drawer_header_title);
        vDrawerTitle.setText(usr.first_name + " " + usr.last_name);

    }



    public void setupMaterialTabs(){
        tabHost = this.findViewById(R.id.main_materialTabHost);
        pager = this.findViewById(R.id.main_materialViewPager);
        pager.setOffscreenPageLimit(2);
        pagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(new MainThisAreaFragment(), "THIS AREA");
        pagerAdapter.addFragment(new MainConnectionsFragment(), "CONNECTIONS");
        pagerAdapter.addFragment(new MainExploreFragment(), "EXPLORE");

        pager.setAdapter(pagerAdapter);
        tabHost.setupWithViewPager(pager);

    }


    public void authoriseGeolocationFunctionality() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, GEOLOCATION_PERMISSION_REQUEST);
        } else {
            setupGeolocationUpdates();

        }
    }



    public void setupGeolocationUpdates() {

        RxLocation rxLocation = new RxLocation(getApplicationContext());
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000);
        this.locationObservable = rxLocation.location().updates(locationRequest);

        cd.add( locationObservable
                .subscribe(this::setCurrentLocation, throwable -> {})
        );
        cd.add( locationObservable
                .debounce(10,  TimeUnit.SECONDS)
                .flatMapCompletable(location -> {
                    DocumentReference ref = firestore.collection("Users").document(mAuth.getUid());
                    return  RxFirestore.updateDocument(ref,"updated_at", Calendar.getInstance().getTime(),"location", new UserLocation(getCurrentLocation()));
                })
                .subscribe(()->{}, throwable -> {})
        );
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GEOLOCATION_PERMISSION_REQUEST:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        setupGeolocationUpdates();

                    }

                } else {

                    Toast.makeText(this, "GEOLOCATION PERMISSION WAS NOT GRANTED", Toast.LENGTH_SHORT).show();

                }

                break;

            default:

                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_toolbar, menu);
        return true;
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (prefs.getBoolean("areAlertsEnabled", false)) {
            menu.getItem(0).setIcon(R.drawable.ic_notifications_active_black_24dp);
        } else {
            menu.getItem(0).setIcon(R.drawable.ic_notifications_off_black_24dp);
        }

        if(!prefs.getBoolean("isAnonymous", false) ){
            menu.getItem(1).setIcon(R.drawable.ic_person_outline_black_24dp);
        } else {
            menu.getItem(1).setIcon(R.drawable.ic_person_black_24dp);
        }

        return true;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Drawable icon;
        switch (item.getItemId()) {

            case R.id.action_anontoggle:

                icon = item.getIcon();
                if (icon.getConstantState().equals(getResources().getDrawable(R.drawable.ic_person_black_24dp, null).getConstantState())){
                    item.setIcon(R.drawable.ic_person_outline_black_24dp);
                    Toast.makeText(this, "User Anonymity turned off!", Toast.LENGTH_SHORT).show();
                    prefs.edit().putBoolean("isAnonymous", false).apply();

                } else if (icon.getConstantState().equals(getResources().getDrawable(R.drawable.ic_person_outline_black_24dp,null).getConstantState())){
                    item.setIcon(R.drawable.ic_person_black_24dp);
                    Toast.makeText(this, "User Anonymity turned on!", Toast.LENGTH_SHORT).show();
                    prefs.edit().putBoolean("isAnonymous", true).apply();
                }


            case R.id.action_alerttoggle:


                icon = item.getIcon();
                if (icon.getConstantState().equals(getResources().getDrawable(R.drawable.ic_notifications_active_black_24dp, null).getConstantState())) {
                    item.setIcon(R.drawable.ic_notifications_off_black_24dp);
                    Toast.makeText(this, "Notifications & alerts turned off!", Toast.LENGTH_SHORT).show();
                    prefs.edit().putBoolean("areAlertsEnabled", false).apply();

                } else if (icon.getConstantState().equals(getResources().getDrawable(R.drawable.ic_notifications_off_black_24dp, null).getConstantState())) {
                    item.setIcon(R.drawable.ic_notifications_active_black_24dp);
                    Toast.makeText(this, "Notifications & alerts turned on!", Toast.LENGTH_SHORT).show();
                    prefs.edit().putBoolean("areAlertsEnabled", true).apply();

                }

                break;

            default:
                break;
        }

        return true;
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            
            case R.id.main_drawer_nav_item_signout: {

                Toast.makeText(this, "Signing out ...", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent intent = new Intent(this, RegistrationActivity.class);
                startActivity(intent);
                finish();
                break;

            }
            case R.id.main_drawer_nav_item_feedback: {

                Intent i_survey = new Intent(this, SurveyActivity.class);
                i_survey.putExtra("json_survey", Util.constructSurveyContent());
                startActivityForResult(i_survey, FEEDBACK_QUIZ_REQUEST);

                break;

            }
            case R.id.main_drawer_nav_item_about: {

                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);

                break;

            }
            
            default:

                Toast.makeText(this, "This feature is coming soon!", Toast.LENGTH_SHORT).show();
                break;

        }

        vDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == FEEDBACK_QUIZ_REQUEST) {

            if (resultCode == RESULT_OK) {

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"dexter0175@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "GeoChat App Feedback: ");
                intent.putExtra(Intent.EXTRA_TEXT, Util.constructFeedbackContent(data));


                startActivity(Intent.createChooser(intent, "Email via..."));

            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        networkCheck();

    }




    @Override
    protected void onPause() {
        super.onPause();

    }



    private boolean networkCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() == null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.error_network_connection)
                    .setCancelable(false)
                    .setPositiveButton("Exit...", (dialog, id) -> {

                        finish();
                        System.exit(0);

                    });
            AlertDialog alert = builder.create();
            alert.show();

            return false;

        } else {

            return true;

        }
    }


}
