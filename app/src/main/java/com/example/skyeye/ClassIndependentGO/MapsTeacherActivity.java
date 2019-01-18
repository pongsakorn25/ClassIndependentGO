package com.example.skyeye.ClassIndependentGO;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MapsTeacherActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    ConnectMySQL cn = new ConnectMySQL();
    private ProgressDialog progressDialog;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private static final long MIN_TIME = 5000;
    private static final float MIN_DISTANCE = 15;
    LatLng latLng;
    SupportMapFragment mapFragment;
    private static final LatLng Thai = new LatLng(15.8700, 100.9925);
    Marker marker = null;
    private boolean isNetwork, isGPS, isACCURACY;
    private Double lng, lat;
    private String latitude, longitude, IDcourse, Randomnum, IDcheck, url, result, numbercheck, numcheck, statuscheck;
    int code, num1, num2;
    private GoogleApiClient googleApiClient;
    private Button btnrandomnum, btninsertcoordinates, btndeletecoordinates, btnOpencoordinates;
    private TextView txtrandomnum;
    ArrayList<HashMap<String, Object>> MyArrList = new ArrayList<HashMap<String, java.lang.Object>>();
    android.support.v7.app.ActionBar actionBar;
    private long backPressedTime;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        insertDummyContactWrapper();

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        setContentView(R.layout.activity_maps_teacher);

        IDcourse = getIntent().getStringExtra("Id_Course");

//        Toast.makeText(MapsTeacherActivity.this, IDcourse, Toast.LENGTH_SHORT).show();

        txtrandomnum = (TextView) findViewById(R.id.txtRandomNum);


        GetCoordinates();

        btnrandomnum = (Button) findViewById(R.id.btnRandomNum);
        btnrandomnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random rand = new Random();
                int n = rand.nextInt(9999 - 999 + 1) + 999;
                Randomnum = String.valueOf(n);
                txtrandomnum.setText(Randomnum);
                btninsertcoordinates.setEnabled(true);
            }
        });

        btndeletecoordinates = (Button) findViewById(R.id.btnClose);
        btndeletecoordinates.setEnabled(false);
        btndeletecoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsTeacherActivity.this);
                alertDialog.setTitle("ปิดตำแหน่งการเช็คชื่อ");

                alertDialog.setIcon(R.drawable.ic_noconfirm);

                alertDialog.setPositiveButton(getString(R.string.OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                GetDeletecoordinates();
                                btndeletecoordinates.setEnabled(false);
                                btnOpencoordinates.setEnabled(false);
                                btninsertcoordinates.setEnabled(false);
                                btnrandomnum.setEnabled(true);
                            }
                        });

                alertDialog.setNegativeButton(getString(R.string.Cancle),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();


            }
        });

        btninsertcoordinates = (Button) findViewById(R.id.btncoordinates);
        btninsertcoordinates.setEnabled(false);
        btninsertcoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsTeacherActivity.this);
                alertDialog.setTitle("บันทึกตำแหน่งการเช็คชื่อ");

                alertDialog.setIcon(R.drawable.ic_confirm);

                alertDialog.setPositiveButton(getString(R.string.OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                GetInsertcoordinates();
                                btnOpencoordinates.setEnabled(true);
                            }
                        });

                alertDialog.setNegativeButton(getString(R.string.Cancle),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();


            }
        });

        btnOpencoordinates = (Button) findViewById(R.id.btnOpen);
        btnOpencoordinates.setEnabled(false);
        btnOpencoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsTeacherActivity.this);
                alertDialog.setTitle("เปิดตำแหน่งการเช็คชื่อ");

                alertDialog.setIcon(R.drawable.ic_confirm);

                alertDialog.setPositiveButton(getString(R.string.OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                GetOpencoordinates();
                                btnOpencoordinates.setEnabled(false);
                                btndeletecoordinates.setEnabled(true);
                                btninsertcoordinates.setEnabled(false);
                                btnrandomnum.setEnabled(false);
                            }
                        });

                alertDialog.setNegativeButton(getString(R.string.Cancle),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();


            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);


            return;
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();


    }

    @Override
    public void onStart() {
        super.onStart();
//        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (isNetwork) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
                        , MIN_TIME, MIN_DISTANCE, this);
                Location loc = locationManager.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER);
                if (loc != null) {
                    lat = loc.getLatitude();
                    lat = loc.getLongitude();
//                    Toast.makeText(MapsTeacherActivity.this, "NERWORK", Toast.LENGTH_SHORT).show();
                }
            }

            if (isGPS) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER
                        , MIN_TIME, MIN_DISTANCE, this);
                Location loc = locationManager.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER);
                if (loc != null) {
                    lat = loc.getLatitude();
                    lat = loc.getLongitude();
//                    Toast.makeText(MapsTeacherActivity.this, "GPS", Toast.LENGTH_SHORT).show();
                }
            }

            if (marker != null)
                marker.remove();

            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            marker = mMap.addMarker(new MarkerOptions().position(latLng));

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 19);
            mMap.animateCamera(cameraUpdate);
            mMap.animateCamera(CameraUpdateFactory.zoomIn());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to Mountain View
                    .zoom(19)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//            isACCURACY = locationManager.isProviderEnabled(LocationManager.PRIORITY_HIGH_ACCURACY);

            lat = location.getLatitude();
            lng = location.getLongitude();
            latitude = lat.toString();
            longitude = lng.toString();

