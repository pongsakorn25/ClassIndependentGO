package com.example.skyeye.ClassIndependentGO;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.BaseAdapter;
import android.widget.ImageButton;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmStudentFragment extends Fragment {

    ConnectMySQL cn = new ConnectMySQL();
    private String Username, url, result, ID, IDCourse, NameCourse;
    ArrayList<HashMap<String, Object>> MyArrList = new ArrayList<HashMap<String, java.lang.Object>>();
    ListView listView;
    private int code;
    private ProgressDialog progressDialog;
    Bundle bundle;

    public ConfirmStudentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("รับนักศึกษาเข้าเรียน");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_confirm_student, container, false);
        setHasOptionsMenu(true);
        Username = getArguments().getString("User");
        IDCourse = getArguments().getString("Id_Course");
        NameCourse = getArguments().getString("Name_Coures");

        MyArrList.clear();

        listView = (ListView) rootView.findViewById(R.id.ConfirmStu_listview);
        GetcourseTeacher();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        return rootView;
    }
    private void GetcourseTeacher(){
        new Datastudent().execute();
    }

    public class Datastudent extends AsyncTask<String, Void, Void> {

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
            url = "https://classindependent.000webhostapp.com/Android/confirm_student.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("User", Username));
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
                    map.put("Id", c.getString("Id"));
                    map.put("Id_Teacher", c.getString("Id_Teacher"));
                    map.put("FirtName_S", c.getString("FirtName_S"));
                    map.put("LastName_S", c.getString("LastName_S"));
                    map.put("Idcode_S", c.getString("Idcode_S"));

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

    private void ShowAllContent() {
        try {
            listView.setAdapter(new Confirmstudent(getActivity(), MyArrList));
        } catch (Exception e) {
            Toast.makeText(getActivity(), "ไม่มีข้อมูล", Toast.LENGTH_SHORT).show();
        }
//        Toast.makeText(getActivity(), Username, Toast.LENGTH_SHORT).show();
//        Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
    }

    public class Confirmstudent extends BaseAdapter{

        private Context context;
        private ArrayList<HashMap<String, Object>> MyArr = null;

        ArrayList<String> recordings = new ArrayList<String>();
        public Confirmstudent(Context c, ArrayList<HashMap<String, Object>> list)
        {
            // TODO Auto-generated method stub
            context = c;
            MyArr = list;
        }

        @Override
        public int getCount() {
            return MyArr.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            if (convertView == null) {
                convertView = inflater.inflate(R.layout.confirm_student_list, null);
            }


            //columtext
            TextView txtNamestudentF = (TextView) convertView.findViewById(R.id.txtNamestudent);
            TextView txtIDcode = (TextView) convertView.findViewById(R.id.txtIDcode);

            txtNamestudentF.setText(context.getString(R.string.namestudent) + "  " + MyArr.get(position).get("FirtName_S").toString()
                    + "  " + MyArr.get(position).get("LastName_S").toString());
            txtIDcode.setText(context.getString((R.string.IDcode)) + "  " + MyArr.get(position).get("Idcode_S").toString());


            ImageButton btnConfirm = (ImageButton) convertView.findViewById(R.id.btnconfirm);
            ImageButton btnDelete = (ImageButton) convertView.findViewById(R.id.btndelete);
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ID = MyArrList.get(position).get("Id").toString();
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("ยืนยันการรับนักศึกษา");

                    alertDialog.setIcon(R.drawable.ic_confirm);

                    alertDialog.setPositiveButton(getString(R.string.OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Getcon();
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

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ID = MyArrList.get(position).get("Id").toString();
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("ต้องการลบรายการนี้");

                    alertDialog.setIcon(R.drawable.ic_noconfirm);

                    alertDialog.setPositiveButton(getString(R.string.OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    GetDelete();
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

            return convertView;
        }
    }

    private void Getcon(){
        new UpdateConfirmstudent().execute();
    }

    public class UpdateConfirmstudent extends AsyncTask<String, Void, Void> {

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
            url = "https://classindependent.000webhostapp.com/Android/UpdateConfirmStudent.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id", ID));
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
            ShowAllContent2();
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

    private void ShowAllContent2() {
        if(code == 1){
            Toast.makeText(getActivity(),"ยืนยันเรียบร้อย", Toast.LENGTH_LONG).show();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
        else{
            Toast.makeText(getActivity(),"NOT CONFIRM", Toast.LENGTH_LONG).show();
        }
    }

    private void GetDelete(){
        new DeleteConfirmstudent().execute();
    }

    public class DeleteConfirmstudent extends AsyncTask<String, Void, Void> {

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
            url = "https://classindependent.000webhostapp.com/Android/deletestudent_course.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id", ID));
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
            ShowAllContent3();
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

    private void ShowAllContent3() {
        if(code == 1){
            Toast.makeText(getActivity(),"ลบเรียบร้อย", Toast.LENGTH_LONG).show();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(this).attach(this).commit();
        }
        else{
            Toast.makeText(getActivity(),"NOT DELETE", Toast.LENGTH_LONG).show();
        }
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
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
