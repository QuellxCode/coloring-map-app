package com.example.hp.database;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
//for @menu/login_page
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.BackgroundLayer;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.FillExtrusionLayer;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.HillshadeLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyValue;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.StringTokenizer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static java.lang.String.valueOf;


public class Login_page extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , OnMapReadyCallback,
        MapboxMap.OnMapClickListener {
    public String name, email, country, coordinates, address,
            Current_user_id, colour,lat_Cord,lng_Cord ,zm_Cord;
    double abc,abcd;
    boolean backbutton_clicked,remove_source;
    TextView emailTextView, nameTextView;
    ProgressDialog loadingBar;
    ImageView displayImageView;
    Fragment currentFragment;
    int i = 1,abcde;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreference;
    JSONObject further_features,properties;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    final static int Gallery_pick = 1;
    final static int COLOR_PICK = 9;
    private String TAG = "Login_page";
    MapView mapView;
    MapboxMap mapboxMap;
    Style style;
    private static final String geoJsonSourceId = "source";
    private static final String geoJsonLayerId = "layer";


    private final List<List<Point>> points = new ArrayList<>();
    private final List<Point> outerPoints = new ArrayList<>();

    //    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_login_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login_page);
        toolbar.setTitle("Login Page");
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView = navigationView.getHeaderView(0);

        //TODO:write names of your fields below
        displayImageView = navHeaderView.findViewById(R.id.drawer_imageView);
        nameTextView = (TextView) navHeaderView.findViewById(R.id.drawer_textView);
        emailTextView = (TextView) navHeaderView.findViewById(R.id.drawer_emailView);
        loadingBar = new ProgressDialog(this);
        Mapbox.getInstance(getApplicationContext(), "pk.eyJ1Ijoic2FsbGVoaGV5YXQiLCJhIjoiY2pydzBjbGo4MDcxZjN5cXVkbDM2M3FhYSJ9.9S1FTNefItPw02TA-2WzYQ");
        Mapbox.setAccessToken("pk.eyJ1Ijoic2FsbGVoaGV5YXQiLCJhIjoiY2pydnpxOGowMDZ4ZDQ0bTF4bmh5aW5tbSJ9.kG2So6lAuLtDeNM-P8gT2Q");
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        displayImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                //after intent it wait for user responce to proceed
                startActivityForResult(galleryIntent, Gallery_pick);
            }
        });
    }


    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        Login_page.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                Login_page.this.style = style;
                mapboxMap.addOnMapClickListener(Login_page.this);

//                Toast.makeText(Login_page.this, "Tab on country", Toast.LENGTH_SHORT).show();

