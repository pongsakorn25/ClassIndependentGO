package com.example.skyeye.ClassIndependentGO;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainTFragment extends Fragment {

    ConnectMySQL cn = new ConnectMySQL();
    private String Username, url, result, name;
    ArrayList<HashMap<String, Object>> MyArrList = new ArrayList<HashMap<String, java.lang.Object>>();
    ListView listView;
    private ProgressDialog progressDialog;
    Bundle bundle;


    public MainTFragment() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("วิชาที่เปิดสอน");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_t, container, false);
        setHasOptionsMenu(true);
        Username = getArguments().getString("User");
//        Toast.makeText(getActivity(),Username,Toast.LENGTH_LONG).show();

        listView = (ListView) rootView.findViewById(R.id.showteacourse_listview);
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("รายละเอียดวิชาเรียน");
                alertDialog.setMessage("ชื่อวิชา : " + MyArrList.get(position).get("Name_Coures").toString().trim() + "\n\n" +
                        "ผู้สอน : " + MyArrList.get(position).get("FirstName").toString().trim()+ " " + MyArrList.get(position).get("LastName").toString().trim() + "\n\n" +
                        "คำอธิบายวิชา : " + MyArrList.get(position).get("Description").toString().trim() + "\n\n" +
                        "คะแนนเข้าเรียน : "+ MyArrList.get(position).get("Score").toString().trim() + " คะแนน");

                alertDialog.setIcon(R.drawable.ic_datacourse);

                alertDialog.setNegativeButton(getString(R.string.OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
        });
        GetcourseTeacher();
        return rootView;
    }

    private void GetcourseTeacher(){
        new DownloadCourseTeaher().execute();
    }

    public class DownloadCourseTeaher extends AsyncTask<String, Void, Void>{

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
                    map.put("Name_Coures", c.getString("Name_Coures"));
                    map.put("FirstName", c.getString("FirstName"));
                    map.put("LastName", c.getString("LastName"));
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
            ShowAllContent();
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

    private void ShowAllContent(){
        try {
            listView.setAdapter(new CourseList(getActivity(), MyArrList));
        }
        catch (Exception e) {
            Toast.makeText(getActivity(), "ไม่มีข้อมูล", Toast.LENGTH_SHORT).show();
        }

//        name = MyArrList.get(0).get("Name_Coures").toString();
//        Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_refresh, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.action_refesh2:
                bundle = new Bundle();
                bundle.putString("User", Username);
                //Set the fragment initially
                MainTFragment fragment = new MainTFragment();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_main2, fragment);
                fragmentTransaction.commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }





}
