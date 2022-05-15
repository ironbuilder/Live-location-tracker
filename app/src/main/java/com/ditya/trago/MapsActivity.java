package com.ditya.trago;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ditya.trago.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Arrays;


public class MapsActivity extends FragmentActivity {

    private GoogleMap runmap;
    FusedLocationProviderClient client;
    Handler mhandler = new Handler();
    Polyline pl;
    Button bt;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    SharedPreferences sa;
    String temp;
    Button bt2;
    GoogleMap mMap;
    TextView tv;
    double dist;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    SharedPreferences.Editor edtol;
    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        bt = findViewById(R.id.button);
        bt2 = findViewById(R.id.button2);
        tv = findViewById(R.id.textView);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences u = getSharedPreferences("track", MODE_PRIVATE);
                SharedPreferences.Editor er = u.edit();
                er.putString("latlng", "");
                er.commit();
                pl.remove();
                mMap.clear();
                SharedPreferences dis = getSharedPreferences("dist", MODE_PRIVATE);
                SharedPreferences.Editor editor3 = dis.edit();
                String delta = dis.getString("dis", "");
                try {
                    Integer l4 = Integer.parseInt(delta);
                    Integer add = (int) dist + l4;
                    editor3.putString("dis", String.valueOf(add));
                    editor3.commit();
                }
                catch (Exception e){
                    editor3.putString("dis", String.valueOf(dist));
                    editor3.commit();
                }
                startActivity(new Intent(getApplicationContext(), review.class));
            }
        });
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                SharedPreferences re  = getSharedPreferences("checker", MODE_PRIVATE);
                SharedPreferences.Editor lok = re.edit();
                lok.putString("temp", "a");
                lok.putString("checker", "");
                lok.commit();
            }
        });

        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                // No location access granted.
                            }
                        }
                );
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
        client = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this::MapReady);
    }

    public void MapReady(GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //ask for permission
            return;
        }
        SharedPreferences sa = getSharedPreferences("checker", MODE_PRIVATE);
        temp = sa.getString("temp", "");
        if (temp == null || temp.equals("") || temp == ""){
            temp = "a";
        }

        System.out.println(temp);
        SharedPreferences.Editor edel = sa.edit();
        runmap = googleMap;


                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }



                if (isGPSEnabled()){
                    client.requestLocationUpdates(locationRequest, new LocationCallback() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(MapsActivity.this)
                                    .removeLocationUpdates(this);

                            if (locationResult != null && locationResult.getLocations().size() > 0) {
                                SharedPreferences sh = getSharedPreferences("check", MODE_PRIVATE);
                                String check_data = sh.getString("checker", "");
                                SharedPreferences.Editor editor = sh.edit();
                                int index = locationResult.getLocations().size() - 1;
                                System.out.println(check_data);
                                String checkh = String.valueOf(locationResult.getLocations().get(index).getLongitude()) + " " + String.valueOf(locationResult.getLocations().get(index).getLatitude());
                                Boolean t = checkh.equals(check_data);

                                System.out.println(temp);



                                if (temp.equals("a") || temp == "a"){
                                    mMap = googleMap;
                                    LatLng self = new LatLng(locationResult.getLocations().get(index).getLatitude(), locationResult.getLocations().get(index).getLongitude());
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(self, 20.0f));
                                    editor.putString("checker", checkh);
                                    editor.commit();
                                    System.out.println(temp);
                                    edel.putString("temp", "aa");
                                    edel.commit();
                                    System.out.println("1  check test check test");
                                    SharedPreferences q = getSharedPreferences("track", MODE_PRIVATE);
                                    SharedPreferences.Editor eel = q.edit();
                                    String tempo = String.valueOf(self.latitude) + "," + String.valueOf(self.longitude);
                                    eel.putString("latlng", tempo);
                                    System.out.println("0");
                                    System.out.println(tempo);
                                    System.out.println("0");
                                    eel.commit();
                                    mhandler.postDelayed(runnableCode, 100);
                                }
                                if (!t && temp != "a") {
                                    LatLng self = new LatLng(locationResult.getLocations().get(index).getLatitude(), locationResult.getLocations().get(index).getLongitude());
                                    editor = sa.edit();
                                    editor.putString("checker", self.toString());
                                    editor.commit();
                                    System.out.println("2 check test check test");
                                    System.out.println(temp);
                                    System.out.println(t);
                                    SharedPreferences q = getSharedPreferences("track", MODE_PRIVATE);
                                    String lat = q.getString("latlng", "");
                                    if (lat == null || lat == ""){
                                        SharedPreferences.Editor editor5 = q.edit();
                                        String tempo = String.valueOf(self.latitude) + "," + String.valueOf(self.longitude);
                                        editor5.putString("latlng", tempo);
                                        editor5.commit();
                                    }
                                    else{
                                        String[] le = lat.split(",");
                                        System.out.println(le);
                                        double tre = Double.parseDouble(le[0]);
                                        double tre1 = Double.parseDouble(le[1]);
                                        LatLng sample = new LatLng(tre, tre1);
                                        pl = googleMap.addPolyline(new PolylineOptions().add(self, sample));
                                        final int R = 6371; // Radius of the earth

                                        double latDistance = Math.toRadians(sample.latitude - self.latitude);
                                        double lonDistance = Math.toRadians(sample.longitude - self.longitude);
                                        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                                                + Math.cos(Math.toRadians(sample.latitude)) * Math.cos(Math.toRadians(self.latitude))
                                                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
                                        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                                        double distance = R * c * 1000; // convert to meters

                                        distance = Math.pow(distance, 2);
                                        dist = dist + Math.sqrt(distance);
                                        tv.setText(" distance travelled :  " +  String.valueOf((int) (Math.sqrt(dist) * 1000)) + " metres");

                                    }


                                    SharedPreferences.Editor editor5 = q.edit();
                                    String tempo = String.valueOf(self.latitude) + "," + String.valueOf(self.longitude);
                                    editor5.putString("latlng", tempo);
                                    editor5.commit();

                                    mhandler.postDelayed(runnableCode, 100);
                                }
                                else if (t && checkh != null || checkh == "" || temp != "a") {
                                    mhandler.postDelayed(runnableCode, 100);
                                    editor.putString("checker", checkh);
                                    editor.commit();
                                    System.out.println("0 hello there this is it");
                                    System.out.println(temp);
                                    System.out.println(check_data);
                                    System.out.println(checkh);
                                }
                            }
                        }



                    }, Looper.getMainLooper());
                }
                else{
                    TurnOnGps(googleMap);
                }



    }



    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            MapReady(runmap);
        }
    };


    public void TurnOnGps(GoogleMap googleMap) {


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MapsActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MapsActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });
        MapReady(googleMap);

    }


    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
     public void onDestroy() {
         super.onDestroy();
         SharedPreferences se = getSharedPreferences("checker", MODE_PRIVATE);
         SharedPreferences.Editor er = se.edit();
         er.putString("temp", "");
         er.commit();
     }
}