/*              Layer mapText = mapboxMap.getStyle().getLayer("country-label");
                mapText.setProperties();*/
            }
        });
    }
    public boolean onMapClick(@NonNull LatLng point) {


        //get coordinates of point (on user tab)
        coordinates = String.format(Locale.US, "User clicked at: %s", point.toString());
        address = getCountryName(getApplicationContext(), point.getLatitude(), point.getLongitude());

        if(address == null){
            Toast.makeText(Login_page.this, "Network Error", Toast.LENGTH_SHORT).show();
        }

        else {

            if (mapboxMap.getCameraPosition().zoom < 3) {


            country = "{\"name\":\"" + address + "\"}";
            Toast.makeText(Login_page.this, address, Toast.LENGTH_SHORT).show();

                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(30.3308401, 71.247499)) // Sets the new camera position
                        .zoom(4) // Sets the zoom
                        .bearing(0) // Rotate the camera
                        .tilt(0) // Set the camera tilt
                        .build(); // Creates a CameraPosition from the builder

                mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 6000);

                Toast.makeText(Login_page.this, "Tab on Province", Toast.LENGTH_SHORT).show();


            } else if (mapboxMap.getCameraPosition().zoom < 5) {

                address = getProvinceName(getApplicationContext(), point.getLatitude(), point.getLongitude());
                String province = "{\"name\":\"" + address + "\"}";
                Toast.makeText(Login_page.this, address, Toast.LENGTH_SHORT).show();

                //map camera positioning
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(point.getLatitude(),point.getLongitude())) // Sets the new camera position
                        .zoom(7) // Sets the zoom
                        .bearing(0) // Rotate the camera
                        .tilt(0) // Set the camera tilt
                        .build(); // Creates a CameraPosition from the builder

                mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 6000);

                Toast.makeText(Login_page.this, "Tab on Region", Toast.LENGTH_SHORT).show();


            }

            else if(mapboxMap.getCameraPosition().zoom < 7){

                //map camera positioning
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(point.getLatitude(),point.getLongitude())) // Sets the new camera position
                        .zoom(9) // Sets the zoom
                        .bearing(0) // Rotate the camera
                        .tilt(0) // Set the camera tilt
                        .build(); // Creates a CameraPosition from the builder

                mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 6000);

                Toast.makeText(Login_page.this, "Tab on city", Toast.LENGTH_SHORT).show();


            }
            else {

                address = getCityName(getApplicationContext(), point.getLatitude(), point.getLongitude());
                String City = "{\"name\":\"" + address + "\"}";
                Toast.makeText(Login_page.this, address, Toast.LENGTH_SHORT).show();

                try {

                    InputStream inputStream = getResources().openRawResource(R.raw.city_geo);
                    Scanner scanner = new Scanner(inputStream);
                    StringBuilder builder = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        builder.append(scanner.nextLine());
                    }
                    JSONObject root = new JSONObject(builder.toString());
                    JSONArray features = root.getJSONArray("features");

                    for (i = 0; i < features.length(); i++) {
                        further_features = features.getJSONObject(i);
                        properties = further_features.getJSONObject("properties");
                        if (properties.toString().equals(City)) {
//                      Toast.makeText(Login_page.this, properties.toString(), Toast.LENGTH_SHORT).show();
                            Log.i("Properties: ", properties.toString());
                            break;
                        }
                    }
                    if (style.getLayer(geoJsonLayerId) == null) {
                        startActivityForResult(new Intent(Login_page.this, Pop_up.class), COLOR_PICK);
                    } else {
                        style.removeLayer(new FillLayer(geoJsonLayerId, geoJsonSourceId));
                        remove_source = true;
                        addGeoJsonSourceToMap(style);
                    }
                } catch (Throwable throwable) {
                    Log.e("ClickOnLayerActivity", "Couldn't add GeoJsonSource to map", throwable);
                }
            }
        }
        return true;
    }

    private void addGeoJsonSourceToMap(@NonNull Style loadedMapStyle) {

        if (remove_source){
            //remove source
            loadedMapStyle.removeSource(geoJsonSourceId);
             remove_source = false;
        }

        else {
            loadedMapStyle.addSource(new GeoJsonSource(geoJsonSourceId, valueOf(further_features)));

            colour = Pop_up.get_colour();

            style.addLayer(new FillLayer(geoJsonLayerId, geoJsonSourceId)
                    .withProperties(fillOpacity(0.8f),
                            fillColor(Color.parseColor(colour))));
        }
    }


    public static String getCountryName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address result;

            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getCountryName();
            }
            return null;
        } catch (IOException ignored) {
            //do something
        }
        return null;
    }

    public static String getProvinceName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address result;

            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAdminArea();
            }
            return null;
        } catch (IOException ignored) {
            //do something
        }
        return null;
    }

    public static String getCityName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address result;

            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getLocality();
            }
            return null;
        } catch (IOException ignored) {
            //do something
        }
        return null;
    }

    public void addImport() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (currentFragment != null) {
            fragmentTransaction.remove(currentFragment);
        }
        Fragment_import fragment_import = new Fragment_import();
        fragmentTransaction.add(R.id.fragment_container, fragment_import);
        fragmentTransaction.commit();
        currentFragment = fragment_import;
    }


    public void addGallery() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (currentFragment != null) {
            fragmentTransaction.remove(currentFragment);
        }
        Fragment_gallery fragment_gallery = new Fragment_gallery();
        fragmentTransaction.add(R.id.fragment_container, fragment_gallery);
        fragmentTransaction.commit();
        currentFragment = fragment_gallery;
    }

    public void addSlideshow() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (currentFragment != null) {
            fragmentTransaction.remove(currentFragment);
        }
        Fragment_slideshow fragment_slideshow = new Fragment_slideshow();
        fragmentTransaction.add(R.id.fragment_container, fragment_slideshow);
        fragmentTransaction.commit();
        currentFragment = fragment_slideshow;
    }

    public void addTools() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (currentFragment != null) {
            fragmentTransaction.remove(currentFragment);
        }
        Fragment_tools fragment_tools = new Fragment_tools();
        fragmentTransaction.add(R.id.fragment_container, fragment_tools);
        fragmentTransaction.commit();
        currentFragment = fragment_tools;
    }

    public void addShare() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (currentFragment != null) {
            fragmentTransaction.remove(currentFragment);
        }
        Fragment_share fragment_share = new Fragment_share();
        fragmentTransaction.add(R.id.fragment_container, fragment_share);
        fragmentTransaction.commit();
        currentFragment = fragment_share;
    }

    public void addSend() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (currentFragment != null) {
            fragmentTransaction.remove(currentFragment);
        }
        Fragment_send fragment_send = new Fragment_send();
        fragmentTransaction.add(R.id.fragment_container, fragment_send);
        fragmentTransaction.commit();
        currentFragment = fragment_send;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == COLOR_PICK && resultCode == RESULT_OK) {
            addGeoJsonSourceToMap(style);
        }
        if (requestCode == Gallery_pick && resultCode == RESULT_OK && data != null) {
            Uri image_uri = data.getData();
            CropImage.activity(image_uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("updating your profie image");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);
                Uri result_Uri = result.getUri();
                StorageReference filePath = storageReference.child(Current_user_id + ".jpg");
                filePath.putFile(result_Uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login_page.this, "image stored successfully(storage)", Toast.LENGTH_SHORT).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            databaseReference.child(Current_user_id).child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Login_page.this, "image stored successfully(database)", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            } else {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(Login_page.this, "Error Occured" + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
            } else {
                Toast.makeText(Login_page.this, "Error Occured : Crop image again", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    //-------//
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        else {
            super.onBackPressed();
        }

        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                backbutton_clicked = false;
            }
        }.start();

    }

/*    @Override
    protected void onDestroy() {
        Process.killProcess(Process.myPid());
        super.onDestroy();
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_page, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            addImport();
        } else if (id == R.id.nav_gallery) {
            addGallery();
        } else if (id == R.id.nav_slideshow) {
            addSlideshow();
        } else if (id == R.id.nav_manage) {
            addTools();
        } else if (id == R.id.nav_share) {
            addShare();
        } else if (id == R.id.nav_send) {
            addSend();
        } else if (id == R.id.signout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
