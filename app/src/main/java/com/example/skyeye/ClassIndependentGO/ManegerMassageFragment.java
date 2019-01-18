package com.example.skyeye.ClassIndependentGO;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ManegerMassageFragment extends Fragment {
    ConnectMySQL cn = new ConnectMySQL();
    private String Username, url, result, ID, IDCourse, NameCourse, IDTeacher, namemessage, message, IDMessage;
    ListView listView;
    private ProgressDialog progressDialog;
    ArrayList<HashMap<String, Object>> MyArrList = new ArrayList<HashMap<String, java.lang.Object>>();
    private ImageButton btndeletemessage;

    public ManegerMassageFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_maneger_massage, container, false);

        Username = getArguments().getString("User");
        IDCourse = getArguments().getString("Id_Course");
        NameCourse = getArguments().getString("Name_Coures");
        IDTeacher = getArguments().getString("Id_Teacher");

        MyArrList.clear();
        GetDatamessage();

        listView = (ListView) rootView.findViewById(R.id.message_listview);
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),UpdateMessage.class);

                namemessage = MyArrList.get(position).get("Name_message").toString();
                message = MyArrList.get(position).get("message").toString();
                IDMessage = MyArrList.get(position).get("Id_message").toString();

                intent.putExtra("Id_Course",IDCourse);

                intent.putExtra("Name_message",namemessage);
                intent.putExtra("message",message);
                intent.putExtra("Id_message",IDMessage);

                startActivity(intent);

            }
        });
        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_message, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;

        switch (item.getItemId()) {
            case R.id.action_refesh1:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
                return true;

            case R.id.action_add:
                intent  = new Intent(getActivity(), AddMessage.class);
                intent.putExtra("Id_Course", IDCourse);
                intent.putExtra("Id_Teacher", IDTeacher);
                getActivity().startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void GetDatamessage(){
        new Datamessage().execute();
    }


    public class Datamessage extends AsyncTask<String, Void, Void> {

        //ทำงานก่อนทำงานหลัก AsyncTask เมื่อถูกเรียกใช้
        @Override
        protected void onPreExecute() { //โหลดก่อนทำงาน
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("กรุณารอสักครู่ ...");
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(String... params) {
            url = "https://classindependent.000webhostapp.com/Android/getMessage.php";
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Id_Course", IDCourse));
            result = cn.connect(nameValuePairs, url);

            try{
//                result = cn.connect(url);
                Log.d("result", result);
                JSONArray data = new JSONArray(result); //get เป็ย array
//                MyArrList = new ArrayList<HashMap<String, Object>>();
                HashMap<String, Object> map;//การจัดเก็บค่าตัวแป

                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i); //ข้อมูลที่ได้กลับมาทำ json
                    map = new HashMap<String, Object>();
                    map.put("Name_message", c.getString("Name_message"));
                    map.put("message", c.getString("message"));
                    map.put("Id_message", c.getString("Id_message"));

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

    private void ShowAllContent() {
//        IDTeacher = MyArrList.get(0).get("Id_Teacher").toString();
        try {
            listView.setAdapter(new Message(getActivity(), MyArrList));
        } catch (Exception e) {
            Toast.makeText(getActivity(), "ไม่มีข้อมูล", Toast.LENGTH_SHORT).show();
        }
    }

    public class Message extends BaseAdapter {

        private Context context;
        private ArrayList<HashMap<String, Object>> MyArr = null;

        ArrayList<String> recordings = new ArrayList<String>();
        public Message(Context c, ArrayList<HashMap<String, Object>> list)
        {
            // TODO Auto-generated method stub
            context = c;
            MyArr = list;
        }

        @Override
        public int getCount() {
            return MyArr.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            if (convertView == null) {
                convertView = inflater.inflate(R.layout.maneger_message_list, null);
            }


            //columtext
            TextView txtNameC = (TextView) convertView.findViewById(R.id.txtnamemessage);
            TextView txtNamepdf = (TextView) convertView.findViewById(R.id.txtmessage);

            txtNameC.setText(MyArr.get(position).get("Name_message").toString());
            txtNamepdf.setText("\n" + MyArr.get(position).get("message").toString());


            this.notifyDataSetChanged();





            return convertView;
        }
    }



}
