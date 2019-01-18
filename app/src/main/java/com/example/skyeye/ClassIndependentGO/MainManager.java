package com.example.skyeye.ClassIndependentGO;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by TPK on 10-Apr-17.
 */

public class MainManager extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Bundle bundle;
    private String IDCourse, NameCourse, Username, IDTeacher;
    FragmentTransaction fragmentTransaction;
    private long backPressedTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmaneger);

        IDCourse = getIntent().getStringExtra("Id_Course");
        NameCourse = getIntent().getStringExtra("Name_Coures");
        Username = getIntent().getStringExtra("Username");
        IDTeacher = getIntent().getStringExtra("Id_Teacher");

        toolbar = (Toolbar) findViewById(R.id.toolbarcourseT);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(NameCourse);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpagerT);
        viewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));

        tabLayout = (TabLayout) findViewById(R.id.tabsT);
        tabLayout.setupWithViewPager(viewPager);
    }

    class TabFragmentPagerAdapter extends FragmentPagerAdapter {
        String[] title = new String[]{
                "จัดการข้อมูลวิชา", "ยืนยันการรับนึกศึกษาเข้าเรียน", "จัดการการเช็คชื่อ", "จัดการข้อมูลข่าวสาร"
        };

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            switch (position) {
                case 0:
                    fragment = new ManegerCourseFragment();
                    bundle = new Bundle();
                    bundle.putString("Id_Course", IDCourse);
                    bundle.putString("Name_Coures", NameCourse);
                    bundle.putString("Username", Username);

                    fragment.setArguments(bundle);
                    break;
                case 1:
                    fragment = new ConfirmStudentFragment();
                    bundle = new Bundle();
                    bundle.putString("User", Username);
                    bundle.putString("Id_Course", IDCourse);
                    bundle.putString("Name_Coures", NameCourse);
                    bundle.putString("Username", Username);
                    fragment.setArguments(bundle);
                    break;
                case 2:
                    fragment = new ManegerCheckNameFragment();
                    bundle = new Bundle();
                    bundle.putString("Id_Course", IDCourse);
                    fragment.setArguments(bundle);
                    break;
                case 3:
                    fragment = new ManegerMassageFragment();
                    bundle = new Bundle();
                    bundle.putString("Id_Course", IDCourse);
                    bundle.putString("Name_Coures", NameCourse);
                    bundle.putString("Username", Username);
                    bundle.putString("Id_Teacher", IDTeacher);

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
}
