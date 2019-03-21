package com.example.skyeye.ClassIndependentGO;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterSActivity extends AppCompatActivity {

    private EditText etUsername, etPassword1,etPassword2, etFname, etLname, etIdcode, etPhone, etEmail;
    private Button bRegister;
    private String Username, Password1,Password2, Firstname, Lastname, Idcode, Email,Phone,Gender;
    private RadioGroup rgGender;
    private RadioButton rMale, rFemale;
    private long backPressedTime;
    ProgressDialog progressDialog;
    android.support.v7.app.ActionBar actionBar;

    public class RegisterRequest extends StringRequest {
        private static final String REGISTER_REQUEST_URL = "https://classindependent.000webhostapp.com/Android/Register_Student.php";
        private Map<String, String> params;


        public RegisterRequest( String fname, String lname, String idcode, String email, String phone, String gender,String username, String password,  String status, Response.Listener<String> listener){
            super (Method.POST, REGISTER_REQUEST_URL, listener, null);
            params = new HashMap<>();//query string สำหรับใช้กับ MySql
            params.put("FirtName_S", fname);
            params.put("LastName_S", lname);
            params.put("Idcode_S", idcode);
            params.put("Email_S", email);
            params.put("Phone_S",  phone);
            params.put("Gender_S", gender);
            params.put("User_S", username);
            params.put("Password_S", password);
            params.put("Status_S", status);
        }

        @Override
        public Map<String, String> getParams() {
            return params;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_s);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        etUsername =  findViewById(R.id.etUsername);
        etPassword1 =  findViewById(R.id.etPassword1);
        etPassword2 =  findViewById(R.id.etPassword2);
        etFname =  findViewById(R.id.etFname);
        etLname =  findViewById(R.id.etLname);
        etIdcode =  findViewById(R.id.etIdcode);
        etPhone =  findViewById(R.id.etPhone);
        etEmail =  findViewById(R.id.etEmail);
        rgGender =  findViewById(R.id.radioGrpGender);
        rMale =  findViewById(R.id.radioMale);
        rFemale =  findViewById(R.id.radioFemale);
        bRegister =  findViewById(R.id.bRegister);


        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Firstname = etFname.getText().toString().trim();
                Lastname = etLname.getText().toString().trim();
                Idcode = etIdcode.getText().toString().trim();
                Email = etEmail.getText().toString().trim();
                Phone = etPhone.getText().toString().trim();
                Username = etUsername.getText().toString().trim();
                Password1 = etPassword1.getText().toString().trim();
                Password2 = etPassword2.getText().toString().trim();

                isEmailValid(Email);

                if(TextUtils.isEmpty(Username)) {
                    etUsername.setError(getString(R.string.error1));
                    etUsername.requestFocus();
                    return;
                }
                else if(TextUtils.isEmpty(Password1)) {
                    etPassword1.setError(getString(R.string.error2));
                    etPassword1.requestFocus();
                    return;
                }
                else if(TextUtils.isEmpty(Password2)) {
                    etPassword2.setError(getString(R.string.error3));
                    etPassword2.requestFocus();
                    return;
                }
                else if(TextUtils.isEmpty(Firstname)) {
                    etFname.setError(getString(R.string.error4));
                    etFname.requestFocus();
                    return;
                }
                else if(TextUtils.isEmpty(Lastname)) {
                    etLname.setError(getString(R.string.error5));
                    etLname.requestFocus();
                    return;
                }
                else if(TextUtils.isEmpty(Idcode)) {
                    etIdcode.setError(getString(R.string.error6));
                    etIdcode.requestFocus();
                    return;
                }
                else if(TextUtils.isEmpty(Email)) {
                    etEmail.setError(getString(R.string.error7));
                    etEmail.requestFocus();
                    return;

                }


//                if(TextUtils.isEmpty(Phone)) {
//                    etPhone.setError(getString(R.string.error8));
//                    etPhone.requestFocus();
//                    return;
//                }

                if ((Password1 != null && !Password1.isEmpty()) && (Password2 != null && !Password2.isEmpty())) {
                    if (!Password1.matches(Password2)) {
                        etPassword1.setError(getString(R.string.error9));
                        etPassword2.setError(getString(R.string.error9));
                        etPassword2.requestFocus();
                        return;
                    }
                }

                if (rgGender.getCheckedRadioButtonId() == rMale.getId()) {
                    Gender = "ชาย";
                }
                else if (rgGender.getCheckedRadioButtonId() == rFemale.getId()) {
                    Gender = "หญิง";
                }



                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog = new ProgressDialog(RegisterSActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("กรุณารอสักครู่ ...");
                        progressDialog.show();

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int code = jsonResponse.getInt("code");
                            if (code == 1) {
                                Toast.makeText(getBaseContext(), R.string.register,
                                        Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                               finish();

                            } else if (code == 2 || code == 4) {
                                etUsername.setError(getString(R.string.regis1));
                                etUsername.requestFocus();
                                progressDialog.dismiss();
                                return;

                            } else if (code == 3 || code == 5) {
                                etEmail.setError(getString(R.string.regis2));
                                etEmail.requestFocus();
                                progressDialog.dismiss();
                                return;

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterSActivity.this);
                                builder.setMessage(R.string.regis3)
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
//                Toast.makeText(RegisterSActivity.this, Firstname + Lastname + Idcode + Email + Phone  + Gender + Username + Password1, Toast.LENGTH_LONG).show();
                RegisterRequest registerRequest = new RegisterRequest(Firstname, Lastname, Idcode, Email, Phone, Gender, Username, Password1,"S", responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterSActivity.this);
                queue.add(registerRequest);
            }
        });

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

    boolean isEmailValid(CharSequence Email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(Email)
                .matches();
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
}
