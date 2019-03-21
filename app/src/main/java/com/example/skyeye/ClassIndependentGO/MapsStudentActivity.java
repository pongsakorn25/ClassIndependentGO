package com.example.skyeye.ClassIndependentGO;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MapsStudentActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnInfoWindowClickListener {

    ConnectMySQL cn = new ConnectMySQL();
    private GoogleMap mMap;
    private ProgressDialog progressDialog;
    private String latitude1;
    private String longitude1;
    private String IDcourse;
    private String distance;
    private String NumberCheck, Inputnum, IDStudent;
    private String url;
    private String result, date;
    private int code;
    private boolean isNetwork, isGPS, isACCURACY;
    private static final LatLng Thai = new LatLng(15.8700, 100.9925);
    private static final long MIN_TIME = 5000;
    private static final float MIN_DISTANCE = 10;
    SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private GoogleApiClient googleApiClient;
    private Double lng1, lat1, lng2, lat2, haverdistanceKM, meter;
    LatLng latLng;
    Marker marker = null;
    ArrayList<HashMap<String, Object>> MyArrList = new ArrayList<HashMap<String, java.lang.Object>>();
    DateFormat df;
    android.support.v7.app.ActionBar actionBar;
    private long backPressedTime;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        insertDummyContactWrapper();
//        if (ContextCompat.checkSelfPermission(MapsStudentActivity.this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsStudentActivity.this,
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//                // Show an expanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            }
//
//
//            else {
//
//                // No explanation needed, we can request the permission.
//
//                ActivityCompat.requestPermissions(MapsStudentActivity.this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        PERMISSION_REQUEST_CODE);
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        }

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        setContentView(R.layout.activity_maps_student);

        lat1 = new Double(0);
        lng1 = new Double(0);
        lat2 = new Double(0);
        lng2 = new Double(0);

        IDcourse = getIntent().getStringExtra("Id_Course");
        IDStudent = getIntent().getStringExtra("Id_Student");

        GetCoordinates();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        date = df.format(Calendar.getInstance().getTime());
//        Toast.makeText(MapsStudentActivity.this, date, Toast.LENGTH_SHORT).show();

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
//            isACCURACY = locationManager.isProviderEnabled(LocationManager.PRIORITY_HIGH_ACCURACY);


            if (isNetwork) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER
                        , MIN_TIME, MIN_DISTANCE, this);
                Location loc = locationManager.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER);
                if (loc != null) {
                    lat1 = loc.getLatitude();
                    lng1 = loc.getLongitude();
                }
            } else if (isGPS) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER
                        , MIN_TIME, MIN_DISTANCE, this);
                Location loc = locationManager.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER);
                if (loc != null) {
                    lat1 = loc.getLatitude();
                    lng1 = loc.getLongitude();
                }
            }


            latLng = new LatLng(location.getLatitude(), location.getLongitude());
//            if (marker != null)
//                marker.remove();
//        marker = mMap.addMarker(new MarkerOptions().position(latLng));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 19);
            mMap.animateCamera(cameraUpdate);
            mMap.animateCamera(CameraUpdateFactory.zoomIn());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to Mountain View
                    .zoom(19)                   // Sets the zoom
                    .bearing(0)                 // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            lat1 = location.getLatitude();
            lng1 = location.getLongitude();
            latitude1 = lat1.toString();
            longitude1 = lng1.toString();

            haversine(lat1, lng1, lat2, lng2);

//        Toast.makeText(MapsStudentActivity.this, latitude1 + " , " + longitude1, Toast.LENGTH_SHORT).show();

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

//    public void onPause() {
//        super.onPause();
//        locationManager.removeUpdates(this);
//    }


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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Thai, 5));
                mMap.setOnInfoWindowClickListener(this);

            }
            return;
        }


    }

    private void GetCoordinates() {
        new MapsStudentActivity.DownloadCoordinates().execute();
    }


    public class DownloadCoordinates extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MapsStudentActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/getCoordinates.php";
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
                    map.put("lat", c.getString("lat"));
                    map.put("lng", c.getString("lng"));
                    map.put("Name_Coures", c.getString("Name_Coures"));
                    map.put("FirstName", c.getString("FirstName"));
                    map.put("LastName", c.getString("LastName"));
                    map.put("Random_num", c.getString("Random_num"));

                    MyArrList.add(map);
                }


            } catch (JSONException e) {
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
        for (int i = 0; i < MyArrList.size(); i++) {
            NumberCheck = MyArrList.get(i).get("Random_num").toString().trim();
            lat2 = Double.parseDouble(MyArrList.get(i).get("lat").toString());
            lng2 = Double.parseDouble(MyArrList.get(i).get("lng").toString());
            LatLng location = new LatLng(lat2, lng2);
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title("วิชา : " + MyArrList.get(i).get("Name_Coures").toString())
                    .snippet("ผู้สอน : " + MyArrList.get(i).get("FirstName").toString() + MyArrList.get(i).get("LastName").toString())
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.checkname)));
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (meter <= 15) {
//            Toast.makeText(MapsStudentActivity.this, "อยู่ในระยะการเช็คชื่อ", Toast.LENGTH_SHORT).show();
            CheckRandomNum();
        } else {
            Toast.makeText(MapsStudentActivity.this, "คุณอยู่นอกระยะการเช็คชื่อ", Toast.LENGTH_SHORT).show();
        }
    }


    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        double Rad = 6372.8; //Earth's Radius In kilometers
        // TODO Auto-generated method stub

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        haverdistanceKM = Rad * c;
        meter = haverdistanceKM * 1000;
        distance = meter.toString();
//        Toast.makeText(MapsStudentActivity.this, distance, Toast.LENGTH_SHORT).show();
        return meter;
    }

    private void CheckRandomNum() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsStudentActivity.this);
        alertDialog.setTitle("กรอกรหัสผ่านในการเช็คชื่อ");
        alertDialog.setMessage("รหัส 4 หลัก");

        final EditText input = new EditText(MapsStudentActivity.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_password);


        alertDialog.setPositiveButton(getString(R.string.OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Inputnum = input.getText().toString().trim();
                        if (Inputnum.equals(NumberCheck)) {
                            GetInsertCheckName();
                        } else {
                            Toast.makeText(MapsStudentActivity.this, "รหัสผ่านผิด กรุณากรอกใหม่", Toast.LENGTH_SHORT).show();
                        }
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

    private void GetInsertCheckName() {
        new MapsStudentActivity.InsertCheckName().execute();
    }

    public class InsertCheckName extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MapsStudentActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/InsertScoreCheck.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Student", IDStudent));
            nameValuePairs.add(new BasicNameValuePair("Id_Course", IDcourse));
            nameValuePairs.add(new BasicNameValuePair("DateCheck", date));

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
            Toast.makeText(MapsStudentActivity.this, "เช็คชื่อสำเร็จ", Toast.LENGTH_SHORT).show();
            finish();
        } if(code == 2){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("เช็คชื่อไม่สำเร็จ");
            dialog.setIcon(R.drawable.ic_noconfirm);
            dialog.setCancelable(true);
            dialog.setMessage("เนืองจากคุณได้ทำการเช็คชื่อไปแล้ว");
            dialog.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog.show();
        } else {
            Toast.makeText(MapsStudentActivity.this, "เกิดข้อผิดพลาด", Toast.LENGTH_SHORT).show();

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
        if (ActivityCompat.shouldShowRequestPermissionRationale(MapsStudentActivity.this,
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
