package com.example.skyeye.ClassIndependentGO;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisCourse extends AppCompatActivity {
    ConnectMySQL cn = new ConnectMySQL();
    private ProgressDialog progressDialog;
    private String IDStu;
    private String namec, idcourse, codecourse, fnameteacher,lnameteacger, score, idteacher, Username, url, result;
    EditText etnamec, etcc, etnamet, etscore, etdescription, etids, etidc;
    Button bregisC, bcancle;
    int code;
    private TextView txtnameS;
    ArrayList<HashMap<String, Object>> MyArrList = new ArrayList<HashMap<String, java.lang.Object>>();
//    private int activity_regis_course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis_course);

        etnamec = findViewById(R.id.etNameC);
        etcc =  findViewById(R.id.etcodecourse);
        etnamet =  findViewById(R.id.etNameT);
        etscore =  findViewById(R.id.etScore);
        etdescription =  findViewById(R.id.etDescription);
        bregisC =  findViewById(R.id.btnRegisC);
        bcancle = findViewById(R.id.btnCancle);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        namec = getIntent().getStringExtra("Name_Coures");
        fnameteacher = getIntent().getStringExtra("FirstName");
        lnameteacger = getIntent().getStringExtra("LastName");

        IDStu = getIntent().getStringExtra("Id_Student");

//        Toast.makeText(this, namec, Toast.LENGTH_SHORT).show();

        GetDataC();




        etnamec.setText(getString(R.string.namecourse) +" " + namec );
        etnamet.setText(getString(R.string.nameteacher)+" " + fnameteacher + "  " + lnameteacger);

        bregisC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisCourse.this);
                alertDialog.setTitle("ยันยันการสมัครเรียน");

                alertDialog.setPositiveButton(getString(R.string.OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new Insertstutentcourse().execute();
                                Toast.makeText(RegisCourse.this, "สมัครเรียนเรียบร้อย รอการยืนยันจากครูผู้สอน", Toast.LENGTH_SHORT).show();
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

        bcancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }
    public void ShowAllContent() {
        if (code == 1) {
            Toast.makeText(RegisCourse.this,getString(R.string.Regiscouese), Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        else{
//            Toast.makeText(RegisCourse.this, "คุณสมัคเรียนวิชานี้ไปแล้ว กรุณาเลือกวิชาใหม่", Toast.LENGTH_SHORT).show();
//            onBackPressed();

        }


    }

    public class Insertstutentcourse extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(RegisCourse.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/Register_Course.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Course", MyArrList.get(0).get("Id_Course").toString()));
            nameValuePairs.add(new BasicNameValuePair("Id_Student", IDStu));
            nameValuePairs.add(new BasicNameValuePair("Id_Teacher", MyArrList.get(0).get("Id_Teacher").toString()));

            result = cn.connect(nameValuePairs, url);
            Log.d("result", result);
            JSONObject json_data;
            try {
                json_data = new JSONObject(result);
                code = (json_data.getInt("code"));
                } catch (JSONException e) {
                Log.e("Fail 1", e.toString());
                e.printStackTrace();
            }

            return null;


        }

        protected void onPostExecute(Void unused) {
            progressDialog.dismiss();
            ShowAllContent(); // When Finish Show Content
        }

    }

    private void GetDataC() {
        new DownloadDatacourse().execute();
    }

    public class DownloadDatacourse extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(RegisCourse.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/getCourse2.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Name_Coures", namec ));
            nameValuePairs.add(new BasicNameValuePair("FirstName", fnameteacher ));
            result = cn.connect(nameValuePairs, url);

            try {
                Log.d("result", result);
                JSONArray data = new JSONArray(result); //get เป็ย array
                MyArrList = new ArrayList<HashMap<String, Object>>();
                HashMap<String, Object> map;

                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i); //ข้อมูลที่ได้กลับมาทำ json
                    map = new HashMap<String, Object>();
                    map.put("Id_Course", c.getString("Id_Course"));
                    map.put("Id_Teacher", c.getString("Id_Teacher"));
                    map.put("Code_Course", c.getString("Code_Course"));
                    map.put("Description", c.getString("Description"));
                    map.put("Score", c.getString("Score"));

                    MyArrList.add(map);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ShowAllContent3();
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }

        public void ShowAllContent3() {
            for(int i = 0;i<MyArrList.size();i++) {
                etcc.setText(getString(R.string.codecourse) + " " + MyArrList.get(i).get("Code_Course").toString());
                etscore.setText(getString(R.string.Score1) + " " + MyArrList.get(i).get("Score") + " " + getString(R.string.Score2));
                etdescription.setText("คำอธิบายรายวิชา :" + " " + MyArrList.get(i).get("Description").toString());
//            etidt.setText(MyArrList.get(0).get("Id_Teacher").toString());
            }



//        idcourse = MyArrList.get(0).get("Id_Course").toString();
//         idteacher = MyArrList.get(0).get("Id_Teacher").toString();
//        codecourse = MyArrList.get(0).get("Code_Course").toString();
//        score = MyArrList.get(0).get("Score").toString();
        }
    }





}

