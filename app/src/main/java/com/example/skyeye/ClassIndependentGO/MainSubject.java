package com.example.skyeye.ClassIndependentGO;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainSubject extends AppCompatActivity {
    ConnectMySQL cn = new ConnectMySQL();
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Bundle bundle;
    private String IDCourse, NameCourse, IDStudent, Username, url, result, NewLesson, NewVideo, NewMessage, new_lesson;
    FragmentTransaction fragmentTransaction;
    private long backPressedTime;
    ArrayList<HashMap<String, Object>> MyArrList = new ArrayList<HashMap<String, java.lang.Object>>();
    ArrayList<HashMap<String, Object>> MyArrList2 = new ArrayList<HashMap<String, java.lang.Object>>();
    ArrayList<HashMap<String, Object>> MyArrList3 = new ArrayList<HashMap<String, java.lang.Object>>();
    private int numnewlesson, numnewvideo, nummessage, code, sizelesson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainsubject);

        IDCourse = getIntent().getStringExtra("Id_Course");
        NameCourse = getIntent().getStringExtra("Name_Coures");
        IDStudent = getIntent().getStringExtra("Id_Student");
        Username = getIntent().getStringExtra("User_S");

//        Toast.makeText(this, IDCourse, Toast.LENGTH_SHORT).show();

        toolbar = (Toolbar) findViewById(R.id.toolbarcourseS);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(NameCourse);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        viewPager = (ViewPager) findViewById(R.id.viewpagerS);
        viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));

        tabLayout = (TabLayout) findViewById(R.id.tabsS);
        tabLayout.setupWithViewPager(viewPager);


        GetDatalesson();
        GetDataVideo();
        GetDatamessage();


    }

