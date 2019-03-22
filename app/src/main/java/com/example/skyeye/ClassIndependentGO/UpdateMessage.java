package com.example.skyeye.ClassIndependentGO;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import java.util.zip.Inflater;

public class UpdateMessage extends AppCompatActivity {

    android.support.v7.app.ActionBar actionBar;
    ConnectMySQL cn = new ConnectMySQL();
    private int code;
    private ProgressDialog progressDialog;
    private String namemessage,message,result, url, IDCourse, IDTeacher, updatenamessage, updatemessage, IDMessage;
    private EditText etnamemessage, etmessage;
    private Button btnupdate,btncancle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_message);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        IDCourse = getIntent().getStringExtra("Id_Course");
        namemessage = getIntent().getStringExtra("Name_message");
        message = getIntent().getStringExtra("message");
        IDMessage = getIntent().getStringExtra("Id_message");

        etnamemessage = (EditText) findViewById(R.id.etUpdateNameMessage);
        etmessage = (EditText) findViewById(R.id.etUpdateMessage);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        etnamemessage.setText(namemessage);
        etmessage.setText(message);

        btnupdate = (Button) findViewById(R.id.btnUpdateMessage);
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateMessage.this);
                alertDialog.setTitle("ยืนยันการแก้ไขข้อมูล");

                alertDialog.setIcon(R.drawable.ic_confirm);

                alertDialog.setPositiveButton(getString(R.string.OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                updatenamessage = etnamemessage.getText().toString().trim();
                                updatemessage = etmessage.getText().toString().trim();

                                if(TextUtils.isEmpty(updatenamessage)) {
                                    etnamemessage.setError("กรุณากรอกชื่อเรื่อง");
                                    etnamemessage.requestFocus();
                                    return;
                                }

                                if(TextUtils.isEmpty(updatemessage)) {
                                    etmessage.setError("กรุณากรอกรายละเอียด");
                                    etmessage.requestFocus();
                                    return;
                                }

                                GetUpdateMessage();

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

        btncancle = (Button) findViewById(R.id.btnCancleM);
        btncancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateMessage.this);
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

    private void GetUpdateMessage() {
        new Updatemessage().execute();
    }

    public class Updatemessage extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(UpdateMessage.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/Update_message.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Course", IDCourse));
            nameValuePairs.add(new BasicNameValuePair("Id_message", IDMessage));
            nameValuePairs.add(new BasicNameValuePair("Name_message", updatenamessage));
            nameValuePairs.add(new BasicNameValuePair("message", updatemessage));


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
                finish();
                return true;

            case R.id.action_delete:

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateMessage.this);
                alertDialog.setTitle("ต้องการลบข้อมูลหรือไม่");

                alertDialog.setPositiveButton(getString(R.string.OK),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                GetDelete();
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

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateMessage.this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete, menu);
        return true;
    }

    private void GetDelete(){
        new DeleteMessage().execute();
    }

    public class DeleteMessage extends AsyncTask<String, Void, Void> {

        //ทำงานก่อนทำงานหลัก AsyncTask เมื่อถูกเรียกใช้
        @Override
        protected void onPreExecute() { //โหลดก่อนทำงาน
            super.onPreExecute();
            progressDialog = new ProgressDialog(UpdateMessage.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/deleteMessage.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_message", IDMessage));
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
            Toast.makeText(this,"ลบเรียบร้อย", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this,"NOT DELETE", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        progressDialog.dismiss(); // try this
    }



}
