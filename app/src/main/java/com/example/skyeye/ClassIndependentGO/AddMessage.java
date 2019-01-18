package com.example.skyeye.ClassIndependentGO;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddMessage extends AppCompatActivity {
    android.support.v7.app.ActionBar actionBar;
    ConnectMySQL cn = new ConnectMySQL();
    private int code;
    private ProgressDialog progressDialog;
    private String namemessage,message,result, url, IDCourse, IDTeacher;
    private EditText etnamemessage, etmessage;
    private  Button add,cancle;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        IDCourse = getIntent().getStringExtra("Id_Course");
        IDTeacher = getIntent().getStringExtra("Id_Teacher");


        etnamemessage = (EditText) findViewById(R.id.etNameMessage);
        etmessage = (EditText) findViewById(R.id.etMessage);

        add = (Button) findViewById(R.id.btnAddMessage);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddMessage.this);
                alertDialog.setTitle("ยืนยันการเพิ่มข้อมูล");

                alertDialog.setIcon(R.drawable.ic_confirm);

                alertDialog.setPositiveButton(getString(R.string.OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                namemessage = etnamemessage.getText().toString().trim();
                                message = etmessage.getText().toString().trim();

                                if(TextUtils.isEmpty(namemessage)) {
                                    etnamemessage.setError("กรุณากรอกชื่อเรื่อง");
                                    etnamemessage.requestFocus();
                                    return;
                                }

                                if(TextUtils.isEmpty(message)) {
                                    etmessage.setError("กรุณากรอกข้อมูลข่าว");
                                    etmessage.requestFocus();
                                    return;
                                }
                                GetInsertMessage();

                                finish();
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


        cancle = (Button) findViewById(R.id.btnCancleM);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddMessage.this);
                alertDialog.setTitle("ยกเลิกการเพิ่มข้อมูลข่าวสาร ");

                alertDialog.setPositiveButton(getString(R.string.OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
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

    }

    private void GetInsertMessage() {
        new InsertMessage().execute();
    }

    public class InsertMessage extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(AddMessage.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/InsertMessage.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Course", IDCourse));
            nameValuePairs.add(new BasicNameValuePair("Name_message", namemessage));
            nameValuePairs.add(new BasicNameValuePair("message", message));
            nameValuePairs.add(new BasicNameValuePair("Id_Teacher", IDTeacher));

            result = cn.connect(nameValuePairs, url);
            Log.d("result11", result);
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
//            progressDialog.dismiss();
            ShowAllContent2(); // When Finish Show Content
        }

    }

    public void ShowAllContent2() {
        if (code == 1) {
            Toast.makeText(this, "เพิ่มข่าวสารสำเร็จ", Toast.LENGTH_SHORT).show();
        } else {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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
}
