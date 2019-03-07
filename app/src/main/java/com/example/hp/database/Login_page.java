package com.example.hp.database;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
//for @menu/login_page
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMap.OnMapClickListener;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.light.Position;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertPathBuilder;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

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
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;

public class Login_page extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , OnMapReadyCallback,
        MapboxMap.OnMapClickListener  {
    String name, email,country,coordinates,address;
    boolean backbutton_clicked;
    TextView emailTextView;
    TextView nameTextView;
    ProgressDialog loadingBar;
    ImageView displayImageView;
    Fragment currentFragment;
    String Current_user_id;
    public String colour;
    //drawer variables
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    //to store data in device storage
    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreference;
    //jason object
    JSONObject further_features;
    JSONObject properties;
    //firebase references
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    //user profile image variable
    final static int Gallery_pick = 1;
    private String TAG = "Login_page";
    //mapbox
    MapView mapView;
    //colouring
    MapboxMap mapboxMap;
    Style style;
    private FeatureCollection featureCollection;
    private static final String geoJsonSourceId = "";
    private static final String geoJsonLayerId = "";


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mapbox line
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_login_page);


        //code for toolbar(which contains menu bars , arrow icons etc)
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login_page);
        toolbar.setTitle("Login Page");
        setSupportActionBar(toolbar);

        //floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //actionbar and button (some of code include in onOptionsItemSelected method below )
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //----------//

        //items/menus on drawer (rest of code below at onNavigationItemSelected method)
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //fetching authenticating data from firebase
        mAuth = FirebaseAuth.getInstance();
        //initaitaion and providing file name
        sharedPreferences = getSharedPreferences("userid", Context.MODE_PRIVATE);
        //if user logged in thorugh google sign in (no id required from firebase and storage)
        //login mode will be not null and will hava text manual
        String loginMode = getIntent().getStringExtra("loginMode");
        if (loginMode != null && loginMode.equals("Manual") ) {
            //if id arrives from phone storage else go for firebase

            if (sharedPreferences.getString("id", "").equals("")) {
                // get specific id of database entries at variable (Current_user_id)
                Current_user_id = mAuth.getCurrentUser().getUid();
            } else {
                //taking id from device storage
                Current_user_id = sharedPreferences.getString("id", "");
            }
        }
        //firebase database instance code
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //firebase storage instance code
        storageReference = FirebaseStorage.getInstance().getReference();

        //importing data on image , text and email from firebase database on header
        View navHeaderView = navigationView.getHeaderView(0);

        //TODO:write names of your fields below
        //improting image (add library as well)
        displayImageView = navHeaderView.findViewById(R.id.drawer_imageView);
        //improting name
        nameTextView = (TextView) navHeaderView.findViewById(R.id.drawer_textView);
        //improting email
        emailTextView = (TextView) navHeaderView.findViewById(R.id.drawer_emailView);
        //loading bar variable and field
        loadingBar = new ProgressDialog(this);

        //retrofit (inclues an interface named as api)
        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .build();*/

//        GitHubService service = retrofit.create(GitHubService.class);

        // Mapbox Access token
        //go to mapbox sign up and get accesstoken
        //toolbar disappears if this(mapbox) code is allowed
        Mapbox.getInstance(getApplicationContext(), "pk.eyJ1Ijoic2FsbGVoaGV5YXQiLCJhIjoiY2pydzBjbGo4MDcxZjN5cXVkbDM2M3FhYSJ9.9S1FTNefItPw02TA-2WzYQ");

        Mapbox.setAccessToken("pk.eyJ1Ijoic2FsbGVoaGV5YXQiLCJhIjoiY2pydnpxOGowMDZ4ZDQ0bTF4bmh5aW5tbSJ9.kG2So6lAuLtDeNM-P8gT2Q");



        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        //taking image from user
        displayImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_pick);
            }
        });

        new User_data().execute(Current_user_id); //id parsed to user data (async task) method

        //saving user id in device(for automatic login)
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("id", Current_user_id);
        editor.apply();

    }

    //mapbox code
    //access map from mapbox
    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        Login_page.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                Login_page.this.style = style;
                mapboxMap.addOnMapClickListener(Login_page.this);
                //map language
