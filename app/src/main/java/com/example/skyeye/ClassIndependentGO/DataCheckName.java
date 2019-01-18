package com.example.skyeye.ClassIndependentGO;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DataCheckName extends AppCompatActivity {

    ConnectMySQL cn = new ConnectMySQL();
    private String url, result, IDstudent, datastu, IDcourse, NumOfCheck, SumScore2,numcheck;
    ArrayList<HashMap<String, Object>> MyArrList = new ArrayList<HashMap<String, java.lang.Object>>();
    ArrayList<HashMap<String, Object>> MyArrList2 = new ArrayList<HashMap<String, java.lang.Object>>();
    private ProgressDialog progressDialog;
    private int num1, num2, SumScore1, num3, aa;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_check_name);

        IDstudent = getIntent().getStringExtra("Id_Student");
        IDcourse = getIntent().getStringExtra("Id_Course");
        GetDataCheckname();

    }

    private void GetDataCheckname(){
        new DownloadDataCheckName().execute();
    }

    public class DownloadDataCheckName extends AsyncTask<String, Void, Void> {

        //ทำงานก่อนทำงานหลัก AsyncTask เมื่อถูกเรียกใช้
        @Override
        protected void onPreExecute() { //โหลดก่อนทำงาน
            super.onPreExecute();
            progressDialog = new ProgressDialog(DataCheckName.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/getDatacheckname.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Student", IDstudent));
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
                    map.put("FirtName_S", c.getString("FirtName_S"));
                    map.put("LastName_S", c.getString("LastName_S"));
                    map.put("Idcode_S", c.getString("Idcode_S"));
                    map.put("TimeCheck", c.getString("TimeCheck"));
                    map.put("ScoreCheck", c.getString("ScoreCheck"));
                    map.put("Score", c.getString("Score"));
                    map.put("Number_of_times", c.getString("Number_of_times"));
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

    @SuppressLint("WrongConstant")
    private void ShowAllContent(){
        TableLayout datacheck = (TableLayout) findViewById(R.id.datacheck_table);
        TextView txtdatastu = (TextView) findViewById(R.id.txtdatastu);
        TextView txtscore = (TextView) findViewById(R.id.txtScore);

        int dip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (float) 1, getResources().getDisplayMetrics());

        for(int i=0;i<MyArrList.size();i++) {
                aa = 1 + i;
                TableRow row = new TableRow(this);
                datastu = MyArrList.get(i).get("Idcode_S").toString() + " "  + MyArrList.get(i).get("FirtName_S").toString() + " " + MyArrList.get(i).get("LastName_S").toString();
                txtdatastu.setText(datastu);

                NumOfCheck = MyArrList.get(i).get("Number_of_times").toString();
                num1 = Integer.parseInt(NumOfCheck);
//                numcheck = String.valueOf(aa);
//                Toast.makeText(this, SumScore2, Toast.LENGTH_SHORT).show();



                String timecheck = MyArrList.get(i).get("TimeCheck").toString();
                String scorecheck = MyArrList.get(i).get("ScoreCheck").toString();
                String score = MyArrList.get(i).get("Score").toString();
                num2 = Integer.parseInt(score);
                num3 = Integer.parseInt(scorecheck);

                SumScore1 = aa *  num2 / num1 ;
                SumScore2 = String.valueOf(SumScore1);

                txtscore.setText("คะแนนข้าเรียน : " + SumScore2 + "  คะแนน" + "\n" + "คะแนนเต็ม : " + score + " คะแนน" + "\n" + "เปิดเช็คชื่อทั้งหมด : " + NumOfCheck + " ครั้ง");

                TextView t1 = new TextView(this);
                t1.setText("       " + aa);
                row.addView(t1);

                TextView t2 = new TextView(this);
                t2.setText("" + timecheck);
                row.addView(t2);

                TextView t3 = new TextView(this);
                t3.setText("" + scorecheck);
                row.addView(t3);

                t1.setTypeface(null, 1);
                t2.setTypeface(null, 1);
                t3.setTypeface(null, 1);

                t1.setTextSize(15);
                t2.setTextSize(15);
                t3.setTextSize(15);


                t1.setWidth(50 * dip);
                t2.setWidth(150 * dip);
                t3.setWidth(50 * dip);

//                t1.setPadding(20*dip, 0, 0, 0);
//                t2.setPadding(20*dip, 0, 0, 0);
//                t3.setPadding(20*dip, 0, 0, 0);
                datacheck.addView(row);
            }

//        name = MyArrList.get(0).get("Name_Coures").toString();
//        Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();

    }


}
