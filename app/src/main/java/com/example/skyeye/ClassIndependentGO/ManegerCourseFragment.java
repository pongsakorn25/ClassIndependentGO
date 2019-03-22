package com.example.skyeye.ClassIndependentGO;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.R.attr.fragment;


public class ManegerCourseFragment extends Fragment {
    private ProgressDialog progressDialog;
    ConnectMySQL cn = new ConnectMySQL();
    private String result,url,score,codecourse,namecourse,description, IDCourse, NameCourse1,Username;
    ArrayList<HashMap<String, Object>> MyArrList = new ArrayList<HashMap<String, java.lang.Object>>();
    private ImageButton updatecourse;
    Bundle bundle;
    public ManegerCourseFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_maneger_course, container, false);

        IDCourse = getArguments().getString("Id_Course");
        NameCourse1 = getArguments().getString("Name_Coures");
        Username = getArguments().getString("Username");

        updatecourse = (ImageButton) rootView.findViewById(R.id.btnUpdateC);
        updatecourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateCourse.class);
                intent.putExtra("Code_Course", codecourse);
                intent.putExtra("Name_Coures", namecourse);
                intent.putExtra("Description", description);
                intent.putExtra("Score", score);
                intent.putExtra("Id_Course", IDCourse);
                startActivity(intent);
            }
        });


        new AsyncTask<Void, Void, Void>() {

            //ทำงานก่อนทำงานหลัก AsyncTask เมื่อถูกเรียกใช้
            @Override
            protected void onPreExecute() { //โหลดก่อนทำงาน
                super.onPreExecute();
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(false);
                progressDialog.setMessage("กรุณารอสักครู่ ...");
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {

                url = "https://classindependent.000webhostapp.com/Android/getUpdateCourse.php";
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("Id_Course", IDCourse));
                result = cn.connect(nameValuePairs, url);
                try {


                    Log.d("result", result);
                    JSONArray data = new JSONArray(result); //get เป็ย array
                    MyArrList = new ArrayList<HashMap<String, Object>>();
                    HashMap<String, Object> map;//การจัดเก็บค่าตัวแปร

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i); //ข้อมูลที่ได้กลับมาทำ json
                        map = new HashMap<String, Object>();
                        map.put("Code_Course", c.getString("Code_Course"));
                        map.put("Name_Coures", c.getString("Name_Coures"));
                        map.put("Description", c.getString("Description"));
                        map.put("Score", c.getString("Score"));

                        MyArrList.add(map);


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ShowAllContent();
                progressDialog.dismiss();
            }
        }.execute();

        return rootView;
    }

    // Show All Content
    public void ShowAllContent() {
        codecourse = MyArrList.get(0).get("Code_Course").toString();
        namecourse = MyArrList.get(0).get("Name_Coures").toString();
        description = MyArrList.get(0).get("Description").toString();
        score = MyArrList.get(0).get("Score").toString();
//        Toast.makeText(getActivity(), codecourse+namecourse+description+score, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStop() {
        super.onStop();
        progressDialog.dismiss(); // try this
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_refresh, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.action_refesh2:
                intent = new Intent(getActivity(), MainManager.class);
                intent.putExtra("Id_Course", IDCourse);
                intent.putExtra("Name_Coures", NameCourse1);
                intent.putExtra("Username", Username);
                getActivity().startActivity(intent);
                getActivity().finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
