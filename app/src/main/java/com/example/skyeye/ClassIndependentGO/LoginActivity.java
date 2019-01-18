package com.example.skyeye.ClassIndependentGO;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    private long backPressedTime;
//    ProgressDialog dialog = null;
    private EditText etUsername, etPassword;
    private Button bLogin, registerSlink, registerTlink;
    private String Username,Password, Status;
    private ProgressDialog progressDialog;

    public class LoginRequest extends StringRequest {
        private static final String Login_REQUEST_URL = "https://classindependent.000webhostapp.com/Android/Login_Android.php";
        private Map<String, String> params;

        public LoginRequest(String username, String password, String stutus, Response.Listener<String> listener) {
            super(Request.Method.POST, Login_REQUEST_URL, listener, null);
            params = new HashMap<>();
            params.put("User", username);
            params.put("Password", password);
            params.put("Stutus", stutus);
        }
        @Override
        public Map<String, String> getParams() {
            return params;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword2);
        SlideImage();
        bLogin = (Button) findViewById(R.id.bLogin);
        registerSlink = (Button) findViewById(R.id.bRegisterS);
//        registerTlink = (Button) findViewById(R.id.bRegisterT);

        registerSlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerSIntent = new Intent(LoginActivity.this, RegisterSActivity.class);
                LoginActivity.this.startActivity(registerSIntent);
                Cleardata();
            }
        });

//        registerTlink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent registerTIntent = new Intent(LoginActivity.this, RegisterTActivity.class);
//                LoginActivity.this.startActivity(registerTIntent);
//            }
//        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("กรุณารอสักครู่ ...");
                progressDialog.show();

                 Username = etUsername.getText().toString();
                 Password = etPassword.getText().toString();


                if(TextUtils.isEmpty(Username)) {
                    etUsername.setError(getString(R.string.error1));
                    etUsername.requestFocus();
                    progressDialog.dismiss();
                    return;
                }

                else if(TextUtils.isEmpty(Password)) {
                    etPassword.setError(getString(R.string.error2));
                    etPassword.requestFocus();
                    progressDialog.dismiss();
                    return;
                }


                Response.Listener<String> responseListner = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            int code  = jsonResponse.getInt("code");
                            if (code == 1) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("User_S", Username);
                                LoginActivity.this.startActivity(intent);
                                Cleardata();
                                progressDialog.dismiss();

                            }
                            else if (code == 2) {
                                Intent Intent = new Intent(LoginActivity.this, Main2Activity.class);
                                Intent.putExtra("User", Username);
                                LoginActivity.this.startActivity(Intent);
                                Cleardata();
                                progressDialog.dismiss();
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("ชื่อผู้ใช้ หรือ รหัสผ่านผิด กรุณากรอกใหม่")
                                        .setNegativeButton("ตกลง", null)
                                        .create()
                                        .show();
                                progressDialog.dismiss();
                                Cleardata();


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(Username, Password,"", responseListner ) ;
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
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
            finish();     // bye
        }
    }

    public  void Cleardata(){
        etUsername.requestFocus();
        etUsername.setText("");
        etPassword.setText("");
    }

    private void SlideImage() {
        ImageView img_animation = (ImageView) findViewById(R.id.imgLogo);
        TextView txthead = (TextView) findViewById(R.id.txthead);
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, 430.0f, 0.0f);
        animation.setDuration(1000);
        animation.setStartOffset(3000);
        animation.setFillAfter(true);
        img_animation.startAnimation(animation);;
        txthead.startAnimation(animation);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
                layout.setVisibility(View.VISIBLE);

                Button button = (Button) findViewById(R.id.bRegisterS);
                button.setVisibility(View.VISIBLE);
            }
        }, 5000);
    }



}
