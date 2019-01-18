package com.example.skyeye.ClassIndependentGO;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

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
public class MainSFragment extends Fragment implements SearchView.OnQueryTextListener {
    private String Username;
    ListView listView;
    private  int code;
    private ProgressDialog progressDialog;
    ConnectMySQL cn = new ConnectMySQL();
    TextView txt_coursename, txt_nameteacher, txt_Lnameteacher;
    private String IDStu, namec, fnameteacher, idcourse, datacourse;
    private String result, url, score, idteacher, codecourse, namecourse, Fnameteacher, Lnameteacher, url2;
    ArrayList<HashMap<String, Object>> MyArrList = new ArrayList<HashMap<String, java.lang.Object>>();
    Bundle bundle;


    public MainSFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("วิชาเรียน");
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_main_s, container, false);
        View rootView = inflater.inflate(R.layout.fragment_main_s, container, false);
//        listView.setTextFilterEnabled(true);

        Username = getArguments().getString("User_S");
//        Toast.makeText(getActivity(), Username, Toast.LENGTH_LONG).show();

        listView = (ListView) rootView.findViewById(R.id.showstucourse_listview);
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
        GetcouseStu();

        new IconicsDrawable(getActivity(), FontAwesome.Icon.faw_adjust);

    return rootView;
    }


    private void GetcouseStu() {
        new DowloadsStudentCourse().execute();
    }


    public class DowloadsStudentCourse extends AsyncTask<String, Void, Void>{

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

            url = "https://classindependent.000webhostapp.com/Android/getStudent_course.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("User_S", Username ));
            result = cn.connect(nameValuePairs, url);

            try{
                Log.d("result", result);
                JSONArray data = new JSONArray(result); //get เป็ย array
//                MyArrList = new ArrayList<HashMap<String, Object>>();
                HashMap<String, Object> map;

                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i); //ข้อมูลที่ได้กลับมาทำ json
                    map = new HashMap<String, Object>();
                    map.put("Name_Coures", c.getString("Name_Coures"));
                    map.put("FirstName", c.getString("FirstName"));
                    map.put("LastName", c.getString("LastName"));
                    map.put("Id_Student", c.getString("Id_Student"));
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
        protected void onPostExecute(Void aVoid) { //ทำงานขั้นตอนสุดท้าย
            ShowAllContent();
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

    public void ShowAllContent() {
        try {
            listView.setAdapter(new CourseList(getActivity(), MyArrList));

        }
        catch (Exception e) {
            Toast.makeText(getActivity(), "ไม่มีข้อมูล", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search).setIcon(new IconicsDrawable(getActivity(), FontAwesome.Icon.faw_search).color(Color.WHITE).actionBar());
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("ค้นหาวิชาเรียน...");


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ArrayList<HashMap<String, Object>> filteredValues = new ArrayList<HashMap<String, java.lang.Object>>(MyArrList);
        for (HashMap<String, Object> value : MyArrList) {
            if (!value.toString().toLowerCase().contains(newText.toLowerCase())) {
                filteredValues.remove(value);
            }
        }

        listView.setAdapter(new CourseList(getActivity(), filteredValues));

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_refesh:
                bundle = new Bundle();
                bundle.putString("User_S", Username);
                //Set the fragment initially
                MainSFragment fragment = new MainSFragment();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_main, fragment);
                fragmentTransaction.commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
