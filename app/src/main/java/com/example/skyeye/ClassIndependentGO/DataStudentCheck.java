package com.example.skyeye.ClassIndependentGO;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

public class DataStudentCheck extends AppCompatActivity implements SearchView.OnQueryTextListener{

    ConnectMySQL cn = new ConnectMySQL();
    private String url, result, IDcourse, IDStudent;
    ArrayList<HashMap<String, Object>> MyArrList = new ArrayList<HashMap<String, java.lang.Object>>();
    private ProgressDialog progressDialog;
    ListView listView;
    android.support.v7.app.ActionBar actionBar;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_student_check);

        IDcourse = getIntent().getStringExtra("Id_Course");
        listView = (ListView) findViewById(R.id.showdatastu_listview);
//        Toast.makeText(this, IDcourse, Toast.LENGTH_LONG).show();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        actionBar = getSupportActionBar();

        GetDataStu();
    }

    private void GetDataStu(){
        new DownloadDatastu().execute();
    }

    public class DownloadDatastu extends AsyncTask<String, Void, Void> {

        //ทำงานก่อนทำงานหลัก AsyncTask เมื่อถูกเรียกใช้
        @Override
        protected void onPreExecute() { //โหลดก่อนทำงาน
            super.onPreExecute();
            progressDialog = new ProgressDialog(DataStudentCheck.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/getDatastudentcheck.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Course", IDcourse));
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
                    map.put("Id_Student", c.getString("Id_Student"));
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

    private void ShowAllContent(){
        try {
            listView.setAdapter(new DataStudentlist(this, MyArrList));
        } catch (Exception e) {
            Toast.makeText(this , "ไม่มีข้อมูล", Toast.LENGTH_SHORT).show();
        }
//        name = MyArrList.get(0).get("Name_Coures").toString();
//        Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();

    }

    public class DataStudentlist extends BaseAdapter {

        private Context context;
        private ArrayList<HashMap<String, Object>> MyArr = null;

        ArrayList<String> recordings = new ArrayList<String>();
        public DataStudentlist(Context c, ArrayList<HashMap<String, Object>> list)
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
                convertView = inflater.inflate(R.layout.datastu_list, null);
            }


            //columtext
            TextView txtNamestudentF = (TextView) convertView.findViewById(R.id.txtNamestudentCheck);
            TextView txtIDcode = (TextView) convertView.findViewById(R.id.txtIDcodeCheck);

            txtNamestudentF.setText(context.getString(R.string.namestudent) + " " + MyArr.get(position).get("FirtName_S").toString()
                    + "  " + MyArr.get(position).get("LastName_S").toString());

            txtIDcode.setText(context.getString((R.string.IDcode)) + " " + MyArr.get(position).get("Idcode_S").toString());

            ImageView imageView = (ImageView) convertView.findViewById(R.id.iconstudent);
            imageView.setImageResource(R.drawable.ic_student);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    IDStudent = MyArrList.get(position).get("Id_Student").toString();
                    Intent intent = new Intent(DataStudentCheck.this,DataCheckName.class);
                    intent.putExtra("Id_Student", IDStudent);
                    intent.putExtra("Id_Course", IDcourse);
                    DataStudentCheck.this.startActivity(intent);
                }
            });

            return convertView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;

            case R.id.action_refesh:
                DataStudentCheck.this.startActivity(getIntent());
                finish();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search).setIcon(new IconicsDrawable(this, FontAwesome.Icon.faw_search).color(Color.WHITE).actionBar());
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("ค้นหานักศึกษา...");
        super.onCreateOptionsMenu(menu);
        return true;
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

        listView.setAdapter(new DataStudentlist(this, filteredValues));
        return true;
    }

}