//            Toast.makeText(MapsTeacherActivity.this, latitude + " , " + longitude, Toast.LENGTH_SHORT).show();

            UiSettings uis = mMap.getUiSettings();
            uis.setZoomControlsEnabled(true);

            return;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Thai, 5));
            }

            return;
        }


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
            if (locationAvailability.isLocationAvailable()) {

                LocationRequest locationRequest = new LocationRequest()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(5000)
                        .setFastestInterval(1000);

                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);

            } else {
//                Toast.makeText(MapsTeacherActivity.this, "NOCONNECT", Toast.LENGTH_SHORT).show();
            }
            return;
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void GetInsertcoordinates() {
        new Insertcoordinates().execute();
    }

    public class Insertcoordinates extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MapsTeacherActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/Insertcoordinates.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Course", IDcourse));
            nameValuePairs.add(new BasicNameValuePair("lat", latitude));
            nameValuePairs.add(new BasicNameValuePair("lng", longitude));
            nameValuePairs.add(new BasicNameValuePair("Random_num", Randomnum));

            result = cn.connect(nameValuePairs, url);
            Log.d("result", result);
            JSONObject json_data;
            try {
                json_data = new JSONObject(result);
                code = (json_data.getInt("code"));
            } catch (JSONException e) {
                Log.e("Fail 1", e.toString());
                e.printStackTrace();
            }

            return null;


        }

        protected void onPostExecute(Void unused) {
            progressDialog.dismiss();
            ShowAllContent(); // When Finish Show Content
        }

    }

    public void ShowAllContent() {
        if (code == 1) {
//            Toast.makeText(MapsTeacherActivity.this,getString(R.string.savecoordinates), Toast.LENGTH_SHORT).show();
            GetUpdatecoordinates();
        } else {
            GetUpdatecoordinates();
        }
    }

    private void GetUpdatecoordinates() {
        new Updatecoordinates().execute();
    }

    public class Updatecoordinates extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MapsTeacherActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/updateCoordinates.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Course", IDcourse));
            nameValuePairs.add(new BasicNameValuePair("lat", latitude));
            nameValuePairs.add(new BasicNameValuePair("lng", longitude));
            nameValuePairs.add(new BasicNameValuePair("Random_num", Randomnum));

            result = cn.connect(nameValuePairs, url);
            Log.d("result11", result);
            JSONObject json_data;
            try {
                json_data = new JSONObject(result);
                code = (json_data.getInt("code"));
            } catch (JSONException e) {
                Log.e("Fail 1", e.toString());
                e.printStackTrace();
            }

            return null;


        }

        protected void onPostExecute(Void unused) {
            progressDialog.dismiss();
            ShowAllContent2(); // When Finish Show Content
        }

    }

    public void ShowAllContent2() {
        if (code == 1) {
            Toast.makeText(MapsTeacherActivity.this, getString(R.string.savecoordinates), Toast.LENGTH_SHORT).show();
        } else {
        }
    }

    private void GetDeletecoordinates() {
        new Deletecoordinates().execute();
    }

    public class Deletecoordinates extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MapsTeacherActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/CloseCoordinates.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Course", IDcourse));

            result = cn.connect(nameValuePairs, url);
            Log.d("result", result);
            JSONObject json_data;
            try {
                json_data = new JSONObject(result);
                code = (json_data.getInt("code"));
            } catch (JSONException e) {
                Log.e("Fail 1", e.toString());
                e.printStackTrace();
            }

            return null;


        }

        protected void onPostExecute(Void unused) {
            progressDialog.dismiss();
            ShowAllContent4(); // When Finish Show Content
        }

    }

    public void ShowAllContent4() {
        if (code == 1) {
            Toast.makeText(MapsTeacherActivity.this, "ปิดตำแหน่งเรียบร้อย", Toast.LENGTH_SHORT).show();
        } else {
        }
    }

    private void GetOpencoordinates() {
        new Opencoordinates().execute();
    }

    public class Opencoordinates extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MapsTeacherActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/OpenCoordinates.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Course", IDcourse));
            nameValuePairs.add(new BasicNameValuePair("Number_of_times", numcheck));
            result = cn.connect(nameValuePairs, url);
            Log.d("result", result);
            JSONObject json_data;
            try {
                json_data = new JSONObject(result);
                code = (json_data.getInt("code"));
            } catch (JSONException e) {
                Log.e("Fail 1", e.toString());
                e.printStackTrace();
            }

            return null;


        }

        protected void onPostExecute(Void unused) {
            progressDialog.dismiss();
            ShowAllContent5(); // When Finish Show Content
        }

    }

    public void ShowAllContent5() {
        if (code == 1) {
            Toast.makeText(MapsTeacherActivity.this, "เปิดตำแหน่งเรียบร้อย", Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, numcheck, Toast.LENGTH_LONG).show();
        } else {
        }
    }

    private void GetCoordinates() {
        new MapsTeacherActivity.DownloadCoordinates().execute();
    }

    public class DownloadCoordinates extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MapsTeacherActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {

            url = "https://classindependent.000webhostapp.com/Android/getCoordinatesTeacher.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Course", IDcourse));
            result = cn.connect(nameValuePairs, url);

            try {
//                result = cn.connect(url);
                Log.d("result", result);
                JSONArray data = new JSONArray(result); //get เป็ย array
//                MyArrList = new ArrayList<HashMap<String, Object>>();
                HashMap<String, Object> map;

                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i); //ข้อมูลที่ได้กลับมาทำ json
                    map = new HashMap<String, Object>();
                    map.put("Random_num", c.getString("Random_num"));
                    map.put("Id_check", c.getString("Id_check"));
                    map.put("Number_of_times", c.getInt("Number_of_times"));
                    map.put("Status_check", c.getString("Status_check"));

                    MyArrList.add(map);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void unused) {
            progressDialog.dismiss();
            ShowAllContent3(); // When Finish Show Content
        }

    }

    public void ShowAllContent3() {
        for (int i = 0; i < MyArrList.size(); i++) {
            Randomnum = MyArrList.get(i).get("Random_num").toString();
            IDcheck = MyArrList.get(i).get("Id_check").toString();
            numbercheck = MyArrList.get(i).get("Number_of_times").toString();
            statuscheck = MyArrList.get(i).get("Status_check").toString();


            num1 = Integer.parseInt(numbercheck);
            num2 = num1 + 1;

            numcheck = String.valueOf(num2);

            txtrandomnum.setText(Randomnum);

            if(statuscheck.equals("Open")){
                btnOpencoordinates.setEnabled(false);
                btndeletecoordinates.setEnabled(true);
                btninsertcoordinates.setEnabled(false);
                btnrandomnum.setEnabled(false);
            }
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onBackPressed() {
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t;
            Toast.makeText(this, "กด กลับ อีกครั้งเพื่อออก",
                    Toast.LENGTH_SHORT).show();
        } else {    // this guy is serious
            // clean up
            super.onBackPressed();       // bye
        }
    }

    final private int PERMISSION_REQUEST_CODE = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void insertDummyContactWrapper() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
            return;
        }

        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(MapsTeacherActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            // Show an expanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
//
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    finish();
                    startActivity(getIntent());

                    // permission was granted, yay!

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



}
