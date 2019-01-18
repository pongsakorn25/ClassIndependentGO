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
    android.support.v7.app.ActionBar actionBar;

    public class RegisterRequest extends StringRequest {
        private static final String REGISTER_REQUEST_URL = "https://classindependent.000webhostapp.com/Android/Register_Student.php";
        private Map<String, String> params;


        public RegisterRequest(String username, String password, String idcode, String fname, String lname, String phone, String email,String gender, String status, Response.Listener<String> listener){
            super (Method.POST, REGISTER_REQUEST_URL, listener, null);
            params = new HashMap<>();//query string สำหรับใช้กับ MySql
            params.put("User", username);
            params.put("Password", password);
            params.put("FirtName_S", fname);
            params.put("LastName_S", lname);
            params.put("Idcode", idcode);
            params.put("Phone",  phone);
            params.put("Email", email);
            params.put("Gender", gender);
            params.put("Status", status);
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

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword1 = (EditText) findViewById(R.id.etPassword1);
        etPassword2 = (EditText) findViewById(R.id.etPassword2);
        etFname = (EditText) findViewById(R.id.etFname);
        etLname = (EditText) findViewById(R.id.etLname);
        etIdcode = (EditText) findViewById(R.id.etIdcode);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etEmail = (EditText) findViewById(R.id.etEmail);
        rgGender = (RadioGroup) findViewById(R.id.radioGrpGender);
        rMale = (RadioButton) findViewById(R.id.radioMale);
        rFemale = (RadioButton) findViewById(R.id.radioFemale);
        bRegister = (Button) findViewById(R.id.bRegister);


        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Username = etUsername.getText().toString().trim();
                Password1 = etPassword1.getText().toString().trim();
                Password2 = etPassword2.getText().toString().trim();
                Firstname = etFname.getText().toString().trim();
                Lastname = etLname.getText().toString().trim();
                Idcode = etIdcode.getText().toString().trim();
                Email = etEmail.getText().toString().trim();
                Phone = etPhone.getText().toString().trim();
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

                        final ProgressDialog pd = new ProgressDialog(RegisterSActivity.this);
                        pd.setMessage("กรุณารอสักครู่ ...");
                        pd.show();

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int code = jsonResponse.getInt("code");
                            if (code == 1) {
                                Toast.makeText(getBaseContext(), R.string.register,
                                        Toast.LENGTH_LONG).show();
                                pd.dismiss();
                               finish();

                            } else if (code == 2 || code == 4) {
                                etUsername.setError(getString(R.string.regis1));
                                etUsername.requestFocus();
                                pd.dismiss();
                                return;

                            } else if (code == 3 || code == 5) {
                                etEmail.setError(getString(R.string.regis2));
                                etEmail.requestFocus();
                                pd.dismiss();
                                return;

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterSActivity.this);
                                builder.setMessage(R.string.regis3)
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                                pd.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                RegisterRequest registerRequest = new RegisterRequest(Username, Password1, Idcode, Firstname, Lastname, Phone, Email, Gender, "S", responseListener);
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