//    private void setupViewPager(ViewPager viewPager) {
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFrag(new CourseSyllabusFragment(), "แผนบริหารการสอน");
//        adapter.addFrag(new LessonFragment(), "เนื้อหา");
//        adapter.addFrag(new VideoFragment(), "วิดีโอ");
//        adapter.addFrag(new CheckNameFragment(), "เช็คชื่อ");
//        adapter.addFrag(new MessageFragment(), "ข่าวสาร");
//        adapter.addFrag(new SixFragment(), "SIX");
//        adapter.addFrag(new SevenFragment(), "SEVEN");
//        adapter.addFrag(new EightFragment(), "EIGHT");
//        adapter.addFrag(new NineFragment(), "NINE");
//        adapter.addFrag(new TenFragment(), "TEN");
//        viewPager.setAdapter(adapter);
//    }

    class TabFragmentPagerAdapter extends FragmentPagerAdapter {
        String[] title = new String[]{
                "แผนบริหารการสอน", "เนื้อหา", "วิดีโอ", "เช็คชื่อ", "ข่าวสาร"
        };

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            switch (position) {
                case 0:
                    fragment = new CourseSyllabusFragment();
                    bundle = new Bundle();
                    bundle.putString("Id_Course", IDCourse);
                    bundle.putString("Name_Coures", NameCourse);
                    bundle.putString("Id_Student", IDStudent);
                    bundle.putString("User_S", Username);

                    fragment.setArguments(bundle);
//                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.viewpager, fragment);
//                    fragmentTransaction.commit();
                    break;
                case 1:
                    fragment = new LessonFragment();
                    bundle = new Bundle();
                    bundle.putString("Id_Course", IDCourse);
                    bundle.putString("Name_Coures", NameCourse);
                    bundle.putString("Id_Student", IDStudent);
                    bundle.putString("User_S", Username);
                    fragment.setArguments(bundle);
                    break;
                case 2:
                    fragment = new VideoFragment();
                    bundle = new Bundle();
                    bundle.putString("Id_Course", IDCourse);
                    bundle.putString("Name_Coures", NameCourse);
                    bundle.putString("Id_Student", IDStudent);
                    bundle.putString("User_S", Username);
                    fragment.setArguments(bundle);
                    break;
                case 3:
                    fragment = new CheckNameFragment();
                    bundle = new Bundle();
                    bundle.putString("Id_Course", IDCourse);
                    bundle.putString("Id_Student", IDStudent);
                    fragment.setArguments(bundle);
                    break;
                case 4:
                    fragment = new MessageFragment();
                    bundle = new Bundle();
                    bundle.putString("Id_Course", IDCourse);
                    bundle.putString("Name_Coures", NameCourse);
                    bundle.putString("Id_Student", IDStudent);
                    bundle.putString("User_S", Username);
                    fragment.setArguments(bundle);
                    break;

                default:
                    fragment = null;
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return title.length;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("User_S", Username);
                MainSubject.this.startActivity(intent);
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
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("User_S", Username);
            MainSubject.this.startActivity(intent);
//            super.onBackPressed();// bye

        }
    }

    //********************************************************************************************************************
    private void GetDatalesson() {
        new Datalesson().execute();
    }

    public class Datalesson extends AsyncTask<String, Void, Void> {

        //ทำงานก่อนทำงานหลัก AsyncTask เมื่อถูกเรียกใช้
        @Override
        protected void onPreExecute() { //โหลดก่อนทำงาน
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/getLesson.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Course", IDCourse));
            result = cn.connect(nameValuePairs, url);

            try {
//                result = cn.connect(url);
                Log.d("result", result);
                JSONArray data = new JSONArray(result); //get เป็ย array
//                MyArrList = new ArrayList<HashMap<String, Object>>();
                HashMap<String, Object> map;//การจัดเก็บค่าตัวแป

                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i); //ข้อมูลที่ได้กลับมาทำ json
                    map = new HashMap<String, Object>();
                    map.put("New_lesson", c.getString("New_lesson"));
                    MyArrList.add(map);


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ShowAllContent2();
//            showNotificationNewLesson();
            super.onPostExecute(aVoid);
        }
    }

    private void ShowAllContent2() {
        for (int i = 0; i < MyArrList.size(); i++) {
            NewLesson = MyArrList.get(i).get("New_lesson").toString();
            numnewlesson = Integer.parseInt(NewLesson);

//            sizelesson = 10;
//            new_lesson = String.valueOf(sizelesson);
//            Toast.makeText(this, new_lesson, Toast.LENGTH_SHORT).show();
//            GetInsernewlesson();

            if(numnewlesson == 1){
                showNotificationNewLesson();

                GetUpdatenewlesson();
//                Toast.makeText(this, New, Toast.LENGTH_SHORT).show();
            }
        }

    }


