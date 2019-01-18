package com.example.skyeye.ClassIndependentGO;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class CourseList extends BaseAdapter
{
    private Context context;
    private ArrayList<HashMap<String, Object>> MyArr = null;

    ArrayList<String> recordings = new ArrayList<String>();

    public CourseList(Context c, ArrayList<HashMap<String, Object>> list)
    {
        // TODO Auto-generated method stub
        context = c;
        MyArr = list;
    }

    public int getCount() {
    // TODO Auto-generated method stub
    return MyArr.size();
}

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.course_list, null);
        }

        // Colname
        TextView txtNameC = (TextView) convertView.findViewById(R.id.txtCoursename);
        TextView txtNameF = (TextView) convertView.findViewById(R.id.txtNameF);
        TextView txtNameL = (TextView) convertView.findViewById(R.id.txtNameL);

//        ImageView imageView = (ImageView) convertView.findViewById(R.id.iconicsImageView);
//        imageView.setImageResource(R.drawable.ic_book_black_24dp);

        txtNameC.setText(context.getString(R.string.namecourse) +" " + MyArr.get(position).get("Name_Coures").toString());
        txtNameF.setText(context.getString(R.string.nameteacher) +" " + MyArr.get(position).get("FirstName").toString());
        txtNameL.setText(MyArr.get(position).get("LastName").toString());


        return convertView;
    }


//

}
