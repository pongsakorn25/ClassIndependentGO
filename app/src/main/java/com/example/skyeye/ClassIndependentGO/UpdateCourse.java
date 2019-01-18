package com.example.skyeye.ClassIndependentGO;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UpdateCourse extends AppCompatActivity {

    EditText etnamec, etcc, etscore, etdescription;
    private Button btnupdatecourse, btncancle;
    private String result,url,score,codecourse,namecourse,description, IDCourse;
    private String updatecodecourse, updatenamecourse, updatescore, updatedescription;
    private ProgressDialog progressDialog;
    ConnectMySQL cn = new ConnectMySQL();
    private int code;
    android.support.v7.app.ActionBar actionBar;
    Build build;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_course);


        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        codecourse = getIntent().getStringExtra("Code_Course");
        namecourse = getIntent().getStringExtra("Name_Coures");
        description = getIntent().getStringExtra("Description");
        score = getIntent().getStringExtra("Score");
        IDCourse = getIntent().getStringExtra("Id_Course");

        etnamec = findViewById(R.id.etUpdateNameCourse);
        etcc =  findViewById(R.id.etUpdateCodecourse);
        etscore = findViewById(R.id.etUpdateScore);
        etdescription = findViewById(R.id.etUpdateDescription);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        etnamec.setText(namecourse);
        etcc.setText(codecourse);
        etscore.setText(score);
        etdescription.setText(description);


        btnupdatecourse = findViewById(R.id.btnUpdateC);
        btnupdatecourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateCourse.this);
                alertDialog.setTitle("ยืนยันการแก้ไขข้อมูล");

                alertDialog.setIcon(R.drawable.ic_confirm);

                alertDialog.setPositiveButton(getString(R.string.OK),
                        new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            public void onClick(DialogInterface dialog, int which) {
                                updatecodecourse = etcc.getText().toString().trim();
                                updatenamecourse = etnamec.getText().toString().trim();
                                updatescore = etscore.getText().toString().trim();
                                updatedescription = etdescription.getText().toString().trim();

                                if(TextUtils.isEmpty(updatenamecourse)) {
                                    etnamec.setError(getString(R.string.error11));
                                    etnamec.requestFocus();
                                    return;
                                }

                                if(TextUtils.isEmpty(updatecodecourse)) {
                                    etcc.setError(getString(R.string.error10));
                                    etcc.requestFocus();
                                    return;
                                }

                                if(TextUtils.isEmpty(updatescore)) {
                                    etscore.setError(getString(R.string.error12));
                                    etscore.requestFocus();
                                    return;
                                }

                                if(TextUtils.isEmpty(updatedescription)) {
                                    etdescription.setError("กรุณากรอกคำอธิบายรายวิชา");
                                    etdescription.requestFocus();
                                    return;
                                }


                                GetUpdatecourse();
//                                Reloadcourse();
                                finish();
//                                UpdateCourse.super.onBackPressed();
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

        btncancle = findViewById(R.id.btnCancleupdate);
        btncancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateCourse.this);
                alertDialog.setTitle("ยกเลิกการแก้ไข");

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

    private void GetUpdatecourse() {
        new Updatecourse().execute();
    }

    public class Updatecourse extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(UpdateCourse.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/update_Course.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Course", IDCourse));
            nameValuePairs.add(new BasicNameValuePair("Code_Course", updatecodecourse));
            nameValuePairs.add(new BasicNameValuePair("Name_Coures", updatenamecourse));
            nameValuePairs.add(new BasicNameValuePair("Description", updatedescription));
            nameValuePairs.add(new BasicNameValuePair("Score", updatescore));

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
            Toast.makeText(this, "แก้ไขสำเร็จ", Toast.LENGTH_SHORT).show();
        } else {
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateCourse.this);
        alertDialog.setTitle("ยกเลิกการแก้ไข");

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

    public void Reloadcourse() {

        Intent intent = new Intent(this, UpdateCourse.class);
        intent.putExtra("Code_Course", codecourse);
        intent.putExtra("Name_Coures", namecourse);
        intent.putExtra("Description", description);
        intent.putExtra("Score", score);
        intent.putExtra("Id_Course", IDCourse);
        startActivity(intent);
        finish();

    }

}