//    private void GetInsernewlesson(){
//        new Insernewlesson().execute();
//    }
//
//    public class Insernewlesson extends AsyncTask<String, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(String... params) {
//            url = "https://classindependent.000webhostapp.com/Android/Insernewlesson.php";
//            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//            nameValuePairs.add(new BasicNameValuePair("Id_Course", IDCourse));
//            nameValuePairs.add(new BasicNameValuePair("Id_Student", IDStudent));
//            nameValuePairs.add(new BasicNameValuePair("New_lesson", new_lesson));
//
//            result = cn.connect(nameValuePairs, url);
//            Log.d("result00", result);
//            JSONObject json_data;
//            try {
//                json_data = new JSONObject(result);
//                code = (json_data.getInt("code"));
//            } catch (JSONException e) {
//                Log.e("Fail 1", e.toString());
//                e.printStackTrace();
//            }
//
//            return null;
//
//
//        }
//
//        protected void onPostExecute(Void unused) {
//            ShowAllContent(); // When Finish Show Content
//        }
//
//    }
//
//    public void ShowAllContent() {
//        if (code == 1) {
//            Toast.makeText(MainSubject.this,getString(R.string.Regiscouese), Toast.LENGTH_SHORT).show();
//        }
//        else{
////            Toast.makeText(RegisCourse.this, "คุณสมัคเรียนวิชานี้ไปแล้ว กรุณาเลือกวิชาใหม่", Toast.LENGTH_SHORT).show();
////            onBackPressed();
//        }
//
//
//    }



    private void GetUpdatenewlesson(){
        new UpdateLessonnew().execute();
    }

    public class UpdateLessonnew extends AsyncTask<String, Void, Void> {

        //ทำงานก่อนทำงานหลัก AsyncTask เมื่อถูกเรียกใช้
        @Override
        protected void onPreExecute() { //โหลดก่อนทำงาน
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/Update_newlesson.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Course", IDCourse));
            result = cn.connect(nameValuePairs, url);
            Log.d("result", result);
            JSONObject jsonObject;
            try{
                jsonObject = new JSONObject(result);
                code = (jsonObject.getInt("code"));
            } catch (JSONException e) {
                Log.e("Fail 1" , e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


    public void showNotificationNewLesson() {
        Context context = this;
        int color = ContextCompat.getColor(context, R.color.colorPrimary);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_pdf);
        Notification notification =
                new NotificationCompat.Builder(context) // this is context
                        .setSmallIcon(R.drawable.ic_pdf)
                        .setLargeIcon(bitmap)
                        .setContentTitle("มีเนื้อหาบทเรียนใหม่ !")
//                        .setContentText("วิชา: " + NameCourse)
                        .setAutoCancel(true)
                        .setColor(color)
                        .setContentIntent(getPendingIntent(context))
                        .setVibrate(new long[]{500, 800, 500})
                        .setLights(color, 3000, 3000)
                        .build();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }


    //********************************************************************************************************************

    private void GetDataVideo(){
        new DataVideo().execute();
    }

    public class DataVideo extends AsyncTask<String, Void, Void> {

        //ทำงานก่อนทำงานหลัก AsyncTask เมื่อถูกเรียกใช้
        @Override
        protected void onPreExecute() { //โหลดก่อนทำงาน
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/getVideo.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Course", IDCourse));
            result = cn.connect(nameValuePairs, url);

            try{
//                result = cn.connect(url);
                Log.d("result", result);
                JSONArray data = new JSONArray(result); //get เป็ย array
//                MyArrList = new ArrayList<HashMap<String, Object>>();
                HashMap<String, Object> map;//การจัดเก็บค่าตัวแป

                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i); //ข้อมูลที่ได้กลับมาทำ json
                    map = new HashMap<String, Object>();
                    map.put("New_Video", c.getString("New_Video"));

                    MyArrList2.add(map);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ShowAllContent3();
            super.onPostExecute(aVoid);
        }
    }

    private void ShowAllContent3() {
        for (int i = 0; i < MyArrList2.size(); i++) {
            NewVideo = MyArrList2.get(i).get("New_Video").toString();
            numnewvideo = Integer.parseInt(NewVideo);
            if(numnewvideo == 1){
                showNotificationNewVideo();
                GetUpdatenewVideo();

            }

        }
    }

    private void GetUpdatenewVideo(){
        new UpdateVideo().execute();
    }

    public class UpdateVideo extends AsyncTask<String, Void, Void> {

        //ทำงานก่อนทำงานหลัก AsyncTask เมื่อถูกเรียกใช้
        @Override
        protected void onPreExecute() { //โหลดก่อนทำงาน
            super.onPreExecute();
//            progressDialog = new ProgressDialog(getActivity());
//            progressDialog.setCancelable(false);
//            progressDialog.setMessage("กรุณารอสักครู่ ...");
//            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/Update_newvideo.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Course", IDCourse));
            result = cn.connect(nameValuePairs, url);
            Log.d("result", result);
            JSONObject jsonObject;
            try{
                jsonObject = new JSONObject(result);
                code = (jsonObject.getInt("code"));
            } catch (JSONException e) {
                Log.e("Fail 1" , e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }


    public void showNotificationNewVideo() {
        Context context = this;
        int color = ContextCompat.getColor(context, R.color.colorPrimary);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_video);
        Notification notification =
                new NotificationCompat.Builder(context) // this is context
                        .setSmallIcon(R.drawable.ic_video)
                        .setLargeIcon(bitmap)
                        .setContentTitle("มีวิดีสื่อการสอนเพิ่มใหม่ !")
//                        .setContentText("วิชา: " + NameCourse)
                        .setAutoCancel(true)
                        .setColor(color)
                        .setContentIntent(getPendingIntent(context))
                        .setVibrate(new long[]{500, 800, 500})
                        .setLights(color, 3000, 3000)
                        .build();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, notification);
    }

    //********************************************************************************************************************

    private void GetDatamessage(){
        new Datamessage().execute();
    }


    public class Datamessage extends AsyncTask<String, Void, Void> {

        //ทำงานก่อนทำงานหลัก AsyncTask เมื่อถูกเรียกใช้
        @Override
        protected void onPreExecute() { //โหลดก่อนทำงาน
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/getMessage.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Course", IDCourse));
            result = cn.connect(nameValuePairs, url);

            try{
//                result = cn.connect(url);
                Log.d("result", result);
                JSONArray data = new JSONArray(result); //get เป็ย array
//                MyArrList = new ArrayList<HashMap<String, Object>>();
                HashMap<String, Object> map;//การจัดเก็บค่าตัวแป

                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i); //ข้อมูลที่ได้กลับมาทำ json
                    map = new HashMap<String, Object>();
                    map.put("New_message", c.getString("New_message"));

                    MyArrList3.add(map);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ShowAllContent4();
            super.onPostExecute(aVoid);
        }
    }

    private void ShowAllContent4() {
        for (int i = 0; i < MyArrList3.size(); i++) {
            NewMessage = MyArrList3.get(i).get("New_message").toString();
            nummessage = Integer.parseInt(NewMessage);
            if(nummessage == 1){
                showNotificationNewMessage();
                GetUpdatenewMessage();
            }
        }
    }

    public void GetUpdatenewMessage(){
        new UpdateMessage().execute();
    }

    public class UpdateMessage extends AsyncTask<String, Void, Void> {

        //ทำงานก่อนทำงานหลัก AsyncTask เมื่อถูกเรียกใช้
        @Override
        protected void onPreExecute() { //โหลดก่อนทำงาน
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/Update_newmessage.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Course", IDCourse));
            result = cn.connect(nameValuePairs, url);
            Log.d("result", result);
            JSONObject jsonObject;
            try{
                jsonObject = new JSONObject(result);
                code = (jsonObject.getInt("code"));
            } catch (JSONException e) {
                Log.e("Fail 1" , e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public void showNotificationNewMessage() {
        Context context = this;
        int color = ContextCompat.getColor(context, R.color.colorPrimary);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_messages);
        Notification notification =
                new NotificationCompat.Builder(context) // this is context
                        .setSmallIcon(R.drawable.ic_messages)
                        .setLargeIcon(bitmap)
                        .setContentTitle("มีข่าวสารประจำวิชาใหม่ !")
//                        .setContentText("วิชา: " + NameCourse)
                        .setAutoCancel(true)
                        .setColor(color)
                        .setContentIntent(getPendingIntent(context))
                        .setVibrate(new long[]{500, 800, 500})
                        .setLights(color, 3000, 3000)
                        .build();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(3, notification);
    }

    private PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, MainSubject.class);
        intent.putExtra("Id_Course", IDCourse);
        intent.putExtra("Name_Coures", NameCourse);
        intent.putExtra("IDStudent", IDStudent);
        intent.putExtra("User_S", Username);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainSubject.class);
        stackBuilder.addNextIntent(intent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
    }



}

