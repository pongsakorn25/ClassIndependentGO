package com.example.skyeye.ClassIndependentGO;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    ConnectMySQL cn = new ConnectMySQL();
    ArrayList<HashMap<String, Object>> MyArrList;
    ArrayList<HashMap<String, Object>> MyArrList2 = new ArrayList<HashMap<String, java.lang.Object>>();
    Bundle bundle;
    NavigationView navigationView = null;
    Toolbar toolbar = null;
    private ProgressDialog progressDialog;
    private String Username, url, result, IDTeacher, Name, Course, NameCourse;
    private TextView txtnamet;
    private AccountHeader headerResult = null;
    private Drawer drawer = null;
    private IProfile profile;
    private ExpandableDrawerItem mCourseGroup;
    private List<IDrawerItem> mCourseItem = new ArrayList<>();
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Username = getIntent().getStringExtra("User");
        GetIDtea();
        ShowHomeFragment();
        profile = new ProfileDrawerItem().withName(Name).withEmail("สถานะ : อาจารย์ผู้สอน").withIcon(R.drawable.user_t);
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
//                .withCompactStyle(true)
                .withHeaderBackground(R.drawable.header)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        profile
                )

                .withSavedInstance(savedInstanceState)
                .build();
        //Create the drawer
        drawer = new DrawerBuilder(this)
                .withActivity(this)
                //this layout have to contain child layouts
//                .withRootView(R.id.drawer_container)
                .withToolbar(toolbar)
//                .withDisplayBelowStatusBar(false)
//                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_Opencourse).withIcon(FontAwesome.Icon.faw_pencil_square_o).withIdentifier(2),
//                        new PrimaryDrawerItem().withName(R.string.drawer_item_Confirmstudent).withIcon(FontAwesome.Icon.faw_gamepad).withIdentifier(3),


                        new SectionDrawerItem().withName("จัดการข้อมูลวิชาที่เปิดสอน"),

                        mCourseGroup = new ExpandableDrawerItem()
                                .withName("วิชาที่เปิดสอน")
                                .withIcon(FontAwesome.Icon.faw_book)
                                .withIdentifier(19).withSelectable(false)
                                .withSubItems(mCourseItem),

//                        new SecondaryDrawerItem().withName("CollapsableItem").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_8tracks).withIdentifier(2002),
//                        new SecondaryDrawerItem().withName("CollapsableItem 2").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_8tracks).withIdentifier(2003)

                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_Logout).withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(4)
                )

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Intent intent = null;
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 1) {
//                                ShowHomeFragment();
                                finish();
                                startActivity(getIntent());
                            }
                            else if (drawerItem.getIdentifier() == 2) {
                                ShowOpenCourseFragment();
                            }
//                            else if (drawerItem.getIdentifier() == 3) {
//                                ShowConfirmStuden();
//                            }
                            else if (drawerItem.getIdentifier() == 4) {
                                Logout();
                            }
                            else if (drawerItem instanceof SecondaryDrawerItem){
                                intent = new Intent(Main2Activity.this, MainManager.class);
                                Course = MyArrList2.get(position-5).get("Id_Course").toString();
                                NameCourse = MyArrList2.get(position-5).get("Name_Coures").toString();

                                intent.putExtra("Id_Course", Course);
                                intent.putExtra("Name_Coures", NameCourse);
                                intent.putExtra("Username", Username);
                                intent.putExtra("Id_Teacher", IDTeacher);

                                    Main2Activity.this.startActivity(intent);

//                                Toast.makeText(Main2Activity.this, "เอาหน่อยๆๆๆ", Toast.LENGTH_SHORT).show();
                            }

                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            drawer.setSelection(1, false);
        }

        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                url = "https://classindependent.000webhostapp.com/Android/getCourseTeacher.php";
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("User", Username));
                result = cn.connect(nameValuePairs, url);

                try{
//                result = cn.connect(url);
                    Log.d("result", result);
                    JSONArray data = new JSONArray(result); //get เป็ย array
//                MyArrList = new ArrayList<HashMap<String, Object>>();
                    HashMap<String, Object> map;

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i); //ข้อมูลที่ได้กลับมาทำ json
                        map = new HashMap<String, Object>();
                        map.put("Id_Teacher", c.getString("Id_Teacher"));
                        map.put("Id_Course", c.getString("Id_Course"));
                        map.put("Name_Coures", c.getString("Name_Coures"));
                        map.put("FirstName", c.getString("FirstName"));
                        map.put("LastName", c.getString("LastName"));
                        MyArrList2.add(map);

                        mCourseItem.add(new SecondaryDrawerItem()
                                .withName(c.getString("Name_Coures"))
                                .withIcon(GoogleMaterial.Icon.gmd_8tracks)
                                .withLevel(2));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = drawer.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            Logout();
        }
    }

    private void GetIDtea() {
        new DownloadDataTeacher().execute();
    }

    public class DownloadDataTeacher extends AsyncTask<String, Void, Void> {

        //ทำงานก่อนทำงานหลัก AsyncTask เมื่อถูกเรียกใช้
        @Override
        protected void onPreExecute() { //โหลดก่อนทำงาน

            super.onPreExecute();

            progressDialog = new ProgressDialog(Main2Activity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/getDatateacher.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("User", Username));
            result = cn.connect(nameValuePairs, url);
            try {
                Log.d("result", result);

                JSONArray data = new JSONArray(result); //get เป็ย array
                MyArrList = new ArrayList<HashMap<String, Object>>();
                HashMap<String, Object> map;

                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i); //ข้อมูลที่ได้กลับมาทำ json
                    map = new HashMap<String, Object>();
                    map.put("Id_Teacher", c.getString("Id_Teacher"));
                    map.put("FirstName", c.getString("FirstName"));
                    map.put("LastName", c.getString("LastName"));
                    MyArrList.add(map);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ShowAllContent();
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

    public void ShowAllContent() {
        IDTeacher = MyArrList.get(0).get("Id_Teacher").toString();

        Name = MyArrList.get(0).get("FirstName").toString() + " " + MyArrList.get(0).get("LastName").toString();
        profile.withName(Name);
        headerResult.updateProfile(profile);
    }

    private void ShowHomeFragment(){
        bundle = new Bundle();
        bundle.putString("User", Username);
        MainTFragment fragment = new MainTFragment();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//           fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.content_main2, fragment);
        fragmentTransaction.commit();
    }

    private void ShowOpenCourseFragment(){
        bundle = new Bundle();
        bundle.putString("Id_Teacher", IDTeacher);
        OpenCourseFragment openCourseFragment = new OpenCourseFragment();
        openCourseFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.content_main2, openCourseFragment);
        fragmentTransaction.commit();
    }

//    private void ShowConfirmStuden(){
//        bundle = new Bundle();
//        bundle.putString("User", Username);
//        ConfirmStudentFragment confirmStudentFragment = new ConfirmStudentFragment();
//        confirmStudentFragment.setArguments(bundle);
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
////            fragmentTransaction.addToBackStack(null);
//        fragmentTransaction.replace(R.id.content_main2, confirmStudentFragment);
//        fragmentTransaction.commit();
//    }

    private void Logout() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("ออกจากระบบ");
        dialog.setIcon(R.drawable.ic_logout);
        dialog.setCancelable(true);
        dialog.setMessage("คุณต้องการออกจากระบบหรือไม่ ?");
        dialog.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        dialog.setNegativeButton(getString(R.string.Cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.show();

    }


}