/*              Layer mapText = mapboxMap.getStyle().getLayer("country-label");
                mapText.setProperties(textField("{name_en}"));*/



            }
        });
    }

    //selecting layer on map
    @Override
        public boolean onMapClick(@NonNull LatLng point) {

        //pop up initiates
//        startActivity(new Intent(Login_page.this,Pop_up.class));

        //check on already coloured layer
        PointF pointf = mapboxMap.getProjection().toScreenLocation(point);
        RectF rectF = new RectF(pointf.x - 10, pointf.y - 10, pointf.x + 10, pointf.y + 10);
        List<Feature> features = mapboxMap.queryRenderedFeatures(rectF, geoJsonLayerId);

        //layer is already coloured
        if (features.size() > 0) {

            for (Feature feature : features) {
                Log.d("Feature found with %1$s", feature.toJson());
                Toast.makeText(Login_page.this, R.string.click_on_polygon_toast,
                        Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        //send its coordinate to colour layer function
        else {

            //gets country by coordinates (when user tab)
            coordinates = String.format(Locale.US, "User clicked at: %s", point.toString());
            address = getCountryName(getApplicationContext(), point.getLatitude(), point.getLongitude());
            country = "{\"name\":\"" + address + "\"}";
//            Toast.makeText(Login_page.this, address, Toast.LENGTH_SHORT).show();
            //--------------//
            addGeoJsonSourceToMap(style);
        }
            return false;
    }

    private void addGeoJsonSourceToMap(@NonNull Style loadedMapStyle) {
        try {

            //accessing courntry file from raw folder in form of stream
            InputStream inputStream = getResources().openRawResource(R.raw.countries_geo);
            Scanner scanner = new Scanner(inputStream);
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
            builder.append(scanner.nextLine());
            }
             //maping with jason file
            JSONObject root = new JSONObject(builder.toString());
            JSONArray features = root.getJSONArray("features");

            //loop compares country on tab with assest file
            for (int i = 1; i < 183; i++) {
                //access complete instance of coordinates (passable in geojasonsource method)
                further_features = features.getJSONObject(i);
                //access name of country with that coordinates
                properties = further_features.getJSONObject("properties");
                if (properties.toString().equals(country)) {

                    Toast.makeText(Login_page.this, properties.toString(), Toast.LENGTH_SHORT).show();
                    Log.i("Properties: ", properties.toString());
                    break;
                    }
                }

            //passing coordinates from assest file
            loadedMapStyle.addSource(new GeoJsonSource(geoJsonSourceId, String.valueOf(further_features)));



//            while (colour == null) {
                //method from pop up class (get colour)
                colour = Pop_up.get_colour();
//            }

            //colour layer function
            style.addLayer(new FillLayer(geoJsonLayerId, geoJsonSourceId)
                    .withProperties(fillOpacity(0.5f),
                            fillColor(Color.parseColor("#1CFF1C")))
            );

        }
           catch (Throwable throwable) {
            Log.e("ClickOnLayerActivity", "Couldn't add GeoJsonSource to map", throwable);
        }
    }
        //getting name of country on map by coordinates on user tab
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


    //adding fragments to login_page (dynamically)
    public void addImport() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (currentFragment != null) {
            //will terminate the last fragment
            fragmentTransaction.remove(currentFragment);
        }
        //object of fragment (import_class)
        Fragment_import fragment_import = new Fragment_import();
        // id of framelayout of activity on which you are upto add fragments,name of java class of that activity
        fragmentTransaction.add(R.id.fragment_container, fragment_import);
        fragmentTransaction.commit();
        currentFragment = fragment_import;
    }


    public void addGallery() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (currentFragment != null) {
            //will terminate the last fragment
            fragmentTransaction.remove(currentFragment);
        }
        //object of fragment (import_class)
        Fragment_gallery fragment_gallery = new Fragment_gallery();
        // id of framelayout on which you are upto add,name of java class of fragment
        fragmentTransaction.add(R.id.fragment_container, fragment_gallery);
        fragmentTransaction.commit();
        currentFragment = fragment_gallery;
    }

    public void addSlideshow() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (currentFragment != null) {
            //will terminate the last fragment
            fragmentTransaction.remove(currentFragment);
        }
        //object of fragment (import_class)
        Fragment_slideshow fragment_slideshow = new Fragment_slideshow();
        // id of framelayout on which you are upto add,name of java class of fragment
        fragmentTransaction.add(R.id.fragment_container, fragment_slideshow);
        fragmentTransaction.commit();
        currentFragment = fragment_slideshow;
    }

    public void addTools() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (currentFragment != null) {
            //will terminate the last fragment
            fragmentTransaction.remove(currentFragment);
        }
        //object of fragment (import_class)
        Fragment_tools fragment_tools = new Fragment_tools();
        // id of framelayout on which you are upto add,name of java class of fragment
        fragmentTransaction.add(R.id.fragment_container, fragment_tools);
        fragmentTransaction.commit();
        currentFragment = fragment_tools;
    }

    public void addShare() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (currentFragment != null) {
            //will terminate the last fragment
            fragmentTransaction.remove(currentFragment);
        }
        //object of fragment (import_class)
        Fragment_share fragment_share = new Fragment_share();
        // id of framelayout on which you are upto add,name of java class of fragment
        fragmentTransaction.add(R.id.fragment_container, fragment_share);
        fragmentTransaction.commit();
        currentFragment = fragment_share;
    }

    public void addSend() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (currentFragment != null) {
            //will terminate the last fragment
            fragmentTransaction.remove(currentFragment);
        }
        //object of fragment (import_class)
        Fragment_send fragment_send = new Fragment_send();
        // id of framelayout on which you are upto add,name of java class of fragment
        fragmentTransaction.add(R.id.fragment_container, fragment_send);
        fragmentTransaction.commit();
        currentFragment = fragment_send;
    }

    //croping that image here and storing it back as well
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //gallery_pick variable used here
        if (requestCode == Gallery_pick && resultCode == RESULT_OK && data != null) {
            Uri image_uri = data.getData();
            CropImage.activity(image_uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //some work on loading bar now
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("updating your profie image");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);
                //-------------//
                Uri result_Uri = result.getUri();
                //name of image on firebase storage (Current_user_id)
                StorageReference filePath = storageReference.child(Current_user_id + ".jpg");
                filePath.putFile(result_Uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login_page.this, "image stored successfully(storage)", Toast.LENGTH_SHORT).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            //url of profile image (from firebase storage), stored in database in specific user database (under his user id)
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
        else if (currentFragment.isAdded()){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(currentFragment);
            fragmentTransaction.commit();
        }
        else if(!backbutton_clicked) {
            Toast.makeText(Login_page.this, "press again to close", Toast.LENGTH_SHORT).show();
            backbutton_clicked = true;
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

    //to close the app
/*    @Override
    protected void onDestroy() {
        Process.killProcess(Process.myPid());
        super.onDestroy();
    }*/
    //for @menu/login_page (menu bar appears on top rightside on toolbar)(menu bar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_page, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //--------//

    //actionbar/toolbar code
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //actionbar button code
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        //menu bar items
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //contain menus/items on drawer (initiated above)
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            //added fragment
            addImport();
        } else if (id == R.id.nav_gallery) {
            //fragment
            addGallery();
        } else if (id == R.id.nav_slideshow) {
            //fragment
            addSlideshow();
        } else if (id == R.id.nav_manage) {
            //fragment
            addTools();
        } else if (id == R.id.nav_share) {
            //fragment
            addShare();
        } else if (id == R.id.nav_send) {
            //fragment
            addSend();
        } else if (id == R.id.signout) {

            Intent intent = new Intent(Login_page.this, Login2.class);
            startActivity(intent);
            //authentication goes empty
            mAuth = null;
            Current_user_id = null;
            //database goes empty
            databaseReference = null;
            //storage goes empty
            storageReference = null;
            //setting user id from phone storage into null (for logout)
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("id", "");
            editor.apply();

            //folder selected = google_signin_id
            sharedPreference = getSharedPreferences("google_Signin_id", Context.MODE_PRIVATE);
            //saving user google id in device to null(for logout)
            SharedPreferences.Editor google_editor = sharedPreference.edit();
            google_editor.putString("google_id", "");
            google_editor.apply();

            //terminate activity
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    //async task (to run accessing data from database in background)
    @SuppressLint("StaticFieldLeak")
    class User_data extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(final String... strings) {

            databaseReference.addValueEventListener(new ValueEventListener() {
                //retreaving name and email
                //retreaving image (add library as well)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //if user logged in by google (id of firebase sign in will be null)
                    if (strings[0] != null) {
                        if (dataSnapshot.exists()) {
                            email = dataSnapshot.child(strings[0]).child("iemail").getValue(String.class).toString();
                            name = dataSnapshot.child(strings[0]).child("iname").getValue(String.class).toString();
                            //TODO:Write fields names below
                            //parsing user data to variables
                            nameTextView.setText(name);
                            emailTextView.setText(email);

                            //if image is available on storage
                            if (dataSnapshot.child(strings[0]).child("profileimage").getValue() != null) {
                                String image = dataSnapshot.child(strings[0]).child("profileimage").getValue().toString();
                                Picasso.get().load(image).into(displayImageView);
                            } else {
                                //ic_menu_gallery image (shall be shown first time)
                                Picasso.get().load(R.drawable.ic_menu_gallery).into(displayImageView);
//                       Picasso.get().load(new File(...)).into(displayImageView);
                            }
                        }
                    } else {
                        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                        if (acct != null) {
                            //accessing data from account into fields
                            String personName = acct.getDisplayName();
                            nameTextView.setText(personName);
                            String personGivenName = acct.getGivenName();
                            String personFamilyName = acct.getFamilyName();
                            String personEmail = acct.getEmail();
                            emailTextView.setText(personEmail);
                            String personId = acct.getId();
                            //saving id in google sign id folder
                            sharedPreference = getSharedPreferences("google_Signin_id", Context.MODE_PRIVATE);
                            //saving user google id in device(for automatic login)
                            SharedPreferences.Editor editor = sharedPreference.edit();
                            editor.putString("google_id", personId);
                            editor.apply();

                            Uri personPhoto = acct.getPhotoUrl();
                            Picasso.get().load(personPhoto).into(displayImageView);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

    }
}
