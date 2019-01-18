package com.example.skyeye.ClassIndependentGO;

import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;

/**
 * Created by TPK on 20-Feb-17.
 */

public class ConnectMySQL {

    InputStream inputStream = null;
    String result = null;
    String line = null;

    private void policys() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public String connect(ArrayList<NameValuePair> nameValuePairs, String url)
    {
        policys();
        try
        {//รูปแบบการติดต่อกับ Web Server ผ่าน HttpGet
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);//ติดต่อแบบมีการส่งค่าได้
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();//ส่งข้อมูลแบบเรียงต่อกัน
            Log.e("pass 1", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 1", e.toString());
        }

        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            inputStream.close();
            result = sb.toString();
            Log.e("pass 2", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 2", e.toString());
        }

        return result;
    }

    public String connect(String url) {
        policys();
        try {//รูปแบบการติดต่อกับ Web Server ผ่าน HttpGet
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url); //ติดต่อแบบมีการส่งค่าได้
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent(); //ส่งข้อมูลแบบเรียงต่อกัน
            Log.e("pass 1", "connection success ");

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try
        {

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));//อ่าข้อมูลในลักษณะแถว
            StringBuilder sb = new StringBuilder(); //เก็บค่าเป็นบรรทัด

            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            inputStream.close();
            result = sb.toString();
            Log.e("pass 2", "connection success ");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }




}
