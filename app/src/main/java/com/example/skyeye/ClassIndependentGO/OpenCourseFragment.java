package com.example.skyeye.ClassIndependentGO;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class OpenCourseFragment extends Fragment {

    ConnectMySQL cn = new ConnectMySQL();
    ProgressDialog progressDialog;
    private EditText etcodecourse, etnamecourse, etscore, etdescription;
    private Button btnopencourse, btncancle;
    private String IDteacher,CodeCourse,NameCourse,Score,Description, url, result;

    private int code;
    public OpenCourseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("เปิดสอนหลักสูตร");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_open_course, container, false);

        etcodecourse = (EditText) rootView.findViewById(R.id.etCodecourse);
        etnamecourse = (EditText) rootView.findViewById(R.id.etNameCourse);
        etdescription = (EditText) rootView.findViewById(R.id.etDescription);
        etscore = (EditText) rootView.findViewById(R.id.etScore);



        IDteacher = getArguments().getString("Id_Teacher");
//        Toast.makeText(getActivity(), IDteacher,Toast.LENGTH_LONG).show();

        btnopencourse = (Button) rootView.findViewById(R.id.btnOpenC);
        btnopencourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeCourse = etcodecourse.getText().toString();
                NameCourse = etnamecourse.getText().toString();
                Description = etdescription.getText().toString();
                Score = etscore.getText().toString();


                if(TextUtils.isEmpty(CodeCourse)) {
                    etcodecourse.setError(getString(R.string.error10));
                    etcodecourse.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(NameCourse)) {
                    etnamecourse.setError(getString(R.string.error11));
                    etnamecourse.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(Description)) {
                    etdescription.setError("กรุณากรอกคำอธิบายรายวิชา");
                    etdescription.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(Score)) {
                    etscore.setError(getString(R.string.error12));
                    etscore.requestFocus();
                    return;
                }

                new InsertOpenCourse().execute();
            }
        });
        return  rootView;
    }

    private void ShowAllContnt(){
        if(code == 1){
            Toast.makeText(getActivity(),getString(R.string.Regiscouese), Toast.LENGTH_LONG).show();
        }
        else if(code == 2){
                etcodecourse.setError(getString(R.string.error13));
                etcodecourse.requestFocus();
                return;
        }
        else{
            Toast.makeText(getActivity(),"สมัครไม่สำเร็จ", Toast.LENGTH_LONG).show();
        }
    }

    private class InsertOpenCourse extends AsyncTask<String, Void, Void>{

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/OpenCourse.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Code_Course", CodeCourse ));
            nameValuePairs.add(new BasicNameValuePair("Name_Coures", NameCourse ));
            nameValuePairs.add(new BasicNameValuePair("Description", Description ));
            nameValuePairs.add(new BasicNameValuePair("Score", Score ));
            nameValuePairs.add(new BasicNameValuePair("Id_Teacher", IDteacher));

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
            ShowAllContnt();
            progressDialog.dismiss();
            super.onPostExecute(aVoid);
        }

    }

}
