package com.selim_jnu.kothayacho;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.ConnectionCallbacks;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleApiClient.ConnectionCallbacks {
    @SuppressLint("DefaultLocale") String k;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String oldAddress;
    Button map, sub, change, share;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;
    int gap = 5;
    LocationRequest mLocationRequest;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;

    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
    TextView location;
    TextView keyView;
    LottieAnimationView success;
    String number;
    Boolean newKey = false;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        number = SharedPrefs.getString(getApplicationContext(), "number");
        k = SharedPrefs.getString(getApplicationContext(), "key");

        Log.d("dve", "number: "+ number);
        Log.d("dve", "key: "+ k);

        if(number.length()!=11){
            finish();
        }

        if( k.length() !=4 ){
            k = String.format("%04d", new Random().nextInt(10000));
            SharedPrefs.getString(getApplicationContext(), "key", k);
        }

        success = findViewById(R.id.animation_view);
        map = findViewById(R.id.button2);
        sub = findViewById(R.id.button3);
        change = findViewById(R.id.button4);
        share = findViewById(R.id.button5);
        myRef = FirebaseDatabase.getInstance().getReference("Users").child(number);

        location = findViewById(R.id.Location);
        keyView = findViewById(R.id.keyView);
        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{mPermission}, REQUEST_CODE_PERMISSION);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        buildGoogleApiClient();

        map.setOnClickListener(view -> Map());
        keyView.setText(String.valueOf(k));
        keyView.setOnClickListener(view -> setClipboard(getApplicationContext(), k));

        change.setOnClickListener(v -> {
            newKey = true;
            k = String.format("%04d", new Random().nextInt(10000));
            SharedPrefs.getString(getApplicationContext(), "key", k);
            fetchLocation();
        });

        sub.setOnClickListener(view -> {
          fetchLocation();
        });

        share.setOnClickListener(view ->{
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "https://kothayacho-7e984.web.app/?key="+k);
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });

    }

    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getBaseContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showSettingsAlert();
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    public void Map() {
        Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
        startActivity(intent);
    }
    public void how() {
        Intent intent = new Intent(getApplicationContext(), HowToUseActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onLocationChanged(@NonNull Location loc) {
        updateLocation(loc);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        mLocationRequest.setWaitForAccurateLocation(true);
        mLocationRequest.setSmallestDisplacement(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showSettingsAlert();
        } else {
            if (mGoogleApiClient.isConnected())
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(result -> {
            final Status status = result.getStatus();
            //final LocationSettingsStates state = result.getLocationSettingsStates();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    //...
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(
                                MainActivity.this,
                                REQUEST_LOCATION);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have no way to fix the
                    // settings so we won't show the dialog.
                    //...
                    break;
            }
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult()", Integer.toString(resultCode));
        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        if (requestCode == REQUEST_LOCATION) {
            switch (resultCode) {
                case Activity.RESULT_OK: {
                    Toast.makeText(MainActivity.this, "Location Enabled. Thank you", Toast.LENGTH_LONG).show();
                    if (mGoogleApiClient.isConnected())
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            showSettingsAlert();
                        }else {
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                        }
                    break;
                }
                case Activity.RESULT_CANCELED: {
                    location.setText("Location not enabled, user cancelled");
                    location.setTextColor(Color.RED);
                    success.setVisibility(View.GONE);
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    void updateLocation(Location loc){
        location.setVisibility(View.VISIBLE);
        double latitude = loc.getLatitude();
        double longitude = loc.getLongitude();
        database = FirebaseDatabase.getInstance();
        String newAddress = getCompleteAddressString(latitude, longitude);

        myRef.child("key").setValue(k);
        myRef.child("lat").setValue(latitude);
        myRef.child("lon").setValue(longitude);
        myRef.child("lastSeen").setValue(System.currentTimeMillis());




        oldAddress = SharedPrefs.getString(getApplicationContext(), "address");
        float oldLat = SharedPrefs.getFloat(getApplicationContext(), "lat", 0);
        float oldLon = SharedPrefs.getFloat(getApplicationContext(), "lon", 0);

        Log.d("locav", "updateLocation: old address "+ oldAddress);
        Log.d("locav", "updateLocation: new address "+ newAddress);


        float[] results = new float[1];
        Location.distanceBetween(latitude, longitude, oldLat, oldLon, results);
        float currentDistanceFromPreviousStation = results[0];

        Log.d("locav", "updateLocation: distance "+ currentDistanceFromPreviousStation);

        if ( (oldAddress.length() == 0 || oldLat == 0) || (currentDistanceFromPreviousStation>10 && !Objects.equals(newAddress, oldAddress)) || newKey) {
            String id = getSaltString();
            com.selim_jnu.kothayacho.Address ad = new com.selim_jnu.kothayacho.Address(id, newAddress, latitude, longitude, System.currentTimeMillis(), k);
            FirebaseDatabase.getInstance().getReference().child("Users").child(number).child("Places").child(id).setValue(ad);
            SharedPrefs.save(getApplicationContext(), "address", ad.getAddress());
            SharedPrefs.save(getApplicationContext(), "lat", (float) latitude);
            SharedPrefs.save(getApplicationContext(), "lon", (float) longitude);
            newKey = false;
        }

        keyView.setText(String.valueOf(k));

        location.setText("Your current location : "+newAddress);
        success.setAnimation(R.raw.success);
        success.loop(false);
        success.playAnimation();

       setClipboard(getApplicationContext(), k);

//        } else {
//            success.setAnimation(R.raw.mid);
//            success.loop(true);
//            success.playAnimation();
//            location.setText("You are around "+ (int) currentDistanceFromPreviousStation + " meter apart from \n" + oldAddress);
//        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom);

        alertDialog.setTitle("GPS Error");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Fix",
                (dialog, which) -> {
                    Intent intent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                });
        alertDialog.setNegativeButton("Cancel",
                (dialog, which) -> {
                    dialog.cancel();
                });

        // Showing Alert Message
        alertDialog.show();
    }
    @SuppressLint("ObsoleteSdkInt")
    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show();
    }
    @SuppressLint("LongLogTag")
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current location address", strReturnedAddress.toString());
            } else {
                Log.w("My Current location address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current location address", "Canont get Address!");
        }
        return strAdd;
    }

    protected String getSaltString() {
        String SALTCHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }

    @Override
    public void onStart(){
        super.onStart();
        try {
            if(this.mGoogleApiClient != null){
                this.mGoogleApiClient.connect();
            }
        }catch (Exception ignored){

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            //stop location updates when Activity is no longer active
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }catch (Exception ignored){

        }
    }

}