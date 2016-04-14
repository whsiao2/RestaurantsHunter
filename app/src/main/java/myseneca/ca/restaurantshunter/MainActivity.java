package myseneca.ca.restaurantshunter;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_FINE_LOCATION = 113;

    public static final int COMM_FROM_MAIN = 1;
    public static final int COMM_FROM_LIST = 2;
    public static final int PHOTO_REQUEST_CAMERA = 3;
    public static final int PHOTO_SELECT_FILE = 4;
    public static final String RH_PHOTO_FOLDER = "RestaurantHunter";
    public static final String RH_REWARD_FILE = "RH_rewards.txt";

    protected static DataBaseHelper mDb; //All activities operators only one Database
    protected Marker mUpdateMarker;
    protected Location mLastLocation;
    protected Marker mCurrLocationMarker;

    private GoogleMap mMap;
    private MapFragment mMapFragment;
    private List<Restaurant> mRestaurants;
    private BroadcastReceiver mMessageReceiver;
    private Button mListBtn;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    public boolean onMarkerClick(final Marker marker) {

        mUpdateMarker = marker;
        final Dialog d = new Dialog(MainActivity.this, R.style.dialog);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(R.layout.map_marker_content);
        Restaurant res = new Restaurant();
        for (Restaurant r : mRestaurants) {
            if (r.getId() == Integer.parseInt(marker.getSnippet()))
                res = r;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(res.getLatitude(), res.getLongitude()), 17));
        WindowManager.LayoutParams lp = d.getWindow().getAttributes();
        lp.y = -250; //Move dialog to the marker's top (Originally it would cover marker)
        lp.dimAmount = 0f; //When dialog pops up, no dim effect
        d.getWindow().setAttributes(lp);

        Bitmap bmp = CommentActivity.decodeSampledBitmapFromFile(CommentActivity.mPhotosDir + "/"
                + Restaurant.transToImageStringArray(res.getImages()).get(0) + ".jpg");
        ImageView resImage = (ImageView) d.findViewById(R.id.defPhoto);
        if (bmp != null)
            resImage.setImageBitmap(bmp);

        TextView tvName = (TextView) d.findViewById(R.id.ResName);
        tvName.setText(res.getName());
        TextView tvAddr = (TextView) d.findViewById(R.id.ResAddr);
        tvAddr.setText(res.getAddress());
        TextView tvPost = (TextView) d.findViewById(R.id.ResPostCode);
        tvPost.setText(res.getPostCode());
        TextView tvCountry = (TextView) d.findViewById(R.id.ResCountry);
        tvCountry.setText(res.getCountry());


        Button dialog_btn = (Button) d.findViewById(R.id.btnComment);
        dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Restaurant res = new Restaurant();
                for (Restaurant r : mRestaurants) {
                    if (r.getId() == Integer.parseInt(marker.getSnippet()))
                        res = r;
                }
                Intent commentIntent = new Intent(MainActivity.this, CommentActivity.class);
                commentIntent.putExtra("RequestFrom", COMM_FROM_MAIN);
                commentIntent.putExtra("RestaurantObj", res);
                startActivityForResult(commentIntent, 1);
                d.dismiss();
            }
        });

        //Set-up custom Ratingbar
        //Ref: http://kozyr.zydako.net/2010/05/23/pretty-ratingbar/
        RatingBar ratingBar = (RatingBar) d.findViewById(R.id.rates);
        ratingBar.setRating(res.getRate());

        d.show();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDb = new DataBaseHelper(this);
        if (!mDb.isEmpty())
            //Log.d("******* Data Num: ", String.valueOf(mDb.getRestaurantCount()));
            mRestaurants = mDb.getSpecificRestaurants("");

        //To Check the permission that users can permit to get their locations.
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        mMapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        mMapFragment.getMapAsync(this); //Call back onMapReady() to set up GoogleMap

        mListBtn = (Button) findViewById(R.id.Btn_List);
        mListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listIntent = new Intent(MainActivity.this, RestaurantListActivity.class);
                startActivityForResult(listIntent, 2);
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        LatLng center;
        int count = 0;
        for (Restaurant res : mRestaurants) {
            if (count <= 0) {
                center = new LatLng(res.getLatitude(), res.getLongitude());
                // Move the camera instantly to first recorded restaurant with a zoom of 17.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 17));
            }
            count++;

            //If Rate = 0 && no comment, no user's photos, it means this restaurant has never visited before.
            if (!res.isCommented())
                mMap.addMarker(new MarkerOptions().position(new LatLng(res.getLatitude(), res.getLongitude()))
                        .title(res.getName())
                        .snippet(String.valueOf(res.getId())));
            else {
                mMap.addMarker(new MarkerOptions().position(new LatLng(res.getLatitude(), res.getLongitude()))
                        .title(res.getName())
                        .snippet(String.valueOf(res.getId()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(res.getLatitude(), res.getLongitude()), 17));
            }

            // When click marker, show a custom dialog
            mMap.setOnMarkerClickListener(this);

        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        // Register to receive messages.
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction("myseneca.ca.restaurantshunter.updatMarker");
        mMessageReceiver = new UpdateBroadcastReceiver();
        registerReceiver(mMessageReceiver, ifilter);
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mDb.close();
        // Unregister since the activity is destroy.
        if (mMessageReceiver != null)
            unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    /** Custom a Broadcast Receiver
     * When the comment of restaurant is update,
     * the CommentActivity will send the broadcast to MainActivity **/
    public class UpdateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Restaurant res = (Restaurant) intent.getSerializableExtra("UpdateMarker_RestaurantObj");
            for (int i = 0; i < mRestaurants.size(); i++) {
                if (res.getId() == mRestaurants.get(i).getId()) {
                    mRestaurants.set(i, res);
                    break;
                }
            }
            //If the comment of restaurant is not null, set the marker visited (change marker's icon)
            if (mUpdateMarker != null
                    && !(res.getRate() <= 0 && res.getComment().equals("") && res.getImages().equals("")))
                mUpdateMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant));

            //Close the CommentActivity
            finishActivity(1);
        }
    }

    /** GoogleApiClient ConnectCallbacks **/
    @Override
    public void onConnected(Bundle bundle) {
        //Google Services Connected
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000); //Read Location information per 1 sec.(1000ms)
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    /**  LocationListener **/
    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    //Ref: http://stackoverflow.com/questions/34582370/how-can-i-use-google-maps-and-locationmanager-to-show-current-location-on-androi
    private boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    // permission denied
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
