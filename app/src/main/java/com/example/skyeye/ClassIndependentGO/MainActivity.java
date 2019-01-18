package com.example.skyeye.ClassIndependentGO;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {
    ConnectMySQL cn = new ConnectMySQL();
    private String Username, url, result, IDStu, Name, Course, NameCourse, New;
    private ProgressDialog progressDialog;
    private TextView txtnames;
    ArrayList<HashMap<String, Object>> MyArrList = new ArrayList<HashMap<String, java.lang.Object>>();
    ArrayList<HashMap<String, Object>> MyArrList2 = new ArrayList<HashMap<String, java.lang.Object>>();
    List<IDrawerItem> subItems = new ArrayList<>();
    Bundle bundle;
    private List<IDrawerItem> mCourseItem = new ArrayList<>();
    private AccountHeader headerResult = null;
    //save our header or result
    private Drawer drawer = null;
    private IProfile profile;
    private ExpandableDrawerItem mCourseGroup;
    private long backPressedTime;
    MainSubject mainSubject = new MainSubject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//         Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(R.string.drawer_item_custom_container_drawer);
        mainSubject.GetUpdatenewMessage();
//        String ss = String.valueOf(lessonFragment.Checkdata(1)) ;
//        Toast.makeText(this, ss, Toast.LENGTH_SHORT).show();

        Username = getIntent().getStringExtra("User_S");

        if(Username == null){
            finish();
        }

        GetIDstu();
        GetCourse();
        ShowHomeFragment();

        profile = new ProfileDrawerItem().withName(Name).withEmail("สถานะ : นักศึกษา").withIcon(R.drawable.user_s);
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
//                .withCompactStyle(true)
                .withHeaderBackground(R.drawable.header2)
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
                        new PrimaryDrawerItem().withName(R.string.drawer_item_Regiscourse).withIcon(FontAwesome.Icon.faw_pencil_square_o).withIdentifier(2),


                        new SectionDrawerItem().withName("เข้าสู่วิชาเรียน"),

                        mCourseGroup = new ExpandableDrawerItem()
                                .withName("วิชาเรียน")
                                .withIcon(FontAwesome.Icon.faw_book)
                                .withIdentifier(19).withSelectable(false)
                                .withSubItems(mCourseItem),

//                        new SecondaryDrawerItem().withName("CollapsableItem").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_8tracks).withIdentifier(2002),
//                        new SecondaryDrawerItem().withName("CollapsableItem 2").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_8tracks).withIdentifier(2003)

                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_Logout).withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(3)

                )

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Intent intent = null;
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 1) {
//                                ShowHomeFragment();
//                                GetIDstu();
                                 finish();
                                 startActivity(getIntent());
                            } else if (drawerItem.getIdentifier() == 2) {
                                ShowRegistercouresFragment();

                            } else if (drawerItem.getIdentifier() == 3) {
                                Logout();
                            } else if (drawerItem instanceof SecondaryDrawerItem) {
                                intent = new Intent(MainActivity.this, MainSubject.class);
                                Course = MyArrList2.get(position-5).get("Id_Course").toString();
                                NameCourse = MyArrList2.get(position-5).get("Name_Coures").toString();
                                intent.putExtra("Id_Course", Course);
                                intent.putExtra("Name_Coures", NameCourse);
                                intent.putExtra("Id_Student", IDStu);
                                intent.putExtra("User_S", Username);
                                finish();
                                MainActivity.this.startActivity(intent);

//                                Toast.makeText(MainActivity.this, Course, Toast.LENGTH_SHORT).show();
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
        }

        Logout();
    }


    private void GetIDstu() {
        new DownloadDatastudent().execute();
    }

    public class DownloadDatastudent extends AsyncTask<String, Void, Void> {

        //ทำงานก่อนทำงานหลัก AsyncTask เมื่อถูกเรียกใช้
        @Override
        protected void onPreExecute() { //โหลดก่อนทำงาน

            super.onPreExecute();

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/getDatastudent.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("User_S", Username));
            result = cn.connect(nameValuePairs, url);
            try {
                Log.d("result", result);

                JSONArray data = new JSONArray(result); //get เป็ย array
//                MyArrList = new ArrayList<HashMap<String, Object>>();
                HashMap<String, Object> map;

                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i); //ข้อมูลที่ได้กลับมาทำ json
                    map = new HashMap<String, Object>();
                    map.put("Id_Student", c.getString("Id_Student"));
                    map.put("FirtName_S", c.getString("FirtName_S"));
                    map.put("LastName_S", c.getString("LastName_S"));
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
        for (int i = 0; i < MyArrList.size(); i++) {
            IDStu = MyArrList.get(i).get("Id_Student").toString();
            Name = MyArrList.get(i).get("FirtName_S").toString() + " " + MyArrList.get(i).get("LastName_S").toString();
            profile.withName(Name);
            headerResult.updateProfile(profile);

        }
    }


    private void ShowHomeFragment() {
        bundle = new Bundle();
        bundle.putString("User_S", Username);
        //Set the fragment initially
        MainSFragment fragment = new MainSFragment();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_main, fragment);
        fragmentTransaction.commit();
    }

    private void ShowRegistercouresFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("User_S", Username);
        ShowAllCourseFragment showAllCourseFragment = new ShowAllCourseFragment();
        showAllCourseFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_main, showAllCourseFragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }


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

    private void GetCourse() {
        new DownloadCourse().execute();
    }

    public class DownloadCourse extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/getStudent_course.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("User_S", Username));
            result = cn.connect(nameValuePairs, url);

            try {
                Log.d("result2", result);
                JSONArray data = new JSONArray(result); //get เป็ย array
                HashMap<String, Object> map;

                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i); //ข้อมูลที่ได้กลับมาทำ json
                    map = new HashMap<String, Object>();
                    map.put("Id_Course", c.getString("Id_Course"));
                    map.put("Name_Coures", c.getString("Name_Coures"));
                    MyArrList2.add(map);

                    mCourseItem.add(new SecondaryDrawerItem()
                            .withName(c.getString("Name_Coures"))
                            .withIcon(GoogleMaterial.Icon.gmd_8tracks)
                            .withLevel(2));


                }

//                    drawer.setSelectionAtPosition(0, false);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


}
