package com.example.skyeye.ClassIndependentGO;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.skyeye.ClassIndependentGO.R.id.txtCoursename;
import static com.example.skyeye.ClassIndependentGO.R.id.txtNameF;
import static com.example.skyeye.ClassIndependentGO.R.id.txtNameL;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
/**
 * A simple {@link Fragment} subclass.
 */
public class ShowAllCourseFragment extends Fragment implements SearchView.OnQueryTextListener{


    ListView showallcoure;
//    private ArrayList<String> exData;
    private ProgressDialog progressDialog;
    private String IDStu,namec,fnameteacher,idcourse;
    private String result,url,score,idteacher,codecourse,namecourse,Fnameteacher,Lnameteacher;
    private String Username;
    private int code;
    TextView txt_coursename,txt_nameteacher,txt_Lnameteacher;
    ConnectMySQL cn = new ConnectMySQL();
    ArrayList<HashMap<String, Object>> MyArrList = new ArrayList<HashMap<String, java.lang.Object>>();
    ArrayList<HashMap<String, Object>> MyArrList2;
    Bundle bundle;

    public ShowAllCourseFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("สมัครลงเรียน");


    }

    // Show All Content
    public void ShowAllContent() {
        try {
            showallcoure.setAdapter(new CourseList(getActivity(), MyArrList));
            GetIDstu();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "ไม่มีข้อมูล", Toast.LENGTH_SHORT).show();
        }
    }

    public class CourseList extends BaseAdapter {
        private Context context;
        private ArrayList<HashMap<String, Object>> MyArr = null;

        ArrayList<String> recordings = new ArrayList<String>();

        public CourseList(Context c, ArrayList<HashMap<String, Object>> list) {
            // TODO Auto-generated method stub
            context = c;
            MyArr = list;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return MyArr.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.course_list, null);
            }

            // Colname
            TextView txtNameC =  convertView.findViewById(R.id.txtCoursename);
            TextView txtNameF =  convertView.findViewById(R.id.txtNameF);
            TextView txtNameL = convertView.findViewById(R.id.txtNameL);

            ImageView imageView =  convertView.findViewById(R.id.iconicsImageView);
            imageView.setImageResource(R.drawable.regiscourse);

            txtNameC.setText(context.getString(R.string.namecourse) + " " + MyArr.get(position).get("Name_Coures").toString());
            txtNameF.setText(context.getString(R.string.nameteacher) + " " + MyArr.get(position).get("FirstName").toString());
            txtNameL.setText(MyArr.get(position).get("LastName").toString());


            return convertView;
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_showallcourse, container, false);


        Username = getArguments().getString("User_S");
//        Toast.makeText(getActivity(), Username, Toast.LENGTH_LONG).show();


        showallcoure =  rootView.findViewById(R.id.showallcouse_listview);
        showallcoure.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                idcourse = MyArrList.get(position).get("Id_Course").toString();
                namecourse = MyArrList.get(position).get("Name_Coures").toString();
                Fnameteacher = MyArrList.get(position).get("FirstName").toString();
                Lnameteacher = MyArrList.get(position).get("LastName").toString();

//                Toast.makeText(getActivity(), idcourse + namecourse + Fnameteacher + Lnameteacher, Toast.LENGTH_SHORT).show();

                GetCheckCourse();
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

                url = "https://classindependent.000webhostapp.com/Android/getCourse.php";

                try {

                    result = cn.connect(url);
                    Log.d("result", result);
                    JSONArray data = new JSONArray(result); //get เป็ย array
                    MyArrList = new ArrayList<HashMap<String, Object>>();
                    HashMap<String, Object> map;//การจัดเก็บค่าตัวแปร

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i); //ข้อมูลที่ได้กลับมาทำ json
                        map = new HashMap<String, Object>();
                        map.put("Name_Coures", c.getString("Name_Coures"));
                        map.put("FirstName", c.getString("FirstName"));
                        map.put("LastName", c.getString("LastName"));
                        map.put("Id_Course", c.getString("Id_Course"));

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

    private void GetIDstu() {
        new DownloadDatastudent().execute();
    }


    public class DownloadDatastudent extends AsyncTask<String, Void, Void> {

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
            url = "https://classindependent.000webhostapp.com/Android/getDatastudent.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("User_S", Username));
            result = cn.connect(nameValuePairs, url);
            try {
                Log.d("result", result);

                JSONArray data = new JSONArray(result); //get เป็ย array
                MyArrList2 = new ArrayList<HashMap<String, Object>>();
                HashMap<String, Object> map;

                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i); //ข้อมูลที่ได้กลับมาทำ json
                    map = new HashMap<String, Object>();
                    map.put("Id_Student", c.getString("Id_Student"));
                    MyArrList2.add(map);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ShowAllContent2();
            super.onPostExecute(aVoid);
        }
    }

    public void ShowAllContent2() {
        IDStu = MyArrList2.get(0).get("Id_Student").toString();

    }

    private void GetCheckCourse() {
        new CheckCourse().execute();
    }


    public class CheckCourse extends AsyncTask<String, Void, Void> {

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
            url = "https://classindependent.000webhostapp.com/Android/Check_course.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Course", idcourse));
            nameValuePairs.add(new BasicNameValuePair("Id_Student", IDStu));
            result = cn.connect(nameValuePairs, url);
            Log.d("result1", result);
            JSONObject json_data;
            try {
                json_data = new JSONObject(result);
                code = (json_data.getInt("code"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ShowAllContent3();
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

    public void ShowAllContent3() {
        if(code == 1) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("ไม่สำเร็จ");
            dialog.setIcon(R.drawable.ic_noconfirm);
            dialog.setCancelable(true);
            dialog.setMessage("เนืองจากคุณได้ทำการสมัครวิชานี้ไปแล้ว");
            dialog.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog.show();
        }

        else{
            Intent intent = new Intent(getActivity(),RegisCourse.class);
            intent.putExtra("User_S",Username);

//            txt_coursename =  getActivity().findViewById(txtCoursename);
//            txt_nameteacher =  getActivity().findViewById(txtNameF);
//            txt_Lnameteacher =  getActivity().findViewById(txtNameL);
//
//            namecourse = txt_coursename.getText().toString();
//            Fnameteacher = txt_nameteacher.getText().toString();
//            Lnameteacher = txt_Lnameteacher.getText().toString();
//
//            String[] separated1 = Fnameteacher.split("ผู้สอน:");
//            String[] separated2 = namecourse.split("วิชา:");

//            Toast.makeText(getActivity(), separated2[1] + separated1[1], Toast.LENGTH_LONG).show();

            intent.putExtra("Name_Coures" , namecourse);
            intent.putExtra("FirstName", Fnameteacher);
            intent.putExtra("LastName",Lnameteacher );

            intent.putExtra("Id_Student",IDStu );

            intent.putExtra("Id_Course", idcourse);
            startActivity(intent);
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

        showallcoure.setAdapter(new CourseList(getActivity(), filteredValues));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_refesh:
                Bundle bundle = new Bundle();
                bundle.putString("User_S", Username);
                ShowAllCourseFragment showAllCourseFragment = new ShowAllCourseFragment();
                showAllCourseFragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_main, showAllCourseFragment);
//        transaction.addToBackStack(null);
                transaction.commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
